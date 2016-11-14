package com.ubcsolar.exception;

public class NotEnoughChargeException extends Exception{
	
	public final double actualCharge;
	public final double minCharge;
	
	public NotEnoughChargeException(double actualCharge, double minCharge, String message){
		super(message);
		this.actualCharge = actualCharge;
		this.minCharge = minCharge;
	}

}
