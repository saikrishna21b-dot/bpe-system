package com.bpe.security.annotations;

public class AuthorizationException extends RuntimeException {

	private static final long serialVersionUID = 5347530182755910969L;

	public AuthorizationException(String message) {
		super(message);
	}
}
