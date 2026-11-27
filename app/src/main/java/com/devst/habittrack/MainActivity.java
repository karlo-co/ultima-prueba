package com.devst.habittrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.habittrack.adapter.HabitoAdapter;
import com.devst.habittrack.model.Habito;
import com.devst.habittrack.repository.HabitoRepositorio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla principal de HabitTrack.
 * Permite crear, consultar en tiempo real, filtrar, editar y eliminar los hábitos
 * del usuario autenticado mediante {@link HabitoRepositorio}.
 */
public class MainActivity extends AppCompatActivity implements HabitoAdapter.OnHabitoActionListener {

    // Componentes visuales del layout activity_main.xml
    private TextView tvUsuarioBienvenida;
    private EditText etNombreHabito;
    private EditText etCategoriaHabito;
    private EditText etHoraHabito;
    private EditText etBuscarHabito;
    private Button btnGuardarHabito;
    private Button btnCancelarEdicion;
    private Button btnCerrarSesion;
    private RecyclerView rvHabitos;

    // Dependencias de autenticación y acceso a datos en Firestore
    private FirebaseAuth autenticacion;
    private FirebaseUser usuarioActual;
    private HabitoRepositorio repositorio;
    private ListenerRegistration escuchaHabitos;

    // Listas para respaldar todos los hábitos del usuario y los resultados filtrados en pantalla
    private final List<Habito> listaCompleta = new ArrayList<>();
    private final List<Habito> listaHabitos = new ArrayList<>();
    private HabitoAdapter adaptador;
    private Habito habitoEnEdicion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifica que exista una sesión activa antes de inicializar la vista y Firestore
        autenticacion = FirebaseAuth.getInstance();
        usuarioActual = autenticacion.getCurrentUser();

        if (usuarioActual == null) {
            volverALogin();
            return;
        }

        repositorio = new HabitoRepositorio();

