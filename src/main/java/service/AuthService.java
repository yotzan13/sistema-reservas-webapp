package service;

import exception.BusinessException;
import model.Usuario;
import security.SessionManager;
import db.ConectarBD;

import java.sql.*;

public class AuthService {

    public record LoginResult(String token, String nombreCompleto, String rol) {}

    public LoginResult login(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || contrasena == null || nombreUsuario.isBlank()) {
            throw new BusinessException("Credenciales inválidas", 400);
        }

        String sql = """
                SELECT u.id, u.nombreUsuario, u.contrasena, u.nombreCompleto, u.dni,
                       u.telefono, u.correo, u.rol, r.nombre AS nombreRol
                FROM Usuario u
                JOIN Rol r ON u.rol = r.id
                WHERE u.nombreUsuario = ?
                """;

        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new BusinessException("Usuario o contraseña incorrectos", 401);
                }

                String storedPassword = rs.getString("contrasena");

                // Comparación directa (en producción usar BCrypt)
                if (!storedPassword.equals(contrasena)) {
                    throw new BusinessException("Usuario o contraseña incorrectos", 401);
                }

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombreUsuario(rs.getString("nombreUsuario"));
                usuario.setNombreCompleto(rs.getString("nombreCompleto"));
                usuario.setRol(rs.getInt("rol"));
                usuario.setNombreRol(rs.getString("nombreRol"));
                String token = SessionManager.getInstance().crearSesion(usuario);
                return new LoginResult(token, usuario.getNombreCompleto(), usuario.getNombreRol());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos durante login", e);
        }
    }

    public void logout(String token) {
        if (!SessionManager.getInstance().esValido(token)) {
            throw new BusinessException("Token no válido o ya cerrado", 401);
        }
        SessionManager.getInstance().eliminarSesion(token);
    }
}