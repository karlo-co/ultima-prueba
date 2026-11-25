package com.devst.habittrack.repository;

import com.devst.habittrack.model.Habito;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HabitoRepositorio {

    private static final String COLLECTION_NAME = "habitos";
    private final FirebaseFirestore db;
    private final CollectionReference habitosRef;

    public HabitoRepositorio() {
        db = FirebaseFirestore.getInstance();
        habitosRef = db.collection(COLLECTION_NAME);
    }

    public Task<Void> crearHabito(Habito habito) {
        DocumentReference docRef = habitosRef.document();
        habito.setId(docRef.getId());
        return docRef.set(habito);
    }

    public Query obtenerHabitosPorUsuario(String userId) {
        return habitosRef.whereEqualTo("userId", userId);
    }

    public Task<Void> actualizarHabito(Habito habito) {
        return habitosRef.document(habito.getId()).set(habito);
    }

    public Task<Void> eliminarHabito(String id) {
        return habitosRef.document(id).delete();
    }
}