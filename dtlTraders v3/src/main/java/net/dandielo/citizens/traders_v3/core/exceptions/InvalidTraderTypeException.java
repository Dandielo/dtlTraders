package net.dandielo.citizens.traders_v3.core.exceptions;

public class InvalidTraderTypeException extends Throwable {
	private String type;
	
	public InvalidTraderTypeException(String type) {
		this.type = type;
	}

	private static final long serialVersionUID = -2609821390326679752L;

}
