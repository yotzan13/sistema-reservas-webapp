package controller;

import dto.MesaDTO;
import model.Mesa;
import service.MesaService;
import exception.BusinessException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/mesas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MesaController {

    private final MesaService mesaService = new MesaService();

    //Lista todas las mesas registradas
    @GET
    public Response listar() {
        try {
            List<Mesa> lista = mesaService.listarTodas();
            if (lista.isEmpty()) {
                return Response.ok(Map.of(
                    "status", "success",
                    "message", "Actualmente no contamos con mesas registradas en el catálogo",
                    "data", lista
                )).build();
            }
            return Response.ok(Map.of(
                "status", "success",
                "message", "Se han recuperado " + lista.size() + " mesas exitosamente",
                "data", lista
            )).build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of(
                "status", "error", 
                "message", "Lo sentimos, hubo un problema al cargar la lista de mesas"
            )).build();
        }
    }

   
    
     //Busca una mesa específica por su id
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            Mesa mesa = mesaService.buscarPorId(id);
            return Response.ok(Map.of(
                "status", "success",
                "message", "Informacion de la mesa obtenida correctamente",
                "data", mesa
            )).build();
        } catch (BusinessException e) {
            return Response.status(e.getStatusCode()).entity(Map.of(
                "status", "error", 
                "message", e.getMessage()
            )).build();
        }
    }

    
    
    //Registra nueva mesa e valida que la ubi sea Ventana, Interior o Terraza
    @POST
    public Response crear(MesaDTO dto) {
        try {
            Mesa creada = mesaService.crearMesa(dto);
            return Response.status(201).entity(Map.of(
                "status", "success",
                "message", "¡Registro exitoso!! La mesa N* " + creada.getNumeroMesa() + " ya esta disponible en el sistema",
                "data", creada
            )).build();
        } catch (BusinessException e) {
            return Response.status(e.getStatusCode()).entity(Map.of(
                "status", "error", 
                "message", e.getMessage()
            )).build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of(
                "status", "error", 
                "message", "No se pudo completar el registro.Por favor, verifique los datos de la mesa"
            )).build();
        }
    }
    //actualiza los date de una mesa existente
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, MesaDTO dto) {
        try {
            Mesa actualizada = mesaService.actualizarMesa(id, dto);
            return Response.ok(Map.of(
                "status", "success",
                "message", "Los cambios en la mesa N* " + actualizada.getNumeroMesa() + " se guardaron correctamente",
                "data", actualizada
            )).build();
        } catch (BusinessException e) {
            return Response.status(e.getStatusCode()).entity(Map.of(
                "status", "error", 
                "message", e.getMessage()
            )).build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of(
                "status", "error", 
                "message", "Hubo un inconveniente al actualizar la mesa.Intentelo de nuevo"
            )).build();
        }
    }

    
    
    //elima mesa
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        try {
            mesaService.eliminarMesa(id);
            return Response.ok(Map.of(
                "status", "success",
                "message", "La mesa ha sido retirada del sistema satisfactoriamente"
            )).build();
        } catch (BusinessException e) {
            return Response.status(e.getStatusCode()).entity(Map.of(
                "status", "error", 
                "message", e.getMessage()
            )).build();
        } catch (Exception e) {
            return Response.status(400).entity(Map.of(
                "status", "error", 
                "message", "No es posible eliminar la mesa.Es probable que tenga reservas pendientes asociadas"
            )).build();
        }
    }
}