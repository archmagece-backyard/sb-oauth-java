package com.example.oauth.controller;

import com.example.oauth.service.OAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Home Controller
 *
 * Handles main pages (index, profile, error)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final OAuthService oauthService;

	/**
	 * Home page
	 */
	@GetMapping("/")
	public String index(HttpSession session, Model model) {
		// Check if user is already logged in
		String accessToken = (String) session.getAttribute("access_token");
		model.addAttribute("isLoggedIn", accessToken != null);

		return "index";
	}

	/**
	 * Profile page
	 *
	 * Displays user profile from Naver
	 */
	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {
		String accessToken = (String) session.getAttribute("access_token");

		if (accessToken == null) {
			log.warn("No access token in session, redirecting to login");
			return "redirect:/";
		}

		try {
			// Fetch user profile from Naver
			String profileJson = oauthService.fetchUserProfile(accessToken);

			log.debug("Profile JSON: {}", profileJson);

			// Add profile to model (we'll display raw JSON in this example)
			model.addAttribute("profile", profileJson);
			model.addAttribute("accessToken", accessToken);

			Integer expiresIn = (Integer) session.getAttribute("expires_in");
			model.addAttribute("expiresIn", expiresIn);

			return "profile";

		} catch (Exception e) {
			log.error("Failed to fetch user profile", e);
			return "redirect:/error?message=Failed to fetch profile: " + e.getMessage();
		}
	}

	/**
	 * Error page
	 */
	@GetMapping("/error")
	public String error(@RequestParam(required = false) String message, Model model) {
		model.addAttribute("errorMessage", message != null ? message : "Unknown error occurred");
		return "error";
	}
}
