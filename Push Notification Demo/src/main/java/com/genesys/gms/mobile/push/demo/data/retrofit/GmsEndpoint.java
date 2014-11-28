package com.genesys.gms.mobile.push.demo.data.retrofit;

/**
 * Created by stau on 11/27/2014.
 */
import android.util.Log;
import retrofit.Endpoint;

/**
 * Created by stau on 28/10/2014.
 */
public class GmsEndpoint implements Endpoint {
    private static final String ENDPOINT_HOST = "endpoint_host";
    private static final String ENDPOINT_PORT = "endpoint_port";
    private static final String ENDPOINT_API_VERSION = "endpoint_api_version";
    private String m_strUrl;

    @Override
    public String getName() {
        return "default";
    }

    public void setUrl(String p_strHost, String p_strPort, int p_nApiVersion) {
        m_strUrl = String.format("http://%s:%s/genesys/%d", p_strHost, p_strPort, p_nApiVersion);
        Log.d("setUrl", m_strUrl);
    }

    @Override
    public String getUrl() {
        if (m_strUrl == null) throw new IllegalStateException("setUrl() has not been called");
        return m_strUrl;
    }
}
