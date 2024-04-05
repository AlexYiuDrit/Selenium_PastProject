package hkma.gov.hk.utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import hkma.gov.hk.entity.ResponseData;


public class HttpsAPIGateway {

	public static String method_get = "GET";
	public static String method_post = "POST";
	
	static {
		System.setProperty("https.proxyHost", ConfigProperties.getInstance().getConfig("proxy.server.host"));
		System.setProperty("https.proxyPort", ConfigProperties.getInstance().getConfig("proxy.server.port"));
	}

	public static ResponseData<String> getResult(String url, Map<String,String> param, String method, String phpSessionID) throws Exception {
		String resultUrl = url;
		String paramStr = "";
		for(String key : param.keySet()) {
			try {
				paramStr += "&" + key + "=" + URLEncoder.encode(param.get(key),"UTF-8") ;
			} catch (UnsupportedEncodingException e) {
				throw e;
			} 
		}
		return getResult(resultUrl, paramStr, method, phpSessionID);
	}
	
	public static ResponseData<String> getResult(String urlStr, String paramStr, String method, String phpSessionID) throws Exception {			
		if(method.equalsIgnoreCase(method_get)) {
			urlStr = urlStr + "?" + paramStr;
		}
		try {
			trustAllHosts();
			URL url = new URL(urlStr);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			conn.setRequestProperty("Cookie", phpSessionID);
//			conn.setRequestProperty("Authorization", token);
			
			if (method.equalsIgnoreCase(method_get)) {
				conn.setRequestMethod(method_get);
			} else {
				conn.setRequestMethod(method_post);
				conn.setDoOutput(true);
				
		        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
		            wr.writeBytes(paramStr);
		            wr.flush();
		        }
			}
			String responseBody = null;	
			int statusCode = conn.getResponseCode();
			if (statusCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				responseBody = response.toString();
			}
			conn.disconnect();
			return new ResponseData<String>(statusCode, responseBody);
		} catch (MalformedURLException e) {
			System.err.println(e);
			throw e;
		} catch (IOException e) {
			System.err.println(e);
			throw e;
		}			
	}	

	private static void trustAllHosts() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
						throws java.security.cert.CertificateException {

				}

			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
