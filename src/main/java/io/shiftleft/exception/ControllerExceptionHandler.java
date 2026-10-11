package io.shiftleft.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.shiftleft.controller.CustomerController;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.ErrorResponse;


@ControllerAdvice
public class ControllerExceptionHandler {

  private static Logger log = LoggerFactory.getLogger(CustomerController.class);

	@ResponseStatus(HttpStatus.NOT_FOUND) // 404
	@ExceptionHandler(CustomerNotFoundException.class)
	public void handleNotFound(CustomerNotFoundException ex) {
		log.error("Resource not found");
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	@ExceptionHandler(InvalidCustomerRequestException.class)
	public void handleBadRequest(InvalidCustomerRequestException ex) {
		log.error("Invalid Fund Request");
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Void> handleResponseStatus(ResponseStatusException ex) {
		return ResponseEntity.status(ex.getStatusCode()).build();
	}

	@ExceptionHandler(Exception.class)
	public void handleGeneralError(Exception ex, HttpServletResponse response) throws IOException {
		log.error("An error occurred procesing request", ex);
		int status = ex instanceof ErrorResponse error ? error.getStatusCode().value()
				: HttpStatus.INTERNAL_SERVER_ERROR.value();
		response.sendError(status);
	}
}
