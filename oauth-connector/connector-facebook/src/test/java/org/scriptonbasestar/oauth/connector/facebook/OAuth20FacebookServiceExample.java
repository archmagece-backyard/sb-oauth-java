package org.scriptonbasestar.oauth.connector.facebook;//package org.scripton.test.o20;
//
//import org.junit.Ignore;
//import org.junit.Test;
//import org.scriptonbasestar.oauth.client.core.model.OAuthPersonalConfig;
//import org.scriptonbasestar.oauth.client.nobi.state.RandomStringStateGenerator;
//import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
//import org.scriptonbasestar.oauth.client.o20.model.OAuth20AccessTokenConfig;
//import org.scriptonbasestar.oauth.client.o20.model.OAuth20AuthorizeTokenConfig;
//import org.scriptonbasestar.oauth.client.o20.model.Token20;
//import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
//
///**
// * @author archmagece
// * @since 2016-10-25
// */
//public class OAuth20FacebookServiceExample {
//
//	private static final String SERVICE_NAME = "FACEBOOK";
//	private static final String redirectUri = "http://test1.polypia.net/callback1";
//	private static OAuth20Client oAuth20Service = new OAuth20Client(
//			SERVICE_NAME,
//			new RandomStringStateGenerator(),
//			new OAuthPersonalConfig("client-id", "client-secret"),
//			OAuth20AuthorizeTokenConfig.builder()
//					.authorizeUrl("https://www.facebook.com/v2.8/dialog/oauth")
//					.callbackUrl(redirectUri)
//
//					//nullable default CODE
//					//CODE 코드나옴 code=verifier accessToken 호출해야함
//					.responseType(VerifierResponseType.CODE)
//					//TOKEN access token.. 사용자별 토큰? 바로나옴
////					.responseType(VerifierResponseType.TOKEN)
//
//					//nullable scope 없을시 기본값 적용
//					.scope("public_profile user_friends email")
//
//					.tokenFormatNobi(new JsonTokenExtractor())
////					.tokenFormatNobi(new StreamOutTokenExtractor())
//					.build(),
//			OAuth20AccessTokenConfig.builder()
//					.accessTokenUrl("https://graph.facebook.com/v2.8/oauth/access_token")
//					.callbackUrl(redirectUri)
//
////					.accessTokenVerb(OAuthHttpVerb.POST)
////					.signatureType(SignatureType.Header)
//
//					.tokenFormatNobi(new JsonTokenExtractor())
////					.tokenFormatNobi(new StreamOutTokenExtractor())
//					.build()
//	);
//
//	public static void main(String[] args) {
//		OAuth2ExampleHelper.test(oAuth20Service, SERVICE_NAME, "https://graph.facebook.com/v2.8/me");
//	}
//
//	@Ignore
//	@Test
//	public void accessTokenBearerTest() {
//		Token20 token20 = oAuth20Service.getAccessTokenBearer();
//		System.out.println("accessToken " + token20.getAccessToken());
//		System.out.println("expireIn " + token20.getExpireIn());
//		System.out.println("type " + token20.getTokenType());
//		System.out.println("idToken " + token20.getIdToken());
//		System.out.println("refreshToken " + token20.getRefreshToken());
//	}
//
//	@Ignore
//	@Test
//	public void accessTokenBearer_resource_Test() {
//		Token20 token20 = oAuth20Service.getAccessTokenBearer();
//		System.out.println("accessToken " + token20.getAccessToken());
//		System.out.println("expireIn " + token20.getExpireIn());
////		System.out.println("type "+token20.getTokenType());
////		System.out.println("idToken "+token20.getIdToken());
////		System.out.println("refreshToken "+token20.getRefreshToken());
//
//		String result = oAuth20Service.getResource("https://graph.facebook.com/v2.8/pizzahutkorea/feed", token20.getAccessToken());
//		System.out.println(result);
//	}
//
//}
