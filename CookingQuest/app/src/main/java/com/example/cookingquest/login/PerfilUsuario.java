package com.example.cookingquest.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingquest.R;
import com.example.cookingquest.usuario.Puntuacion_GlobalActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;

public class PerfilUsuario extends AppCompatActivity {
    private FirebaseAuth myAuth;
    private TextView nombreCorreoUsuario, nombreC,telefonoC,mostrarPuntuacionC;
    private FirebaseUser user;
    private FirebaseFirestore myFireStore;

    private ImageView imagen_user;
    private Button puntuacionGlobalC;
    private StorageReference myStorage;
    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        myAuth =  FirebaseAuth.getInstance();
        user= myAuth.getCurrentUser();
        myFireStore = FirebaseFirestore.getInstance();
        myStorage = FirebaseStorage.getInstance().getReference();
        //Intent intent = getIntent();   //Antes de utilizar los datos del usuario del firebase lo podias hacer con Intents
        //String email = intent.getStringExtra("correo");
        nombreCorreoUsuario = findViewById(R.id.mostrarCorreo);
        nombreC = findViewById(R.id.mostrarUsuario);
        telefonoC = findViewById(R.id.mostrarTelefono);
        imagen_user = findViewById(R.id.imagen_usuario);
        mostrarPuntuacionC = findViewById(R.id.mostrarPuntuacion);
        puntuacionGlobalC = findViewById(R.id.btn_puntuacionGlobal);

        if( user != null){
            DocumentReference docRef = myFireStore.collection("usuarios").document(user.getUid());
            docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    nombreCorreoUsuario.setText(value.getString("Email"));
                    nombreC.setText(value.getString("Nombre"));
                    telefonoC.setText(value.getString("Telefono"));
                    mostrarPuntuacionC.setText(String.valueOf(value.getLong("Puntos")));
                    cargarImagenDesdeStorage();
                }
            });
        }

        imagen_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFoto();
            }
        });

        puntuacionGlobalC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Puntuacion_GlobalActivity.class);
                startActivity(intent);
            }
        });

        /* EJEMPLO DE CARGA DE IMAGEN CON BITS

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("usuarios/" + user.getUid() + "/imagen_perfil.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;

        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                miAvatar.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
         */

    }




    public void signOut(View view) {

        if(user != null){
            myAuth.signOut();
            //Se cierra tambien automaticamente
            //FirebaseAuth.getInstance().signOut();
            //Hay que cerrar  el FirebaseStore con terminate por que cerrando miAuth, prevalece que sigue el usuario conectado y entra en el principal!
            FirebaseFirestore.getInstance().terminate();
        }

        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);


        finish();
    }

    private void cambiarFoto(){
        Intent abrirGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleriaIntent, 8888);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imagenUri = data.getData();
                imagen_user.setImageURI(imagenUri);

                subirImagenCloudStorageFirebase(imagenUri);

            }
        }
    }

    private void subirImagenCloudStorageFirebase(Uri imagenUri) {
        //Código para subir la imagen al Storage de Firebase
        //Recibe, la uri de la imagen, la referencia al storage y el nombre con el que queremos guardar el fichero.
        //Se pueden crear rutas, o guardarlo todo en raiz.

        //Para ejercicio de crear un root por usuario:
        //StorageReference referenciaFichero = myStorage.child("imagen_perfil.jpg");

        StorageReference referenciaFichero = myStorage.child("usuarios/" + user.getUid() + "/imagen_perfil.jpg");
        referenciaFichero.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PerfilUsuario.this, "Imagen Cargada.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilUsuario.this, "Imagen No cargada.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarImagenDesdeStorage() {
        //Le entra como paramtero la instancia del ImageView para cargar la foto aqui.
        // Obtén la referencia al archivo en Firebase Storage
        //Para el glide he creado la clase de MyAppGlydeModule despues las dependencias que son 3, FirebaseUI,etc
        StorageReference referenciaFichero = myStorage.child("usuarios/" + user.getUid() + "/imagen_perfil.jpg");
        Glide.with(this).load(referenciaFichero).into(imagen_user);

    }


    //TEXT TO SPEECH
    public void reproducirTexto() {
        String texto = telefonoC.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ReproducirTexto");
        textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, params);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}