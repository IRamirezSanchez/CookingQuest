package com.example.cookingquest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.cookingquest.login.PerfilUsuario;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.HashMap;
import java.util.Locale;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RecetaPrincipalActivity extends AppCompatActivity {


    private FirebaseFirestore myFireStore;
    private StorageReference myStorage;
    private FirebaseStorage storage;
    private FirebaseAuth myAuth;
    private FirebaseUser user;
    private Button playPauseButton1, playPauseButton2, playPauseButton3, playPauseButton4, playPauseButton5;
    private TextToSpeech textToSpeech;
    private boolean isPlaying = false;
    private int whichButtonClicked;
    private ImageButton estrellaON, estrellaOFF;
    private String ingredientesConSaltos, preparacion_1ConSaltos, preparacion_2ConSaltos, preparacion_3ConSaltos, preparacion_4ConSaltos;
    private String recetaId,nombreReceta,categoriaReceta, racionesReceta, tiempoReceta , linkReceta;
    private Translator translator;
    private Button translateButton;
    private TextView categoriaTextView,ingredientesTextView;
    private boolean isTranslatedToSpanish = false;
    private boolean isSpainFlag = true;
    DownloadConditions conditions;
    private ImageView imagenSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receta_principal_activity_main);
        playPauseButton1 = findViewById(R.id.playPauseButton1);
        playPauseButton2 = findViewById(R.id.playPauseButton2);
        playPauseButton3 = findViewById(R.id.playPauseButton3);
        playPauseButton4 = findViewById(R.id.playPauseButton4);
        playPauseButton5 = findViewById(R.id.playPauseButton5);
        translateButton = findViewById(R.id.translateButton);
        ingredientesTextView= findViewById(R.id.RSTextIngredientes);
        estrellaON = findViewById(R.id.estrella_ON);
        estrellaOFF = findViewById(R.id.estrella_OFF);
        imagenSplash = findViewById(R.id.splashImageView);

        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();
        myFireStore = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        user = myAuth.getCurrentUser();
        conditions = new DownloadConditions.Builder().requireWifi().build();


        crearReceta();
        cargaSplash();
        setButtonClickListener(playPauseButton1, 1);
        setButtonClickListener(playPauseButton2, 2);
        setButtonClickListener(playPauseButton3, 3);
        setButtonClickListener(playPauseButton4, 4);
        setButtonClickListener(playPauseButton5, 5);


        // Inicializar el traductor y el reproductor
        inicializarTraductorYreproducir();




    }
    private void cargaSplash(){
        long timestamp = System.currentTimeMillis();
        String imageUrl = "https://source.unsplash.com/800x600/?cooking&t="+timestamp;
        Glide.with(this)
                .load(imageUrl)
                .override(800,600)
                .centerCrop()
                .into(imagenSplash);
    }
    //INSTANCIA LOS VALORES DE LOS ATRIBUTOS QUE CONTIENEN LAS RECETAS DEPENDIENDO DE LA RECETA SELECCIONADA
    private void crearReceta() {

        //Obtenemos información que el intent anterior envió a esta nueva actividad
        Intent intent = getIntent();
        if (intent != null) {
            // Recupera el nombre de la receta enviado desde MainActivity2
            //int recetaNombre = intent.getIntExtra(RecetasPaisActivity.recetaNombre,0);
            //FIRESTORE CARGA RECETA
            String[] nombreYPaisRecetaArray = intent.getStringExtra(RecetasPaisActivity.recetaNombre).split("-");
            String nombreRecetaIntent = nombreYPaisRecetaArray[0];
            String nombrePaisIntent = nombreYPaisRecetaArray[1];


            DocumentReference docRef = myFireStore.collection("recetario").document(nombrePaisIntent.toString().toLowerCase());

            docRef.collection("recetas").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Acceder a los datos de cada documento de receta
                                    if (document.getString("nombre").equalsIgnoreCase(nombreRecetaIntent)) {
                                        recetaId = document.getId();
                                        nombreReceta = document.getString("nombre");
                                        categoriaReceta = document.getString("categoria");
                                        racionesReceta = document.getString("raciones");
                                        tiempoReceta = document.getString("tiempo");
                                        String ingredientesReceta = document.getString("ingredientes");
                                        linkReceta = document.getString("link");
                                        String preparacion_1Receta = document.getString("preparacion_1");
                                        String preparacion_2Receta = document.getString("preparacion_2");
                                        String preparacion_3Receta = document.getString("preparacion_3");
                                        String preparacion_4Receta = document.getString("preparacion_4");

                                        //Verificar si es favorito
                                        verificarSiEsFavorito(recetaId, nombrePaisIntent);
                                        //INTRODUCIR CON STORAGE LA IMAGEN==================================================
                                        // Instanciamos una variable de un ImageView para poder hacer un set de la imagen que deseamos cambiar
                                        ImageView imagenRecetaImageView = findViewById(R.id.rs_imagenreceta);
                                        String rutaImagen = "images_recetas/" + nombrePaisIntent.toString().toLowerCase() + "/" + nombreRecetaIntent + ".jpg";
                                        myStorage.child(rutaImagen).getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Glide.with(RecetaPrincipalActivity.this)
                                                                .load(uri)
                                                                .into(imagenRecetaImageView);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Manejar errores al obtener la URL de la imagen
                                                        Log.e(TAG, "Error al obtener la URL de la imagen:", exception);
                                                    }
                                                });

                                        //INtroducir losdatos Interfaz grafica
                                        TextView nombreRecetaTextView = findViewById(R.id.rs_text_nombreReceta);
                                        nombreRecetaTextView.setText(nombreReceta);

                                        categoriaTextView = findViewById(R.id.rs_textCategoria);
                                        categoriaTextView.setText(categoriaReceta);

                                        TextView ingredientesTextView = findViewById(R.id.rs_ingredientesContent);
                                        // Transformo para la visualizacion Correcta de los ingredientes
                                        ingredientesConSaltos = ingredientesReceta.replaceAll("-", "\n");
                                        ingredientesTextView.setText(ingredientesConSaltos);

                                        TextView preparacion_1TextView = findViewById(R.id.rs_preparacion_1);
                                        preparacion_1ConSaltos = preparacion_1Receta.replaceAll("-", "\n");
                                        preparacion_1TextView.setText(preparacion_1ConSaltos);

                                        TextView preparacion_2TextView = findViewById(R.id.rs_preparacion_2);
                                        preparacion_2ConSaltos = preparacion_2Receta.replaceAll("-", "\n");
                                        preparacion_2TextView.setText(preparacion_2ConSaltos);

                                        TextView preparacion_3TextView = findViewById(R.id.rs_preparacion_3);
                                        preparacion_3ConSaltos = preparacion_3Receta.replaceAll("-", "\n");
                                        preparacion_3TextView.setText(preparacion_3ConSaltos);

                                        TextView preparacion_4TextView = findViewById(R.id.rs_preparacion_4);
                                        preparacion_4ConSaltos = preparacion_4Receta.replaceAll("-", "\n");
                                        preparacion_4TextView.setText(preparacion_4ConSaltos);

                                        TextView tiempoTextView = findViewById(R.id.rs_textTiempo);
                                        tiempoTextView.setText(tiempoReceta);
                                        //Obtenemos una referencia para saber a cual queremos meter el link
                                        ImageButton btn_link_ = findViewById(R.id.img_link);

                                        btn_link_.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //con este metodo indicamos el enlace que se abrirá cuando se haga click sobre el textView
                                                Uri uri = Uri.parse(linkReceta);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        });

                                        estrellaOFF.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                estrellaON.setVisibility(View.VISIBLE);
                                                estrellaOFF.setVisibility(View.INVISIBLE);
                                                agregarAFavoritos(recetaId, nombrePaisIntent.toLowerCase());
                                            }
                                        });
                                        estrellaON.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                estrellaON.setVisibility(View.INVISIBLE);
                                                estrellaOFF.setVisibility(View.VISIBLE);
                                                quitarDeFavoritos(recetaId, nombrePaisIntent.toLowerCase());
                                            }
                                        });
                                    } else {
                                        //ALERT DIALOG CONTROLANDO QUE NO SE HA PODIDO CARGAR LA RECETA VUELVE  INTENTARLO MAS TARDE...
                                    }
                                }

                            } else {
                                Log.e(TAG, "Error al obtener documentos de recetas:", task.getException());
                            }
                        }
                    });
        }
    }
    //////////////////////////////////////////////[AGREGAR o ELIMINAR DE FAVORITOS, VERIFICAR SI ES FAVORITO]/////////////////////////////////////////

    //Metodo para agregar a firebase en el cmapo de recetasFavoritas del usuario el id de la receta y el pais de la receta, en un map (Objeto)
    private void agregarAFavoritos(String recetaId, String pais) {

        if (user != null) {
            DocumentReference userRef = myFireStore.collection("usuarios").document(user.getUid());

            Map<String, Object> favorito = new HashMap<>();
            favorito.put("recetaId", recetaId);
            favorito.put("pais", pais);

            // Agrega el objeto de la receta a la lista de recetas favoritas del usuario
            userRef.update("recetasFavoritas", FieldValue.arrayUnion(favorito))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Receta agregada a favoritos correctamente.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error al agregar receta a favoritos", e);
                        }
                    });
        }
    }

    private void quitarDeFavoritos(String recetaId, String pais) {
        if (user != null) {
            DocumentReference userRef = myFireStore.collection("usuarios").document(user.getUid());

            Map<String, Object> favorito = new HashMap<>();
            favorito.put("recetaId", recetaId);
            favorito.put("pais", pais);

            // Elimina el objeto de la receta de la lista de recetas favoritas del usuario
            userRef.update("recetasFavoritas", FieldValue.arrayRemove(favorito))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Receta eliminada de favoritos correctamente.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error al eliminar receta de favoritos", e);
                        }
                    });
        }
    }

    private void verificarSiEsFavorito(String recetaId, String pais) {
        DocumentReference userRef = myFireStore.collection("usuarios").document(user.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Map<String, Object>> favoritos = (List<Map<String, Object>>) documentSnapshot.get("recetasFavoritas");
                if (favoritos != null) {
                    for (Map<String, Object> favorito : favoritos) {
                        if (recetaId.equals(favorito.get("recetaId")) && pais.toLowerCase().equals(favorito.get("pais"))) {
                            estrellaOFF.setVisibility(View.INVISIBLE);
                            estrellaON.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                }
                estrellaON.setVisibility(View.INVISIBLE);
                estrellaOFF.setVisibility(View.VISIBLE);
            }
        });
    }

    /////////////////////////////////////////////[TEXT TO SPEECH]/////////////////////////////////////////////////

    private void setButtonClickListener(Button button, final int buttonNumber) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Button clickedButton = (Button) view;
                whichButtonClicked = buttonNumber; // Actualiza el botón clicado antes de togglePlayPause
                if (isPlaying && whichButtonClicked != buttonNumber) {
                    stopSpeech();
                    updatePlayPauseButtonIcon(whichButtonClicked);
                } else {
                    togglePlayPause();
                }
                updatePlayPauseButtonIcon(whichButtonClicked); // Actualiza el icono después de togglePlayPause
            }
        });
    }

    private void stopSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        isPlaying = false; // Actualiza el estado después de detener la reproducción
    }

    private void togglePlayPause() {
        if (isPlaying) {
            pauseSpeech();
        } else {
            playSpeech();
        }
    }

    private void playSpeech() {
        isPlaying = true;
        speakCurrentText();
    }

    private void pauseSpeech() {
        isPlaying = false;
        stopSpeech();
    }

    private void speakCurrentText() {
        String textToSpeak = getCurrentText();
        if (textToSpeak != null && !textToSpeak.isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ReproducirTexto");
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, params);
        }
    }

    private String getCurrentText() {
        String currentText = "";
        switch (whichButtonClicked) {
            case 1:
                currentText = ingredientesConSaltos;
                break;
            case 2:
                currentText = preparacion_1ConSaltos;
                break;
            case 3:
                currentText = preparacion_2ConSaltos;
                break;
            case 4:
                currentText = preparacion_3ConSaltos;
                break;
            case 5:
                currentText = preparacion_4ConSaltos;
                break;
        }
        return currentText;
    }

    private void updatePlayPauseButtonIcon(int buttonNumber) {
        int iconResourceId = isPlaying ? R.drawable.icons_no_audio : R.drawable.icons_audio;
        int iconResourceId2 = R.drawable.icons_audio;
        switch (buttonNumber) {
            case 1:
                playPauseButton1.setCompoundDrawablesWithIntrinsicBounds(iconResourceId, 0, 0, 0);
                playPauseButton2.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2,0, 0, 0);
                playPauseButton3.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton4.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton5.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                break;
            case 2:
                playPauseButton1.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton2.setCompoundDrawablesWithIntrinsicBounds(iconResourceId, 0, 0, 0);
                playPauseButton3.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton4.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton5.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                break;
            case 3:
                playPauseButton1.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton2.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton3.setCompoundDrawablesWithIntrinsicBounds(iconResourceId, 0, 0, 0);
                playPauseButton4.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton5.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                break;
            case 4:
                playPauseButton1.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton2.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton3.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton4.setCompoundDrawablesWithIntrinsicBounds(iconResourceId, 0, 0, 0);
                playPauseButton5.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                break;
            case 5:
                playPauseButton1.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton2.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton3.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton4.setCompoundDrawablesWithIntrinsicBounds(iconResourceId2, 0, 0, 0);
                playPauseButton5.setCompoundDrawablesWithIntrinsicBounds(iconResourceId, 0, 0, 0);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    ///////////////////////////////////////////////[TRANSLATE]///////////////////////////////////////////////

    private void translateText() {
        // Agrega las condiciones de descarga para asegurarse de que el modelo esté disponible


        // Descarga el modelo si es necesario
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // El modelo está disponible, realiza la traducción de los ingredientes
                        if (isTranslatedToSpanish) {
                            translateIngredientsToEnglish();
                        } else {
                            translateIngredientsToSpanish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al descargar el modelo
                        Log.e(TAG, "Error al descargar el modelo de traducción: " + e.getMessage());
                    }
                });
    }

    private void translateIngredientsToEnglish() {
        String originalText = ingredientesConSaltos;
        translator.translate(originalText)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        // Actualiza el texto traducido en la interfaz de usuario
                        TextView ingredientesTextView = findViewById(R.id.rs_ingredientesContent);
                        ingredientesTextView.setText(translatedText);
                        // Después de traducir los ingredientes, traduce las preparaciones
                        translatePreparationStepsToEnglish();
                        isTranslatedToSpanish = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al traducir los ingredientes
                        Log.e(TAG, "Error al traducir ingredientes: " + e.getMessage());
                    }
                });
    }

    private void translateIngredientsToSpanish() {
        // Restaura el texto original en español para los ingredientes
        TextView ingredientesTextView = findViewById(R.id.rs_ingredientesContent);
        ingredientesTextView.setText(ingredientesConSaltos);

        // Restaura el texto original en español para las preparaciones
        translatePreparationStepsToSpanish();
        isTranslatedToSpanish = true;
    }

    private void translatePreparationStepsToEnglish() {
        // Traduce cada preparación al inglés
        translatePreparationStepToEnglish(preparacion_1ConSaltos, R.id.rs_preparacion_1);
        translatePreparationStepToEnglish(preparacion_2ConSaltos, R.id.rs_preparacion_2);
        translatePreparationStepToEnglish(preparacion_3ConSaltos, R.id.rs_preparacion_3);
        translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);

        translatePreparationStepToEnglish(categoriaReceta, R.id.rs_textCategoria);
        translatePreparationStepToEnglish(getResources().getString(R.string.ingredientes), R.id.RSTextIngredientes);
        //translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
