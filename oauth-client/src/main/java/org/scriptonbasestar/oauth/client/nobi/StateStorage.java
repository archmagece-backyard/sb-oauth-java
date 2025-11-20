package org.scriptonbasestar.oauth.client.nobi;

import org.scriptonbasestar.oauth.client.model.State;

/**
 * state 추가add/확인exists만 존재
 * exists확인 후에는 storage에서 삭제.
 * memory db 이용 expire time 설정
 * jdbc 이용시는 flush schedule
 */
public interface StateStorage {
  /**
   * @param userId
   * @param state  add
   */
  void add(String userId, State state);

  /**
   * @param userId
   * @param state  check exists and drop
   */
  void exists(String userId, State state);
}
