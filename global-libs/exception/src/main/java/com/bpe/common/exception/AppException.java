package com.bpe.common.exception;



public abstract class AppException extends RuntimeException {

    private static final long serialVersionUID = 2143987233104298032L;

   private final ErrorCode errorCode;

    protected AppException(ErrorCode errorCode, String message) {
       super(message);
       this.errorCode = errorCode;
     }

     public ErrorCode getErrorCode() {
         return errorCode;
     }
 }
