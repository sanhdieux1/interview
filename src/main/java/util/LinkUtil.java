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
        try {
            doc = Jsoup.connect(link).header("authorization", "Basic " + token)
                    .timeout(Constant.TIMEOUT).ignoreContentType(true).get();
        } catch (Exception e) {
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
        try {
            URL url = new URL(link);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            // non-proxy
            // HttpURLConnection uc = (HttpURLConnection)url.openConnection();

            uc.connect();

            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null) {
                tmp.append(line);
            }

            doc = Jsoup.parse(String.valueOf(tmp));
        } catch (IOException e) {
            logger.error(
                    String.format("Connect %s using proxy %s:%s with error %s", link, ip, port, e));
        }
        return doc;
    }

    public boolean isUserAndPasswordValid(String username, String password) {
        if (username != null && password != null) {
            Response respond = null;
            String authString = username + ":" + password;
            String token = new String(Base64.encodeBase64(authString.getBytes()));
            try {
                respond = Jsoup.connect(Constant.LINK_CRUCIBLE)
                        .header("authorization", "Basic " + token).timeout(Constant.TIMEOUT)
                        .execute();
            } catch (IOException e) {
                logger.error(e);
                return false;
            }
            return respond.header("X-AUSERNAME").equals(username);
        }
        return false;
    }

    public String getLegacyDataWithProxy(String path, Map<String, String> parameters) {
        String host = PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_HOST);
        String scheme = PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_HOST_TYPE);
        URIBuilder builder = new URIBuilder();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setScheme(scheme).setHost(host).setPath(path);
        builder.setUserInfo("\"");
        parameters.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String paramName, String paramValue) {
                builder.setParameter(paramName, paramValue);
            }
        });

        StringBuffer result = new StringBuffer();
        CloseableHttpResponse response = null;
        BufferedReader rd = null;
        try {
            URI uri = builder.build();
            logger.info("Connecting to URI " + uri);
            HttpGet httpget = new HttpGet(uri);
            RequestConfig config = getProxyConfig();
            if (config != null) {
                httpget.setConfig(config);
            }
            try {
                response = HTTPClientUtil.getInstance().execute(httpget);
                String line = "";
                if (response != null) {
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                }
            } finally {
                if (response != null) {
                    response.close();
                }
                if(rd != null){
                    rd.close();
                }
            }
        } catch (URISyntaxException | IOException e) {
            logger.error("cannot connect to " + host, e);
        }
        return result.toString();
    }

    public RequestConfig getProxyConfig() {
        RequestConfig config = null;
        String proxyIP = PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PROXY_IP);
        String proxyPortStr = PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PROXY_PORT);
        String proxyType = PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PROXY_TYPE);
        int proxyPort = 0;
        try {
            if (proxyPortStr != null) {
                proxyPort = Integer.parseInt(proxyPortStr);
            }
        } catch (NumberFormatException e) {
            // ignore exeption
        }
        if (proxyIP != null && proxyType != null) {
//            logger.info("using proxy:" + proxyType + "://" + proxyIP + ":" + proxyPort);
            HttpHost proxy = new HttpHost(proxyIP, proxyPort, proxyType);
            config = RequestConfig.custom().setProxy(proxy).build();
        }
        return config;
    }
}
