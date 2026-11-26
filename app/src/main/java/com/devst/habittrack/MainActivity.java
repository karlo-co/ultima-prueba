package com.devst.habittrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devst.habittrack.adapter.HabitoAdapter;
import com.devst.habittrack.model.Habito;
import com.devst.habittrack.repository.HabitoRepositorio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnCancelarEdicion;
    private EditText etNombre, etCategoria, etHora, etEstado;
    private Button btnGuardar, btnCerrarSesion;
    private RecyclerView rvHabitos;

    private HabitoRepositorio repositorio;
    private FirebaseAuth mAuth;
    private HabitoAdapter adapter;
    private List<Habito> listaHabitos;

    private Habito habitoEnEdicion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        repositorio = new HabitoRepositorio();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        etNombre = findViewById(R.id.etNombre);
        etCategoria = findViewById(R.id.etCategoria);
        etHora = findViewById(R.id.etHora);
        etEstado = findViewById(R.id.etEstado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion);
        rvHabitos = findViewById(R.id.rvHabitos);

        listaHabitos = new ArrayList<>();
        rvHabitos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HabitoAdapter(listaHabitos, new HabitoAdapter.OnHabitoActionListener() {
            @Override
            public void onEditar(Habito habito) {
                cargarParaEditar(habito);
            }

            @Override
            public void onEliminar(Habito habito) {
                confirmarEliminar(habito);
            }
        });
        rvHabitos.setAdapter(adapter);
        etHora.setOnClickListener(v -> mostrarSelectorHora());
        btnGuardar.setOnClickListener(v -> guardarHabito());
        btnCancelarEdicion.setOnClickListener(v -> limpiarFormulario());
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        cargarHabitosEnTiempoReal();
    }

    private void guardarHabito() {
        String nombre = etNombre.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String estado = etEstado.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(categoria) ||
                TextUtils.isEmpty(hora) || TextUtils.isEmpty(estado)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        if (habitoEnEdicion == null) {
            Habito nuevo = new Habito(null, nombre, categoria, hora, estado, uid);
            repositorio.crearHabito(nuevo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Hábito guardado en la nube", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            habitoEnEdicion.setNombre(nombre);
            habitoEnEdicion.setCategoria(categoria);
            habitoEnEdicion.setHoraRecordatorio(hora);
            habitoEnEdicion.setEstado(estado);

            repositorio.actualizarHabito(habitoEnEdicion)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Hábito actualizado", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void cargarHabitosEnTiempoReal() {
        String uid = mAuth.getCurrentUser().getUid();

        repositorio.obtenerHabitosPorUsuario(uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        listaHabitos.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            listaHabitos.add(doc.toObject(Habito.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void cargarParaEditar(Habito habito) {
        habitoEnEdicion = habito;
        etNombre.setText(habito.getNombre());
        etCategoria.setText(habito.getCategoria());
        etHora.setText(habito.getHoraRecordatorio());
        etEstado.setText(habito.getEstado());
        btnGuardar.setText("Actualizar Hábito");
        btnCancelarEdicion.setVisibility(android.view.View.VISIBLE);
    }

    private void confirmarEliminar(Habito habito) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Hábito")
                .setMessage("¿Deseas eliminar '" + habito.getNombre() + "'?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    repositorio.eliminarHabito(habito.getId())
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Hábito eliminado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etCategoria.setText("");
        etHora.setText("");
        etEstado.setText("");
        habitoEnEdicion = null;
        btnGuardar.setText("Guardar Hábito");
        btnCancelarEdicion.setVisibility(android.view.View.GONE);
    }

    private void mostrarSelectorHora() {
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        int horaActual = calendario.get(java.util.Calendar.HOUR_OF_DAY);
        int minutoActual = calendario.get(java.util.Calendar.MINUTE);

        new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String horaFormateada = String.format(java.util.Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            etHora.setText(horaFormateada);
        }, horaActual, minutoActual, true).show();
    }
}