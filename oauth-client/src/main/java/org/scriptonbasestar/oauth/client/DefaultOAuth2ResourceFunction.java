package org.scriptonbasestar.oauth.client;

import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

@Slf4j
public class DefaultOAuth2ResourceFunction
		implements OAuth2ResourceFunction<String> {

	private final String resourceUri;

	public DefaultOAuth2ResourceFunction(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	@Override
	public String run(String accessToken) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(resourceUri);
			httpGet.addHeader("Authorization", "Bearer " + accessToken);
			log.debug("Executing request {} {}", httpGet.getMethod(), httpGet.getRequestUri());

			HttpClientResponseHandler<String> responseHandler = response -> {
				int status = response.getCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException(
						String.format("Unexpected response status: %d", status)
					);
				}
			};
			return httpClient.execute(httpGet, responseHandler);
		} catch (JsonParseException | IOException e) {
			log.error("Failed to fetch OAuth resource from {}: {}", resourceUri, e.getMessage(), e);
			return null;
		}
	}
}
