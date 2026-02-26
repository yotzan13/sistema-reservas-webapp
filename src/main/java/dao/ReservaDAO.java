package dao;

import dto.ReservaResponse;
import model.Mesa;
import model.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaDAO {
    List<ReservaResponse> listarTodas();
    ReservaResponse buscarPorId(int id);
    int crear(Reserva reserva);
    boolean actualizarEstado(int id, String nuevoEstado);
    List<ReservaResponse> listarPorFecha(LocalDate fecha);
    List<Mesa> obtenerMesasDisponibles(LocalDate fecha, String hora, int cantidad);
    boolean existeConflicto(int mesaId, LocalDate fecha, String hora, Integer excludeId);
}