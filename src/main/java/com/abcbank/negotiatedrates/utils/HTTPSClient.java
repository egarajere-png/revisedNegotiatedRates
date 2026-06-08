package com.abcbank.negotiatedrates.utils;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Utility for making HTTPS requests using OkHttp.
 *
 * Supports various content types and custom headers. The client is configured
 * to accept all SSL certificates, which is used in this application for
 * environments where certificate validation is intentionally bypassed.
 */
@Slf4j
@Component
public class HTTPSClient {

	static OkHttpClient client = getUnsafeOkHttpClient();
	
	/**
	 * Sends an HTTPS request with custom headers and content type.
	 *
	 * The request is logged unless it targets authentication or echo endpoints.
	 *
	 * @param uri Request URI
	 * @param data Request body content
	 * @param requestMethod HTTP method to use (currently only POST or text fallback)
	 * @param otherHeaders Additional headers to include in the request
	 * @param contentType Content type name such as json, xml, soap, or text
	 * @return Response body string or null on failure
	 */
	public String sendHttpsRequest(String uri, String data, String requestMethod, HashMap<String,String> otherHeaders, String contentType) {
		boolean post = true;
		if(!uri.contains("token") && !uri.contains("auth") && !uri.contains("echo"))
		    log.info("\n ==================== Uri {} \n ==================== Request {} ====================", uri, data == null ? "" : data);
		if(contentType.equalsIgnoreCase("text")) {
			post = false;
			contentType = "text/plain";
		} else if(contentType.equalsIgnoreCase("json")) {
			contentType = "application/json";
		} else if(contentType.equalsIgnoreCase("xml")) {
			contentType = "application/xml";
		} else if(contentType.equalsIgnoreCase("soap")) {
			contentType = "application/soap+xml; charset=UTF-8";
		} else if(contentType.equalsIgnoreCase("soap2")) {
			contentType = "text/xml; charset=UTF-8";
		} else {
			contentType = "application/" + contentType.toLowerCase();
		}
		if(data == null) data = "";
		RequestBody body = RequestBody.create(MediaType.parse(contentType), data);

		Request.Builder requestBuilder = new Request.Builder().url(uri);
		requestBuilder.addHeader("Content-Type", contentType);
		
		for (Map.Entry<String,String> entry : otherHeaders.entrySet()) {
			String key = entry.getKey();
            String value = entry.getValue();
            if(key != null && value != null)
                requestBuilder.addHeader(key, value);
		}
		
		if(post == true)
			requestBuilder.post(body);

		Call call = client.newCall(requestBuilder.build());
		Response response;
		String responseString = null;
		try {
			response = call.execute();
			responseString = response.body().string();
			response.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		if(!uri.contains("token") && !uri.contains("auth") && !uri.contains("echo"))
		    log.info("\n ======================= Response {} ====================", responseString.length() < 500 ? responseString : responseString.substring(0, 500));
		return responseString;
	}
	
	/**
	 * Sends an HTTPS request with optional authorization header.
	 *
	 * @param uri Request URI
	 * @param data Request body payload
	 * @param requestMethod HTTP method to use
	 * @param authorization Authorization header value, such as Bearer token
	 * @param contentType Content type name such as json, xml, soap, or text
	 * @return Response body string or null on failure
	 */
	public String sendHttpsRequest(String uri, String data, String requestMethod, String authorization, String contentType) {
		if(!uri.contains("token") && !uri.contains("auth") && !uri.contains("echo"))
		    log.info("\n ==================== Uri {} \n ==================== Request {} ====================", uri, data == null ? "" : data);
		boolean post = true;
		if(contentType.equalsIgnoreCase("text")) {
			post = false;
			contentType = "text/plain";
		} else if(contentType.equalsIgnoreCase("json")) {
			contentType = "application/json";
		} else if(contentType.equalsIgnoreCase("xml")) {
			contentType = "application/xml";
		} else if(contentType.equalsIgnoreCase("soap")) {
			contentType = "application/soap+xml; charset=UTF-8";
		} else if(contentType.equalsIgnoreCase("soap2")) {
			contentType = "text/xml; charset=UTF-8";
		} else {
			contentType = "application/" + contentType.toLowerCase();
		}

		if(data == null) data = "";
		RequestBody body = RequestBody.create(MediaType.parse(contentType), data);

		Request.Builder requestBuilder = new Request.Builder().url(uri);
		requestBuilder.addHeader("Content-Type", contentType);
		if(authorization != null)
			requestBuilder.addHeader("Authorization", authorization);
		if(post == true)
			requestBuilder.post(body);

		Call call = client.newCall(requestBuilder.build());
		Response response;
		String responseString = null;
		try {
			response = call.execute();
			responseString = response.body().string();
			response.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		if(!uri.contains("token") && !uri.contains("auth") && !uri.contains("echo"))
			log.info("\n ======================= Response {} ====================", responseString.length() < 500 ? responseString : responseString.substring(0, 500));
		return responseString;
	}

	/**
	 * Returns an OkHttpClient configured to trust all SSL certificates and hostnames.
	 *
	 * This unsafe client is intended for environments where certificate validation
	 * is not required or when using self-signed certificates.
	 *
	 * @return OkHttpClient instance with permissive SSL settings
	 */
	private static OkHttpClient getUnsafeOkHttpClient() {
		try {
			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
								String authType) throws CertificateException {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
								String authType) throws CertificateException {
						}

						@Override
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[0];
						}
					}
			};

			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			return new OkHttpClient.Builder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.writeTimeout(60, TimeUnit.SECONDS)
				    .readTimeout(60, TimeUnit.SECONDS)
					.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					}).build();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}