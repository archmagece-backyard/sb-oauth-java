package org.scriptonbasestar.oauth.client;

import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.model.Token;
import org.scriptonbasestar.oauth.client.model.Verifier;

/**
 * token action list
 * issueToken
 * refreshToken
 * deleteToken
 * checkToken
 * <p>
 * TokenStorage.load
 * TokenStorage.drop
 * TokenStorage.save
 * <p>
 * Resource.use
 * <p>
 * <p>
 * issueToken, TokenStorage.load, checkToken, refreshToken, TokenStorage.store
 * TokenStorage.load -> (return)
 * TokenStorage.load -> (null) -> issueToken -> TokenStorage.store -> (return)
 * TokenStorage.load -> (expired) -> refreshToken -> TokenStorage.store -> (return)
 * TokenStorage.load -> (expired) -> refreshToken -> (fail) -> issueToken -> TokenStorage.store -> (return)
 *
 * @param <TOKEN_RES>
 */
public interface OAuth2AccessTokenEndpointFunction<TOKEN_RES extends TokenPack> {

  TOKEN_RES issue(Verifier verifier, State state);

  TOKEN_RES refresh(Token refreshToken);

  TOKEN_RES revoke(Token accessToken);

//  TOKEN_RES bearer();
}
