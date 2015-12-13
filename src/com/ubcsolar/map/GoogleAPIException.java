package com.ubcsolar.map;

import java.io.IOException;

public class GoogleAPIException extends IOException {
	
	private final String responseCode;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GoogleAPIException(String message, String responseCode){
		super(message);
		this.responseCode = responseCode;
	}
	
	public String getResponseCode() {
		return responseCode;
	}
	
	
}
