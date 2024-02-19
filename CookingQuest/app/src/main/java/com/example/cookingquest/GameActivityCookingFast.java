package com.example.cookingquest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingquest.login.PerfilUsuario;

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
    private int currentFood = R.drawable.patatas_crudas; // Imagen por defecto
    private String nameFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_cooking_fast);
        hornillaImageView=findViewById(R.id.hornilla_img);
        comidaImageview = findViewById(R.id.food_img);
        restart_btn = findViewById(R.id.reiniciar_btn);
        patatas_btn=findViewById(R.id.patatas_btn);
        filete_btn=findViewById(R.id.filete_btn);
        san_btn=findViewById(R.id.san_btn);
        nameFood="Patatas";
        restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarbtn();
            }
        });

        patatas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar la comida actual a patatas
                currentFood = R.drawable.patatas_crudas;
                nameFood="Patatas";
                //cambiar_img_pat(); // Cambiar la imagen de inmediato
                reiniciarbtn();
            }
        });

        san_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFood=R.drawable.sandwich_crudo_as;
                nameFood="Sandwich";
                // cambiar_img_san(); // Cambiar la imagen de inmediato
                reiniciarbtn();
            }
        });

        filete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar la comida actual a filete
                currentFood = R.drawable.filete_crudo;
                nameFood="Filete";
                // cambiar_img_fil(); // Cambiar la imagen de inmediato
                reiniciarbtn();
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Inicializar y empezar el hilo de cocción
        startCookingThread();
    }

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

    private void changeFoodImage() {
        cookingState++;

        // Cambiar la imagen de acuerdo al estado de cocción y al tipo de comida actual
        switch (cookingState) {

            case 1:
                if(currentFood==R.drawable.patatas_crudas){
                    comidaImageview.setImageResource(R.drawable.patatas_pocohechas);
                }else if(currentFood == R.drawable.filete_crudo){
                    comidaImageview.setImageResource(R.drawable.filete_pocohecho);
                }else{
                    comidaImageview.setImageResource(R.drawable.sandwich_pocohecho);
                }
                break;
            case 2:
                if(currentFood==R.drawable.patatas_crudas){
                    comidaImageview.setImageResource(R.drawable.patatas_hechas);
                }else if(currentFood == R.drawable.filete_crudo){
                    comidaImageview.setImageResource(R.drawable.filete_hecho);
                }else{
                    comidaImageview.setImageResource(R.drawable.sandwich_hecho);
                }
                break;
            case 3:
                if(currentFood==R.drawable.patatas_crudas){
                    comidaImageview.setImageResource(R.drawable.patatas_muyhechas);
                }else if(currentFood == R.drawable.filete_crudo){
                    comidaImageview.setImageResource(R.drawable.filete_muyhecho);
                }else{
                    comidaImageview.setImageResource(R.drawable.sandwich_muyhecho);
                }
                break;
            case 4:
                if(currentFood==R.drawable.patatas_crudas){
                    comidaImageview.setImageResource(R.drawable.patatas_quemadas);
                }else if(currentFood == R.drawable.filete_crudo){
                    comidaImageview.setImageResource(R.drawable.filete_quemado);
                }else{
                    comidaImageview.setImageResource(R.drawable.sandwich_quemado);
                }
                break;
            default:
                stopCookingThread();
                cookingState = 0;
                break;
        }
    }


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
    private void stopCookingThreadRe() {
        // Cambiar la variable de control para salir del bucle
        isCooking = false;
        // Despertamos al hilo para que salga del sleep y verifique la condición de salida
        cookingThread.interrupt();
        hornillaImageView.setImageResource(R.drawable.hornillaapagada);
        // Mostrar el cuadro de diálogo con la puntuación
        cookingState = 0;
    }
    private void reiniciarbtn() {
        comidaImageview.setImageResource(currentFood);
        // Detener el hilo actual y comenzar uno nuevo
        stopCookingThreadRe(); // Detener el hilo actual
        // Restablecer el estado del hilo de cocción
        isCooking = true;
        cookingState = 0;
        // Iniciar un nuevo hilo de cocción
        startCookingThread();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener del acelerómetro cuando la actividad está en primer plano
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener del acelerómetro cuando la actividad está en segundo plano
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Detectar movimiento hacia arriba (eje y)
        float y = event.values[1];
        if (y < -3.5f) {
            // Movimiento hacia arriba detectado, detener la cocción
            stopCookingThread();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar esto en este ejemplo
    }

    private String calcularPuntuacion() {
        if(cookingState==0){
            return nameFood+ " crud@/s 0 puntos Intentalo nuevamente";
        }
        if(cookingState==1){
            return nameFood+" poco hech@/s 5 puntos";
        }
        if(cookingState==2){
            return nameFood+" hech@/s 10 puntos Felicidades";
        }
        if (cookingState==3){
            return nameFood+" muy hech@/s 5 puntos";
        }
        if(cookingState==4){
            return nameFood+" quemad@/s 0 puntos";
        }
        if(cookingState>4){
            return nameFood+" quemad@/s 0 puntos";
        }
        return "";
    }
    private void mostrarDialogoPuntuacion(String puntuacion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Puntuación");
        builder.setMessage(calcularPuntuacion());
        builder.setPositiveButton("Volver a intentarlo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Volver a intentarlo: reiniciar la cocción
                reiniciarbtn();
            }
        });
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Salir de la aplicación
                finish();
            }
        });
        builder.setCancelable(false); // Evitar que se cierre el diálogo al tocar fuera de él
        builder.show();
    }
    private void cambiar_img_san(){
        comidaImageview.setImageResource(R.drawable.sandwich_crudo);
    }
    private void cambiar_img_fil(){
        comidaImageview.setImageResource(R.drawable.filete_crudo);
    }
    private void cambiar_img_pat(){comidaImageview.setImageResource(R.drawable.patatas_crudas);}


    /////////////////////////////////////////////[MENU TOOLBAR]/////////////////////////////////////////////////

    //Control Horizontal y Vertical, botones de menu.
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
        if (getClass() == GameActivityCookingFast.class) {
            MenuItem itemToRemove1 = menu.findItem(R.id.P_Boton_IMG_EMPAREJA);
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_IMG_SALIR);
            if (itemToRemove1 != null || itemToRemove != null) {
                menu.removeItem(itemToRemove1.getItemId());
                menu.removeItem(itemToRemove.getItemId());
            }
        }
        return true;
    }


    //Envio de Intents al pulsar los botones que hay disponibles.
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
        }else if (id == R.id.P_Boton_IMG_ADIVINA) {
            Intent emparejaIntent = new Intent(this, GameActivityAdivina.class);
            startActivity(emparejaIntent);
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}