package org.scriptonbasestar.oauth.client.http;

import org.apache.hc.core5.http.NameValuePair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for ParamUtil
 */
class ParamUtilTest {

  @Test
  void generateOAuthQuery_withParamList_shouldGenerateQuery() {
    ParamList paramList = new ParamList();
    paramList.add("client_id", "test123");
    paramList.add("redirect_uri", "http://example.com/callback");

    String result = ParamUtil.generateOAuthQuery("http://api.example.com/oauth", paramList);

    assertThat(result).startsWith("http://api.example.com/oauth?");
    assertThat(result).contains("client_id=test123");
    assertThat(result).contains("redirect_uri=http");
  }

  @Test
  void generateOAuthQuery_withCollection_shouldGenerateQuery() {
    Param param1 = new Param("key1", "value1");
    Param param2 = new Param("key2", "value2");
    List<Param> params = Arrays.asList(param1, param2);

    String result = ParamUtil.generateOAuthQuery("http://example.com/api", params);

    assertThat(result).isEqualTo("http://example.com/api?key1=value1&key2=value2");
  }

  @Test
  void generateOAuthQuery_withVarargs_shouldGenerateQuery() {
    Param param1 = new Param("name", "test");
    Param param2 = new Param("age", "25");

    String result = ParamUtil.generateOAuthQuery("http://example.com", param1, param2);

    assertThat(result).isEqualTo("http://example.com?name=test&age=25");
  }

  @Test
  void generateOAuthQuery_withSpecialCharacters_shouldEncodeValues() {
    Param param = new Param("message", "hello world");

    String result = ParamUtil.generateOAuthQuery("http://example.com", param);

    assertThat(result).contains("message=hello+world");
  }

  @Test
  void generateOAuthQuery_withMultipleValues_shouldGenerateMultipleParams() {
    Param param = new Param("scope", "read", "write", "admin");

    String result = ParamUtil.generateOAuthQuery("http://example.com", param);

    assertThat(result).contains("scope=read");
    assertThat(result).contains("scope=write");
    assertThat(result).contains("scope=admin");
  }

  @Test
  void generateNameValueList_shouldConvertToNameValuePairs() {
    ParamList paramList = new ParamList();
    paramList.add("key1", "value1");
    paramList.add("key2", "value2");

    List<NameValuePair> result = ParamUtil.generateNameValueList(paramList);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("key1");
    assertThat(result.get(0).getValue()).isEqualTo("value1");
  }

  @Test
  void generateNameValueList_withMultipleValues_shouldCreateMultiplePairs() {
    ParamList paramList = new ParamList();
    paramList.add("scope", "read", "write");

    List<NameValuePair> result = ParamUtil.generateNameValueList(paramList);

    assertThat(result).hasSize(2);
    assertThat(result.stream().map(NameValuePair::getName)).containsOnly("scope");
    assertThat(result.stream().map(NameValuePair::getValue)).contains("read", "write");
  }

  @Test
  void generateNameValueList_withEmptyParamList_shouldReturnEmptyList() {
    ParamList paramList = new ParamList();

    List<NameValuePair> result = ParamUtil.generateNameValueList(paramList);

    assertThat(result).isEmpty();
  }
}
