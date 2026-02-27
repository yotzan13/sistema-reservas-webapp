package dao.impl;

import dao.MesaDAO;
import model.Mesa;
import db.ConectarBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAOImpl implements MesaDAO{

	@Override
	public List<Mesa> listarTodas() {
	    List<Mesa> lista = new ArrayList<>();
	    // Agregamos el ORDER BY al final de la consulta
	    String sql = "SELECT * FROM Mesa ORDER BY id ASC"; 
	    
	    try (Connection cn = ConectarBD.getConexion();
	         PreparedStatement ps = cn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	            lista.add(mapRow(rs));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error al listar las mesas", e);
	    }
	    return lista;
	}

    @Override
    public Mesa buscarPorId(int id) {
        String sql = "SELECT id, numeroMesa, ubicacion, capacidad FROM Mesa WHERE id = ?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar mesa", e);
        }
        return null;
    }

    @Override
    public int crear(Mesa mesa) {
        String sql = "INSERT INTO Mesa(numeroMesa, ubicacion, capacidad) VALUES (?,?,?)";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mesa.getNumeroMesa());
            ps.setString(2, mesa.getUbicacion());
            ps.setInt(3, mesa.getCapacidad());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear mesa", e);
        }
        return -1;
    }

    @Override
    public boolean actualizar(Mesa mesa) {
        String sql = "UPDATE Mesa SET numeroMesa=?, ubicacion=?, capacidad=? WHERE id=?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mesa.getNumeroMesa());
            ps.setString(2, mesa.getUbicacion());
            ps.setInt(3, mesa.getCapacidad());
            ps.setInt(4, mesa.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar mesa", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Mesa WHERE id = ?";
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se puede eliminar la mesa (tiene dependencias)", e);
        }
    }

    @Override
    public boolean existeNumeroMesa(int numeroMesa, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM Mesa WHERE numeroMesa = ?" + (excludeId != null ? " AND id <> ?" : "");
        try (Connection conn = ConectarBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroMesa);
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Mesa mapRow(ResultSet rs) throws SQLException {
        return new Mesa(
                rs.getInt("id"),
                rs.getInt("numeroMesa"),
                rs.getString("ubicacion"),
                rs.getInt("capacidad")
        );
    }
}