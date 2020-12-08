package com.ventas.havr.havrventas.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ventas.havr.havrventas.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> names;
    private List<String> numeros;
    private int layout;
    private OnItemClickListener itemClickListener;
    private int img_res;
    private Context context;


    public MyAdapter(Context context, List<String> names, List<String> numeros, int layout, OnItemClickListener listener, int img_res) {
        this.names = names;
        this.numeros = numeros;
        this.layout = layout;
        this.context = context;
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
        holder.bind(names.get(position),numeros.get(position), itemClickListener, position, context);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos UI a rellenar
        private ImageView imagenView;
        public TextView textViewName;
        public TextView textDescripcion;
        public TextView textArticulos;

        public ViewHolder(View itemView) {
            // Recibe la View completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declaradas justo arriba.
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.text_folio);
            this.imagenView = (ImageView) itemView.findViewById(R.id.image_recycler);
            this.textDescripcion = (TextView) itemView.findViewById(R.id.text_descripcion);
            this.textArticulos = (TextView) itemView.findViewById(R.id.text_articulos);
        }

        public void bind(final String name,final String numero, final OnItemClickListener listener,final int img_res, Context contextA) {
            //Recuperamos la imagen
            String[] Descripciones = contextA.getResources().getStringArray(R.array.conjunto_componentes_descripcion);
            int imagen = 0;
            imagen = R.drawable.arduino_img;
            switch (img_res){
                case 0:
                    imagen = R.drawable.arduino_img;
                    break;
                case 1:
                    imagen = R.drawable.bateria_img;
                    break;
                case 2:
                    imagen = R.drawable.base_img;
                    break;
                case 3:
                    imagen = R.drawable.brocas_img;
                    break;
                case 4:
                    imagen = R.drawable.bluetooth_img;
                    break;
                case 5:
                    imagen = R.drawable.cable_img;
                    break;
                case 6:
                    imagen = R.drawable.capacitor_img;
                    break;
                case 7:
                    imagen = R.drawable.cautin_img;
                    break;
                case 8:
                    imagen = R.drawable.circuito_img;
                    break;
                case 9:
                    imagen = R.drawable.dupont_img;
                    break;
                case 10:
                    imagen = R.drawable.disipador_img;
                    break;
                case 11:
                    imagen = R.drawable.dip_img;
                    break;
                case 12:
                    imagen = R.drawable.oled_img;
                    break;
                case 13:
                    imagen = R.drawable.header_img;
                    break;
                case 14:
                    imagen = R.drawable.hv_img;
                    break;
                case 15:
                    imagen = R.drawable.laser_img;
                    break;
                case 16:
                    imagen = R.drawable.led_img;
                    break;
                case 17:
                    imagen = R.drawable.medidor_img;
                    break;
                case 18:
                    imagen = R.drawable.modulo_img;
                    break;
                case 19:
                    imagen = R.drawable.motores_img;
                    break;
                case 20:
                    imagen = R.drawable.placa_img;
                    break;
                case 21:
                    imagen = R.drawable.potenciometro_img;
                    break;
                case 22:
                    imagen = R.drawable.programador_img;
                    break;
                case 23:
                    imagen = R.drawable.pinzas_img;
                    break;
                case 24:
                    imagen = R.drawable.radiofrecuencia_img;
                    break;
                case 25:
                    imagen = R.drawable.res_smd;
                    break;
                case 26:
                    imagen = R.drawable.resistencia_img;
                    break;
                case 27:
                    imagen = R.drawable.relevador_img;
                    break;
                case 28:
                    imagen = R.drawable.cnc_img;
                    break;
                case 29:
                    imagen = R.drawable.solar_img;
                    break;
                case 30:
                    imagen = R.drawable.raspberry_img;
                    break;
                case 31:
                    imagen = R.drawable.sensores_img;
                    break;
                case 32:
                    imagen = R.drawable.servomotor_img;
                    break;
                case 33:
                    imagen = R.drawable.switch_img;
                    break;
                case 34:
                    imagen = R.drawable.miscelaneo_img;
                    break;
                case 35:
                    imagen = R.drawable.transformadores_img;
                    break;
                case 36:
                    imagen = R.drawable.lineal_img;
                    break;
                case 37:
                    imagen = R.drawable.trimpot_img;
                    break;
                case 38:
                    imagen = R.drawable.wire_img;
                    break;
                case 39:
                    imagen = R.drawable.wire_img;
                    break;
            }
            // Procesamos los datos a renderizar
            this.textViewName.setText(name);
            this.textDescripcion.setText(Descripciones[img_res]);
            this.imagenView.setImageDrawable(contextA.getResources().getDrawable(imagen));
            this.textArticulos.setText("Modelos disponibles: "+numero);
            //this.imagenView.setImageResource(imagen);
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