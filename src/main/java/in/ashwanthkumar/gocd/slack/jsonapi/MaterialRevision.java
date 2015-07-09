package in.ashwanthkumar.gocd.slack.jsonapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

public class MaterialRevision {
    static private final Pattern PIPELINE_REVISION_PATTERN =
        Pattern.compile("^([^/]+)/(\\d+)");

    private Logger LOG = Logger.getLoggerFor(MaterialRevision.class);

    @SerializedName("changed")
    public boolean changed;

    @SerializedName("material")
    public Material material;

    @SerializedName("modifications")
    public Modification[] modifications;

    /**
     * Is this revision a pipeline, or something else (generally a commit
     * to a version control system)?
     */
    public boolean isPipeline() {
        return material.isPipeline();
    }

    /**
     * Collect all changed MaterialRevision objects, walking changed
     * "Pipeline" objects recursively instead of including them directly.
     */
    void addChangesRecursively(Server server, List<MaterialRevision> outChanges)
        throws MalformedURLException, IOException
    {
        // Give up now if this material hasn't changed.
        if (!changed) {
            return;
        }

        if (!isPipeline()) {
            outChanges.add(this);
        } else {
            // Recursively walk pipeline.  We're not entirely sure what it
            // would mean to have multiple associated modifications with
            // isPipeline is true, so we walk all of them just to be on the
            // safe side.
            for (Modification m : modifications) {
                // Parse out the pipeline info.
                Matcher matcher = PIPELINE_REVISION_PATTERN.matcher(m.revision);
                if (matcher.matches()) {
                    String pipelineName = matcher.group(1);
                    int pipelineCounter = Integer.parseInt(matcher.group(2));

                    // Fetch the pipeline and walk it recursively.
                    Pipeline pipeline =
                        server.getPipelineInstance(pipelineName, pipelineCounter);
                    pipeline.addChangesRecursively(server, outChanges);
                } else {
                    LOG.error("Error matching pipeline revision: " + m.revision);
                }
            }
        }
    }
}
