package in.ashwanthkumar.gocd.slack.jsonapi;

import com.google.gson.annotations.SerializedName;

public class Material {
    // Format: "Pipeline", etc.
    @SerializedName("type")
    public String type;

    public boolean isPipeline() {
        return type.equals("Pipeline");
    }

    //"id": 4080,
    //"fingerprint": "d22ec438c20be7f700e2aca7f4f416eef11e5ec2bbcf201c6f03f02ed8b2a6e0",
    //"description": "zoo"
}
