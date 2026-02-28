package controller;

import dto.ApiResponse;
import service.AuthService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private final AuthService authService = new AuthService();

    @POST
    @Path("/login")
    public Response login(Map<String, String> body) {

        String nombreUsuario = body.get("nombreUsuario");
        String contrasena    = body.get("contrasena");

        AuthService.LoginResult result = authService.login(nombreUsuario, contrasena);

        Map<String, Object> data = Map.of(
                "token",          result.token(),
                "nombreCompleto", result.nombreCompleto(),
                "rol",            result.rol()
        );

        return Response.ok(
                new ApiResponse<>(true, "Login exitoso", data)
        ).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {

        // Validación de token en el service, lanzar excepción si no es válido
        authService.logout(authHeader);

        return Response.ok(
                new ApiResponse<>(true, "Sesión cerrada correctamente", null)
        ).build();
    }
}