package com.devst.habittrack.model;

public class Habito {
    private String id;
    private String nombre;
    private String categoria;
    private String horaRecordatorio;
    private String estado;
    private String userId;

    // Constructor vacío obligatorio para Cloud Firestore
    public Habito() {
    }

    public Habito(String id, String nombre, String categoria, String horaRecordatorio, String estado, String userId) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.horaRecordatorio = horaRecordatorio;
        this.estado = estado;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getHoraRecordatorio() { return horaRecordatorio; }
    public void setHoraRecordatorio(String horaRecordatorio) { this.horaRecordatorio = horaRecordatorio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}