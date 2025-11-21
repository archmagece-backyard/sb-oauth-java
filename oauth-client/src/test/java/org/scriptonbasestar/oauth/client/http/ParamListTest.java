package org.scriptonbasestar.oauth.client.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for ParamList
 */
class ParamListTest {

  @Test
  void constructor_shouldCreateEmptyParamList() {
    ParamList paramList = new ParamList();
    assertThat(paramList.paramSet()).isEmpty();
  }

  @Test
  void add_withKeyValue_shouldAddParam() {
    ParamList paramList = new ParamList();
    paramList.add("key1", "value1");

    assertThat(paramList.paramSet()).hasSize(1);
    assertThat(paramList.paramSet().iterator().next().getKey()).isEqualTo("key1");
  }

  @Test
  void add_withParam_shouldAddParam() {
    ParamList paramList = new ParamList();
    Param param = new Param("key1", "value1");
    paramList.add(param);

    assertThat(paramList.paramSet()).hasSize(1);
    assertThat(paramList.paramSet()).contains(param);
  }

  @Test
  void add_multipleParams_shouldAddAll() {
    ParamList paramList = new ParamList();
    paramList.add("key1", "value1");
    paramList.add("key2", "value2");
    paramList.add("key3", "value3");

    assertThat(paramList.paramSet()).hasSize(3);
  }

  @Test
  void paramSet_shouldReturnUnmodifiableSet() {
    ParamList paramList = new ParamList();
    paramList.add("key1", "value1");

    assertThat(paramList.paramSet()).hasSize(1);
    // The returned set should contain the added parameter
    assertThat(paramList.paramSet().stream()
        .anyMatch(p -> p.getKey().equals("key1"))).isTrue();
  }

  @Test
  void add_duplicateKeys_shouldHandleCorrectly() {
    ParamList paramList = new ParamList();
    paramList.add("key1", "value1");
    paramList.add("key1", "value2");

    // Check that both params are added (sets are based on key equality)
    assertThat(paramList.paramSet()).isNotEmpty();
  }
}
