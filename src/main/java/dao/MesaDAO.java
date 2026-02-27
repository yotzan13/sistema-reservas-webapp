package dao;

import model.Mesa;
import java.util.List;

public interface MesaDAO {
    List<Mesa> listarTodas();
    Mesa buscarPorId(int id);
    int crear(Mesa mesa);
    boolean actualizar(Mesa mesa);
    boolean eliminar(int id);
    boolean existeNumeroMesa(int numeroMesa, Integer excludeId);
}