package controller;

import dto.ApiResponse;
import dto.ReservaRequest;
import dto.ReservaResponse;
import model.Mesa;
import model.Usuario;
import service.ReservaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaController {

    private final ReservaService reservaService = new ReservaService();

    @Context
    private ContainerRequestContext requestContext;

    @GET
    public Response listarTodas() {
        return Response.ok(new ApiResponse<>(true, "Listado de reservas", reservaService.listarTodas())).build();
    }

    @GET
    @Path("/fecha/{fecha}")
    public Response listarPorFecha(@PathParam("fecha") String fechaParam) {
        LocalDate fecha = LocalDate.parse(fechaParam);
        return Response.ok(new ApiResponse<>(true, "Listado de reservas por fecha", reservaService.listarPorFecha(fecha))).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        ReservaResponse reserva = reservaService.buscarPorId(id);
        return Response.ok(new ApiResponse<>(true, "Reserva encontrada", reserva)).build();
    }

    @GET
    @Path("/disponibilidad")
    public Response obtenerDisponibilidad(
            @QueryParam("fecha") String fechaParam,
            @QueryParam("hora") String hora,
            @QueryParam("cantidad") int cantidad) {

        LocalDate fecha = LocalDate.parse(fechaParam);
        List<Mesa> mesas = reservaService.obtenerDisponibilidad(fecha, hora, cantidad);
        return Response.ok(new ApiResponse<>(true, "Disponibilidad obtenida", mesas)).build();
    }

    @POST
    public Response crear(ReservaRequest request) {
        // Usamos el servicio para validar el token y obtener el usuario
        Usuario usuario = reservaService.validarToken(requestContext);

        ReservaResponse reservaCreada = reservaService.crearReserva(request, usuario.getId());

        return Response.status(201)
                .entity(new ApiResponse<>(true, "Reserva creada correctamente", reservaCreada))
                .build();
    }

    @PATCH
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        ReservaResponse reservaActualizada = reservaService.cambiarEstado(id, nuevoEstado);
        return Response.ok(new ApiResponse<>(true, "Estado actualizado correctamente", reservaActualizada))
        		.build();
    }
}