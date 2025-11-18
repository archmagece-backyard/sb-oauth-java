package com.example.oauth.controller;

import com.example.oauth.service.OAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scripton.oauth.connector.naver.OAuth2NaverTokenRes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OAuth Controller
 *
 * Handles OAuth login and callback
 */
@Slf4j
@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

	private final OAuthService oauthService;

	/**
	 * Initiate Naver OAuth login
	 *
	 * Generates authorization URL and redirects user to Naver login page
	 */
	@GetMapping("/naver/login")
	public String naverLogin(HttpSession session) {
		log.info("Initiating Naver OAuth login");

		// Generate authorization URL with state
		String authUrl = oauthService.generateAuthUrl(session);

		log.debug("Redirecting to Naver: {}", authUrl);

		// Redirect to Naver login page
		return "redirect:" + authUrl;
	}

	/**
	 * OAuth callback endpoint
	 *
	 * Naver redirects back here with authorization code
	 *
	 * @param code Authorization code from Naver
	 * @param state State parameter for CSRF protection
	 * @param session HTTP session
	 * @return Redirect to profile page or error page
	 */
	@GetMapping("/callback/naver")
	public String naverCallback(
			@RequestParam("code") String code,
			@RequestParam("state") String state,
			HttpSession session) {

		log.info("Received OAuth callback - code: {}, state: {}", code, state);

		try {
			// Verify state (CSRF protection)
			String savedState = (String) session.getAttribute("oauth_state");
			if (savedState == null) {
				log.error("No saved state in session");
				return "redirect:/error?message=Session expired";
			}

			if (!state.equals(savedState)) {
				log.error("State mismatch - expected: {}, received: {}", savedState, state);
				return "redirect:/error?message=Invalid state (CSRF check failed)";
			}

			// Remove state from session (prevent reuse)
			session.removeAttribute("oauth_state");

			// Exchange code for token
			OAuth2NaverTokenRes tokenRes = oauthService.exchangeCodeForToken(code, state);

			log.info("Token exchange successful");
			log.debug("Access token: {}...", tokenRes.getAccess_token().substring(0, 10));

			// Store tokens in session
			session.setAttribute("access_token", tokenRes.getAccess_token());
			session.setAttribute("refresh_token", tokenRes.getRefresh_token());
			session.setAttribute("token_type", tokenRes.getToken_type());
			session.setAttribute("expires_in", tokenRes.getExpires_in());

			// Redirect to profile page
			return "redirect:/profile";

		} catch (Exception e) {
			log.error("OAuth callback failed", e);
			return "redirect:/error?message=" + e.getMessage();
		}
	}

	/**
	 * Logout endpoint
	 *
	 * Clears session and redirects to home
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		log.info("User logging out");

		// Clear session
		session.invalidate();

		return "redirect:/";
	}
}
