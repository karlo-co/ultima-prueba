package com.devst.habittrack.repository;

import androidx.annotation.NonNull;

import com.devst.habittrack.model.Habito;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Repositorio encargado de las operaciones CRUD de hábitos en Cloud Firestore.
 * Centraliza el acceso a la colección "habitos" para separar la lógica de datos de la interfaz.
 */
public class HabitoRepositorio {

    // Nombre de la colección en Cloud Firestore donde se almacenan los hábitos
    private static final String COLECCION_HABITOS = "habitos";

    // Nombre del campo utilizado para filtrar los hábitos por usuario
    private static final String CAMPO_UID = "uid";

    private final FirebaseFirestore baseDatos;
    private final CollectionReference referenciaHabitos;

    /**
     * Inicializa el repositorio obteniendo la instancia de Firestore y la referencia
     * a la colección "habitos".
     */
    public HabitoRepositorio() {
        this.baseDatos = FirebaseFirestore.getInstance();
        this.referenciaHabitos = this.baseDatos.collection(COLECCION_HABITOS);
    }

    /**
     * Crea un nuevo hábito generando un ID automático de documento en Firestore,
     * asigna ese ID al objeto {@link Habito} y guarda el registro.
     *
     * @param habito Objeto {@link Habito} que se desea guardar.
     * @return {@link Task} void que representa el estado de la operación de escritura.
     */
    public Task<Void> crearHabito(@NonNull Habito habito) {
        if (habito == null) {
            throw new IllegalArgumentException("El objeto Habito no puede ser nulo.");
        }

        // Genera un nuevo documento con ID automático antes de persistir
        DocumentReference nuevoDocumento = referenciaHabitos.document();
        habito.setId(nuevoDocumento.getId());

        return nuevoDocumento.set(habito);
    }

    /**
     * Obtiene la consulta de hábitos filtrada por el identificador de usuario ({@code uid}).
     *
     * @param uid Identificador único del usuario autenticado.
     * @return {@link Query} filtrada donde el campo "uid" coincide con el usuario indicado.
     */
    public Query obtenerHabitosPorUsuario(@NonNull String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("El uid del usuario no puede estar vacío.");
        }

        return referenciaHabitos.whereEqualTo(CAMPO_UID, uid);
    }

    /**
     * Actualiza un hábito existente en Firestore usando el ID almacenado en {@link Habito#getId()}.
     *
     * @param habito Objeto {@link Habito} con los datos modificados y un ID válido.
     * @return {@link Task} void que representa el estado de la operación de actualización.
     */
    public Task<Void> actualizarHabito(@NonNull Habito habito) {
        if (habito == null || habito.getId() == null || habito.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El hábito y su ID son obligatorios para actualizar.");
        }

        return referenciaHabitos.document(habito.getId()).set(habito);
    }

    /**
     * Elimina un hábito de Firestore a partir de su identificador de documento.
     *
     * @param id Identificador único del documento a eliminar.
     * @return {@link Task} void que representa el estado de la operación de eliminación.
     */
    public Task<Void> eliminarHabito(@NonNull String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del hábito no puede estar vacío para eliminar.");
        }

        return referenciaHabitos.document(id).delete();
    }
}