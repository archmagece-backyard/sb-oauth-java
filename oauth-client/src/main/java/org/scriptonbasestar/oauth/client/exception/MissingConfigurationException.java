package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when required OAuth configuration is missing.
 *
 * <p>This exception indicates that mandatory configuration properties
 * (such as client ID, client secret, or redirect URI) are not provided.</p>
 *
 * @since 1.0.0
 */
public class MissingConfigurationException extends OAuth2ConfigurationException {

	private static final String ERROR_CODE = "missing_configuration";

	private final String missingProperty;

	/**
	 * Creates a missing configuration exception.
	 *
	 * @param message the error message
	 * @param missingProperty the name of the missing property
	 */
	public MissingConfigurationException(String message, String missingProperty) {
		super(message, ERROR_CODE, null, null);
		this.missingProperty = missingProperty;
		addContext("missing_property", missingProperty);
	}

	/**
	 * Creates a missing configuration exception with provider.
	 *
	 * @param message the error message
	 * @param missingProperty the name of the missing property
	 * @param provider the OAuth provider name
	 */
	public MissingConfigurationException(String message, String missingProperty, String provider) {
		super(message, ERROR_CODE, provider, null);
		this.missingProperty = missingProperty;
		addContext("missing_property", missingProperty);
	}

	/**
	 * Gets the name of the missing configuration property.
	 *
	 * @return the missing property name
	 */
	public String getMissingProperty() {
		return missingProperty;
	}
}
