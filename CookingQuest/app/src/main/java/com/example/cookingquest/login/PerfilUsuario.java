package com.example.cookingquest.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingquest.EliminarCuenta;
import com.example.cookingquest.GameActivityAdivina;
import com.example.cookingquest.GameActivityCookingFast;
import com.example.cookingquest.InicioActivity;
import com.example.cookingquest.R;
import com.example.cookingquest.usuario.Puntuacion_GlobalActivity;
import com.example.cookingquest.usuario.Recetas_UsuarioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class PerfilUsuario extends AppCompatActivity {
    private FirebaseAuth myAuth;
    private TextView nombreCorreoUsuario, nombreC,telefonoC,mostrarPuntuacionC;
    private FirebaseUser user;
    private FirebaseFirestore myFireStore;

    private ImageView imagePerfil,imagen_user,Btn_cambioFondo;
    private Button puntuacionGlobalC;
    private StorageReference myStorage;
    private ProgressBar esperando;



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
        Btn_cambioFondo= findViewById(R.id.BTN_cambioFondo);
        imagePerfil= findViewById(R.id.imagen_perfil);
        esperando=findViewById(R.id.dialogProgressBar);
        if( user != null){
            DocumentReference docRef = myFireStore.collection("usuarios").document(user.getUid());
            docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                /*@Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    nombreCorreoUsuario.setText(value.getString("Email"));
                    nombreC.setText(value.getString("Nombre"));
                    telefonoC.setText(value.getString("Telefono"));
                    mostrarPuntuacionC.setText(String.valueOf(value.getLong("Puntos")));
                    cargarImagenDesdeStorage();
                }*/
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Manejar el error, por ejemplo, mostrando un mensaje de error en un Toast
                        Toast.makeText(PerfilUsuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al obtener los datos del usuario", error);
                        return;
                    }

                    if (value != null && value.exists()) {
                        // Verificar si el DocumentSnapshot no es nulo y si el documento existe
                        // Acceder a los campos del DocumentSnapshot
                        String email = value.getString("Email");
                        String nombre = value.getString("Nombre");
                        String telefono = value.getString("Telefono");
                        Long puntos = value.getLong("Puntos");

                        // Verificar si los campos son nulos antes de asignarlos a los TextViews
                        if (email != null) {
                            nombreCorreoUsuario.setText(email);
                        }
                        if (nombre != null) {
                            nombreC.setText(nombre);
                        }
                        if (telefono != null) {
                            telefonoC.setText(telefono);
                        }
                        if (puntos != null) {
                            mostrarPuntuacionC.setText(String.valueOf(puntos));
                        }

                        // Llamar a cargarImagenDesdeStorage() si es necesario
                        cargarImagenDesdeStorage();
                    } else {
                        // Documento no encontrado o nulo
                        // Puedes mostrar un mensaje o realizar otras acciones según tu lógica de aplicación
                        Toast.makeText(PerfilUsuario.this, "El documento del usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        imagen_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFotoPerfil();
            }
        });
        Btn_cambioFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFotoFondo();
            }
        });


    }

    ///////////////////////////////////////////MENU INFERIOR//////////////////////////////////////////////////////
    public void misRecetas(View view){
        Intent intent = new Intent(PerfilUsuario.this, Recetas_UsuarioActivity.class);
        startActivity(intent);

    }
    public void eliminarCuenta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilUsuario.this);
        builder.setTitle("Eliminar cuenta");
        builder.setMessage("¿Seguro que desea eliminar su cuenta?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (user != null) {
                    // Eliminar datos del usuario en Firestore
                    myFireStore.collection("usuarios").document(user.getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Datos del usuario eliminados correctamente en Firestore
                                    // Ahora, eliminar al usuario en Firebase Authentication
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Usuario eliminado correctamente
                                                        Toast.makeText(PerfilUsuario.this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(PerfilUsuario.this, Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // Error al eliminar al usuario
                                                        Toast.makeText(PerfilUsuario.this, "Error al eliminar cuenta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error al eliminar los datos del usuario en Firestore
                                    Toast.makeText(PerfilUsuario.this, "Error al eliminar datos del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void puntuacionGlobal(View view){
        Intent intent = new Intent(getApplicationContext(), Puntuacion_GlobalActivity.class);
        startActivity(intent);

    }

    public void cerrarSesion(View view) {
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

    ////////////////////////////////////////GESTION DE IMAGENES///////////////////////////////////////////////////


    private void cambiarFotoPerfil() {
        Intent abrirGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleriaIntent, 8888); // Imagen de perfil
    }

    private void cambiarFotoFondo() {
        Intent abrirGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleriaIntent, 9999); // Imagen de fondo
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 8888 && resultCode == Activity.RESULT_OK) {
            // Imagen de perfil seleccionada
            Uri imagenUri = data.getData();
            imagen_user.setImageURI(imagenUri);
            subirImagenCloudStorageFirebase(imagenUri, "imagen_perfil.jpg");
        } else if (requestCode == 9999 && resultCode == Activity.RESULT_OK) {
            // Imagen de fondo seleccionada
            Uri imagenUri = data.getData();
            imagePerfil.setImageURI(imagenUri);
            subirImagenCloudStorageFirebase(imagenUri, "imagen_perfil_fondo.jpg");
        }
    }

    private void subirImagenCloudStorageFirebase(Uri imagenUri, String nombreImagen) {
        //Código para subir la imagen al Storage de Firebase
        //Recibe, la uri de la imagen, la referencia al storage y el nombre con el que queremos guardar el fichero.
        //Se pueden crear rutas, o guardarlo todo en raiz.

        //Para ejercicio de crear un root por usuario:
        //StorageReference referenciaFichero = myStorage.child("imagen_perfil.jpg");

        StorageReference referenciaFichero = myStorage.child("usuarios/" + user.getUid() +"/"+ nombreImagen);
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
        StorageReference referenciaFicheroFondo = myStorage.child("usuarios/" + user.getUid() + "/imagen_perfil_fondo.jpg");
        Glide.with(this).load(referenciaFichero).into(imagen_user);
        Glide.with(this).load(referenciaFicheroFondo).into(imagePerfil);

    }


    /////////////////////////////////////////////[MENU TOOLBAR]/////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cooquing_quest_menu, menu);
        if (getClass() == PerfilUsuario.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_Perfil_Usuario);
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
        }else{
            return super.onOptionsItemSelected(item);
        }

    }

}