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
			HttpGet httpget1 = new HttpGet(resourceUri);
			httpget1.addHeader("Authorization", "Bearer " + accessToken);
			log.debug("Executing request {} {}", httpget1.getMethod(), httpget1.getRequestUri());
			HttpClientResponseHandler<String> responseHandler1 = response -> {
				int status = response.getCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			};
			return httpClient.execute(httpget1, responseHandler1);
		} catch (JsonParseException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
