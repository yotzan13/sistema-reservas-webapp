package controller;

import dto.ClienteDTO;
import model.Cliente;
import service.ClienteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    private final ClienteService clienteService = new ClienteService();

    @GET
    public Response listarTodos() {
        List<Cliente> lista = clienteService.listarTodos();
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Cliente c = clienteService.buscarPorId(id);
        return Response.ok(c).build();
    }

    @GET
    @Path("/dni/{dni}")
    public Response buscarPorDni(@PathParam("dni") String dni) {
        Cliente c = clienteService.buscarPorDni(dni);
        if (c == null) return Response.status(404).entity(Map.of("error", "Cliente no encontrado")).build();
        return Response.ok(c).build();
    }

    @POST
    public Response crear(ClienteDTO dto) {
        Cliente creado = clienteService.crearCliente(dto);
        return Response.status(201).entity(creado).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, ClienteDTO dto) {
        Cliente actualizado = clienteService.actualizarCliente(id, dto);
        return Response.ok(actualizado).build();
    }
}