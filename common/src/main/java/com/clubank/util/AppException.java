package com.clubank.util;

public class AppException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6948161652953059471L;
	private int code;

	public AppException(int code) {
		this.code = code;
	}

	public AppException(String message) {
		super(message);
	}

	public AppException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
