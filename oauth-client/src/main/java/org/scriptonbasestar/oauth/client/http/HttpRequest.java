package org.scriptonbasestar.oauth.client.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.scriptonbasestar.oauth.client.exception.OAuthNetworkException;
import org.scriptonbasestar.oauth.client.exception.OAuthNetworkRemoteException;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * @author archmagece
 * @since 2016-10-25 16
 */
@Slf4j
public final class HttpRequest {

	private final CloseableHttpClient httpclient;
	private final String url;
	private final ParamList paramList;

	/**
	 * Sensitive parameter names to sanitize in logs
	 */
	private static final String[] SENSITIVE_PARAMS = {
		"client_secret",
		"access_token",
		"refresh_token",
		"code",
		"password",
		"api_key",
		"apikey"
	};

	private HttpRequest(String url, ParamList paramList) {
		this.httpclient = HttpClients.createDefault();
		this.url = url;
		this.paramList = paramList;
	}

	private HttpRequest(String url, ParamList paramList, Collection<Header> headers) {
		this.httpclient = HttpClients.custom().setDefaultHeaders(headers).build();
		this.url = url;
		this.paramList = paramList;
	}

	public static HttpRequest create(String url) {
		return new HttpRequest(url, new ParamList());
	}

	public static HttpRequest create(String url, ParamList paramList) {
		return new HttpRequest(url, paramList);
	}

	public static HttpRequest create(String url, ParamList paramList, Collection<Header> headers) {
		return new HttpRequest(url, paramList, headers);
	}

	public static HttpRequest create(String url, Collection<Header> headers) {
		return new HttpRequest(url, new ParamList(), headers);
	}

	/**
	 * Sanitize URL by masking sensitive parameters for safe logging
	 *
	 * @param urlOrUri the URL or URI string to sanitize
	 * @return sanitized string with sensitive values masked
	 */
	private static String sanitizeForLogging(String urlOrUri) {
		if (urlOrUri == null) {
			return null;
		}

		String sanitized = urlOrUri;
		for (String sensitiveParam : SENSITIVE_PARAMS) {
			// Match pattern: param=value (value can contain any characters except & and space)
			String pattern = "(" + sensitiveParam + "=)[^&\\s]*";
			sanitized = sanitized.replaceAll(pattern, "$1***");
		}
		return sanitized;
	}

	public String run(OAuthHttpVerb httpVerb) {
		try {
			switch (httpVerb) {
				case POST:
					return postContent();
				case GET:
				default:
					return getContent();
			}
		} catch (IOException e) {
//			e.printStackTrace();
			throw new OAuthNetworkException("extends IOException - 네트워크 오류");
		}
	}

	private String postContent() throws IOException {
		log.debug("postContent()");
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
			ParamUtil.generateNameValueList(paramList),
			StandardCharsets.UTF_8
		);
		log.debug("post to: {}", sanitizeForLogging(url));

		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(urlEncodedFormEntity);

		log.debug("Executing request {} {}", httpPost.getMethod(), sanitizeForLogging(httpPost.getRequestUri()));
		try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
			return httpResponseToString(response);
		} finally {
			httpclient.close();
		}
	}

	private String getContent() throws IOException {
		log.debug("getContent()");
		HttpGet httpget = new HttpGet(ParamUtil.generateOAuthQuery(url, paramList));
		log.trace("get to: {}", sanitizeForLogging(httpget.getUri().toString()));

		log.debug("Executing request {} {}", httpget.getMethod(), sanitizeForLogging(httpget.getRequestUri()));
		try (CloseableHttpResponse response = httpclient.execute(httpget)) {
			return httpResponseToString(response);
		} finally {
			httpclient.close();
		}
	}

	private String httpResponseToString(CloseableHttpResponse response) throws IOException {
		log.debug("HTTP {} {}", response.getCode(), response.getReasonPhrase());
		try {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new OAuthNetworkRemoteException("network connection exception. Remote 서버에서 응답이 없습니다.");
			}

			try {
				String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
				EntityUtils.consume(entity);
				return result;
			} catch (IOException e) {
				throw new OAuthNetworkRemoteException("network stream exception. 데이터를 받아오는 중 문제 발생", e);
			}
		} finally {
			response.close();
		}
	}

}
