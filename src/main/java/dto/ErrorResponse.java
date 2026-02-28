package dto;

import java.time.LocalDateTime;

public class ErrorResponse {

    private boolean success;
    private String mensaje;
    private int codigo;
    private String timestamp;

    public ErrorResponse(String mensaje, int codigo) {
        this.success = false;
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.timestamp = LocalDateTime.now().toString();
    }

    public boolean isSuccess() { return success; }
    public String getMensaje() { return mensaje; }
    public int getCodigo() { return codigo; }
    public String getTimestamp() { return timestamp; }
}