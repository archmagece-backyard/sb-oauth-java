package org.scriptonbasestar.oauth.client.http;

import lombok.experimental.UtilityClass;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.scriptonbasestar.oauth.client.util.OAuthEncodeUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author archmagece
 * @since 2016-10-26 13
 */
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@UtilityClass
public final class ParamUtil {
	private static final char QUERY_QUESTION = '?';
	private static final char QUERY_AND = '&';
	private static final char QUERY_EQUAL = '=';

	public static String generateOAuthQuery(String url, ParamList paramList) {
		return generateOAuthQuery(url, paramList.paramSet().toArray(new Param[paramList.paramSet().size()]));
	}

	public static String generateOAuthQuery(String url, Collection<Param> params) {
		return generateOAuthQuery(url, params.toArray(new Param[params.size()]));
	}

	public static String generateOAuthQuery(String url, Param... params) {
		String queryString = Arrays.stream(params)
				.flatMap(param -> Arrays.stream(param.getValues())
						.map(value -> param.getKey() + QUERY_EQUAL + OAuthEncodeUtil.encode(value)))
				.collect(Collectors.joining(String.valueOf(QUERY_AND)));

		return url + QUERY_QUESTION + queryString;
	}

	public static List<NameValuePair> generateNameValueList(ParamList paramList) {
		return paramList.paramSet().stream()
				.flatMap(param -> Arrays.stream(param.getValues())
						.map(value -> new BasicNameValuePair(param.getKey(), value)))
				.collect(Collectors.toList());
	}

}
