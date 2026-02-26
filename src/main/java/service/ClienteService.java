package service;

import dao.ClienteDAO;
import dao.impl.ClienteDAOImpl;
import dto.ClienteDTO;
import exception.BusinessException;
import model.Cliente;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarPorId(int id) {
        Cliente c = clienteDAO.buscarPorId(id);
        if (c == null) throw new BusinessException("Cliente no encontrado", 404);
        return c;
    }

    public Cliente buscarPorDni(String dni) {
        return clienteDAO.buscarPorDni(dni);
    }

    public Cliente crearCliente(ClienteDTO dto) {
        validar(dto);

        if (clienteDAO.buscarPorDni(dto.getDni()) != null) {
            throw new BusinessException("Ya existe un cliente con ese DNI", 409);
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido().trim());
        cliente.setDni(dto.getDni().trim());
        cliente.setTelefono(dto.getTelefono().trim());
        cliente.setCorreo(dto.getCorreo() != null ? dto.getCorreo().trim() : null);

        int id = clienteDAO.crear(cliente);
        cliente.setId(id);
        return cliente;
    }

    public Cliente actualizarCliente(int id, ClienteDTO dto) {
        buscarPorId(id); // valida existencia
        validar(dto);

        Cliente existente = clienteDAO.buscarPorDni(dto.getDni());
        if (existente != null && existente.getId() != id) {
            throw new BusinessException("El DNI ya pertenece a otro cliente", 409);
        }

        Cliente cliente = new Cliente(id,
                dto.getNombre().trim(),
                dto.getApellido().trim(),
                dto.getDni().trim(),
                dto.getTelefono().trim(),
                dto.getCorreo() != null ? dto.getCorreo().trim() : null);

        clienteDAO.actualizar(cliente);
        return cliente;
    }

    private void validar(ClienteDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new BusinessException("El nombre es obligatorio", 400);
        if (dto.getApellido() == null || dto.getApellido().isBlank())
            throw new BusinessException("El apellido es obligatorio", 400);
        if (dto.getDni() == null || dto.getDni().isBlank())
            throw new BusinessException("El DNI es obligatorio", 400);
        if (dto.getTelefono() == null || dto.getTelefono().isBlank())
            throw new BusinessException("El teléfono es obligatorio", 400);
    }
}