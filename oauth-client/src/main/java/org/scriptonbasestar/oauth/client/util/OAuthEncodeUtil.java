package org.scriptonbasestar.oauth.client.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author archmagece
 * @since 2016-10-26 14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OAuthEncodeUtil {

	public static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	public static String[] encodeArray(String[] values) {
		return Arrays.stream(values)
				.map(OAuthEncodeUtil::encode)
				.toArray(String[]::new);
	}
}
