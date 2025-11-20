package org.scripton.test;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.scriptonbasestar.oauth.client.OAuth2AccessTokenEndpointFunction;
import org.scriptonbasestar.oauth.client.OAuth2GenerateAuthorizeEndpointFunction;
import org.scriptonbasestar.oauth.client.OAuth2ResourceFunction;
import org.scriptonbasestar.oauth.client.TokenPack;
import org.scriptonbasestar.oauth.client.exception.OAuthAuthException;
import org.scriptonbasestar.oauth.client.exception.OAuthUnknownException;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.model.Verifier;
import org.scriptonbasestar.oauth.client.nobi.state.StateGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author archmagece
 * @since 2015-11-18
 */
public class OAuth2ExampleHelper<TOKEN_RES extends TokenPack> {

	private final String SERVICE_NAME;
	private final StateGenerator stateGenerator;
	public OAuth2ExampleHelper(String serviceName, StateGenerator stateGenerator){
		this.SERVICE_NAME = serviceName;
		this.stateGenerator = stateGenerator;
	}

	public void test(OAuth2GenerateAuthorizeEndpointFunction authorizeUrlFunction, OAuth2AccessTokenEndpointFunction<TOKEN_RES> tokenFunction, OAuth2ResourceFunction<String> resourceFunction) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("=== " + SERVICE_NAME + "  ===");
		System.out.println();

		State state0 = stateGenerator.generate(SERVICE_NAME);
		//authorize url
		System.out.println("Authorize URL 생성s");
		String authorizationUrl = authorizeUrlFunction.generate(state0);
		System.out.println("url : " + authorizationUrl);
		System.out.println("Authorize URL 생성e");

		System.out.println();
		System.out.println();

		System.out.println(SERVICE_NAME + "로부터 받은 code 입력");
		System.out.println("verifier >>");
		Verifier verifier0 = new Verifier(scanner.nextLine());

		System.out.println(SERVICE_NAME + "로부터 받은 state 입력");
		System.out.println("state >>");
		String stateReturn = scanner.nextLine();
		if (!stateReturn.equals(state0.getValue())) {
			throw new OAuthAuthException("인증실패 - state가 변조되었나 모르겠는데 값이다름. 잘못잘라붙이는 경우 확인");
		}
//		if (SERVICE_NAME.equals("GOOGLE")) {
//			state0 = null;
//		}
		System.out.println();
		System.out.println();

		//access token
		System.out.println("Access Token 호출s");
		TOKEN_RES token20 = tokenFunction.issue(verifier0, state0);
		System.out.println("access token : " + token20);
		System.out.println("Access Token 호출e");
		System.out.println();
		System.out.println();

		//protected resource
		System.out.println("Projected Resource 호출s");
		String response = resourceFunction.run(token20.getAccessToken());
		System.out.println("(resource : " + response + " )");
		System.out.println("Projected Resource 호출e");
		System.out.println();
		System.out.println();

		System.out.println("FIN.");
	}

	public void nakedCall(String urlString) {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpget = new HttpGet(urlString);
			try (CloseableHttpResponse response = httpclient.execute(httpget)) {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
				System.out.println(result);
				EntityUtils.consume(entity);
			}
		} catch (IOException e) {
			throw new OAuthUnknownException(e);
		} catch (org.apache.hc.core5.http.ParseException e) {
			throw new OAuthUnknownException("response parsing exception", e);
		}
	}
}
