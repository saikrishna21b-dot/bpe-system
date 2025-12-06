// package com.bpe.common.exception;


// import com.finpay.userservice.dto.ApiError;
// import com.finpay.userservice.util.ErrorCode;
// import jakarta.servlet.http.HttpServletRequest;
// import org.apache.kafka.common.errors.ResourceNotFoundException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.slf4j.MDC;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

// 	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

// 	// MAP ERROR CODE → HTTP STATUS
// 	private HttpStatus mapStatus(ErrorCode code) {
// 		return switch (code) {
// 		case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
// 		case INVALID_INPUT, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
// 		case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
// 		case FORBIDDEN -> HttpStatus.FORBIDDEN;
// 		case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
// 		default -> HttpStatus.INTERNAL_SERVER_ERROR;
// 		};
// 	}

// // 
// 	@ExceptionHandler(AppException.class)
// 	public ResponseEntity<ApiError> handleAppException(AppException ex, HttpServletRequest req) {

// 		// Extract HTTP status
// 		HttpStatus status = mapStatus(ex.getErrorCode());

// 		// Extract traceId
// 		String traceId = MDC.get("traceId");

// 		// Build ApiError clearly & simply
// 		ApiError error = new ApiError(status.value(), // HTTP status code (e.g. 404)
// 				status.getReasonPhrase(), // Status name (e.g. "Not Found")
// 				ex.getErrorCode().name(), // BUSINESS CODE (e.g. USER_NOT_FOUND)
// 				ex.getMessage(), // User-friendly message
// 				req.getRequestURI(), // Path
// 				traceId, // TraceId for logs
// 				null // Details (optional)
// 		);

// 		log.error("TraceId={} | AppException={} | message={}", traceId, ex.getErrorCode().name(), ex.getMessage());

// 		return ResponseEntity.status(status).body(error);
// 	}

// 	// 2) HANDLE RESOURCE NOT FOUND (Kafka / DB)
// 	@ExceptionHandler(ResourceNotFoundException.class)
// 	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {

// 		String traceId = MDC.get("traceId");

// 		ApiError body = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", "NOT_FOUND", ex.getMessage(),
// 				req.getRequestURI(), traceId, null);

// 		log.error("TraceId={} | Resource not found={}", traceId, ex.getMessage());

// 		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
// 	}

// 	// 3) HANDLE @Valid VALIDATION ERRORS
// 	@ExceptionHandler(MethodArgumentNotValidException.class)
// 	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

// 		String traceId = MDC.get("traceId");

// 		String msg = ex.getBindingResult().getFieldErrors().stream()
// 				.map(err -> err.getField() + ": " + err.getDefaultMessage()).reduce((a, b) -> a + "; " + b)
// 				.orElse("Validation failed");

// 		ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ErrorCode.VALIDATION_ERROR.name(),
// 				msg, req.getRequestURI(), traceId, null);

// 		log.warn("TraceId={} | Validation failed={}", traceId, msg);

// 		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
// 	}

// 	// 4) HANDLE ANY OTHER ERROR
// 	@ExceptionHandler(Exception.class)
// 	public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {

// 		String traceId = MDC.get("traceId");

// 		ApiError body = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
// 				ErrorCode.INTERNAL_ERROR.name(), "Internal server error", req.getRequestURI(), traceId, null);

// 		log.error("TraceId={} | Unexpected error", traceId, ex);

// 		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
// 	}
// }
