package com.ventas.havr.havrventas.Adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class AdaptadorSKU extends BaseAdapter {

    private Context context;
    private List<BaseSKU> ListaSKU;
    private List<BaseImagenes> ListaImagenes;
    private int layout;
    private int tipo;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseImagenes BaseImagenes;
    public Realm realm;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    public AdaptadorSKU(Context context, List<BaseSKU> sku,List<BaseImagenes> imagenes, int layout, int tipo){
        this.context = context;
        this.ListaSKU = sku;
        this.ListaImagenes = imagenes;
        this.layout = layout;
        this.tipo = tipo;
    }

    @Override
    public int getCount() {
        return ListaSKU.size();
    }

    @Override
    public Object getItem(int position) {
        return ListaSKU.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.Titulo = (TextView) convertView.findViewById(R.id.text_cot);
            vh.Cantidad = (TextView) convertView.findViewById(R.id.text_fecha);
            vh.Precio = (TextView) convertView.findViewById(R.id.text_componentes);
            vh.Descripcion = (TextView) convertView.findViewById(R.id.text_folio);
            vh.ImagenProducto = (ImageView) convertView.findViewById(R.id.imagen_producto);


            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        BaseSKU baseListaSKU = ListaSKU.get(position);
        String Precio = "$0.0";
        if(tipo == 1){
            Precio = baseListaSKU.getPrecio();
        }else{
            Precio = baseListaSKU.getPrecioPublico();
        }
        String SKURevisar = baseListaSKU.getSKU();
        Log.d("Adaptador SKU","Tamaño SkuRevisar:"+SKURevisar.length());
        realm = Realm.getDefaultInstance();
        try {
            ResulstBaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", SKURevisar).findAll();
            BaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", SKURevisar).findFirst();
            try {
                Glide.with(context).load(BaseImagenes.getLink()).into(vh.ImagenProducto);
            }catch (Exception e){
                Glide.with(context).load("https://imagizer.imageshack.com/v2/640x480q90/923/tKIHkA.png").into(vh.ImagenProducto);
            }
        }catch (Exception e){
            vh.ImagenProducto.setImageResource(R.drawable.ic_question);
            // Toast.makeText(context,"No se encontraron imagenes",Toast.LENGTH_LONG).show();
        }
        String Cantidad = baseListaSKU.getCantidad();
        String Titulo = baseListaSKU.getSKU();
        String Descripcion = baseListaSKU.getDescripcion();
        vh.Cantidad.setText("Cantidad: " + Cantidad);
        vh.Precio.setText("" + Precio);
        vh.Descripcion.setText(Titulo);
        vh.Titulo.setText(Descripcion);

        return convertView;
    }
    public class ViewHolder{
        TextView Titulo;
        TextView Descripcion;
        TextView Precio;
        TextView Cantidad;
        ImageView ImagenProducto;

    }
}
