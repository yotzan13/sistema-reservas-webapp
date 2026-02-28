package db;

import javax.swing.JOptionPane;

public class TestConectarBD {

    public static void main(String[] args) {
        // obtener la conexión
        if (ConectarBD.getConexion() != null) {
            JOptionPane.showMessageDialog(null,
                "Hay conexión en BD", "MENSAJE", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                "No se pudo conectar a la BD", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}