package controller;

import dto.ApiResponse;
import dto.ClienteDTO;
import model.Cliente;
import service.ClienteService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    private final ClienteService clienteService = new ClienteService();

    @GET
    public Response listarTodos() {

        List<Cliente> lista = clienteService.listarTodos();

        return Response.ok(
                new ApiResponse<>(true, "Listado de clientes", lista)
        ).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        Cliente cliente = clienteService.buscarPorId(id);

        return Response.ok(
                new ApiResponse<>(true, "Cliente encontrado", cliente)
        ).build();
    }

    @GET
    @Path("/dni/{dni}")
    public Response buscarPorDni(@PathParam("dni") String dni) {

        Cliente cliente = clienteService.buscarPorDni(dni);

        return Response.ok(
                new ApiResponse<>(true, "Cliente encontrado", cliente)
        ).build();
    }

    @POST
    public Response crear(ClienteDTO dto) {

        Cliente creado = clienteService.crearCliente(dto);

        return Response.status(201)
                .entity(new ApiResponse<>(true, "Cliente creado correctamente", creado))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, ClienteDTO dto) {

        Cliente actualizado = clienteService.actualizarCliente(id, dto);

        return Response.ok(
                new ApiResponse<>(true, "Cliente actualizado correctamente", actualizado)
        ).build();
    }
}