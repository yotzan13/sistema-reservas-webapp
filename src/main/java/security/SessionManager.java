package security;

import model.Usuario;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Usuario> sessions = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public String crearSesion(Usuario usuario) {
    	
        String token = UUID.randomUUID().toString();
        sessions.put(token, usuario);
        return token;
    }

    public Usuario obtenerUsuario(String token) {
        return sessions.get(token);
    }

    public void eliminarSesion(String token) {
        sessions.remove(token);
    }

    public boolean esValido(String token) {
        return token != null && sessions.containsKey(token);
    }
}