package com.cooperate.utils;

import com.ec.utils.LogUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zangyaoyi on 2016/12/30.
 */
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(HttpUtils.class.getName()));

    /**
     * POST方式发起http请求
     *
     * @param url    要请求的url
     * @param params 请求参数
     * @param token  运营商授权返回的token
     * @return http返回的response的body内容
     */
    public static String httpJSONPost(String url, Map<String, String> params, String token) throws IOException {
        logger.debug(LogUtil.addExtLog("url|params|token"), new Object[]{url, params, token});
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        if (token != null && token.length() > 0) {
            post.addHeader("Authorization", token);
            post.addHeader("Content-Type", "application/json;charset=utf-8");
        }

        JSONObject jsonObject = JSONObject.fromObject(params);
        // 设置参数
        StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        stringEntity.setContentType("application/json;charset=utf-8");
        post.setEntity(stringEntity);

        HttpClient httpClient = getHttpClient();
        HttpResponse response = httpClient.execute(post);
        StringBuffer sb = new StringBuffer();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String url = "http://10.9.2.232/html/tk/query_third_token.do";
        String param = "OperatorID=589179428";
        try {
            System.out.println("aAAAAAAAA~~~:" + httpGet(url, param));

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("BBBBBBBBBBBBB~~~:" + sendGet(url, param));
    }

    public static String httpGet(String url, String param)
            throws IOException {
        HttpGet httpGet = new HttpGet();
        // 设置参数
        try {
            httpGet.setURI(new URI(url + "?" + param));
        } catch (URISyntaxException e) {
            logger.debug(LogUtil.addExtLog("usr|param|Exception"), new Object[]{url, param, e.getMessage()});
        }
        // 发送请求
        HttpClient httpClient = getHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        StringBuffer sb = new StringBuffer();
        BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();

    }

    public static String sendGet(String url, String param) {
        logger.debug(LogUtil.addExtLog("url|param"), new Object[]{url, param});
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            result = null;
            logger.debug(LogUtil.addExtLog("Exception"), new Object[]{e.getMessage()});

        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * POST方式发起http请求
     *
     * @param url    要请求的url
     * @param params 请求参数
     * @return http返回的response的body内容
     */
    public static String httpPost(String url, Map<String, String> params) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        // params中参数放入list
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            list.add(basicNameValuePair);
        }
        post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));

        HttpClient httpClient = getHttpClient();
        httpClient.getParams().setIntParameter("time out ", 200);
        HttpResponse response = httpClient.execute(post);
        StringBuffer sb = new StringBuffer();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static String httpPostObject(String url, Map<String, Object> params) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        // params中参数放入list
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString());
            list.add(basicNameValuePair);
        }

        post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
        HttpClient httpClient = getHttpClient();
        HttpResponse response = httpClient.execute(post);
        StringBuffer sb = new StringBuffer();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static HttpClient getHttpClient() {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
            ClientConnectionManager ccm = new PoolingClientConnectionManager(schemeRegistry);

            //fixme 此处创建支持https的httpClient对象，但会接受任意的https证书，有安全隐患，生产环境中应避免不对https证书做校验
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
