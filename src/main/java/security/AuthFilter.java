package security;

import model.Usuario;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    // Rutas públicas que no requieren autenticación
    private static final java.util.Set<String> PUBLIC_PATHS = java.util.Set.of(
            "auth/login"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Permitir rutas públicas y preflight CORS
        if (PUBLIC_PATHS.contains(path) || "OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String authHeader = requestContext.getHeaderString(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            abort(requestContext, "Token no proporcionado");
            return;
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());
        SessionManager sessionManager = SessionManager.getInstance();
        if (!sessionManager.esValido(token)) {
            abort(requestContext, "Sesión inválida o expirada");
            return;
        }
        // Adjuntar usuario al contexto de la solicitud
        Usuario usuario = sessionManager.obtenerUsuario(token);
        requestContext.setProperty("usuarioActual", usuario);
    }

    private void abort(ContainerRequestContext ctx, String mensaje) {
        ctx.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", mensaje))
                .build()
        );
    }
}