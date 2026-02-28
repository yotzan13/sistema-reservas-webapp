package dto;

public class ApiResponse<T> {

    private boolean success;
    private String mensaje;
    private T data;

    public ApiResponse(boolean success, String mensaje, T data) {
        this.success = success;
        this.mensaje = mensaje;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMensaje() { return mensaje; }
    public T getData() { return data; }
}