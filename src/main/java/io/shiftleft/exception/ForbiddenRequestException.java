package io.shiftleft.exception;

public class ForbiddenRequestException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ForbiddenRequestException() {
    super("Access denied");
  }
}
