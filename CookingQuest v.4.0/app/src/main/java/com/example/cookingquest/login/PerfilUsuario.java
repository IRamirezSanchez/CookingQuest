package com.example.cookingquest.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private TextView nombreCorreoUsuario, nombreC, telefonoC, mostrarPuntuacionC;
    private FirebaseUser user;
    private FirebaseFirestore myFireStore;
    private ImageView imagePerfil, imagen_user, Btn_cambioFondo;
    private Button puntuacionGlobalC;
    private StorageReference myStorage;
    private ProgressBar progressBar,dialogProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        myAuth = FirebaseAuth.getInstance();
        user = myAuth.getCurrentUser();
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
        Btn_cambioFondo = findViewById(R.id.BTN_cambioFondo);
        imagePerfil = findViewById(R.id.imagen_perfil);
        dialogProgressBar =findViewById(R.id.dialogProgressBar);
        progressBar = findViewById(R.id.progressBar);

        if (user != null) {
            DocumentReference docRef = myFireStore.collection("usuarios").document(user.getUid());
            docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                // GESTIONAMOS la carga de datos del perfil de usuario.
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Manejar el error, por ejemplo, mostrando un mensaje de error en un Toast
                        //Toast.makeText(PerfilUsuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(PerfilUsuario.this, "El documento del usuario no existe", Toast.LENGTH_SHORT).show();
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

    /**
     * Método que inicia la actividad para mostrar las recetas del usuario.
     * @param view La vista que activó el evento (botón).
     */
    public void misRecetas(View view) {
        Intent intent = new Intent(PerfilUsuario.this, Recetas_UsuarioActivity.class);
        startActivity(intent);

    }

    /**
     * Método que muestra un cuadro de diálogo para confirmar la eliminación de la cuenta del usuario.
     * Si el usuario confirma, se eliminan los datos del usuario en Firestore y se cierra la sesión en Firebase.
     * Después, se redirige a la pantalla de inicio de sesión.
     * @param view La vista que activó el evento (botón).
     */
    public void eliminarCuenta(View view) {
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
                                                        Intent intent = new Intent(PerfilUsuario.this, Login.class);
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

    /**
     * Método que inicia la actividad para mostrar la puntuación global de los usuarios.
     * @param view La vista que activó el evento (botón).
     */
    public void puntuacionGlobal(View view) {
        Intent intent = new Intent(getApplicationContext(), Puntuacion_GlobalActivity.class);
        startActivity(intent);

    }

    /**
     * Método que cierra la sesión del usuario, realiza el cierre automático de Firebase,
     * y redirige a la pantalla de inicio de sesión.
     * @param view La vista que activó el evento (botón).
     */
    public void cerrarSesion(View view) {
        if (user != null) {
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

    /**
     * Método que inicia la actividad para seleccionar una imagen de perfil desde la galería.
     */
    private void cambiarFotoPerfil() {
        Intent abrirGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleriaIntent, 8888); // Imagen de perfil
    }

    /**
     * Método que inicia la actividad para seleccionar una imagen de fondo desde la galería.
     */
    private void cambiarFotoFondo() {
        Intent abrirGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleriaIntent, 9999); // Imagen de fondo
    }

    /**
     * Método que maneja el resultado de la actividad para seleccionar una imagen de perfil o fondo.
     * @param requestCode El código de solicitud pasado a startActivityForResult().
     * @param resultCode  El código de resultado devuelto por la actividad.
     * @param data        Un Intent con datos adicionales.
     */
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

    /**
     * Método que sube una imagen seleccionada a Firebase Storage.
     * @param imagenUri    La URI de la imagen seleccionada.
     * @param nombreImagen El nombre con el que se desea guardar el archivo en Firebase Storage.
     */
    private void subirImagenCloudStorageFirebase(Uri imagenUri, String nombreImagen) {
        //Recibe, la uri de la imagen, la referencia al storage y el nombre con el que queremos guardar el fichero.
        StorageReference referenciaFichero = myStorage.child("usuarios/" + user.getUid() + "/" + nombreImagen);
        referenciaFichero.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(PerfilUsuario.this, "Imagen Cargada.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(PerfilUsuario.this, "Imagen No cargada.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Método que carga la imagen del usuario desde Firebase Storage.
     * Si no hay imagen, se carga la imagen predeterminada.
     * El tiempo de espera de los datos de Firebase se controla con un progressbar.
     */
    private void cargarImagenDesdeStorage() {
        dialogProgressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        // Obtén la referencia al archivo en Firebase Storage
        StorageReference referenciaFichero = myStorage.child("usuarios/" + user.getUid() + "/imagen_perfil.jpg");
        StorageReference referenciaFicheroFondo = myStorage.child("usuarios/" + user.getUid() + "/imagen_perfil_fondo.jpg");

        // Utiliza Glide para cargar la imagen del usuario, y si no hay imagen, muestra la imagen predeterminada
        if (referenciaFichero != null) {
            try {
                referenciaFichero.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imagen_user);
                    dialogProgressBar.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }).addOnFailureListener(exception -> {
                    // Si ocurre un error, carga la imagen predeterminada desde el recurso drawable
                    Glide.with(this).load(R.drawable.imagen_predeterminada_perfil).into(imagen_user);
                    dialogProgressBar.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Repite lo mismo para la imagen de fondo
        if (referenciaFicheroFondo != null) {
            try {
                referenciaFicheroFondo.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imagePerfil);
                }).addOnFailureListener(exception -> {
                    // Si ocurre un error, carga la imagen predeterminada desde el recurso drawable
                    Glide.with(this).load(R.drawable.imagen_predeterminada_fondo).into(imagePerfil);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /////////////////////////////////////////////[MENU TOOLBAR]/////////////////////////////////////////////////

    /**
     * Método que infla el menú en la barra de herramientas.
     * @param menu El menú en el que se inflarán los elementos de la barra de herramientas.
     * @return true para mostrar el menú, false en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cooquing_quest_menu, menu);
        if (getClass() == PerfilUsuario.class) {
            MenuItem itemToRemove = menu.findItem(R.id.P_Boton_Perfil_Usuario);
            MenuItem itemToRemove2 = menu.findItem(R.id.P_Boton_IMG_SALIR);
            if (itemToRemove != null || itemToRemove2 != null) {
                menu.removeItem(itemToRemove.getItemId());
                menu.removeItem(itemToRemove2.getItemId());
            }
        }
        //getMenuInflater().inflate(R.menu.test_menu, menu);

        return true;
    }

    /**
     * Método que maneja los eventos de clic en los elementos del menú de la barra de herramientas.
     * @param item El elemento del menú que fue seleccionado.
     * @return true si el evento fue manejado, false en caso contrario.
     */
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
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

}