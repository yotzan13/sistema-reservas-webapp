package service;

import dao.MesaDAO;
import dao.impl.MesaDAOImpl;
import dto.MesaDTO;
import exception.BusinessException;
import model.Mesa;
import java.util.List;

public class MesaService {

    private final MesaDAO mesaDAO = new MesaDAOImpl();

    public List<Mesa> listarTodas() {
        return mesaDAO.listarTodas();
    }

    public Mesa buscarPorId(int id) {
        Mesa m = mesaDAO.buscarPorId(id);
        if (m == null) {
         
            throw new BusinessException("Lo sentimos, no pudimos encontrar la mesa con el id: " + id, 404);
        }
        return m;
    }

    public Mesa crearMesa(MesaDTO dto) {
        validar(dto);
        if (mesaDAO.existeNumeroMesa(dto.getNumeroMesa(), null)) {
            throw new BusinessException("El N* de mesa " + dto.getNumeroMesa() + " ya está registrado. Por favor, asigne uno diferente", 409);
        }

        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(dto.getNumeroMesa());
        mesa.setUbicacion(dto.getUbicacion().trim());
        mesa.setCapacidad(dto.getCapacidad());

        int id = mesaDAO.crear(mesa);
        mesa.setId(id);
        System.out.println("[MesaService] Éxito: Mesa " + mesa.getNumeroMesa() + " registrada con id " + id);
        return mesa;
    }

    public Mesa actualizarMesa(int id, MesaDTO dto) {
       
        buscarPorId(id);
        validar(dto);
        
        if (mesaDAO.existeNumeroMesa(dto.getNumeroMesa(), id)) {
            throw new BusinessException("No se puede actualizar: el N* " + dto.getNumeroMesa() + " ya esta siendo usado por otra mesa", 409);
        }

        Mesa mesa = new Mesa(id, dto.getNumeroMesa(), dto.getUbicacion().trim(), dto.getCapacidad());
        mesaDAO.actualizar(mesa);
        return mesa;
    }

    public void eliminarMesa(int id) {
        buscarPorId(id);
        mesaDAO.eliminar(id);
    }

    private void validar(MesaDTO dto) {
        // 1. Validar Número de Mesa (Rango razonable)
        if (dto.getNumeroMesa() <= 0 || dto.getNumeroMesa() > 399) {
            throw new BusinessException("El N* de mesa debe estar entre 1 y 399", 400);
        }
        
        // 2. Validar Capacidad (Regla de negocio del restaurante)
        if (dto.getCapacidad() < 1 || dto.getCapacidad() > 12) {
            throw new BusinessException("La capacidad permitida es de 1 a 12 personas por mesa", 400);
        }
        
        // 3. Validar Ubicación y auto-corregir formato
        if (dto.getUbicacion() == null || dto.getUbicacion().isBlank()) {
            throw new BusinessException("La ubicación es obligatoria (Ventana, Interior o Terraza)", 400);
        }

        // Convertimos lo que envíe el usuario para que siempre tenga la primera Mayúscula
        String ubiInput = dto.getUbicacion().trim().toLowerCase();
        String ubiFormateada;
        
        if (ubiInput.equals("ventana")) ubiFormateada = "Ventana";
        else if (ubiInput.equals("interior")) ubiFormateada = "Interior";
        else if (ubiInput.equals("terraza")) ubiFormateada = "Terraza";
        else {
            throw new BusinessException("Ubicación no válida. Solo permitimos: Ventana, Interior o Terraza.", 400);
        }
        
        dto.setUbicacion(ubiFormateada);
    }
}