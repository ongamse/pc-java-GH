package io.shiftleft.exception;

public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccessDeniedException() {
		super("Access denied");
	}

	public AccessDeniedException(String message) {
		super(message);
	}
}
