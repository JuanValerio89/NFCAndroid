package com.ventas.havr.havrventas.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.ventas.havr.havrventas.R;

import java.util.List;

public class AdaptadorAdministracion  extends RecyclerView.Adapter<AdaptadorAdministracion.UsuariosViewHolder>{

    List<CrearUsuarios> crearUsuariosList;

    public AdaptadorAdministracion(List<CrearUsuarios> crearUsuariosList) {
        this.crearUsuariosList = crearUsuariosList;
    }

    @NonNull
    @Override
    public UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_recycler_admin,parent,false);
        UsuariosViewHolder holder = new UsuariosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosViewHolder holder, int position) {
        CrearUsuarios users = crearUsuariosList.get(position);
        try {
            holder.Txusuario.setText("Usuario: " + users.getEmail());
            holder.Txtelefono.setText("Tel: " + users.getPassword());
            holder.Txpassword.setText("Pass: " +users.getPassword());
            Timestamp timestamp = users.getFechaAlta();
            holder.Txfecha.setText(timestamp.toDate().toString());
        }catch (Exception e){
            holder.Txusuario.setText("Error de usuario");
            holder.Txtelefono.setText("Error de usuario");
            holder.Txpassword.setText("Error de usuario");
            holder.Txfecha.setText("Error de usuario");
        }
    }

    @Override
    public int getItemCount() {
        return crearUsuariosList.size();
    }

    public static class UsuariosViewHolder extends RecyclerView.ViewHolder{

        TextView Txusuario;
        TextView Txpassword;
        TextView Txtelefono;
        TextView Txfecha;

        public UsuariosViewHolder(View itemView) {
            super(itemView);
            Txusuario = itemView.findViewById(R.id.tx_usuario);
            Txpassword = itemView.findViewById(R.id.tx_password);
            Txtelefono = itemView.findViewById(R.id.tx_telefono);
            Txfecha = itemView.findViewById(R.id.tx_alta);
        }
    }
}
