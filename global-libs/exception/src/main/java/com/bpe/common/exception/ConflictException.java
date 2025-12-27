package com.bpe.common.exception;

public class ConflictException extends AppException {

	private static final long serialVersionUID = 1L;

	protected ConflictException(String msge, ErrorCode errorCode) {
		super(errorCode, msge);

	}

}
