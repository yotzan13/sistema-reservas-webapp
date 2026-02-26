package dao;

import model.Cliente;
import java.util.List;

public interface ClienteDAO {
    List<Cliente> listarTodos();
    Cliente buscarPorId(int id);
    Cliente buscarPorDni(String dni);
    int crear(Cliente cliente);
    boolean actualizar(Cliente cliente);
}