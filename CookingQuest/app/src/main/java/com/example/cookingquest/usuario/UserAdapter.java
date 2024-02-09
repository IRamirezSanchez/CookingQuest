package com.example.cookingquest.usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookingquest.R;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<Usuario> usuarios;


    //Este adaptador gestionará los objetos Usuario que has definido
    //Recupera todos los usuarios de la colección "usuarios" de Firestore, los convierte en objetos Usuario, y luego actualiza el RecyclerView utilizando el adaptador UserAdapter.
    public UserAdapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userScore;
        public TextView posicion;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userScore = itemView.findViewById(R.id.userScore);
            posicion = itemView.findViewById(R.id.posicion);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_puntos_item, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario currentUsuario = usuarios.get(position);
        int posicion = position + 1;
        holder.posicion.setText(String.valueOf(posicion));
        holder.userName.setText(currentUsuario.getNombre());
        holder.userScore.setText(String.valueOf(currentUsuario.getPuntos()));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void updateUsuarios(List<Usuario> newUsuarios) {
        usuarios.clear();
        usuarios.addAll(newUsuarios);
        notifyDataSetChanged(); //Lo gestiona Observable!  Avisa a RecyclerView que los datos en la lista usuarios han cambiado, y necesita refrescar la vista
    }
}
