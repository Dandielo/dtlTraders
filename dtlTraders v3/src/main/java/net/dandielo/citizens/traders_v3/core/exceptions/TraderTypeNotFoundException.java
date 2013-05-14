package net.dandielo.citizens.traders_v3.core.exceptions;

public class TraderTypeNotFoundException extends Throwable {
	
	private String type;
	
	public TraderTypeNotFoundException(String type) {
		this.type = type;
	}

	private static final long serialVersionUID = 4938166001037263913L;

}
