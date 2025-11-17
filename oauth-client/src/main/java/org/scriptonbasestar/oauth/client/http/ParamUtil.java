package org.scriptonbasestar.oauth.client.http;

import lombok.experimental.UtilityClass;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.scriptonbasestar.oauth.client.util.OAuthEncodeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		StringBuilder sb = new StringBuilder();
		sb.append(url).append(QUERY_QUESTION);

		for (Param param : params) {
			for (int j = 0; j < param.getValues().length; j++) {
				sb.append(param.getKey()).append(QUERY_EQUAL).append(OAuthEncodeUtil.encode(param.getValues()[j]));
				if (j < param.getValues().length) {
					sb.append(QUERY_AND);
				}
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static List<NameValuePair> generateNameValueList(ParamList paramList) {
		List<NameValuePair> formParams = new ArrayList<>();
		for (Param param : paramList.paramSet()) {
			for (String value : param.getValues()) {
				formParams.add(new BasicNameValuePair(param.getKey(), value));
			}
		}
		return formParams;
	}

}
