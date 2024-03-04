package com.example.cookingquest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingquest.login.PerfilUsuario;
import com.example.cookingquest.model.Receta;
import com.example.cookingquest.model.Repositorios_Recetas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class GameActivityAdivina extends AppCompatActivity {
    private ImageView imageView;
    private Button buttonOption1, buttonOption2, buttonOption3, buttonOption4;
    private Animation mBtnAnim;
    private FirebaseAuth myAuth;
    private Long puntosC;
    private FirebaseUser user;
    private FirebaseFirestore myFireStore;
    private ArrayList<Integer> imagenesUtilizadas = new ArrayList<>();
    private List<Receta> recetasList= new ArrayList<>();
    private Receta recetaSeleccionada=null;
    private Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_adivina_main);
        myAuth = FirebaseAuth.getInstance();
        user = myAuth.getCurrentUser();
        myFireStore = FirebaseFirestore.getInstance();
        imagenesUtilizadas.clear();
        cargarDatos(0L);
        crearJuego();
    }

    /**
     * Método que configura y muestra el juego Cooking Fast, presentando al usuario opciones de recetas y verificando
     * si la respuesta seleccionada es correcta. Además, actualiza la puntuación del usuario según el desempeño en el juego.
     */
    private void crearJuego() {
        // Inicializar vistas y animaciones
        imageView = findViewById(R.id.imageView);
        buttonOption1 = findViewById(R.id.buttonOption1);
        buttonOption2 = findViewById(R.id.buttonOption2);
        buttonOption3 = findViewById(R.id.buttonOption3);
        buttonOption4 = findViewById(R.id.buttonOption4);
        mBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.boton_anim);
        Repositorios_Recetas.initializeRecetas(this);

        try {
            // Obtiene la lista de recetas desde Repositorios_Recetas
            recetasList = Repositorios_Recetas.getRecetas();

            if (recetasList.size() >= 4) {
                // Baraja las recetas obtenida anteriormente
                Collections.shuffle(recetasList);


                do {
                    // Seleccionar aleatoriamente una de las recetas
                    recetaSeleccionada = recetasList.get(new Random().nextInt(recetasList.size()));
                } while (imagenesUtilizadas.contains(recetaSeleccionada.getImagen_receta()));// Selecciona aleatoriamente una de las recetas

                imagenesUtilizadas.add(recetaSeleccionada.getImagen_receta());

                int nombreReceta = recetaSeleccionada.getNombre_receta();
                int imageResourceId = recetaSeleccionada.getImagen_receta();

                // Selecciona aleatoriamente una posicion para colocar la opcion correcta
                int posicionNombreReceta = new Random().nextInt(4);

                imageView.setImageResource(imageResourceId);

                ArrayList<Integer> opcionesUsadas = new ArrayList<>(); // Lista para almacenar opciones ya asignadas

                // Configura el nombreReceta en uno de los botones de manera aleatoria
                switch (posicionNombreReceta) {
                    case 0:
                        buttonOption1.setText(nombreReceta);
                        opcionesUsadas.add(nombreReceta);
                        break;
                    case 1:
                        buttonOption2.setText(nombreReceta);
                        opcionesUsadas.add(nombreReceta);
                        break;
                    case 2:
                        buttonOption3.setText(nombreReceta);
                        opcionesUsadas.add(nombreReceta);
                        break;
                    case 3:
                        buttonOption4.setText(nombreReceta);
                        opcionesUsadas.add(nombreReceta);
                        break;
                }

                // Configura los otros botones con los nombres de recetas restantes
                ArrayList<Receta> recetasRestantes = new ArrayList<>(recetasList);
                recetasRestantes.remove(recetaSeleccionada); // Elimina la receta ya utilizada

                for (int i = 0; i < 4; i++) {
                    if (i != posicionNombreReceta) {
                        int index = new Random().nextInt(recetasRestantes.size());
                        Receta recetaActual = recetasRestantes.get(index);
                        recetasRestantes.remove(recetaActual);

                        // Verifica si la opción ya se utilizó y elige otra si es necesario
                        int opcionActual = recetaActual.getNombre_receta();
                        while (opcionesUsadas.contains(opcionActual)) {
                            index = new Random().nextInt(recetasRestantes.size());
                            recetaActual = recetasRestantes.get(index);
                            opcionActual = recetaActual.getNombre_receta();
                        }

                        opcionesUsadas.add(opcionActual);

                        switch (i) {
                            case 0:
                                buttonOption1.setText(opcionActual);
                                break;
                            case 1:
                                buttonOption2.setText(opcionActual);
                                break;
                            case 2:
                                buttonOption3.setText(opcionActual);
                                break;
                            case 3:
                                buttonOption4.setText(opcionActual);
                                break;
                        }
                    }
                }
                // correctOptionIndex es la posición del nombreReceta
                final int correctOptionIndex = posicionNombreReceta;

                buttonOption1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonOption1.startAnimation(mBtnAnim);
                        verificarRespuesta(recetasList.get(correctOptionIndex).getNombre_receta(), recetasList.get(0).getNombre_receta());
                    }
                });

                buttonOption2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonOption2.startAnimation(mBtnAnim);
                        verificarRespuesta(recetasList.get(correctOptionIndex).getNombre_receta(), recetasList.get(1).getNombre_receta());
                    }
                });

                buttonOption3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonOption3.startAnimation(mBtnAnim);
                        verificarRespuesta(recetasList.get(correctOptionIndex).getNombre_receta(), recetasList.get(2).getNombre_receta());
                    }
                });

                buttonOption4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonOption4.startAnimation(mBtnAnim);
                        verificarRespuesta(recetasList.get(correctOptionIndex).getNombre_receta(), recetasList.get(3).getNombre_receta());
                    }
                });
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            mostrarError("Error al cargar las recetas.");
        }
    }

    /**
     * Método que carga y actualiza los datos del usuario, especialmente los puntos obtenidos en el juego actual.
     * @param puntosEntrada Puntos a agregar al total del usuario.
     */
    private void cargarDatos(Long puntosEntrada) {
        DocumentReference docRef = myFireStore.collection("usuarios").document(user.getUid());
        if (user != null) {

            docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    puntosC = value.getLong("Puntos");

                }
            });
            if (puntosEntrada != 0) {
                Long puntosNuevos = puntosC + puntosEntrada;
                docRef.update("Puntos", puntosNuevos)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                puntosC=puntosNuevos;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mostrarError("Error al cargar los puntos.");
                            }
                        });
            }
        }
    }

    /**
     * Método que verifica si la respuesta seleccionada por el usuario es correcta y muestra un diálogo con el resultado.
     * @param respuestaSeleccionada La respuesta seleccionada por el usuario.
     * @param respuestaCorrecta     La respuesta correcta.
     */
    private void verificarRespuesta(int respuestaSeleccionada, int respuestaCorrecta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cocina, null);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        TextView messagePuntuacion = dialogView.findViewById(R.id.puntuacionTotal);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        if (respuestaSeleccionada == respuestaCorrecta) {
            cargarDatos(5L);
            titleTextView.setText("¡Respuesta Correcta!");
            messageTextView.setText("Has ganado 5 puntos");
            messagePuntuacion.setText("Puntos Totales: "+(puntosC+5L));

            positiveButton.setText("CONTINUAR");
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagenesUtilizadas.size() == 17) {
                        imagenesUtilizadas.clear();
                        crearJuego();
                    } else {
                        crearJuego();
                    }
                    // Cerrar el diálogo si es necesario
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            });
        } else {
            titleTextView.setText("Respuesta Incorrecta");
            messageTextView.setText("¡Inténtalo de nuevo!");
            messagePuntuacion.setText("Puntos Totales: "+puntosC);

            positiveButton.setText("REINTENTAR");
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hacer algo si es necesario
                    // Cerrar el diálogo si es necesario
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            });
        }

        negativeButton.setText("SALIR");
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alertDialog = builder.create(); // Asignar el AlertDialog creado a la variable miembro alertDialog
        alertDialog.show();
    }

    /**
     * Método que muestra un diálogo de error con el mensaje proporcionado.
     * @param mensaje El mensaje de error a mostrar.
     */
    private void mostrarError(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    /**
     * Método que infla el menú en la barra de acción, controlando la visibilidad de las opciones según la orientación
     * de la pantalla y la clase de la actividad.
     * @param menu El menú de la barra de acción.
     * @return true si se infla el menú correctamente, false en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int orientation = getResources().getConfiguration().orientation;
        //controlamos que cuadno se gira la pantalla no se muestre el actionBar para que se vea  mejor el juego.
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.cooquing_quest_menu, menu);
        }else{
            getSupportActionBar().hide();
        }
        // Verifica la clase de la actividad y oculta la opción de menú según sea necesario
        if (getClass() == GameActivityAdivina.class) {
            MenuItem itemToRemove1 = menu.findItem(R.id.P_Boton_IMG_ADIVINA);
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            if (itemToRemove1 != null || itemToRemove != null) {
                menu.removeItem(itemToRemove1.getItemId());
                menu.removeItem(itemToRemove.getItemId());
            }
        }
        return true;
    }


    /**
     * Método que maneja los eventos de clic en los elementos del menú de la barra de acción.
     * @param item El elemento del menú que se ha seleccionado.
     * @return true si el evento se maneja correctamente, false en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.P_Boton_IMG_INICIO) {
            Intent adivinaIntent = new Intent(this, InicioActivity.class);
            startActivity(adivinaIntent);
            finish();
            return true;
        }else if (id == R.id.P_Boton_Perfil_Usuario) {
            Intent adivinaIntent = new Intent(this, PerfilUsuario.class);
            startActivity(adivinaIntent);
            finish();
            return true;
        }else if (id == R.id.P_Boton_IMG_EMPAREJA) {
            Intent emparejaIntent = new Intent(this, GameActivityCookingFast.class);
            startActivity(emparejaIntent);
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

}