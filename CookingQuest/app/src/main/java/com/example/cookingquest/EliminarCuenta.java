package com.example.cookingquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cookingquest.login.Login;

public class EliminarCuenta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_cuenta);
    }

    public void login_btn(View view){
        Intent intent = new Intent(EliminarCuenta.this, Login.class);
        startActivity(intent);
        finish();
    }
    public void salir_btn(View view){
        finishAffinity();
    }
}