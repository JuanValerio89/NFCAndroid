package com.ventas.havr.havrventas.Adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class CotAdapter extends BaseAdapter {

    private static final String TAG = "CotAdapter";
    private Context context;
    private List<BaseCotizaciones> cotizaciones;
    private int layout;
    private SharedPreferences.Editor editor;

    public CotAdapter(Context context, List<BaseCotizaciones> cotizaciones, int layout) {
        this.context = context;
        this.cotizaciones = cotizaciones;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return cotizaciones.size();
    }

    @Override
    public BaseCotizaciones getItem(int position) {
        return cotizaciones.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.folio = (TextView) convertView.findViewById(R.id.text_sku_recycler);
            vh.componentes = (TextView) convertView.findViewById(R.id.text_componentes);
            vh.cotizacion = (TextView) convertView.findViewById(R.id.text_cot);
            vh.fecha = (TextView) convertView.findViewById(R.id.text_fecha);
            vh.imagen = (ImageView) convertView.findViewById(R.id.imagen_producto);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        LinearLayout linear = convertView.findViewById(R.id.linear_list_proyectos_a);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        int NumCotizacion = prefs.getInt("Cotizacion", 0);
        Log.d(TAG,"Valor de la posicion:"+NumCotizacion);
        if(NumCotizacion == cotizaciones.get(position).getId()) {
            linear.setBackgroundColor(context.getResources().getColor(R.color.icons));
            vh.imagen.setImageResource(R.drawable.ic_verificacion);
        }
        else {
            linear.setBackgroundColor(context.getResources().getColor(R.color.icons));
            vh.imagen.setImageResource(R.drawable.ic_action_add);
        }


        BaseCotizaciones baseCotizaciones = cotizaciones.get(position);
        int numeroPerfiles = baseCotizaciones.getBasePedidos().size();
        String textoPerfiles = (numeroPerfiles == 1) ? numeroPerfiles + " Componente" : numeroPerfiles + " Componentes";
        try {
            String datosCot = baseCotizaciones.getCotizacion();
            String[] separar = datosCot.split("-");
            vh.folio.setText("Folio: " + separar[0]);
            vh.cotizacion.setText("Cotizacion: " + separar[1]);
            vh.componentes.setText("Total piezas: "+textoPerfiles);
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String createAt = df.format(baseCotizaciones.getCreatedDate());
            vh.fecha.setText(createAt);
        }catch (Exception e){

        }
        return convertView;
    }

    public class ViewHolder {
        TextView folio;
        TextView cotizacion;
        TextView componentes;
        TextView fecha;
        ImageView imagen;
    }
}
