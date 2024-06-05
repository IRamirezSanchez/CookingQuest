package com.example.cookingquest.usuario;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
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
        user = myAuth.getCurrentUser();

        linearLayout = findViewById(R.id.linearLayout);
        storage = FirebaseStorage.getInstance();
        myFireStore = FirebaseFirestore.getInstance();

        // Obtener las recetas favoritas y agregar las vistas de recetas correspondientes
        if(user != null) {
            obtenerRecetasFavoritas();
        }

    }

    /**
     * Obtiene y muestra las recetas favoritas del usuario desde Firestore.
     */
    private void obtenerRecetasFavoritas() {
        // Obtener la referencia del usuario en Firestore
        DocumentReference userRef = myFireStore.collection("usuarios").document(user.getUid());
        // Obtener las recetas favoritas del usuario
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Map<String, Object>> favoritos = (List<Map<String, Object>>) documentSnapshot.get("recetasFavoritas");
                // Verificar si hay recetas favoritas
                if (favoritos != null) {
                    for (Map<String, Object> favorito : favoritos) {
                        // Iterar sobre cada receta favorita y agregarla a la vista
                        String recetaId = (String) favorito.get("recetaId");
                        String pais = (String) favorito.get("pais");
                        agregarVistaReceta(recetaId, pais);
                    }
                }
            }
        });
    }

    /**
     * Agrega una vista de receta a la interfaz de usuario.
     * @param recetaId Identificador de la receta en Firestore.
     * @param pais     País al que pertenece la receta.
     */
    private void agregarVistaReceta(String recetaId, String pais) {
        // Obtener la referencia de la receta en Firestore
        DocumentReference recetaRef = myFireStore.collection("recetario").document(pais)
                .collection("recetas").document(recetaId);
        recetaRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Verificar si la receta existe en Firestore
                if(documentSnapshot.exists()) {
                    // Construir la vista de la receta con la información del documento
                    View recetaView = getLayoutInflater().inflate(R.layout.layout_receta, linearLayout, false);
                    TextView nombreRecetaTextView = recetaView.findViewById(R.id.RP_TEXT_1);
                    nombreRecetaTextView.setText(documentSnapshot.getString("nombre"));
                    TextView categoria = recetaView.findViewById(R.id.categoria_desa1);
                    categoria.setText(documentSnapshot.getString("categoria"));
                    TextView tiempo = recetaView.findViewById(R.id.tiempo_desa1);
                    tiempo.setText(documentSnapshot.getString("tiempo"));
                    TextView raciones = recetaView.findViewById(R.id.raciones_desa1);
                    raciones.setText(documentSnapshot.getString("raciones"));

                    String nombreReceta = documentSnapshot.getString("nombre");
                    System.out.println(nombreReceta);
                    // Cargar la imagen usando Glide
                    ImageButton botonReceta = recetaView.findViewById(R.id.RP_BOTON_1);
                    String rutaImagen = "images_recetas/" + pais + "/" + nombreReceta + ".jpg";
                    StorageReference storageRef = storage.getReference().child(rutaImagen);

                    // Obtener la URI de la imagen para cargarla con Glide
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Cargar la imagen usando Glide
                            ImageButton botonReceta = recetaView.findViewById(R.id.RP_BOTON_1);
                            Glide.with(Recetas_UsuarioActivity.this)
                                    .load(uri)
                                    .into(botonReceta);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar errores al obtener la URL de la imagen
                            Log.e(TAG, "Error al obtener la URL de la imagen:", e);
                        }
                    });


                    // Añadir el OnClickListener para abrir la actividad de detalles de la receta
                    LinearLayout linearReceta = recetaView.findViewById(R.id.linearReceta);
                    linearReceta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Cuando se hace clic en la receta, creamos un Intent y lo enviamos a la nueva actividad
                            Intent intent = new Intent(Recetas_UsuarioActivity.this, RecetaPrincipalActivity.class);
                            // detalles de la receta como extras en el Intent
                            String nombreYPaisReceta = documentSnapshot.getString("nombre") + "-" + pais;
                            intent.putExtra("recetaNombre", nombreYPaisReceta);
                            startActivity(intent);
                        }
                    });
                    botonReceta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Cuando se hace clic en la receta, creamos un Intent y lo enviamos a la nueva actividad
                            Intent intent = new Intent(Recetas_UsuarioActivity.this, RecetaPrincipalActivity.class);
                            // detalles de la receta como extras en el Intent
                            String nombreYPaisReceta = documentSnapshot.getString("nombre") + "-" + pais;
                            intent.putExtra("recetaNombre", nombreYPaisReceta);
                            startActivity(intent);
                        }
                    });

                    // Añadir la vista al layout
                    linearLayout.addView(recetaView);
                } else {
                    Log.e(TAG, "La receta no existe en Firestore");
                }
            }
        });
    }

    /**
     * Infla el menú en la barra de acción.
     * @param menu El menú de la barra de acción.
     * @return Devuelve true para mostrar el menú, false en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflar menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cooquing_quest_menu, menu);
        //Control de botones a mostrar en cada actividad
        if (getClass() == Recetas_UsuarioActivity.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            MenuItem itemToRemove2 = menu.findItem(R.id.P_Boton_Perfil_Usuario);
            if (itemToRemove != null || itemToRemove2 != null) {
                menu.removeItem(itemToRemove.getItemId());
                menu.removeItem(itemToRemove2.getItemId());
            }
        }
        return true;
    }

    /**
     * Maneja los eventos de clic en los elementos del menú de la barra de acción.
     * @param item El elemento del menú seleccionado.
     * @return Devuelve true si el evento fue manejado, false en caso contrario.
     */
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
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
