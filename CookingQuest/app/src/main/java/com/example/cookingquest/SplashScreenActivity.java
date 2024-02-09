package com.example.cookingquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import com.example.cookingquest.login.Login;

public class SplashScreenActivity extends AppCompatActivity {

    //Esta actividad se la conoce como "Splash Screen" Iniciar la APP con una parte multimedia o lo que desees.
    //Tienes que cambiar en el AndroidManifest el principal Actividad para que se ejecute el primero
    private static final int TIEMPO_ESPERA=2000; //Se ejecuta durante 3 segundos antes de ir ala actividad Principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity_main);

        VideoView splashVideo = findViewById(R.id.SplashVideo);

        splashVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.cookingquest);  // Reemplaza "ruta_del_video" con la ubicación de tu archivo de video.
        splashVideo.start();

        //Importas el paquete para utilizar Handler de Android.os
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, Login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.desvanecer, R.anim.aparecer); // Aplica la animación como empieza y como abre la otra actividad en la carpeta res/anim
                finish(); // Cierra la actividad de bienvenida para que no se pueda volver atrás.
            }
        }, TIEMPO_ESPERA);
    }
}