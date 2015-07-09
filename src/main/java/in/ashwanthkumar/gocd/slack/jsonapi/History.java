package in.ashwanthkumar.gocd.slack.jsonapi;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import in.ashwanthkumar.gocd.slack.ruleset.Rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

public class History {
    public static URL url(String pipelineName) throws MalformedURLException {
        return new URL(String.format("http://localhost:8153/go/api/pipelines/%s/history", pipelineName));
    }

    public static History get(Rules rules, String pipelineName)
        throws MalformedURLException, IOException
    {
        // Based on
        // https://github.com/matt-richardson/gocd-websocket-notifier/blob/master/src/main/java/com/matt_richardson/gocd/websocket_notifier/PipelineDetailsPopulator.java
        // http://stackoverflow.com/questions/496651/connecting-to-remote-url-which-requires-authentication-using-java

        URL url = url(pipelineName);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();

        // Add in our HTTP authorization credentials if we have them.
        String username = rules.getGoLogin();
        String password = rules.getGoPassword();
        if (username != null && password != null) {
            String userpass = username + ":" + password;
            String basicAuth = "Basic "
                + DatatypeConverter.printBase64Binary(userpass.getBytes());
            request.setRequestProperty("Authorization", basicAuth);
        }

        request.connect();

        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(new InputStreamReader((InputStream) request.getContent()));
        return new GsonBuilder().create().fromJson(json, History.class);
    }

    @SerializedName("pipelines")
    public Pipeline[] pipelines;
}

