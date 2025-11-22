package org.scriptonbasestar.oauth.client.http;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.TimeValue;
import org.scriptonbasestar.oauth.client.exception.OAuthNetworkException;
import org.scriptonbasestar.oauth.client.exception.OAuthNetworkRemoteException;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author archmagece
 * @since 2016-10-25 16
 */
public final class HttpRequest {

  private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

  /**
   * Shared HTTP client instance with connection pooling and optimized settings.
   * Using a shared client improves performance by reusing connections.
   */
  private static final CloseableHttpClient SHARED_CLIENT;

  static {
    PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
        .setMaxConnPerRoute(20)
        .setMaxConnTotal(100)
        .build();

    SHARED_CLIENT = HttpClients.custom()
      .setConnectionManager(connectionManager)
      .evictIdleConnections(TimeValue.of(30, TimeUnit.SECONDS))
      .build();
  }

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
    this.httpclient = SHARED_CLIENT;
    this.url = url;
    this.paramList = paramList;
  }

  private HttpRequest(String url, ParamList paramList, Collection<Header> headers) {
    // For custom headers, create a new client instance
    // This is less common, so performance impact is minimal
    PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
        .setMaxConnPerRoute(20)
        .setMaxConnTotal(100)
        .build();

    this.httpclient = HttpClients.custom()
      .setConnectionManager(connectionManager)
      .setDefaultHeaders(headers)
      .build();
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
      return switch (httpVerb) {
        case POST -> postContent();
        case GET -> getContent();
      };
    } catch (IOException e) {
      throw new OAuthNetworkException("extends IOException - 네트워크 오류", e);
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
    return httpclient.execute(httpPost, RESPONSE_HANDLER);
  }

  private String getContent() throws IOException {
    log.debug("getContent()");
    HttpGet httpget = new HttpGet(ParamUtil.generateOAuthQuery(url, paramList));
    try {
      log.trace("get to: {}", sanitizeForLogging(httpget.getUri().toString()));
      log.debug("Executing request {} {}", httpget.getMethod(), sanitizeForLogging(httpget.getRequestUri()));
    } catch (java.net.URISyntaxException e) {
      throw new OAuthNetworkException("Invalid URI", e);
    }

    return httpclient.execute(httpget, RESPONSE_HANDLER);
  }

  private static final HttpClientResponseHandler<String> RESPONSE_HANDLER = response -> {
    log.debug("HTTP {} {}", response.getCode(), response.getReasonPhrase());
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
    } catch (org.apache.hc.core5.http.ParseException e) {
      throw new OAuthNetworkRemoteException("response parsing exception. 응답 파싱 중 문제 발생", e);
    }
  };

}
