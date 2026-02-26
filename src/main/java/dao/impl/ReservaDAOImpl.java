package dao.impl;

import dao.ReservaDAO;
import dto.ReservaResponse;
import model.Mesa;
import model.Reserva;
import db.ConectarBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements ReservaDAO {

    @Override
    public List<ReservaResponse> listarTodas() {
        List<ReservaResponse> lista = new ArrayList<>();
        String sql = """
                SELECT r.id, r.fecha, r.hora, r.cantidad, r.estado,
                       m.id AS mesaId, m.numeroMesa, m.ubicacion, m.capacidad,
                       c.id AS clienteId, c.nombre, c.apellido, c.telefono, c.correo AS correoCliente,
                       u.id AS usuarioId, u.nombreUsuario
                FROM Reserva r
                JOIN Mesa    m ON r.mesa    = m.id
                JOIN Cliente c ON r.cliente = c.id
                JOIN Usuario u ON r.usuario = u.id
                ORDER BY r.fecha DESC, r.hora DESC
                """;
        try (Connection conn = ConectarBD.getConexion(); // cambio pequeño
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar reservas", e);
        }
        return lista;
    }

    @Override
    public ReservaResponse buscarPorId(int id) {
        String sql = """
                SELECT r.id, r.fecha, r.hora, r.cantidad, r.estado,
                       m.id AS mesaId, m.numeroMesa, m.ubicacion, m.capacidad,
                       c.id AS clienteId, c.nombre, c.apellido, c.telefono, c.correo AS correoCliente,
                       u.id AS usuarioId, u.nombreUsuario
                FROM Reserva r
                JOIN Mesa    m ON r.mesa    = m.id
                JOIN Cliente c ON r.cliente = c.id
                JOIN Usuario u ON r.usuario = u.id
                WHERE r.id = ?
                """;
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar reserva", e);
        }
        return null;
    }

    @Override
    public int crear(Reserva reserva) {
        String sql = "INSERT INTO Reserva(fecha, hora, cantidad, estado, mesa, cliente, usuario) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(reserva.getFecha()));
            ps.setTime(2, Time.valueOf(reserva.getHora()));
            ps.setInt(3, reserva.getCantidad());
            ps.setString(4, "Pendiente");
            ps.setInt(5, reserva.getMesaId());
            ps.setInt(6, reserva.getClienteId());
            ps.setInt(7, reserva.getUsuarioId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear reserva", e);
        }
        return -1;
    }

    @Override
    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE Reserva SET estado = ? WHERE id = ?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado", e);
        }
    }

    @Override
    public List<ReservaResponse> listarPorFecha(LocalDate fecha) {
        List<ReservaResponse> lista = new ArrayList<>();
        String sql = """
                SELECT r.id, r.fecha, r.hora, r.cantidad, r.estado,
                       m.id AS mesaId, m.numeroMesa, m.ubicacion, m.capacidad,
                       c.id AS clienteId, c.nombre, c.apellido, c.telefono, c.correo AS correoCliente,
                       u.id AS usuarioId, u.nombreUsuario
                FROM Reserva r
                JOIN Mesa    m ON r.mesa    = m.id
                JOIN Cliente c ON r.cliente = c.id
                JOIN Usuario u ON r.usuario = u.id
                WHERE r.fecha = ?
                ORDER BY r.hora ASC
                """;
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar reservas por fecha", e);
        }
        return lista;
    }

    @Override
    public List<Mesa> obtenerMesasDisponibles(LocalDate fecha, String hora, int cantidad) {
        List<Mesa> mesas = new ArrayList<>();
        String sql = """
                SELECT m.id, m.numeroMesa, m.ubicacion, m.capacidad
                FROM Mesa m
                WHERE m.capacidad >= ?
                  AND m.id NOT IN (
                      SELECT r.mesa FROM Reserva r
                      WHERE r.fecha = ? AND r.hora = ?
                        AND r.estado NOT IN ('Cancelado', 'Finalizado')
                  )
                ORDER BY m.capacidad ASC
                """;
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, hora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mesas.add(new Mesa(
                            rs.getInt("id"),
                            rs.getInt("numeroMesa"),
                            rs.getString("ubicacion"),
                            rs.getInt("capacidad")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener mesas disponibles", e);
        }
        return mesas;
    }

    @Override
    public boolean existeConflicto(int mesaId, LocalDate fecha, String hora, Integer excludeId) {
        String sql = """
                SELECT COUNT(*) FROM Reserva
                WHERE mesa = ? AND fecha = ? AND hora = ?
                  AND estado NOT IN ('Cancelado', 'Finalizado')
                """ + (excludeId != null ? " AND id <> ?" : "");
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mesaId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, hora);
            if (excludeId != null) ps.setInt(4, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar conflicto", e);
        }
        return false;
    }

    private ReservaResponse mapRow(ResultSet rs) throws SQLException {
        ReservaResponse r = new ReservaResponse();
        r.setId(rs.getInt("id"));
        r.setFecha(rs.getDate("fecha").toLocalDate());
        r.setHora(rs.getTime("hora").toLocalTime());
        r.setCantidad(rs.getInt("cantidad"));
        r.setEstado(rs.getString("estado"));
        r.setMesaId(rs.getInt("mesaId"));
        r.setNumeroMesa(rs.getInt("numeroMesa"));
        r.setUbicacion(rs.getString("ubicacion"));
        r.setCapacidad(rs.getInt("capacidad"));
        r.setClienteId(rs.getInt("clienteId"));
        r.setNombreCliente(rs.getString("nombre"));
        r.setApellidoCliente(rs.getString("apellido"));
        r.setTelefonoCliente(rs.getString("telefono"));
        r.setCorreoCliente(rs.getString("correoCliente"));
        r.setUsuarioId(rs.getInt("usuarioId"));
        r.setNombreUsuario(rs.getString("nombreUsuario"));
        return r;
    }
}