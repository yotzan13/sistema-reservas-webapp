package controller;

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
        return Response.ok(Map.of(
                "token",          result.token(),
                "nombreCompleto", result.nombreCompleto(),
                "rol",            result.rol()
        )).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return Response.ok(Map.of("mensaje", "Sesión cerrada")).build();
    }
}