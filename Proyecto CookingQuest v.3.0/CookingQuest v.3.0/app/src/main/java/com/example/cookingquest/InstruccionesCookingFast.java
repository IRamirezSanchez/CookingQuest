package com.example.cookingquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InstruccionesCookingFast extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucciones_cooking_fast);
    }

    /**
     * Método que inicia la actividad del juego "Cooking Fast" al hacer clic en el botón de jugar.
     * @param view La vista del botón que activa este método.
     */
    public void jugar(View view) {
        Intent intent= new Intent(getApplicationContext(),GameActivityCookingFast.class);
        startActivity(intent);
        finish();
    }
}