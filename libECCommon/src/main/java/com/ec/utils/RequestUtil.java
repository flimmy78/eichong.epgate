package com.ec.utils;

import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class RequestUtil {
	private static final int timeout = 10000;

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 *            url后参数
	 * @return 请求结果
	 */
	public static String doPost(String url, Map<String, Object> params) {
		URI uri = generateURL(url, params);
		HttpPost post = new HttpPost(uri);
		post.addHeader("Content-Type","application/x-www-form-urlencoded");

		String res = execute(post);
		System.out.println("request: " + url + " response :" + res);
		return res;
	}

	/**
	 * 生成请求的url
	 * 
	 * @param url
	 *            不带参数的url字符串
	 * @param params
	 *            参数
	 * @return URI， 请求的uri对象
	 */
	private static URI generateURL(String url, Map<String, Object> params) {
		URI uri = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			if (null != params) {
				for (Entry<String, Object> entry : params.entrySet()) {
					uriBuilder.addParameter(entry.getKey(),
							String.valueOf(entry.getValue()));
				}
			}
			uri = uriBuilder.build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

	/**
	 * 发起请求，关闭资源
	 * 
	 * @param request
	 *            （HttpPost 或 HttpGet）
	 * @return responseStr 请求返回值
	 */
	private static String execute(HttpUriRequest request) {
		String responseStr = null;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			httpclient = wrapClient(httpclient);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
			HttpResponse response = httpclient.execute(request);
			responseStr = EntityUtils.toString(response.getEntity(),
					CharsetUtil.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return responseStr;
	}

	public static HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
