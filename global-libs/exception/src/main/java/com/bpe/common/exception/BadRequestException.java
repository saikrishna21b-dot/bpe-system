package com.bpe.common.exception;

public class BadRequestException extends AppException {

	private static final long serialVersionUID = 1L;

	protected BadRequestException(String msge, ErrorCode errorCode) {
		super(errorCode, msge);

	}

}