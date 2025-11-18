package com.example.oauth.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scripton.oauth.connector.naver.OAuth2NaverAccesstokenFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverAuthFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverTokenRes;
import org.scriptonbasestar.oauth.client.DefaultOAuth2ResourceFunction;
import org.scriptonbasestar.oauth.client.OAuth2ResourceFunction;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.model.Verifier;
import org.scriptonbasestar.oauth.client.nobi.state.StateGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * OAuth Service
 *
 * Business logic for OAuth operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

	private final OAuth2NaverAuthFunction naverAuthFunction;
	private final OAuth2NaverAccesstokenFunction naverTokenFunction;
	private final StateGenerator stateGenerator;

	@Value("${oauth.naver.profile-endpoint}")
	private String profileEndpoint;

	/**
	 * Generate authorization URL
	 *
	 * @param session HTTP session to store state
	 * @return Authorization URL for Naver login
	 */
	public String generateAuthUrl(HttpSession session) {
		// Generate state for CSRF protection
		State state = stateGenerator.generate("NAVER");

		// Store state in session for later verification
		session.setAttribute("oauth_state", state.getValue());

		log.debug("Generated state: {}", state.getValue());

		// Generate authorization URL
		String authUrl = naverAuthFunction.generate(state);

		return authUrl;
	}

	/**
	 * Exchange authorization code for access token
	 *
	 * @param code Authorization code from Naver
	 * @param stateValue State value for verification
	 * @return Token response from Naver
	 */
	public OAuth2NaverTokenRes exchangeCodeForToken(String code, String stateValue) {
		log.debug("Exchanging code for token - code: {}, state: {}", code, stateValue);

		// Create Verifier and State objects
		Verifier verifier = new Verifier(code);
		State state = new State(stateValue);

		// Exchange code for token
		OAuth2NaverTokenRes tokenRes = naverTokenFunction.issue(verifier, state);

		log.info("Token issued successfully");

		return tokenRes;
	}

	/**
	 * Fetch user profile from Naver
	 *
	 * @param accessToken Access token
	 * @return User profile JSON
	 */
	public String fetchUserProfile(String accessToken) {
		log.debug("Fetching user profile with access token");

		// Create resource function
		OAuth2ResourceFunction<String> resourceFunction =
				new DefaultOAuth2ResourceFunction(profileEndpoint);

		// Fetch profile
		String profileJson = resourceFunction.fetch(accessToken);

		log.debug("Profile fetched successfully");

		return profileJson;
	}
}
