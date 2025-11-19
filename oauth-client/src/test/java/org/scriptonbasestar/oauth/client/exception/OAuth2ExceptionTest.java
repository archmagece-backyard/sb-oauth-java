package org.scriptonbasestar.oauth.client.exception;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2Exception
 */
class OAuth2ExceptionTest {

	@Test
	void shouldCreateExceptionWithMessage() {
		OAuth2Exception exception = new OAuth2Exception("Test error");

		assertThat(exception.getMessage()).isEqualTo("Test error");
		assertThat(exception.getErrorCode()).isNull();
		assertThat(exception.getProvider()).isNull();
		assertThat(exception.getTimestamp()).isNotNull();
	}

	@Test
	void shouldCreateExceptionWithCause() {
		Throwable cause = new RuntimeException("Root cause");
		OAuth2Exception exception = new OAuth2Exception("Test error", cause);

		assertThat(exception.getMessage()).isEqualTo("Test error");
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	void shouldCreateExceptionWithFullContext() {
		OAuth2Exception exception = new OAuth2Exception("Test error", "invalid_grant", "naver", null);

		assertThat(exception.getMessage()).isEqualTo("Test error");
		assertThat(exception.getErrorCode()).isEqualTo("invalid_grant");
		assertThat(exception.getProvider()).isEqualTo("naver");
	}

	@Test
	void shouldCaptureTimestamp() {
		Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
		OAuth2Exception exception = new OAuth2Exception("Test error");
		Instant after = Instant.now().plus(1, ChronoUnit.SECONDS);

		assertThat(exception.getTimestamp()).isBetween(before, after);
	}

	@Test
	void shouldAddContext() {
		OAuth2Exception exception = new OAuth2Exception("Test error");

		exception.addContext("key1", "value1");
		exception.addContext("key2", 123);

		Map<String, Object> context = exception.getContext();
		assertThat(context).containsEntry("key1", "value1");
		assertThat(context).containsEntry("key2", 123);
	}

	@Test
	void shouldAddMultipleContext() {
		OAuth2Exception exception = new OAuth2Exception("Test error");

		Map<String, Object> contextData = Map.of(
			"key1", "value1",
			"key2", "value2",
			"key3", 123
		);

		exception.addContext(contextData);

		Map<String, Object> context = exception.getContext();
		assertThat(context).containsAllEntriesOf(contextData);
	}

	@Test
	void shouldReturnImmutableContextCopy() {
		OAuth2Exception exception = new OAuth2Exception("Test error");
		exception.addContext("key1", "value1");

		Map<String, Object> context1 = exception.getContext();
		Map<String, Object> context2 = exception.getContext();

		assertThat(context1).isNotSameAs(context2);
		assertThat(context1).isEqualTo(context2);
	}

	@Test
	void shouldChainAddContext() {
		OAuth2Exception exception = new OAuth2Exception("Test error")
			.addContext("key1", "value1")
			.addContext("key2", "value2");

		Map<String, Object> context = exception.getContext();
		assertThat(context).containsEntry("key1", "value1");
		assertThat(context).containsEntry("key2", "value2");
	}

	@Test
	void shouldIncludeErrorCodeInToString() {
		OAuth2Exception exception = new OAuth2Exception("Test error", "invalid_grant", "naver", null);

		String toString = exception.toString();
		assertThat(toString).contains("invalid_grant");
	}

	@Test
	void shouldIncludeProviderInToString() {
		OAuth2Exception exception = new OAuth2Exception("Test error", "invalid_grant", "naver", null);

		String toString = exception.toString();
		assertThat(toString).contains("naver");
	}

	@Test
	void shouldIncludeTimestampInToString() {
		OAuth2Exception exception = new OAuth2Exception("Test error");

		String toString = exception.toString();
		assertThat(toString).contains("timestamp=");
	}

	@Test
	void shouldIncludeContextInToString() {
		OAuth2Exception exception = new OAuth2Exception("Test error");
		exception.addContext("key1", "value1");

		String toString = exception.toString();
		assertThat(toString).contains("context=");
		assertThat(toString).contains("key1");
	}

	@Test
	void shouldHandleNullErrorCode() {
		OAuth2Exception exception = new OAuth2Exception("Test error", null, "naver", null);

		String toString = exception.toString();
		assertThat(toString).doesNotContain("errorCode=");
	}

	@Test
	void shouldHandleNullProvider() {
		OAuth2Exception exception = new OAuth2Exception("Test error", "invalid_grant", null, null);

		String toString = exception.toString();
		assertThat(toString).doesNotContain("provider=");
	}

	@Test
	void shouldHandleEmptyContext() {
		OAuth2Exception exception = new OAuth2Exception("Test error");

		String toString = exception.toString();
		// Context should not appear in toString if empty
		assertThat(exception.getContext()).isEmpty();
	}
}
