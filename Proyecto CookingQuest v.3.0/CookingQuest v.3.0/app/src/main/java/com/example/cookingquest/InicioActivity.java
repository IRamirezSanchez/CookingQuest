package com.example.cookingquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.cookingquest.login.Login;
import com.example.cookingquest.login.PerfilUsuario;
import com.example.cookingquest.model.Pais;
import com.example.cookingquest.usuario.MenuTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InicioActivity extends AppCompatActivity {

    public static final String pais="pais";
    private Animation mBtnAnim;
    private FirebaseUser user;
    private FirebaseAuth myAuth;
    private StorageReference myStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_activity_main);
        myAuth =  FirebaseAuth.getInstance();
        user= myAuth.getCurrentUser();
        // Verificar si el usuario no está autenticado y redirigir a la actividad de inicio de sesión
        if(user==null){
            Intent login = new Intent(InicioActivity.this, Login.class);
            startActivity(login);
            overridePendingTransition(R.anim.desvanecer, R.anim.aparecer);
        }

        ImageButton botonFrancia = findViewById(R.id.P_BotonIMG_FRA);
        ImageButton botonAlemania = findViewById(R.id.P_BotonIMG_ALE);
        ImageButton botonEspana = findViewById(R.id.P_BotonIMG_ESP);
        ImageButton botonInglaterra = findViewById(R.id.P_BotonIMG_ING);
        ImageButton botonEmparejalos = findViewById(R.id.P_Boton_IMG_EMPAREJA);
        ImageButton botonAdivina = findViewById(R.id.P_Boton_IMG_ADIVINA);
        mBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.boton_anim);

        myStorage = FirebaseStorage.getInstance().getReference();


        //se utiliza para transmitir datos entre las actividades en función de los botones que se han hecho clic.
        View.OnClickListener paisBotonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(mBtnAnim);
                Pais nombrePais = null;
                String juego= null;
                // Determina qué país se ha seleccionado
                if (v == botonFrancia) {
                    nombrePais = Pais.FRANCIA;
                } else if (v == botonEspana) {
                    nombrePais = Pais.ESPAÑA;
                } else if (v == botonAlemania) {
                    nombrePais = Pais.ALEMANIA;
                } else if (v == botonInglaterra) {
                    nombrePais = Pais.INGLATERRA;
                } else if (v == botonEmparejalos) {
                    juego = "CookingFast";
                } else if (v == botonAdivina) {
                    juego = "Adivina";
                }
                // Crea un Intent y pasa información a la otra actividad
                if (nombrePais != null) {
                    Intent intent = new Intent(InicioActivity.this, RecetasPaisActivity.class);
                    intent.putExtra("pais", nombrePais.name()); //con .name() nos muestra el nombre del pais seleccionado
                    startActivity(intent);
                    overridePendingTransition(R.anim.desvanecer, R.anim.aparecer); //poner despues de iniciar la actividad

                }
                if (juego != null) {
                    if(juego.equals("Adivina")){
                        Intent intent = new Intent(InicioActivity.this, GameActivityAdivina.class);
                        intent.putExtra("pais", juego);//utilizo la misma variable para leer la informacion
                        startActivity(intent);
                        overridePendingTransition(R.anim.desvanecer, R.anim.aparecer);
                    }else{
                        Intent intent = new Intent(InicioActivity.this, InstruccionesCookingFast.class);
                        intent.putExtra("pais", juego);//utilizo la misma variable para leer la informacion
                        startActivity(intent);
                        overridePendingTransition(R.anim.desvanecer, R.anim.aparecer);
                    }
                }
            }
        };
        // Asigna el listener a los botones
        botonFrancia.setOnClickListener(paisBotonClickListener);
        botonEspana.setOnClickListener(paisBotonClickListener);
        botonAlemania.setOnClickListener(paisBotonClickListener);
        botonInglaterra.setOnClickListener(paisBotonClickListener);
        botonEmparejalos.setOnClickListener(paisBotonClickListener);
        botonAdivina.setOnClickListener(paisBotonClickListener);
    }

    /**
     * Método que maneja los eventos de clic en los elementos del menú de la actividad de inicio.
     * Finaliza la aplicación al hacer clic en "Salir" y navega a la actividad del perfil de usuario al hacer clic en "Perfil de Usuario".
     * Utiliza animaciones de transición al cambiar a la actividad del perfil de usuario.
     * @param item Elemento del menú seleccionado.
     * @return `true` si se maneja el evento; `false` en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.P_Boton_IMG_SALIR) {
            finishAffinity();
            //finish();
            return true;
        } else if (id == R.id.P_Boton_Perfil_Usuario){

            Intent intentoPerfil = new Intent(InicioActivity.this, PerfilUsuario.class);
            startActivity(intentoPerfil);
            overridePendingTransition(R.anim.desvanecer, R.anim.aparecer);
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
        StorageReference referenciaFichero = myStorage.child("usuarios/" + user.getUid() + "/imagen_perfil.jpg");
        MenuItem itemPerfilUsuario = menu.findItem(R.id.P_Boton_Perfil_Usuario);
        final MenuTarget menuTarget = new MenuTarget(itemPerfilUsuario);
        if (referenciaFichero != null) {
            try {
                referenciaFichero.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.icon_perfil_usuario) // Icono predeterminado mientras se carga la imagen
                            .circleCrop() // recortar la imagen en forma circular
                            .into(menuTarget);

                }).addOnFailureListener(exception -> {
                    // Si ocurre un error, carga la imagen predeterminada desde el recurso drawable
                    Glide.with(this).load(R.drawable.icon_perfil_usuario).into(menuTarget);

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Verifica la clase de la actividad y oculta la opción de menú según sea necesario
        if (getClass() == InicioActivity.class) {
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