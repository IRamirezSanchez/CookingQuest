package com.example.cookingquest.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingquest.GameActivityAdivina;
import com.example.cookingquest.InicioActivity;
import com.example.cookingquest.R;
import com.example.cookingquest.RecetasPaisActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText entradaCorreoAux, entradaPasswdAux;
    private ProgressBar barraProgreso;
    private FirebaseAuth myAuth;
    private FirebaseUser usuario;
    private Animation mBtnAnim;
    private TextView mensajeErrorC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myAuth = FirebaseAuth.getInstance();
        usuario= myAuth.getCurrentUser();

        if(usuario!=null){
            Intent intentRegistro = new Intent(this, InicioActivity.class);
            startActivity(intentRegistro);
            finish();
        }
        entradaCorreoAux = findViewById(R.id.entrada_correo);
        entradaPasswdAux = findViewById(R.id.entrada_passwd);
        barraProgreso = findViewById(R.id.progressBar);
        mensajeErrorC = findViewById(R.id.mensajeError);

        barraProgreso.setVisibility(View.INVISIBLE);
        mBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.boton_anim);


        //ASI ENTRO AUTOMATICAMENTE SIN ESCRIBIR MI EMAIL Y PASSWD
        entradaCorreoAux.setText("prueba@prueba.com");
        entradaPasswdAux.setText("123456A_?");
    }

    // on click del xml, y el else de dentro se podria eliminar. lo controlo aqui arriba en el oncreate!
    public void nuevoUsuario(View view) {

        if(usuario==null) {
            Intent intentRegistro = new Intent(this, Nuevo_Registro.class);
            startActivity(intentRegistro);

        }else{
            //Realmente el usuario lo controlo  cuando entra en la app y  no cerro la sesion sesion antes,
            // le dirija a la Perfil de usuario (Principal). Por seguridad de Buggeo..etc lo dejo.
            Toast.makeText(Login.this, "Hay un usuario logueado", Toast.LENGTH_SHORT).show();
            Intent intentRegistro = new Intent(this, PerfilUsuario.class);
            startActivity(intentRegistro);
            finish();
        }
    }

    //Boton ONCLICK XML
    public void accederLogin(View view) {
        view.startAnimation(mBtnAnim);
        String email = entradaCorreoAux.getText().toString();
        String password = entradaPasswdAux.getText().toString();

        //Controlar los errores de email y contraseña con FIrebasey datos del cusuario! Datos introducidos erroneos.
        if (!controlEntrada(email,password)) {
           return;
        }


        //Login de firebaseAuth sign in !!
        barraProgreso.setVisibility(View.VISIBLE);
        myAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intentAcceso = new Intent(Login.this, InicioActivity.class);
                            startActivity(intentAcceso);
                            barraProgreso.setVisibility(View.INVISIBLE);
                            finish();
                        } else {
                            mensajeErrorC.setText("Login Incorrecto, Intentalo de nuevo");
                            barraProgreso.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }

    ///////////////////////////////////////////////CONTROL ENTRADA///////////////////////////////////////////////////////
    private boolean controlEntrada(String email, String password){
        mensajeErrorC.setText("");

        if (email.isEmpty() || password.isEmpty()) {
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

        if(!password.matches("^.{6,}$")){
            entradaPasswdAux.setError("Minimo 6 caracteres");
            return false;
        }else if(!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{6,}$")){
            entradaPasswdAux.setError("Introduce una Mayuscula, un caracter especial y un numero");
            return false;
        }

        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            entradaCorreoAux.setError("Introduce un Correo Válido");
            return false;
        }
        return true;
    }

}