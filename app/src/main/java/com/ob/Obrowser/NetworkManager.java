package com.ob.Obrowser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;



/*
 *  This class is used to post the request params to the server.
 */
public class NetworkManager {
	
	private static final String SSL = "SSL";
	private static final String HTTPS = "https";
	private static final String HTTP = "http";
	private static final String HTTP_PROTOCOL_EXPECT_CONTINUE = "http.protocol.expect-continue";
	private static final String UTF_8 = "utf-8";

	public String postData(HttpPost post, List<NameValuePair> nameValuePairs) {
		String responseText = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			DefaultHttpClient client = getClient();
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			responseText = EntityUtils.toString(entity);
			Log.v("responseText from post",  responseText);
		} catch (UnsupportedEncodingException e) {
			Log.v("expection ",""+e.getStackTrace());
		} catch (ClientProtocolException e) {
			Log.v("expection ",""+e.getStackTrace());
		} catch (IOException e) {
			Log.v("expection ",""+e.getStackTrace());
		}
		return responseText;
	}

	/*
	 * This method is used to register the http and https. 
	 */
	
	public DefaultHttpClient getClient() {
		DefaultHttpClient ret = null;

		// sets up parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, UTF_8);
		params.setBooleanParameter(HTTP_PROTOCOL_EXPECT_CONTINUE, false);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(HTTP, PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme(HTTPS,	new com.ob.Obrowser.EasySSLSocketFactory(), 443));

		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
		ret = new DefaultHttpClient(manager, params);
		return ret;
	}
 
	/*
	 * This method is used to Create a trust manager that does not validate certificate chains.
	 */
	public void trustSSLCertificate() {
		 
		
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance(SSL);
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
	}
}
