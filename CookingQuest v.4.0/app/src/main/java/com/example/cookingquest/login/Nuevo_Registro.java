package com.example.cookingquest.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cookingquest.R;
import com.example.cookingquest.SplashScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;


public class Nuevo_Registro extends AppCompatActivity {

    private EditText entrada_emailC, entrada_contraseñaC, confirmar_contraseñaC, entrada_nombreC, entrada_telefonoC;
    private Long puntosC;
    private TextView  mensajeErrorC, mensajeErrorContra;
    private FirebaseAuth myAuth;
    private FirebaseFirestore myFireStore;
    private Animation mBtnAnim;
    private static final int TIEMPO_ESPERA=2000;

    String uidUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_registro);

        myAuth = FirebaseAuth.getInstance();
        myFireStore = FirebaseFirestore.getInstance();

        entrada_nombreC = findViewById(R.id.entrada_nombre);
        entrada_telefonoC = findViewById(R.id.entrada_telefono);
        entrada_emailC = findViewById(R.id.entrada_email);
        entrada_contraseñaC = findViewById(R.id.entrada_contraseña);
        confirmar_contraseñaC = findViewById(R.id.confirmarContraseña);
        puntosC= 0L;
        mensajeErrorC = findViewById(R.id.mensajeError);
        mensajeErrorContra= findViewById(R.id.mensajeErrorContrasenia);
        mBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.boton_anim);

        Button buttonRegistrar = findViewById(R.id.registrar);

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRegistrar.startAnimation(mBtnAnim);
                registrarUsuario();
            }
        });
    }

    /**
     * Se encarga de registrar un nuevo usuario en Firebase Authentication
     * y almacenar información adicional en Firestore. Realiza validaciones antes del registro.
     * Si el registro es exitoso, muestra un mensaje de éxito y redirige a la pantalla de inicio de sesión.
     * Si hay algún error, muestra un mensaje de error.
     */
    private void registrarUsuario() {
        String email = entrada_emailC.getText().toString();
        String password = entrada_contraseñaC.getText().toString();
        String passwordConfirmacion = confirmar_contraseñaC.getText().toString();
        String telefono = entrada_telefonoC.getText().toString();
        String nombre = entrada_nombreC.getText().toString();


        if(!controlEntrada(email,password, telefono, passwordConfirmacion,nombre)){
            return;
        };

        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("RegistroActivity", "createUserWithEmail:success");

                            uidUsuario= myAuth.getCurrentUser().getUid();
                            DocumentReference docRef = myFireStore.collection("usuarios").document(uidUsuario);

                            HashMap<String, Object> infoUsuario = new HashMap<>();
                            infoUsuario.put("Nombre",nombre);
                            infoUsuario.put("Telefono",telefono);
                            infoUsuario.put("Email",email);
                            infoUsuario.put("Puntos",0L);

                            ArrayList<String> recetasFavoritas = new ArrayList<>();
                            infoUsuario.put("recetasFavoritas", recetasFavoritas);

                            docRef.set(infoUsuario);
                            int colorPredeterminado = mensajeErrorC.getCurrentTextColor();
                            mensajeErrorC.setText("Registro exitoso");
                            mensajeErrorC.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            mensajeErrorC.setTextColor(colorPredeterminado);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intentAcceso = new Intent(Nuevo_Registro.this, Login.class);
                                    startActivity(intentAcceso);
                                    mensajeErrorC.setText("");
                                    // Aplica la animación como empieza y como abre la otra actividad en la carpeta res/anim
                                    overridePendingTransition(R.anim.desvanecer, R.anim.aparecer);
                                    finish(); // Cierra la actividad de bienvenida para que no se pueda volver atrás.
                                }
                            }, TIEMPO_ESPERA);

                        } else {
                            Log.w("RegistroActivity", "createUserWithEmail:failure", task.getException());
                            mensajeErrorC.setText("Error al registrar");
                        }
                    }
                });
    }

    /**
     * Método privado que realiza validaciones en la entrada del usuario durante el registro.
     *
     * @param email              El correo electrónico ingresado por el usuario.
     * @param password           La contraseña ingresada por el usuario.
     * @param telefono           El número de teléfono ingresado por el usuario.
     * @param passwordConfirmar La confirmación de la contraseña ingresada por el usuario.
     * @param nombre             El nombre ingresado por el usuario.
     * @return true si la entrada es válida, false de lo contrario.
     */
    private boolean controlEntrada(String email, String password, String telefono, String passwordConfirmar, String nombre){

        if (email.isEmpty() || password.isEmpty() || telefono.isEmpty() || passwordConfirmar.isEmpty() || nombre.isEmpty()) {
            // Toast.makeText(Nuevo_Registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            mensajeErrorC.setText("Por favor, completa todos los campos");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mensajeErrorC.setText("");
                }
            }, 3000);
            return false;
        }

        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            entrada_emailC.setError("Introduce un Correo Válido");
            return false;
        }

        if (!telefono.matches("^[0-9]{9}$")){
            entrada_telefonoC.setError("Introduce 9 digitos");
            return false;
        }
        if(!password.equals(passwordConfirmar)){
            mensajeErrorContra.setTextColor(ContextCompat.getColor(this, R.color.red));
            mensajeErrorContra.setText("Las contraseñas no coinciden");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mensajeErrorContra.setText("");
                }
            }, 4000);
            return false;
        }

        if(!password.matches("^.{6,}$")){
            mensajeErrorContra.setTextColor(ContextCompat.getColor(this, R.color.red));
            mensajeErrorContra.setText("Minimo 6 caracteres");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mensajeErrorContra.setText("");
                }
            }, 4000);
            return false;
        }else if(!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{6,}$")){
            mensajeErrorContra.setTextColor(ContextCompat.getColor(this, R.color.red));
            mensajeErrorContra.setText("Introduce una Mayuscula, un caracter especial y un numero");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mensajeErrorContra.setText("");
                }
            }, 4000);
            return false;
        }


        return true;
    }


}