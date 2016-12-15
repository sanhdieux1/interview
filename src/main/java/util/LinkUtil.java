package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import service.HTTPClientUtil;

public class LinkUtil {
    final static Logger logger = Logger.getLogger(LinkUtil.class);

    private static LinkUtil INSTANCE = new LinkUtil();

    private LinkUtil() {
    }

    public static LinkUtil getInstance() {
        return INSTANCE;
    }

    public Document getConnection(String link, String token) {
        Document doc = null;
        try{
            doc = Jsoup.connect(link).header("authorization", "Basic " + token).timeout(Constant.TIMEOUT).ignoreContentType(true).get();
        } catch (Exception e){
            logger.error(String.format("Connect %s with error %s", link, e));
        }
        return doc;
    }

    /**
     * @param link
     *            String.format(Constant.LINK_GET_SONAR_STATISTIC, metric, sonarKey)
     * @param ip
     *            Constant.PROXY_IP
     * @param port
     *            Constant.PROXY_PORT
     * @return doc
     */
    public Document getConnectionWithProxy(String link, String ip, int port) {
        Document doc = null;
        try{
            URL url = new URL(link);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            // non-proxy
            // HttpURLConnection uc = (HttpURLConnection)url.openConnection();

            uc.connect();

            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null){
                tmp.append(line);
            }

            doc = Jsoup.parse(String.valueOf(tmp));
        } catch (IOException e){
            logger.error(String.format("Connect %s using proxy %s:%s with error %s", link, ip, port, e));
        }
        return doc;
    }

    public boolean isUserAndPasswordValid(String username, String password) {
        if(username != null && password != null){
            Response respond = null;
            String authString = username + ":" + password;
            String token = new String(Base64.encodeBase64(authString.getBytes()));
            try{
                respond = Jsoup.connect(Constant.LINK_CRUCIBLE).header("authorization", "Basic " + token).timeout(Constant.TIMEOUT).execute();
            } catch (IOException e){
                logger.error(e);
            }
            return respond.header("X-AUSERNAME").equals(username);
        }
        return false;
    }

    public String getLegacyDataWithProxy(String host, String scheme, String path, String proxyIP, int proxyPort, String proxyType,
            Map<String, String> parameters) {
        URIBuilder builder = new URIBuilder();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setScheme(scheme).setHost(host).setPath(path);

        parameters.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String paramName, String paramValue) {
                builder.setParameter(paramName, paramValue);
            }
        });

        StringBuffer result = new StringBuffer();
        try{
            URI uri = builder.build();
            logger.info("Connecting to URI " + uri);
            HttpGet httpget = new HttpGet(uri);
            if(proxyIP != null && proxyType != null){
                HttpHost proxy = new HttpHost(proxyIP, proxyPort, proxyType);
                RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
                httpget.setConfig(config);
            }
            try (CloseableHttpResponse response = HTTPClientUtil.getInstance().execute(httpget);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));){

                String line = "";
                while ((line = rd.readLine()) != null){
                    result.append(line);
                }
            }
        } catch (URISyntaxException | IOException e){
            logger.error("cannot connect to " + host, e.getCause());
        }
        logger.info("Retrieved data " + result.toString());
        return result.toString();
    }
}
