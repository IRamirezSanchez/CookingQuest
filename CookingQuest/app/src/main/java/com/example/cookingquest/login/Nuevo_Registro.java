package com.example.cookingquest.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingquest.R;
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
    private TextView  mensajeErrorC;
    private FirebaseAuth myAuth;
    private FirebaseFirestore myFireStore;

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

        Button buttonRegistrar = findViewById(R.id.registrar);

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

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
                            Toast.makeText(Nuevo_Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

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

                            Intent intentAcceso = new Intent(Nuevo_Registro.this, Login.class);
                            startActivity(intentAcceso);
                            finish();
                        } else {
                            Log.w("RegistroActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Nuevo_Registro.this, "Error al registrar. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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

        if (!telefono.matches("^[0-9]{9}$")){
            entrada_telefonoC.setError("Introduce 9 digitos");
            return false;
        }
        if(!password.equals(passwordConfirmar)){
            confirmar_contraseñaC.setError("No coinciden las contraseñas");
            return false;
        }

        if(!password.matches("^.{6,}$")){
            entrada_contraseñaC.setError("Minimo 6 caracteres");
            return false;
        }else if(!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{6,}$")){
            entrada_contraseñaC.setError("Introduce una Mayuscula, un caracter especial y un numero");
            return false;
        }

        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            entrada_emailC.setError("Introduce un Correo Válido");
            return false;
        }
        return true;
    }


}