        inicializarVistas();
        configurarRecyclerView();
        configurarEventos();
        cargarHabitosEnTiempoReal();
    }

    /**
     * Vincula las vistas del layout y muestra el correo del usuario autenticado.
     */
    private void inicializarVistas() {
        tvUsuarioBienvenida = findViewById(R.id.tvUsuarioBienvenida);
        etNombreHabito = findViewById(R.id.etNombreHabito);
        etCategoriaHabito = findViewById(R.id.etCategoriaHabito);
        etHoraHabito = findViewById(R.id.etHoraHabito);
        etBuscarHabito = findViewById(R.id.etBuscarHabito);
        btnGuardarHabito = findViewById(R.id.btnGuardarHabito);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rvHabitos = findViewById(R.id.rvHabitos);

        if (tvUsuarioBienvenida != null && usuarioActual.getEmail() != null) {
            tvUsuarioBienvenida.setText("Usuario: " + usuarioActual.getEmail());
        }
    }

    /**
     * Configura el administrador de diseño y el adaptador del RecyclerView.
     */
    private void configurarRecyclerView() {
        adaptador = new HabitoAdapter(listaHabitos, this);
        rvHabitos.setLayoutManager(new LinearLayoutManager(this));
        rvHabitos.setAdapter(adaptador);
    }

    /**
     * Registra los listeners de botones y del campo de búsqueda en tiempo real.
     */
    private void configurarEventos() {
        if (btnGuardarHabito != null) {
            btnGuardarHabito.setOnClickListener(v -> guardarHabito());
        }
        if (btnCancelarEdicion != null) {
            btnCancelarEdicion.setOnClickListener(v -> limpiarFormulario());
        }
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        }
        if (etBuscarHabito != null) {
            etBuscarHabito.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filtrarHabitos(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    /**
     * Escucha los cambios en Firestore filtrados por el {@code uid} del usuario actual.
     */
    private void cargarHabitosEnTiempoReal() {
        escuchaHabitos = repositorio.obtenerHabitosPorUsuario(usuarioActual.getUid())
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar hábitos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        listaCompleta.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Habito habito = doc.toObject(Habito.class);
                            habito.setId(doc.getId());
                            listaCompleta.add(habito);
                        }
                        String filtroActual = etBuscarHabito != null ? etBuscarHabito.getText().toString() : "";
                        filtrarHabitos(filtroActual);
                    }
                });
    }

    /**
     * Filtra la lista visible según el texto ingresado por nombre o categoría.
     *
     * @param texto Texto de búsqueda ingresado por el usuario.
     */
    private void filtrarHabitos(String texto) {
        String consulta = texto == null ? "" : texto.trim().toLowerCase();
        listaHabitos.clear();

        if (consulta.isEmpty()) {
            listaHabitos.addAll(listaCompleta);
        } else {
            for (Habito habito : listaCompleta) {
                String nombre = habito.getNombre() != null ? habito.getNombre().toLowerCase() : "";
                String categoria = habito.getCategoria() != null ? habito.getCategoria().toLowerCase() : "";
                if (nombre.contains(consulta) || categoria.contains(consulta)) {
                    listaHabitos.add(habito);
                }
            }
        }

        adaptador.notifyDataSetChanged();
    }

    /**
     * Valida los campos del formulario y crea un nuevo hábito o actualiza el existente.
     */
    private void guardarHabito() {
        String nombre = etNombreHabito.getText().toString().trim();
        String categoria = etCategoriaHabito.getText().toString().trim();
        String hora = etHoraHabito.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombreHabito.setError("Ingresa el nombre del hábito");
            return;
        }
        if (TextUtils.isEmpty(categoria)) {
            etCategoriaHabito.setError("Ingresa una categoría");
            return;
        }
        if (TextUtils.isEmpty(hora)) {
            etHoraHabito.setError("Ingresa la hora (ej. 18:30)");
            return;
        }

        if (habitoEnEdicion == null) {
            // Orden exacto del constructor Habito: (id, nombre, categoria, horaRecordatorio, estado, uid)
            Habito nuevoHabito = new Habito(
                    null,
                    nombre,
                    categoria,
                    hora,
                    "Pendiente",
                    usuarioActual.getUid()
            );

            repositorio.crearHabito(nuevoHabito)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Hábito guardado", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            habitoEnEdicion.setNombre(nombre);
            habitoEnEdicion.setCategoria(categoria);
            habitoEnEdicion.setHoraRecordatorio(hora);

            repositorio.actualizarHabito(habitoEnEdicion)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Hábito actualizado", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    /**
     * Carga los datos del hábito seleccionado en el formulario para editarlo.
     *
     * @param habito Hábito seleccionado desde el adaptador.
     */
    @Override
    public void onEditar(Habito habito) {
        if (habito == null) {
            return;
        }
        habitoEnEdicion = habito;
        etNombreHabito.setText(habito.getNombre());
        etCategoriaHabito.setText(habito.getCategoria());
        etHoraHabito.setText(habito.getHoraRecordatorio());
        if (btnGuardarHabito != null) {
            btnGuardarHabito.setText("Actualizar Hábito");
        }
        if (btnCancelarEdicion != null) {
            btnCancelarEdicion.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Elimina el hábito seleccionado de Cloud Firestore.
     *
     * @param habito Hábito a eliminar.
     */
    @Override
    public void onEliminar(Habito habito) {
        if (habito == null || TextUtils.isEmpty(habito.getId())) {
            Toast.makeText(this, "No se pudo identificar el hábito a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        repositorio.eliminarHabito(habito.getId())
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Hábito eliminado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * Restablece el formulario y sale del modo edición.
     */
    private void limpiarFormulario() {
        habitoEnEdicion = null;
        etNombreHabito.setText("");
        etCategoriaHabito.setText("");
        etHoraHabito.setText("");
        if (btnGuardarHabito != null) {
            btnGuardarHabito.setText("Guardar Hábito");
        }
        if (btnCancelarEdicion != null) {
            btnCancelarEdicion.setVisibility(View.GONE);
        }
    }

    /**
     * Cierra la sesión actual en Firebase Authentication y regresa a {@link LoginActivity}.
     */
    private void cerrarSesion() {
        autenticacion.signOut();
        volverALogin();
    }

    /**
     * Navega hacia {@link LoginActivity} y finaliza {@link MainActivity}.
     */
    private void volverALogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (escuchaHabitos != null) {
            escuchaHabitos.remove();
        }
    }
}