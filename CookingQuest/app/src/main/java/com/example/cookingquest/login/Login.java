package com.example.cookingquest.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myAuth = FirebaseAuth.getInstance();
        usuario= myAuth.getCurrentUser();
        if(usuario!=null){
            Toast.makeText(Login.this, "Hay un usuario logueado", Toast.LENGTH_SHORT).show();
            Intent intentRegistro = new Intent(this, InicioActivity.class);
            startActivity(intentRegistro);
            finish();
        }
        entradaCorreoAux = findViewById(R.id.entrada_correo);
        entradaPasswdAux = findViewById(R.id.entrada_passwd);
        barraProgreso = findViewById(R.id.progressBar);

        barraProgreso.setVisibility(View.INVISIBLE);


        //ASI ENTRO AUTOMATICAMENTE SIN ESCRIBIR MI EMAIL Y PASSWD
        entradaCorreoAux.setText("12@12.com");
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
        String email = entradaCorreoAux.getText().toString();
        String password = entradaPasswdAux.getText().toString();

        //Controlar los errores de email y contraseña con FIrebasey datos del cusuario! Datos introducidos erroneos.
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        //Login de firebaseAuth sign in !!

        myAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            barraProgreso.setVisibility(View.VISIBLE);
                            Log.d("Login", "Successfull");
                            Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            //Intent intentAcceso = new Intent(Login.this, PerfilUsuario.class);
                            Intent intentAcceso = new Intent(Login.this, InicioActivity.class);
                            //intentAcceso.putExtra("correo",email);
                            startActivity(intentAcceso);

                            finish();
                        } else {
                            Log.w("Login", "Failure", task.getException());
                            Toast.makeText(Login.this, "Error al iniciar sesión. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            barraProgreso.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.P_Boton_IMG_SALIR) {
            finishAffinity();
            //finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Metodo para controlar que el item INICIO no aparezca e inflar el ActionBar.
     * Condicion que la activity que estes, sea el mainActivity.
     * Controlar que el item NO sea nulo.
     * @param menu The options menu in which you place your items.
     *
     * @return true, de que sea ha controlado que no muestre ese item.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cooquing_quest_menu, menu);

        // Verifica la clase de la actividad y oculta la opción de menú según sea necesario
        if (getClass() == Login.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_INICIO);
            MenuItem itemToRemove1 = menu.findItem(R.id.menu_juegos);

            if (itemToRemove != null || itemToRemove1 != null) {
                menu.removeItem(itemToRemove.getItemId());
                menu.removeItem(itemToRemove1.getItemId());
            }
        }

        return true;
    }
}