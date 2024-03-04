package com.example.cookingquest.login;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    /**
     * Sobrescribe el método registerComponents para registrar un componente personalizado
     * para Glide con el propósito de manejar la carga de imágenes desde StorageReference de Firebase.
     *
     * @param context El contexto de la aplicación.
     * @param glide   La instancia de Glide utilizada para la carga de imágenes.
     * @param registry El registro donde se añaden los componentes.
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Registra FirebaseImageLoader para manejar StorageReference
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}