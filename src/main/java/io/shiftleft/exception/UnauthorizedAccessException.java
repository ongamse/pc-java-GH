package io.shiftleft.exception;

public class UnauthorizedAccessException extends RuntimeException {

	private static final long serialVersionUID = 3L;

	public UnauthorizedAccessException() {
		super("Access denied");
	}
}
