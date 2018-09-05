package com.ventas.havr.havrventas.Adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventas.havr.havrventas.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> names;
    private int layout;
    private OnItemClickListener itemClickListener;
    private int img_res;


    public MyAdapter(List<String> names, int layout, OnItemClickListener listener, int img_res) {
        this.names = names;
        this.layout = layout;
        this.itemClickListener = listener;
        this.img_res = img_res;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos el layout y se lo pasamos al constructor del ViewHolder, donde manejaremos
        // toda la lógica como extraer los datos, referencias...
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Llamamos al método Bind del ViewHolder pasándole objeto y listener
        holder.bind(names.get(position), itemClickListener, position);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos UI a rellenar
        private ImageView imagenView;
        public TextView textViewName;

        public ViewHolder(View itemView) {
            // Recibe la View completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declaradas justo arriba.
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.text_recycler);
            this.imagenView = (ImageView) itemView.findViewById(R.id.image_recycler);
        }

        public void bind(final String name, final OnItemClickListener listener,final int img_res) {
            //Recuperamos la imagen
            int imagen = 0;
            switch (img_res){
                case 0:
                    imagen = R.drawable.im_arduino;
                    break;
                case 1:
                    imagen = R.drawable.im_bateria;
                    break;
                case 2:
                    imagen = R.drawable.im_broca;
                    break;
                case 3:
                    imagen = R.drawable.im_bluetooth;
                    break;
                case 4:
                    imagen = R.drawable.im_cable;
                    break;
                case 5:
                    imagen = R.drawable.im_capacitor;
                    break;
                case 6:
                    imagen = R.drawable.im_cautin;
                    break;
                case 7:
                    imagen = R.drawable.im_circuito;
                    break;
                case 8:
                    imagen = R.drawable.im_cable;
                    break;
                case 9:
                    imagen = R.drawable.im_disipador;
                    break;
                case 10:
                    imagen = R.drawable.im_dip;
                    break;
                case 11:
                    imagen = R.drawable.im_display;
                    break;
                case 12:
                    imagen = R.drawable.im_header;
                    break;
                case 13:
                    imagen = R.drawable.im_hv;
                    break;
                case 14:
                    imagen = R.drawable.im_laser;
                    break;
                case 15:
                    imagen = R.drawable.im_led;
                    break;
                case 16:
                    imagen = R.drawable.im_medidor;
                    break;
                case 17:
                    imagen = R.drawable.im_modulos;
                    break;
                case 18:
                    imagen = R.drawable.im_motores;
                    break;
                case 19:
                    imagen = R.drawable.im_placas;
                    break;
                case 20:
                    imagen = R.drawable.im_potenciometro;
                    break;
                case 21:
                    imagen = R.drawable.im_programador;
                    break;
                case 22:
                    imagen = R.drawable.im_pinzas;
                    break;
                case 23:
                    imagen = R.drawable.im_radiofrecuencia;
                    break;
                case 24:
                    imagen = R.drawable.im_resistencia;
                    break;
                case 25:
                    imagen = R.drawable.im_relevador;
                    break;
                case 26:
                    imagen = R.drawable.im_solar;
                    break;
                case 27:
                    imagen = R.drawable.im_raspberry;
                    break;
                case 28:
                    imagen = R.drawable.im_sensor;
                    break;
                case 29:
                    imagen = R.drawable.im_servo;
                    break;
                case 30:
                    imagen = R.drawable.im_switch;
                    break;
                case 31:
                    imagen = R.drawable.im_miscelaneo;
                    break;
                case 32:
                    imagen = R.drawable.im_transformador;
                    break;
                case 33:
                    imagen = R.drawable.im_lineal;
                    break;
                case 34:
                    imagen = R.drawable.im_trimpot;
                    break;
                case 35:
                    imagen = R.drawable.im_wire;
                    break;
                case 36:
                    imagen = R.drawable.im_zif;
                    break;
            }
            // Procesamos los datos a renderizar
            this.textViewName.setText(name);
            this.imagenView.setImageResource(imagen);
            // que se comporta de la siguiente manera...
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ... pasamos nuestro objeto modelo (este caso String) y posición
                    listener.onItemClick(name, getAdapterPosition());
                }
            });
        }
    }

    // Declaramos nuestra interfaz con el/los método/s a implementar
    public interface OnItemClickListener {
        void onItemClick(String name, int position);
    }


}