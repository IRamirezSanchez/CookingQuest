package com.example.cookingquest.usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookingquest.R;
import java.util.List;

//Este adaptador gestionará los objetos Usuario que has definido
//Recupera todos los usuarios de la colección "usuarios" de Firestore, los convierte en objetos Usuario, y luego actualiza el RecyclerView utilizando el adaptador UserAdapter.
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<Usuario> usuarios;

    /**
     * Constructor que inicializa el adaptador con una lista de usuarios.
     * @param usuarios Lista de usuarios a mostrar.
     */
    public UserAdapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Clase interna que representa un ViewHolder para los elementos de la RecyclerView.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userScore;
        public TextView posicion;

        /**
         * Constructor que inicializa los elementos de la vista del ViewHolder.
         * @param itemView Vista del elemento de la RecyclerView.
         */
        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userScore = itemView.findViewById(R.id.userScore);
            posicion = itemView.findViewById(R.id.posicion);
        }
    }

    /**
     * Crea un nuevo ViewHolder para los elementos de la RecyclerView.
     * @param parent   Grupo de vista padre en el que se inflará la nueva vista.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Nuevo ViewHolder creado.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_puntos_item, parent, false);
        return new UserViewHolder(v);
    }

    /**
     * Vincula los datos del usuario actual al ViewHolder.
     * @param holder   ViewHolder que debe ser actualizado.
     * @param position Posición del usuario en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario currentUsuario = usuarios.get(position);
        int posicion = position + 1;
        holder.userName.setText(currentUsuario.getNombre());
        holder.userScore.setText(String.valueOf(currentUsuario.getPuntos()));
        // Verificar si es uno de los tres primeros elementos
        if (position == 0) {
            holder.posicion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.oro, 0, 0, 0);
            holder.posicion.setText("");
        } else if (position == 1) {
            holder.posicion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plata, 0, 0, 0);
            holder.posicion.setText("");
        } else if (position == 2) {
            holder.posicion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bronce, 0, 0, 0);
            holder.posicion.setText("");
        } else {
            // Si no es uno de los tres primeros elementos, eliminar cualquier icono
            holder.posicion.setText(String.valueOf(posicion));
            holder.posicion.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    /**
     * Obtiene el número total de elementos en la lista de usuarios.
     * @return Número total de usuarios.
     */
    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    /**
     * Actualiza la lista de usuarios con una nueva lista y notifica a la RecyclerView que los datos han cambiado.
     * @param newUsuarios Nueva lista de usuarios.
     */
    public void updateUsuarios(List<Usuario> newUsuarios) {
        usuarios.clear();
        usuarios.addAll(newUsuarios);
        notifyDataSetChanged(); //Lo gestiona Observable!  Avisa a RecyclerView que los datos en la lista usuarios han cambiado, y necesita refrescar la vista
    }
}
