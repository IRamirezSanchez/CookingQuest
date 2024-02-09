package com.example.cookingquest.usuario;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingquest.R;
import com.example.cookingquest.usuario.UserAdapter;
import com.example.cookingquest.usuario.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Puntuacion_GlobalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuacion_global);

        recyclerView = findViewById(R.id.rvUsuarioPuntos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Recorro cada documento y voy creando usuarios tratandolos como objetos y metiendolos al
        //adaptador y de ahi al RecicleView del Xml.
        //Le pido a firebase que me ordene cuando recojo los datos por el campo Puntos y de forma Descendente.
        db.collection("usuarios")
                .orderBy("Puntos", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Usuario> userScores = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Usuario user = document.toObject(Usuario.class);
                                userScores.add(user);
                            }
                            adapter.updateUsuarios(userScores);
                        } else {
                            Log.d("FirestoreError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
