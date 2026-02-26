package service;

import dao.ReservaDAO;
import dao.impl.ReservaDAOImpl;
import dto.ReservaRequest;
import dto.ReservaResponse;
import exception.BusinessException;
import model.Mesa;
import model.Reserva;
import util.EmailUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReservaService {

    private final ReservaDAO reservaDAO = new ReservaDAOImpl();

    private static final Map<String, List<String>> TRANSICIONES_VALIDAS = Map.of(
            "Pendiente",  List.of("Confirmado", "Cancelado"),
            "Confirmado", List.of("Finalizado", "Cancelado"),
            "Finalizado", List.of(),
            "Cancelado",  List.of()
    );

    public List<ReservaResponse> listarTodas() {
        return reservaDAO.listarTodas();
    }

    public List<ReservaResponse> listarPorFecha(LocalDate fecha) {
        return reservaDAO.listarPorFecha(fecha);
    }

    public ReservaResponse buscarPorId(int id) {
        ReservaResponse r = reservaDAO.buscarPorId(id);
        if (r == null) throw new BusinessException("Reserva no encontrada", 404);
        return r;
    }

    public List<Mesa> obtenerDisponibilidad(LocalDate fecha, String hora, int cantidad) {
        if (cantidad <= 0) throw new BusinessException("La cantidad de personas debe ser mayor a 0", 400);
        return reservaDAO.obtenerMesasDisponibles(fecha, hora, cantidad);
    }

    public ReservaResponse crearReserva(ReservaRequest request, int usuarioId) {
        validarRequest(request);

        LocalDate hoy = LocalDate.now();
        if (request.getFecha().isBefore(hoy.plusDays(3))) {
            throw new BusinessException("La reserva debe hacerse con al menos 3 días de anticipación", 400);
        }

        String horaStr = request.getHora().toString();
        if (reservaDAO.existeConflicto(request.getMesaId(), request.getFecha(), horaStr, null)) {
            throw new BusinessException("La mesa ya está reservada en ese horario", 409);
        }

        Reserva reserva = new Reserva();
        reserva.setFecha(request.getFecha());
        reserva.setHora(request.getHora());
        reserva.setCantidad(request.getCantidad());
        reserva.setEstado("Pendiente");
        reserva.setMesaId(request.getMesaId());
        reserva.setClienteId(request.getClienteId());
        reserva.setUsuarioId(usuarioId);

        int id = reservaDAO.crear(reserva);
        return reservaDAO.buscarPorId(id);
    }

    public ReservaResponse cambiarEstado(int id, String nuevoEstado) {
        ReservaResponse actual = buscarPorId(id);

        List<String> permitidos = TRANSICIONES_VALIDAS.getOrDefault(actual.getEstado(), List.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new BusinessException(
                    "No se puede cambiar de '" + actual.getEstado() + "' a '" + nuevoEstado + "'", 400);
        }

        // Cancelación: debe ser al menos 1 día antes
        if ("Cancelado".equals(nuevoEstado)) {
            if (!actual.getFecha().isAfter(LocalDate.now())) {
                throw new BusinessException("No se puede cancelar con menos de 1 día de anticipación", 400);
            }
        }

        reservaDAO.actualizarEstado(id, nuevoEstado);
        ReservaResponse actualizado = reservaDAO.buscarPorId(id);

        // Enviar correo al confirmar
        if ("Confirmado".equals(nuevoEstado) && actualizado.getCorreoCliente() != null) {
            try {
                EmailUtil.enviarConfirmacion(
                        actualizado.getCorreoCliente(),
                        actualizado.getNombreCliente() + " " + actualizado.getApellidoCliente(),
                        actualizado.getFecha().toString(),
                        actualizado.getHora().toString(),
                        actualizado.getNumeroMesa()
                );
            } catch (Exception e) {
                System.err.println("Advertencia: no se pudo enviar el correo de confirmación: " + e.getMessage());
            }
        }

        return actualizado;
    }

    private void validarRequest(ReservaRequest req) {
        if (req.getFecha() == null)
            throw new BusinessException("La fecha es obligatoria", 400);
        if (req.getHora() == null)
            throw new BusinessException("La hora es obligatoria", 400);
        if (req.getCantidad() <= 0)
            throw new BusinessException("La cantidad debe ser mayor a 0", 400);
        if (req.getMesaId() <= 0)
            throw new BusinessException("Debe seleccionar una mesa", 400);
        if (req.getClienteId() <= 0)
            throw new BusinessException("Debe seleccionar un cliente", 400);
    }
}