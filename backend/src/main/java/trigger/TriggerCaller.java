package trigger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by neshati on 1/29/2017.
 * Behpardaz
 */
public abstract class TriggerCaller implements Serializable {
    public static String SDPURLKEY = "sdpURL";
    public HashMap<String, String> params;// params should include sdpURL

    public abstract void fillParams();

    public void fire() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(params.get("sdpURL"));
        List<NameValuePair> urlParameters = new ArrayList();
        for (String paramKey : params.keySet()) {
            urlParameters.add(new BasicNameValuePair(paramKey, params.get(paramKey)));
        }
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        System.out.println("Response Code for Service = : " + this.toString()
                + response.getStatusLine().getStatusCode());
    }
}
