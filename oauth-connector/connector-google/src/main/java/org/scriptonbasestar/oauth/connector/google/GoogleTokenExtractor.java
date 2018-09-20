//package org.scriptonbasestar.oauth.connector.google;
//
//import com.google.gson.Gson;
//import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
//import org.scriptonbasestar.oauth.client.o20.model.Token20;
//import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;
//
//import java.util.Map;
//
///**
// * @author archmagece
// * @since 2016-10-27
// */
//public class GoogleTokenExtractor implements TokenExtractor {
//	private Gson gson = new Gson();
//
//	@Override
//	public Token extract(String socialResponse) {
//		Map<String, Object> map = gson.fromJson(socialResponse, Map.class);
//		return new Token20(
//			map.get("access_token").toString(),
//			map.get("expires_in") == null ? 0 : Double.parseDouble(map.get("expires_in").toString()),
//			map.get("token_type") == null ? null : AccessTokenType.valueOf(map.get("token_type").toString().toUpperCase()),
//			map.get("id_token") == null ? null : map.get("id_token").toString(),
//			map.get("refresh_token") == null ? null : map.get("refresh_token").toString()
//		);
//	}
//}
