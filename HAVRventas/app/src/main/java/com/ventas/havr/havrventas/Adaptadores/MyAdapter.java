package com.ventas.havr.havrventas.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ventas.havr.havrventas.Modelos.BaseActualizar;
import com.ventas.havr.havrventas.R;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

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
        Realm realm;

        List<String> productos = Arrays.asList("AM0000","AR0000","BA0000","BR0000","BS0000","BT0000","CA0000",
                "CC0000","CN0000","CR0000","CS0000","CT0000","DI0000","DP0000","DS0000","EI0000","FC0000",
                "HR0000","HV0000","LD0000","ME0000","MO0000","MT0000","MU0000","PL0000","PM0000","PR0000",
                "PS0000","PT0000","RF0000","RM0000","RS0000","RY0000","SR0000","SV0000","SW0000","TB0000",
                "TE0000","TM0000","TR0000","TV0000");

        public ViewHolder(View itemView) {
            // Recibe la View completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declaradas justo arriba.
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.text_sku_recycler);
            this.imagenView = (ImageView) itemView.findViewById(R.id.image_recycler);
            this.textDescripcion = (TextView) itemView.findViewById(R.id.text_descripcion);
            this.textArticulos = (TextView) itemView.findViewById(R.id.text_articulos);
        }

        public void bind(final String name,final String numero, final OnItemClickListener listener,final int img_res, Context contextA) {
            //Recuperamos la imagen
            String[] Descripciones = contextA.getResources().getStringArray(R.array.conjunto_componentes_descripcion);
            int imagen = 0;
            realm = Realm.getDefaultInstance();
            BaseActualizar baseActualizar = realm.where(BaseActualizar.class).equalTo("SKU", productos.get(img_res)).findFirst();
            imagen = R.drawable.arduino_img;
            try {
                if (!baseActualizar.getEstado()) {
                    textViewName.setBackgroundColor(contextA.getResources().getColor(R.color.amarillo));
                    this.textViewName.setText(name + " - Actualizar");
                } else {
                    textViewName.setBackgroundColor(contextA.getResources().getColor(R.color.colorPrimaryDark));
                    this.textViewName.setText(name);
                }
            }catch (Exception e){
                textViewName.setBackgroundColor(contextA.getResources().getColor(R.color.amarillo));
                this.textViewName.setText(name + " - Actualizar");
            }
            switch (img_res){
                case 0:
                    imagen = R.drawable.am_img;
                    break;
                case 1:
                    imagen = R.drawable.arduino_img;
                    break;
                case 2:
                    imagen = R.drawable.bateria_img;
                    break;
                case 3:
                    imagen = R.drawable.brocas_img;
                    break;
                case 4:
                    imagen = R.drawable.base_img;
                    break;
                case 5:
                    imagen = R.drawable.bluetooth_img;
                    break;
                case 6:
                    imagen = R.drawable.cable_img;
                    break;
                case 7:
                    imagen = R.drawable.capacitor_img;
                    break;
                case 8:
                    imagen = R.drawable.cnc_img;
                    break;
                case 9:
                    imagen = R.drawable.circuito_img;
                    break;
                case 10:
                    imagen = R.drawable.dupont_img;
                    break;
                case 11:
                    imagen = R.drawable.cautin_img;
                    break;
                case 12:
                    imagen = R.drawable.disipador_img;
                    break;
                case 13:
                    imagen = R.drawable.dip_img;
                    break;
                case 14:
                    imagen = R.drawable.oled_img;
                    break;
                case 15:
                    imagen = R.drawable.lamina_img;
                    break;
                case 16:
                    imagen = R.drawable.fuente_img;
                    break;
                case 17:
                    imagen = R.drawable.header_img;
                    break;
                case 18:
                    imagen = R.drawable.hv_img;
                    break;
                case 19:
                    imagen = R.drawable.led_img;
                    break;
                case 20:
                    imagen = R.drawable.medidor_img;
                    break;
                case 21:
                    imagen = R.drawable.modulo_img;
                    break;
                case 22:
                    imagen = R.drawable.motores_img;
                    break;
                case 23:
                    imagen = R.drawable.multimetro_img;
                    break;
                case 24:
                    imagen = R.drawable.placa_img;
                    break;
                case 25:
                    imagen = R.drawable.potenciometro_img;
                    break;
                case 26:
                    imagen = R.drawable.programador_img;
                    break;
                case 27:
                    imagen = R.drawable.pinzas_img;
                    break;
                case 28:
                    imagen = R.drawable.proto_img;
                    break;
                case 29:
                    imagen = R.drawable.radiofrecuencia_img;
                    break;
                case 30:
                    imagen = R.drawable.res_smd;
                    break;
                case 31:
                    imagen = R.drawable.resistencia_img;
                    break;
                case 32:
                    imagen = R.drawable.relevador_img;
                    break;
                case 33:
                    imagen = R.drawable.sensores_img;
                    break;
                case 34:
                    imagen = R.drawable.servomotor_img;
                    break;
                case 35:
                    imagen = R.drawable.switch_img;
                    break;
                case 36:
                    imagen = R.drawable.miscelaneo_img;
                    break;
                case 37:
                    imagen = R.drawable.teclado_img;
                    break;
                case 38:
                    imagen = R.drawable.transformadores_img;
                    break;
                case 39:
                    imagen = R.drawable.lineal_img;
                    break;
                case 40:
                    imagen = R.drawable.trimpot_img;
                    break;
            }
            // Procesamos los datos a renderizar
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