//
        //translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToEnglish(preparacion_4ConSaltos, R.id.rs_preparacion_4);


    }

    private void translatePreparationStepsToSpanish() {
        // Restaura el texto original en español para cada preparación
        translatePreparationStepToSpanish(preparacion_1ConSaltos, R.id.rs_preparacion_1);
        translatePreparationStepToSpanish(preparacion_2ConSaltos, R.id.rs_preparacion_2);
        translatePreparationStepToSpanish(preparacion_3ConSaltos, R.id.rs_preparacion_3);
        translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);

        translatePreparationStepToSpanish(categoriaReceta, R.id.rs_textCategoria);
        translatePreparationStepToSpanish(getResources().getString(R.string.ingredientes), R.id.RSTextIngredientes);

        //translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
//
        //translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);
        //translatePreparationStepToSpanish(preparacion_4ConSaltos, R.id.rs_preparacion_4);

    }

    private void translatePreparationStepToEnglish(String originalText, final int textViewId) {
        translator.translate(originalText)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        // Actualiza el texto traducido en la interfaz de usuario
                        TextView preparacionTextView = findViewById(textViewId);
                        preparacionTextView.setText(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al traducir
                        Log.e(TAG, "Error al traducir la preparación: " + e.getMessage());
                    }
                });
    }

    private void translatePreparationStepToSpanish(String originalText, final int textViewId) {
        // Restaura el texto original en español para cada preparación
        TextView preparacionTextView = findViewById(textViewId);
        preparacionTextView.setText(originalText);
    }


    private void inicializarTraductorYreproducir() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale spanish = new Locale("es", "ES");
                    //Locale ingles = new Locale("en", "GD");
                    //Locale aleman= new Locale("de");
                    //Locale frances = new Locale("fr");
                    textToSpeech.setLanguage(spanish);
                }
            }
        });
        ///////////////////////////////////////////////[TRANSLATE]///////////////////////////////////////////////
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.SPANISH) // Establece el idioma de origen en español
                        .setTargetLanguage(TranslateLanguage.ENGLISH) // Establece el idioma de destino en inglés
                        .build();
        translator = Translation.getClient(options);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSpainFlag) {
                    translateButton.setBackgroundResource(R.drawable.inglaterra_bandera_3d);
                } else {
                    translateButton.setBackgroundResource(R.drawable.spain_bandera_3d);
                }
                isSpainFlag = !isSpainFlag;
                translateText();
            }
        });
    }

    /////////////////////////////////////////////[MENU TOOLBAR]/////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cooquing_quest_menu, menu);
        if (getClass() == RecetaPrincipalActivity.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            if (itemToRemove != null) {
                menu.removeItem(itemToRemove.getItemId());
            }
        }
        //getMenuInflater().inflate(R.menu.test_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        } else if (id == R.id.P_Boton_Perfil_Usuario) {
            Intent adivinaIntent = new Intent(this, PerfilUsuario.class);
            startActivity(adivinaIntent);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}