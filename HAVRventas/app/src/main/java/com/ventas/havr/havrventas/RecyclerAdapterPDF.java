package com.ventas.havr.havrventas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventas.havr.havrventas.Adaptadores.MyAdapter;
import com.ventas.havr.havrventas.Adaptadores.TutorialesVo;

import java.util.ArrayList;

public class RecyclerAdapterPDF extends  RecyclerView.Adapter<RecyclerAdapterPDF.ViewHolderTutoriales> implements MyAdapter.OnItemClickListener, View.OnClickListener {
    ArrayList<TutorialesVo> listaTutoriales;
    private View.OnClickListener listener;

    public RecyclerAdapterPDF(ArrayList<TutorialesVo> listaTutoriales) {
        this.listaTutoriales = listaTutoriales;
    }

    @NonNull
    @Override
    public ViewHolderTutoriales onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_tutoriales,null,false);
        view.setOnClickListener(this);
        return new ViewHolderTutoriales(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTutoriales holder, int position) {
        holder.TxTitulo.setText(listaTutoriales.get(position).getTitulo());
        holder.TxDescripcion.setText(listaTutoriales.get(position).getDescripcion());
        holder.IVfoto.setImageResource(listaTutoriales.get(position).getFoto());
    }

    @Override
    public int getItemCount() {
        return listaTutoriales.size();
    }

    @Override
    public void onItemClick(String name, int position) {

    }

    public void setOnClickListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }
    }

    public class ViewHolderTutoriales extends RecyclerView.ViewHolder {

        TextView TxTitulo, TxDescripcion;
        ImageView IVfoto;

        public ViewHolderTutoriales(View itemView) {
            super(itemView);
            TxTitulo = (TextView) itemView.findViewById(R.id.tx_titulo);
            TxDescripcion = (TextView) itemView.findViewById(R.id.tx_descripcioon);
            IVfoto = (ImageView) itemView.findViewById(R.id.imagen_list_view);

        }

    }
}