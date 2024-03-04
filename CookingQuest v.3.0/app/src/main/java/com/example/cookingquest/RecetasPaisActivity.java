package com.example.cookingquest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cookingquest.login.PerfilUsuario;
import com.example.cookingquest.model.Pais;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RecetasPaisActivity extends AppCompatActivity {

    private static TextView textoPais;
    private static String paisOrigen;
    public static final String recetaNombre="recetaNombre";//se utiliza en la activity3
    private FirebaseFirestore myFireStore;
    private StorageReference myStorage;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recetas_pais_activity_main);
        crearReceta();
    }
    /**
     * Crea dinámicamente vistas de recetas en la interfaz basándose en los datos almacenados
     * en Firestore. Incluyen detalles como nombre, categoría, tiempo y raciones de cada receta.
     * Además, se carga la imagen de cada receta desde Firebase Storage.
     * Las recetas se obtienen del subconjunto de recetas asociadas al país de origen recibido.
     * Al hacer clic en una receta, se inicia la actividad de la receta principal con detalles adicionales.
     */
    private void crearReceta(){
        // Obtener el país de origen de la actividad anterior
        paisOrigen = getIntent().getStringExtra(InicioActivity.pais);
        Pais nombrePais= Pais.valueOf(paisOrigen);
        // Obtener referencias a los elementos de la interfaz de usuario
        textoPais = findViewById(R.id.RP_PAIS);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        // =========================FIRESTORE ============================
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();

        myFireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = myFireStore.collection("recetario").document(nombrePais.toString().toLowerCase());
        // Obtener las recetas de Firestore y generar vistas dinámicamente
        docRef.collection("recetas").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Acceder a los datos de cada documento de receta
                                String nombreReceta = document.getString("nombre");
                                String categoriaReceta = document.getString("categoria");
                                String racionesReceta = document.getString("raciones");
                                String tiempoReceta = document.getString("tiempo");


                                textoPais.setText(String.valueOf(nombrePais));
                                //Creamos item de cada receta automatico de todas las recetas por cada pais asi cuando queramos incluir mas se autorellenan
                                View recetaView = getLayoutInflater().inflate(R.layout.layout_receta, null);

                                //VAMOS INCLUYENDO CADA BLOQUE DE INFORMACION DEL layout_receta en la actividad que estamos

                                //INTRODUCIR CON STORAGE LA IMAGEN==================================================
                                ImageButton botonReceta = recetaView.findViewById(R.id.RP_BOTON_1);

                                String rutaImagen = "images_recetas/" + nombrePais.toString().toLowerCase() + "/" + nombreReceta + ".jpg";
                                myStorage.child(rutaImagen).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(RecetasPaisActivity.this)
                                                        .load(uri)
                                                        .into(botonReceta);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Manejar errores al obtener la URL de la imagen
                                                Log.e(TAG, "Error al obtener la URL de la imagen:", exception);
                                            }
                                        });



                                //Controlamos con el inear que pueda pulsar en cualquier parte.
                                LinearLayout linearReceta =recetaView.findViewById(R.id.linearReceta);
                                //Nos creamos el objeto view.OnclickListener para modificarlo despues, dependiendo cual pulse.
                                View.OnClickListener onClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Cuando se hace clic en la receta, creamos un Intent y lo enviamos a la nueva actividad
                                        Intent intent = new Intent(RecetasPaisActivity.this, RecetaPrincipalActivity.class);
                                        // detalles de la receta como extras en el Intent
                                        String nombreYPaisReceta= nombreReceta+ "-"+nombrePais;
                                        intent.putExtra("recetaNombre", nombreYPaisReceta);
                                        startActivity(intent);
                                    }
                                };
                                //de esta manera ejecuto el intent con los componentes que deseo.
                                // Pulsar cualquier zona de la receta para acceder a ella.
                                linearReceta.setOnClickListener(onClickListener);
                                botonReceta.setOnClickListener(onClickListener);

                                TextView tituloReceta = recetaView.findViewById(R.id.RP_TEXT_1);
                                tituloReceta.setText(nombreReceta);

                                TextView categoria = recetaView.findViewById(R.id.categoria_desa1);
                                categoria.setText(categoriaReceta);

                                TextView tiempo = recetaView.findViewById(R.id.tiempo_desa1);
                                tiempo.setText(tiempoReceta);

                                TextView raciones = recetaView.findViewById(R.id.raciones_desa1);
                                raciones.setText(racionesReceta);

                                // Agregar la vista de receta al diseño lineal
                                linearLayout.addView(recetaView);
                                View espaciosView=getLayoutInflater().inflate(R.layout.espacios,null);
                                linearLayout.addView(espaciosView);

                            }
                        } else {
                            Log.e(TAG, "Error al obtener documentos de recetas:", task.getException());
                        }
                    }
                });

    }

    /**
     * Infla el menú de opciones en la barra de acción. Dependiendo de la actividad actual,
     * algunos botones pueden ser removidos del menú. En RecetasPaisActivity, se elimina
     * el botón de salir (P_Boton_IMG_SALIR).
     * @param menu El menú en el que se inflarán los elementos de la barra de acción.
     * @return Devuelve `true` para mostrar el menú, `false` de lo contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       //Inflar menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cooquing_quest_menu, menu);
        //Control de botones a mostrar en cada actividad
        if (getClass() == RecetasPaisActivity.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            if (itemToRemove != null) {
                menu.removeItem(itemToRemove.getItemId());
            }
        }
        return true;
    }

    /**
     * Maneja los eventos de clic en los elementos del menú de opciones. Dependiendo del elemento
     * seleccionado, inicia la actividad correspondiente. Si la actividad actual es RecetasPaisActivity,
     * se maneja la eliminación del botón de salir en este método.
     * @param item El elemento del menú que fue seleccionado.
     * @return Devuelve `true` si el evento ha sido manejado, `false` de lo contrario.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //FALTARIA el 2 juego y lanzariamos la actividad
        if (id == R.id.P_Boton_IMG_EMPAREJA) {
            Intent emparejaIntent = new Intent(this, GameActivityCookingFast.class);
            startActivity(emparejaIntent);
            finish();
            return true;
        } else if (id == R.id.P_Boton_IMG_ADIVINA) {
            Intent adivinaIntent = new Intent(this, GameActivityAdivina.class);
            startActivity(adivinaIntent);
            finish();
            return true;
        } else if (id == R.id.P_Boton_IMG_INICIO) {
            Intent adivinaIntent = new Intent(this, InicioActivity.class);
            startActivity(adivinaIntent);

            return true;
        }else if (id == R.id.P_Boton_Perfil_Usuario) {
            Intent adivinaIntent = new Intent(this, PerfilUsuario.class);
            startActivity(adivinaIntent);

            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

    }

}