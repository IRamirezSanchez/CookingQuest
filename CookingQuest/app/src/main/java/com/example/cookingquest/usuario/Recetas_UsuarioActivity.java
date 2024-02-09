package com.example.cookingquest.usuario;

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
import com.example.cookingquest.GameActivityAdivina;
import com.example.cookingquest.InicioActivity;
import com.example.cookingquest.R;
import com.example.cookingquest.RecetaPrincipalActivity;
import com.example.cookingquest.RecetasPaisActivity;
import com.example.cookingquest.model.Pais;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public class Recetas_UsuarioActivity extends AppCompatActivity {
    private static TextView textoPais;
    private static String paisOrigen;
    public static final String recetaNombre="recetaNombre";//se utiliza en la activity3
    private LinearLayout linearLayout;
    private FirebaseFirestore myFireStore;
    private StorageReference myStorage;
    private FirebaseStorage storage;
    private FirebaseAuth myAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas_usuario);
        myAuth =  FirebaseAuth.getInstance();
        user= myAuth.getCurrentUser();

        paisOrigen = getIntent().getStringExtra(InicioActivity.pais);
        Pais nombrePais= Pais.valueOf(paisOrigen);

        //textoPais = findViewById(R.id.RP_PAIS);
        linearLayout = findViewById(R.id.linearLayoutRecetas);

        if(user != null) {
            obtenerRecetasFavoritas();
        }
        // =========================FIRESTORE ============================
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();

        myFireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = myFireStore.collection("recetario").document(nombrePais.toString().toLowerCase());

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
                                                Glide.with(Recetas_UsuarioActivity.this)
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
                                        Intent intent = new Intent(Recetas_UsuarioActivity.this, RecetaPrincipalActivity.class);
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
    private void obtenerRecetasFavoritas() {
        DocumentReference userRef = myFireStore.collection("usuarios").document(user.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Map<String, Object>> favoritos = (List<Map<String, Object>>) documentSnapshot.get("recetasFavoritas");
                if (favoritos != null) {
                    for (Map<String, Object> favorito : favoritos) {
                        String recetaId = (String) favorito.get("recetaId");
                        String pais = (String) favorito.get("pais");
                        agregarVistaReceta(recetaId, pais);
                    }
                }
            }
        });
    }

    private void agregarVistaReceta(String recetaId, String pais) {
        DocumentReference recetaRef = myFireStore.collection("recetario").document(pais).collection("recetas").document(recetaId);

        recetaRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    // Aquí construyes la vista de la receta con la información del documento
                    View recetaView = getLayoutInflater().inflate(R.layout.layout_receta, linearLayout, false);
                    TextView nombreRecetaTextView = recetaView.findViewById(R.id.RP_TEXT_1);
                    nombreRecetaTextView.setText(documentSnapshot.getString("nombre"));
                    // Puedes rellenar el resto de los campos de la vista de la misma manera

                    // Cargar la imagen usando Glide
                    String rutaImagen = "images_recetas/" + pais + "/" + recetaId + ".jpg";
                    ImageButton botonReceta = recetaView.findViewById(R.id.RP_BOTON_1);
                    Glide.with(Recetas_UsuarioActivity.this)
                            .load(rutaImagen)
                            .into(botonReceta);

                    // Añadir la vista al layout
                    linearLayout.addView(recetaView);
                } else {
                    Log.e(TAG, "La receta no existe en Firestore");
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflar menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cooquing_quest_menu, menu);
        //Control de botones a mostrar en cada actividad
        if (getClass() == Recetas_UsuarioActivity.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            if (itemToRemove != null) {
                menu.removeItem(itemToRemove.getItemId());
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //FALTARIA el 2 juego y lanzariamos la actividad
        if (id == R.id.P_Boton_IMG_EMPAREJA) {
            Intent emparejaIntent = new Intent(this, GameActivityAdivina.class);
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
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}