package controller;

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
public class ReservaController {

    private final ReservaService reservaService = new ReservaService();

    @Context
    private ContainerRequestContext requestContext;

    @GET
    public Response listarTodas(@QueryParam("fecha") String fechaParam) {
        if (fechaParam != null) {
            LocalDate fecha = LocalDate.parse(fechaParam);
            return Response.ok(reservaService.listarPorFecha(fecha)).build();
        }
        List<ReservaResponse> lista = reservaService.listarTodas();
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        return Response.ok(reservaService.buscarPorId(id)).build();
    }

    @GET
    @Path("/disponibilidad")
    public Response obtenerDisponibilidad(
            @QueryParam("fecha")    String fechaParam,
            @QueryParam("hora")     String hora,
            @QueryParam("cantidad") int cantidad) {
        LocalDate fecha = LocalDate.parse(fechaParam);
        List<Mesa> mesas = reservaService.obtenerDisponibilidad(fecha, hora, cantidad);
        return Response.ok(mesas).build();
    }
    
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response crear(ReservaRequest request) {
        Usuario usuario = (Usuario) requestContext.getProperty("usuarioActual");
        usuario.setId(1);
        ReservaResponse creada = reservaService.crearReserva(request, usuario.getId());
        return Response.status(201).entity(creada).build();
    }
    
    @Consumes(MediaType.APPLICATION_JSON)
    @PATCH
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return Response.status(400).entity(Map.of("error", "El estado es requerido")).build();
        }
        ReservaResponse actualizado = reservaService.cambiarEstado(id, nuevoEstado);
        return Response.ok(actualizado).build();
    }
}