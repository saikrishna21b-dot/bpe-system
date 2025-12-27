package com.bpe.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Maps business error codes to HTTP status
	 */
	private HttpStatus mapStatus(ErrorCode code) {
		return switch (code) {
		case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
		case INVALID_INPUT, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
		case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
		case FORBIDDEN -> HttpStatus.FORBIDDEN;
		case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
		default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}

	/**
	 * Handles application-specific business exceptions
	 */
	@ExceptionHandler(AppException.class)
	public ResponseEntity<ApiError> handleAppException(AppException ex, HttpServletRequest req) {

		HttpStatus status = mapStatus(ex.getErrorCode());
		String traceId = MDC.get("traceId");

		ApiError error = new ApiError(status.value(), // HTTP status code
				status.getReasonPhrase(), // Status text
				ex.getErrorCode().name(), // Business error code
				ex.getMessage(), // User-friendly message
				req.getRequestURI(), // Request path
				traceId, // TraceId
				null // Optional details
		);

		log.error("TraceId={} | AppException={} | message={}", traceId, ex.getErrorCode().name(), ex.getMessage());

		return ResponseEntity.status(status).body(error);
	}

	/**
	 * Handles resource not found cases (no Kafka dependency)
	 */
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {

		String traceId = MDC.get("traceId");

		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
				ErrorCode.USER_NOT_FOUND.name(), ex.getMessage(), req.getRequestURI(), traceId, null);

		log.warn("TraceId={} | Resource not found={}", traceId, ex.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	/**
	 * Handles validation errors (@Valid)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

		String traceId = MDC.get("traceId");

		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage()).reduce((a, b) -> a + "; " + b)
				.orElse("Validation failed");

		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
				ErrorCode.VALIDATION_ERROR.name(), message, req.getRequestURI(), traceId, null);

		log.warn("TraceId={} | Validation failed={}", traceId, message);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	/**
	 * Catch-all handler for unexpected exceptions
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {

		String traceId = MDC.get("traceId");

		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ErrorCode.INTERNAL_ERROR.name(),
				"Internal server error", req.getRequestURI(), traceId, null);

		log.error("TraceId={} | Unexpected error", traceId, ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
