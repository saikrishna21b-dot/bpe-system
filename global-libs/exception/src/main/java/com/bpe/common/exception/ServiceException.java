 package com.bpe.common.exception;


 public class ServiceException extends AppException {

 	private static final long serialVersionUID = 2796395287648130636L;

 	protected ServiceException(String msge, ErrorCode errorCode) {
 		super(errorCode, msge);
 		// TODO Auto-generated constructor stub
 	}

 }
