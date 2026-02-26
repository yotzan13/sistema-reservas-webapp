package dao.impl;

import dao.ClienteDAO;
import model.Cliente;
import db.ConectarBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, dni, telefono, correo FROM Cliente ORDER BY apellido ASC";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes", e);
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT id, nombre, apellido, dni, telefono, correo FROM Cliente WHERE id = ?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente", e);
        }
        return null;
    }

    @Override
    public Cliente buscarPorDni(String dni) {
        String sql = "SELECT id, nombre, apellido, dni, telefono, correo FROM Cliente WHERE dni = ?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por DNI", e);
        }
        return null;
    }

    @Override
    public int crear(Cliente cliente) {
        String sql = "INSERT INTO Cliente(nombre, apellido, dni, telefono, correo) VALUES (?,?,?,?,?)";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear cliente", e);
        }
        return -1;
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE Cliente SET nombre=?, apellido=?, dni=?, telefono=?, correo=? WHERE id=?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
            ps.setInt(6, cliente.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente", e);
        }
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("correo")
        );
    }
}