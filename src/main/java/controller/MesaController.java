package controller;

import dto.MesaDTO;
import dto.ApiResponse;
import model.Mesa;
import service.MesaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/mesas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MesaController {

    private final MesaService mesaService = new MesaService();

    // Lista todas las mesas
    @GET
    public Response listar() {
        List<Mesa> lista = mesaService.listarTodas();
        String mensaje = lista.isEmpty()
                ? "Actualmente no contamos con mesas registradas en el catálogo"
                : "Se han recuperado " + lista.size() + " mesas exitosamente";
        return Response.ok(new ApiResponse<>(true, mensaje, lista)).build();
    }

    // Busca una mesa por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Mesa mesa = mesaService.buscarPorId(id);
        return Response.ok(new ApiResponse<>(true, "Información de la mesa obtenida correctamente", mesa)).build();
    }

    // Crea una nueva mesa
    @POST
    public Response crear(MesaDTO dto) {
        Mesa creada = mesaService.crearMesa(dto);
        String mensaje = "¡Registro exitoso! La mesa N* " + creada.getNumeroMesa() + " ya está disponible en el sistema";
        return Response.status(201)
                .entity(new ApiResponse<>(true, mensaje, creada))
                .build();
    }

    // Actualiza una mesa existente
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, MesaDTO dto) {
        Mesa actualizada = mesaService.actualizarMesa(id, dto);
        String mensaje = "Los cambios en la mesa N* " + actualizada.getNumeroMesa() + " se guardaron correctamente";
        return Response.ok(new ApiResponse<>(true, mensaje, actualizada)).build();
    }

    // Elimina una mesa
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        mesaService.eliminarMesa(id);
        return Response.ok(new ApiResponse<>(true, "La mesa ha sido retirada del sistema satisfactoriamente", null)).build();
    }
}