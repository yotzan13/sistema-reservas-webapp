package model;

public class Mesa {
    private int id;
    private int numeroMesa;
    private String ubicacion;
    private int capacidad;

    public Mesa() {}

    public Mesa(int id, int numeroMesa, String ubicacion, int capacidad) {
        this.id = id;
        this.numeroMesa = numeroMesa;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
}