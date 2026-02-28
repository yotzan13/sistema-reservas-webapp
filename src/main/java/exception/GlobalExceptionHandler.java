package exception;

import dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception ex) {

	    if (ex instanceof BusinessException be) {
	        return Response.status(be.getStatusCode())
	                .entity(new ErrorResponse(be.getMessage(), be.getStatusCode()))
	                .build();
	    }

	    if (ex instanceof jakarta.ws.rs.NotFoundException) {
	        return Response.status(404)
	                .entity(new ErrorResponse("Recurso no encontrado", 404))
	                .build();
	    }

	    if (ex instanceof jakarta.ws.rs.BadRequestException) {
	        return Response.status(400)
	                .entity(new ErrorResponse("Solicitud inválida", 400))
	                .build();
	    }

	    ex.printStackTrace();

	    return Response.status(500)
	            .entity(new ErrorResponse("Error interno del servidor", 500))
	            .build();
	}
}