package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
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
                    .timeout(PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT, 60000)).ignoreContentType(true).get();
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
    public Document getConnectionWithProxy(String link) {
        Document doc = null;
        try {
            URL url = new URL(link);
            
            Proxy proxy = HTTPClientUtil.getInstance().getProxy();
            HttpURLConnection uc = null;
            if(proxy != null){
                uc = (HttpURLConnection) url.openConnection(proxy);
            } else{
                uc = (HttpURLConnection) url.openConnection();
            }
            uc.setConnectTimeout(3000);
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
                    String.format("Connect %s with error %s", link, e));
        }
        return doc;
    }

    public boolean isUserAndPasswordValid(String username, String password) {
        if (username != null && password != null) {
            Response respond = null;
            String authString = username + ":" + password;
            String token = new String(Base64.encodeBase64(authString.getBytes()));
            try {
                Connection req = Jsoup.connect(Constant.LINK_CRUCIBLE)
                        .header("authorization", "Basic " + token).timeout(PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT, 60000))
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true);
                
                respond = req.execute();
            } catch (IOException e) {
                logger.error("Cannot verify user",e);
                return false;
            }
            logger.info("isUserAndPasswordValid:"+respond.header("X-AUSERNAME").equals(username));
            return respond.header("X-AUSERNAME").equals(username);
        }
        return false;
    }

  
}
