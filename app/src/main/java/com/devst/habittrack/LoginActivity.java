package com.devst.habittrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Pantalla de acceso y registro de usuarios para HabitTrack.
 * Gestiona la autenticación por correo y contraseña mediante Firebase Authentication
 * y redirige a {@link MainActivity} cuando existe o se inicia una sesión válida.
 */
public class LoginActivity extends AppCompatActivity {

    // Campos de entrada y botones definidos en activity_login.xml
    private EditText etCorreo;
    private EditText etContrasena;
    private Button btnIniciarSesion;
    private Button btnRegistrar;

    // Instancia principal de Firebase Authentication
    private FirebaseAuth autenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacion = FirebaseAuth.getInstance();

        // Verifica si ya existe una sesión activa para omitir la pantalla de login
        FirebaseUser usuarioActual = autenticacion.getCurrentUser();
        if (usuarioActual != null) {
            irAMainActivity();
            return;
        }

        inicializarVistas();
        configurarEventos();
    }

    /**
     * Vincula los componentes visuales del layout {@code activity_login.xml}.
     */
    private void inicializarVistas() {
        etCorreo = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);
    }

    /**
     * Configura los eventos de clic para iniciar sesión y registrar una nueva cuenta.
     */
    private void configurarEventos() {
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    /**
     * Valida las credenciales ingresadas e inicia sesión en Firebase Authentication.
     */
    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (!validarCredenciales(correo, contrasena)) {
            return;
        }

        cambiarEstadoBotones(false);

        autenticacion.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, tarea -> {
                    if (tarea.isSuccessful()) {
                        Toast.makeText(this, "Bienvenido a HabitTrack", Toast.LENGTH_SHORT).show();
                        irAMainActivity();
                    } else {
                        cambiarEstadoBotones(true);
                        String mensajeError = tarea.getException() != null
                                ? tarea.getException().getMessage()
                                : "Error de autenticación";
                        Toast.makeText(this, "Error al iniciar sesión: " + mensajeError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Valida los datos ingresados y crea una nueva cuenta de usuario en Firebase Authentication.
     */
    private void registrarUsuario() {
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (!validarCredenciales(correo, contrasena)) {
            return;
        }

        cambiarEstadoBotones(false);

        autenticacion.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, tarea -> {
                    if (tarea.isSuccessful()) {
                        Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                        irAMainActivity();
                    } else {
                        cambiarEstadoBotones(true);
                        String mensajeError = tarea.getException() != null
                                ? tarea.getException().getMessage()
                                : "Error al registrar usuario";
                        Toast.makeText(this, "No se pudo registrar: " + mensajeError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Comprueba que el correo tenga formato válido y que la contraseña cumpla la longitud mínima.
     *
     * @param correo     Correo electrónico ingresado por el usuario.
     * @param contrasena Contraseña ingresada por el usuario.
     * @return {@code true} si ambos campos son válidos; {@code false} en caso contrario.
     */
    private boolean validarCredenciales(String correo, String contrasena) {
        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError("Ingresa un correo electrónico");
            etCorreo.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Ingresa un correo electrónico válido");
            etCorreo.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(contrasena)) {
            etContrasena.setError("Ingresa una contraseña");
            etContrasena.requestFocus();
            return false;
        }
        if (contrasena.length() < 6) {
            etContrasena.setError("La contraseña debe tener al menos 6 caracteres");
            etContrasena.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Habilita o deshabilita los botones de autenticación durante una operación de red.
     *
     * @param habilitado {@code true} para activar los botones; {@code false} para bloquearlos.
     */
    private void cambiarEstadoBotones(boolean habilitado) {
        if (btnIniciarSesion != null) {
            btnIniciarSesion.setEnabled(habilitado);
        }
        if (btnRegistrar != null) {
            btnRegistrar.setEnabled(habilitado);
        }
    }

    /**
     * Abre {@link MainActivity} y cierra {@link LoginActivity} para evitar volver atrás al login.
     */
    private void irAMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}