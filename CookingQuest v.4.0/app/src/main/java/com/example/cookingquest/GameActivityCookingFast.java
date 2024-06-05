package com.example.cookingquest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingquest.login.PerfilUsuario;
import com.example.cookingquest.usuario.MenuTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;

public class GameActivityCookingFast extends AppCompatActivity implements SensorEventListener {
    private ImageView comidaImageview;
    private ImageView hornillaImageView;
    private boolean isCooking = true;
    private int cookingState = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Button restart_btn;
    private ImageButton patatas_btn;
    private ImageButton filete_btn;
    private ImageButton san_btn;
    private Thread cookingThread; // Hilo de cocción
    private int currentFood = R.drawable.invisible; // Imagen por defecto
    private String nameFood;
    Dialog alertDialog;
    private TextView puntosC;
    private FirebaseAuth myAuth;
    private FirebaseUser user;
    private FirebaseFirestore myFireStore;
    private StorageReference myStorage;
    private FirebaseStorage storage;
    private Long puntos;
    private Long puntosTotales;
    private boolean movimientoDetectado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_cooking_fast);
        hornillaImageView = findViewById(R.id.hornilla_img);
        comidaImageview = findViewById(R.id.food_img);
        restart_btn = findViewById(R.id.reiniciar_btn);
        patatas_btn = findViewById(R.id.patatas_btn);
        filete_btn = findViewById(R.id.filete_btn);
        san_btn = findViewById(R.id.san_btn);
        nameFood = "invisible";
        myAuth = FirebaseAuth.getInstance();
        user = myAuth.getCurrentUser();
        puntos=0L;
        myFireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();
        movimientoDetectado=false;
        cargarPuntos();
        // Establecer un onClickListener para el botón de reinicio
        restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarbtn();
            }
        });
        // Establecer un onClickListener para el botón de patatas
        patatas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar la comida actual a patatas
                if (currentFood == R.drawable.invisible) {
                    currentFood = R.drawable.patatas_crudas;
                    nameFood = "Patatas";
                } else {
                    currentFood = R.drawable.patatas_crudas;
                    nameFood = "Patatas";
                    //cambiar_img_pat(); // Cambiar la imagen de inmediato
                    reiniciarbtn();
                }
            }
        });
        // Establecer un onClickListener para el botón de sándwich
        san_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFood == R.drawable.invisible) {
                    currentFood = R.drawable.sandwich_crudo_as;
                    nameFood = "Sandwich";
                } else {
                    currentFood = R.drawable.sandwich_crudo_as;
                    nameFood = "Sandwich";
                    // cambiar_img_san(); // Cambiar la imagen de inmediato
                    reiniciarbtn();
                }

            }
        });
        // Establecer un onClickListener para el botón de filete
        filete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFood == R.drawable.invisible) {
                    // Cambiar la comida actual a filete
                    currentFood = R.drawable.filete_crudo;
                    nameFood = "Filete";
                } else {
                    currentFood = R.drawable.filete_crudo;
                    nameFood = "Filete";
                    // cambiar_img_fil(); // Cambiar la imagen de inmediato
                    reiniciarbtn();
                }
                // Cambiar la comida actual a filete

            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Inicializar y empezar el hilo de cocción
        //startCookingThread();
    }

    /**
     * Método que carga los datos del usuario desde Firestore, actualiza la interfaz de usuario con los puntos totales
     * y realiza operaciones adicionales según la entrada de puntos proporcionada.
     */

    private void cargarPuntos(){
        DocumentReference docRef = myFireStore.collection("usuarios").document(user.getUid());
        if (user != null) {
            // Agregar un listener para recibir actualizaciones en tiempo real del documento del usuario
            docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    // Obtener los puntos del documento y actualizar la interfaz de usuario
                    puntosTotales = value.getLong("Puntos");
                }
            });
        }
    }
    private void cargarDatosFirebase(Long puntos){
        DocumentReference docRef = myFireStore.collection("usuarios").document(user.getUid());
        Long nuevosPuntos=puntosTotales+puntos;
        docRef.update("Puntos", nuevosPuntos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostrarError("Error al cargar los puntos.");
                    }
                });
    }

    /**
     * Método que muestra un cuadro de diálogo de error con el mensaje proporcionado.
     * @param mensaje Mensaje de error a mostrar en el cuadro de diálogo.
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
     * Método que inicia un nuevo hilo de cocción. Este método enciende la imagen de la hornilla y comienza
     * un hilo que actualiza la imagen de la comida en intervalos regulares mientras está cocinando.
     */
    private void startCookingThread() {
        hornillaImageView.setImageResource(R.drawable.hornillaencendida);
        cookingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCooking) {
                    try {
                        Thread.sleep(2500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isCooking) {
                                    // Cambiar la imagen de la comida en cada iteración
                                    changeFoodImage();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        // Captura la excepción cuando el hilo es interrumpido
                        // Puede ignorar la excepción, ya que queremos salir del bucle de todos modos
                    }
                }
            }
        });

        cookingThread.start();
    }

    /**
     * Método que cambia la imagen de la comida de acuerdo al estado de cocción y al tipo de comida actual.
     */
    private void changeFoodImage() {
        cookingState++;

        // Cambiar la imagen de acuerdo al estado de cocción y al tipo de comida actual
        switch (cookingState) {

            case 1:
                // Cambiar a la imagen de comida poco hecha
                if (currentFood == R.drawable.patatas_crudas) {
                    comidaImageview.setImageResource(R.drawable.patatas_pocohechas);
                } else if (currentFood == R.drawable.filete_crudo) {
                    comidaImageview.setImageResource(R.drawable.filete_pocohecho);
                } else {
                    comidaImageview.setImageResource(R.drawable.sandwich_pocohecho);
                }
                break;
            case 2:
                // Cambiar a la imagen de comida hecha
                if (currentFood == R.drawable.patatas_crudas) {
                    comidaImageview.setImageResource(R.drawable.patatas_hechas);
                } else if (currentFood == R.drawable.filete_crudo) {
                    comidaImageview.setImageResource(R.drawable.filete_hecho);
                } else {
                    comidaImageview.setImageResource(R.drawable.sandwich_hecho);
                }
                break;
            case 3:
                // Cambiar a la imagen de comida muy hecha
                if (currentFood == R.drawable.patatas_crudas) {
                    comidaImageview.setImageResource(R.drawable.patatas_muyhechas);
                } else if (currentFood == R.drawable.filete_crudo) {
                    comidaImageview.setImageResource(R.drawable.filete_muyhecho);
                } else {
                    comidaImageview.setImageResource(R.drawable.sandwich_muyhecho);
                }
                break;
            case 4:
                // Cambiar a la imagen de comida quemada
                if (currentFood == R.drawable.patatas_crudas) {
                    comidaImageview.setImageResource(R.drawable.patatas_quemadas);
                } else if (currentFood == R.drawable.filete_crudo) {
                    comidaImageview.setImageResource(R.drawable.filete_quemado);
                } else {
                    comidaImageview.setImageResource(R.drawable.sandwich_quemado);
                }
                break;
            default:
                // Detener el hilo de cocción y mostrar el diálogo de puntuación
                stopCookingThread();
                cookingState = 0;
                break;
        }
    }

    /**
     * Método que detiene el hilo de cocción, apaga la imagen de la hornilla y muestra un cuadro de diálogo con la puntuación.
     */
    private void stopCookingThread() {
        // Cambiar la variable de control para salir del bucle
        isCooking = false;
        // Despertamos al hilo para que salga del sleep y verifique la condición de salida
        cookingThread.interrupt();
        hornillaImageView.setImageResource(R.drawable.hornillaapagada);
        // Mostrar el cuadro de diálogo con la puntuación
        mostrarDialogoPuntuacion(calcularPuntuacion());
        cookingState = 0;
    }

    /**
     * Método que detiene el hilo de cocción sin mostrar el cuadro de diálogo de puntuación.
     * Este método se utiliza al reiniciar el juego.
     */
    private void stopCookingThreadRe() {
        // Cambiar la variable de control para salir del bucle
        isCooking = false;
        // Despertamos al hilo para que salga del sleep y verifique la condición de salida
        cookingThread.interrupt();
        hornillaImageView.setImageResource(R.drawable.hornillaapagada);
        // Mostrar el cuadro de diálogo con la puntuación
        cookingState = 0;
    }

    /**
     * Método que reinicia el juego, restableciendo la imagen de la comida y comenzando un nuevo hilo de cocción.
     */
    private void reiniciarbtn() {
        comidaImageview.setImageResource(currentFood);
        // Detener el hilo actual y comenzar uno nuevo
        //stopCookingThreadRe(); // Detener el hilo actual
        // Restablecer el estado del hilo de cocción
        isCooking = true;
        cookingState = 0;
        // Iniciar un nuevo hilo de cocción
    }

    /**
     * Método que se ejecuta cuando la actividad vuelve a estar en primer plano.
     * Registra el listener del acelerómetro cuando la actividad está en primer plano.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener del acelerómetro cuando la actividad está en primer plano
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Método que se ejecuta cuando la actividad pasa a segundo plano.
     * Desregistra el listener del acelerómetro cuando la actividad está en segundo plano.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener del acelerómetro cuando la actividad está en segundo plano
        sensorManager.unregisterListener(this);
    }

    /**
     * Método que se ejecuta cuando cambia un sensor, en este caso, el acelerómetro.
     * Detecta movimiento hacia arriba (eje y) y detiene la cocción si se detecta un movimiento hacia arriba.
     * @param event Objeto que contiene la información del evento del sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Detectar movimiento hacia arriba (eje y)
        //Controlamos que el movimiento se realice una sola vez
        if(!movimientoDetectado){
            float y = event.values[1];
            if (y < -3.5f) {
                // Movimiento hacia arriba detectado, detener la cocción
                movimientoDetectado=true;
                stopCookingThread();
            }
        }

    }

    /**
     * Método que se ejecuta cuando cambia la precisión de un sensor (no es necesario implementar en este caso).
     * @param sensor   Sensor que cambió de precisión.
     * @param accuracy Nueva precisión del sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar esto en este ejemplo
    }

    /**
     * Método que calcula la puntuación del jugador según el estado de cocción actual.
     * @return Cadena que representa la puntuación del jugador (crudo, poco, hecho, muy, pasado).
     */
    private String calcularPuntuacion() {
        if (cookingState == 0) {
            return "crudo";
        }
        if (cookingState == 1) {
            return "poco";
        }
        if (cookingState == 2) {
            return "hecho";
        }
        if (cookingState == 3) {
            return "muy";
        }
        if (cookingState == 4) {
            return "pasado";
        }
        if (cookingState > 4) {
            return "pasado";
        }
        return "";
    }

    /**
     * Método que muestra un cuadro de diálogo con la puntuación del jugador y realiza operaciones según la puntuación.
     * @param puntuacion Puntuación del jugador (crudo, poco, hecho, muy, pasado).
     */
    private void mostrarDialogoPuntuacion(String puntuacion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cocina, null);
        builder.setView(dialogView);
        puntosC = dialogView.findViewById(R.id.puntuacionTotal);
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        String modo = calcularPuntuacion();
        titleTextView.setText("CookingFast");
        if (modo.equalsIgnoreCase("crudo")) {
            messageTextView.setText("Alimento crudo");
            puntosC.setText("Has ganado 0 puntos");
            puntos=puntos+0L;
        } else if (modo.equalsIgnoreCase("poco")) {
            messageTextView.setText("Alimento poco hecho");
            puntosC.setText("Has ganado 5 puntos");
            puntos=puntos+5L;
        } else if (modo.equalsIgnoreCase("hecho")) {
            messageTextView.setText("Felicitaciones Alimento hecho");
            puntosC.setText("Has ganado 10 puntos");
            puntos=puntos+10L;
        } else if (modo.equalsIgnoreCase("muy")) {
            messageTextView.setText("Alimento muy hecho");
            puntosC.setText("Has ganado 5 puntos");
            puntos=puntos+5L;
        } else {
            messageTextView.setText("Alimento quemado");
            puntosC.setText("Has ganado 0 puntos");
            puntos=puntos+0L;
        }
        positiveButton.setText("Volver a intentarlo");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambiamos movimiento detectado a false para que pueda volver a detectar el movimiento el metodo
                movimientoDetectado=false;
                reiniciarbtn();
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        negativeButton.setText("Salir");
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(puntos!=0){

                    cargarDatosFirebase(puntos);
                }
                finish();
            }
        });
        builder.setCancelable(false);// Evitar que se cierre el diálogo al tocar fuera de él
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Método que cambia la imagen de la comida a la de un sandwich crudo.
     */
    private void cambiar_img_san() {
        comidaImageview.setImageResource(R.drawable.sandwich_crudo);
    }

    /**
     * Método que cambia la imagen de la comida a la de un filete crudo.
     */
    private void cambiar_img_fil() {
        comidaImageview.setImageResource(R.drawable.filete_crudo);
    }

    /**
     * Método que cambia la imagen de la comida a la de unas patatas crudas.
     */
    private void cambiar_img_pat() {
        comidaImageview.setImageResource(R.drawable.patatas_crudas);
    }

    /**
     * Método que se llama al hacer clic en el botón de iniciar juego.
     * Verifica si se ha elegido un alimento y, en ese caso, inicia el hilo de cocción.
     * @param view Vista del botón que se hizo clic.
     */
    public void iniciarJuego(View view) {
        if (nameFood.equals("invisible")) {
            System.out.println("Error debes elegir un alimento");

        } else {
            if(puntos!=0){
                //Actualizamos los puntos siempre y cuando sean distintos de 0
                cargarDatosFirebase(puntos);
            }
            startCookingThread();
        }
    }
    /////////////////////////////////////////////[MENU TOOLBAR]/////////////////////////////////////////////////

    /**
     * Método que controla la creación del menú de la barra de herramientas.
     * Oculta opciones de menú específicas según la clase de la actividad.
     * @param menu Menú de la barra de herramientas.
     * @return `true` si se infla el menú; `false` en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int orientation = getResources().getConfiguration().orientation;
        //controlamos que cuadno se gira la pantalla no se muestre el actionBar para que se vea  mejor el juego.
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.cooquing_quest_menu, menu);
        } else {
            getSupportActionBar().hide();
        }
        //Carga de imagen perfil TOOLBAR
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
        if (getClass() == GameActivityCookingFast.class) {
            MenuItem itemRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            MenuItem itemJuegos = menu.findItem(R.id.menu_juegos);
            if (itemRemove != null) {
                menu.removeItem(itemRemove.getItemId());
            }
            if (itemJuegos != null) {
                SubMenu subMenu = itemJuegos.getSubMenu(); // Obtenemos el submenú
                if (subMenu != null) {
                    MenuItem itemToRemove = subMenu.findItem(R.id.P_Boton_IMG_EMPAREJA); // Buscamos el botón "Adivina" dentro del submenú
                    if (itemToRemove != null) {
                        subMenu.removeItem(itemToRemove.getItemId()); // Removemos el botón del submenú
                    }
                }
            }
        }
        return true;
    }


    /**
     * Método que maneja los eventos de clic en los elementos del menú.
     * Envía intents al hacer clic en los elementos del menú.
     * @param item Elemento del menú seleccionado.
     * @return `true` si se maneja el evento; `false` en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.P_Boton_IMG_INICIO) {
            Intent adivinaIntent = new Intent(this, InicioActivity.class);
            startActivity(adivinaIntent);
            finish();
            return true;
        } else if (id == R.id.P_Boton_Perfil_Usuario) {
            Intent adivinaIntent = new Intent(this, PerfilUsuario.class);
            startActivity(adivinaIntent);
            finish();
            return true;
        } else if (id == R.id.P_Boton_IMG_ADIVINA) {
            Intent emparejaIntent = new Intent(this, GameActivityAdivina.class);
            startActivity(emparejaIntent);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}