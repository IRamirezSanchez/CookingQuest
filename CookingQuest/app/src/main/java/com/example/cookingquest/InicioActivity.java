package com.example.cookingquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.cookingquest.login.PerfilUsuario;
import com.example.cookingquest.model.Pais;

public class InicioActivity extends AppCompatActivity {

    public static final String pais="pais";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_activity_main);


        ImageButton botonFrancia = findViewById(R.id.P_BotonIMG_FRA);
        ImageButton botonAlemania = findViewById(R.id.P_BotonIMG_ALE);
        ImageButton botonEspana = findViewById(R.id.P_BotonIMG_ESP);
        ImageButton botonInglaterra = findViewById(R.id.P_BotonIMG_ING);

        ImageButton botonEmparejalos = findViewById(R.id.P_Boton_IMG_EMPAREJA);
        ImageButton botonAdivina = findViewById(R.id.P_Boton_IMG_ADIVINA);



        //se utiliza para transmitir datos entre las actividades en función de los botones que se han hecho clic.
        View.OnClickListener paisBotonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    juego = "Emparejalos";
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
                    Intent intent = new Intent(InicioActivity.this, GameActivityAdivina.class);
                    intent.putExtra("pais", juego);//utilizo la misma variable para leer la informacion
                    startActivity(intent);
                    overridePendingTransition(R.anim.desvanecer, R.anim.aparecer);


                }
            }
        };

        botonFrancia.setOnClickListener(paisBotonClickListener);
        botonEspana.setOnClickListener(paisBotonClickListener);
        botonAlemania.setOnClickListener(paisBotonClickListener);
        botonInglaterra.setOnClickListener(paisBotonClickListener);

        botonEmparejalos.setOnClickListener(paisBotonClickListener);
        botonAdivina.setOnClickListener(paisBotonClickListener);



    }

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