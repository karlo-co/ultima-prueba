package com.devst.habittrack.model;

/**
 * Representa un hábito asociado a un usuario en HabitTrack.
 * Sirve como modelo de datos para guardar y leer documentos en Cloud Firestore.
 */
public class Habito {

    // Identificador único del documento en Firestore
    private String id;

    // Nombre del hábito
    private String nombre;

    // Categoría del hábito (por ejemplo, "Salud", "Estudio")
    private String categoria;

    // Hora del recordatorio del hábito
    private String horaRecordatorio;

    // Estado actual del hábito (por ejemplo, "Pendiente" o "Completado")
    private String estado;

    // Identificador del usuario autenticado (UID de Firebase Authentication)
    private String uid;

    /**
     * Constructor vacío obligatorio para que Firestore pueda deserializar
     * documentos automáticamente al leer datos.
     */
    public Habito() {
    }

    /**
     * Constructor con todos los campos para inicializar un hábito completo.
     *
     * @param id               Identificador único del hábito.
     * @param nombre           Nombre del hábito.
     * @param categoria        Categoría del hábito.
     * @param horaRecordatorio Hora configurada para el recordatorio.
     * @param estado           Estado actual del hábito.
     * @param uid              UID del usuario propietario del hábito.
     */
    public Habito(String id, String nombre, String categoria, String horaRecordatorio, String estado, String uid) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.horaRecordatorio = horaRecordatorio;
        this.estado = estado;
        this.uid = uid;
    }

    /**
     * Devuelve el ID del hábito.
     *
     * @return Identificador del documento.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el ID del hábito.
     *
     * @param id Identificador del documento.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del hábito.
     *
     * @return Nombre del hábito.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del hábito.
     *
     * @param nombre Nombre del hábito.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la categoría del hábito.
     *
     * @return Categoría del hábito.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del hábito.
     *
     * @param categoria Categoría del hábito.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Devuelve la hora del recordatorio.
     *
     * @return Hora del recordatorio.
     */
    public String getHoraRecordatorio() {
        return horaRecordatorio;
    }

    /**
     * Establece la hora del recordatorio.
     *
     * @param horaRecordatorio Hora del recordatorio.
     */
    public void setHoraRecordatorio(String horaRecordatorio) {
        this.horaRecordatorio = horaRecordatorio;
    }

    /**
     * Devuelve el estado del hábito.
     *
     * @return Estado del hábito.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del hábito.
     *
     * @param estado Estado del hábito.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Devuelve el UID del usuario dueño del hábito.
     *
     * @return UID del usuario.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Establece el UID del usuario dueño del hábito.
     *
     * @param uid UID del usuario.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
}