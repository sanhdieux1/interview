package service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import util.Constant;
import util.PropertiesUtil;

public class HTTPClientUtil {
    final static Logger logger = Logger.getLogger(HTTPClientUtil.class);
    private BasicCookieStore cookieStore = new BasicCookieStore();
    private static HTTPClientUtil instance;
    private CloseableHttpClient httpclient;

    private HTTPClientUtil() {

        httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        try{
            login(httpclient);
        } catch (URISyntaxException | IOException e){
            logger.error("cannot login to server:", e.getCause());
        }
    }

    public synchronized static HTTPClientUtil getInstance() {
        if(instance == null){
            instance = new HTTPClientUtil();
        }
        return instance;
    }

    public CloseableHttpResponse execute(HttpUriRequest request) {
        try{
            return httpclient.execute(request);
        } catch (IOException e){
            logger.error("cannot execute request", e.getCause());
        }
        return null;
    }

    private void login(CloseableHttpClient httpclient) throws URISyntaxException, ClientProtocolException, IOException {
        URI uri = new URI(
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST_TYPE) + "://" + (PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST) + "/login.jps"));
        RequestBuilder requestBuilder = RequestBuilder.post().setUri(uri)
                .addParameter("os_username", "hcongle")
                .addParameter("os_password", "hcl49#Tma");
        if(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_IP) != null){
            HttpHost proxy = new HttpHost(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_IP),
                    Integer.parseInt(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_PORT)),
                    PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_TYPE));
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            requestBuilder.setConfig(config);
        }
        HttpUriRequest loginRequest = requestBuilder.build();
            CloseableHttpResponse response = httpclient.execute(loginRequest);
            response.close();
            logger.info("login successfully");
        
    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

}
