package com.ventas.havr.havrventas.Adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseMasVendidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.R;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterMasvendido extends BaseAdapter {

    private Context context;
    private BaseSKU ResulstBaseSKU;
    private RealmResults<BaseSKU> ListaSKU;
    private List<BaseMasVendidos> ListaMasVendidas;
    private List<com.ventas.havr.havrventas.Modelos.BaseImagenes> ListaImagenes;
    private int layout;
    private int tipo;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseImagenes BaseImagenes;
    public Realm realm;
    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;


    public AdapterMasvendido(Context context, RealmResults<BaseSKU> sku, List<BaseImagenes> imagenes, List<BaseMasVendidos> PiezasMasVendidas, int layout, int tipo){
        this.context = context;
        this.ListaSKU = sku;
        this.ListaImagenes = imagenes;
        this.ListaMasVendidas = PiezasMasVendidas;
        this.layout = layout;
        this.tipo = tipo;
    }

    @Override
    public int getCount() {
        return ListaMasVendidas.size();
    }

    @Override
    public Object getItem(int position) {
        return ListaMasVendidas.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        AdapterMasvendido.ViewHolder vh;
        realm = Realm.getDefaultInstance();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new AdapterMasvendido.ViewHolder();
            vh.Titulo = (TextView) convertView.findViewById(R.id.text_cot);
            vh.Cantidad = (TextView) convertView.findViewById(R.id.text_fecha);
            vh.Precio = (TextView) convertView.findViewById(R.id.text_componentes);
            vh.Descripcion = (TextView) convertView.findViewById(R.id.text_folio);
            vh.ImagenProducto = (ImageView) convertView.findViewById(R.id.imagen_producto);

            convertView.setTag(vh);
        }else{
            vh = (AdapterMasvendido.ViewHolder) convertView.getTag();
        }

        BaseMasVendidos baseListaSKU = ListaMasVendidas.get(position);
        try {
            ResulstBaseSKU = realm.where(BaseSKU.class)
                    .contains("SKU", baseListaSKU.getSKU(), Case.INSENSITIVE)
                    .findFirst();
            String Precio = "$0.0";
            if (tipo == 1) {
                Precio = ResulstBaseSKU.getPrecio();
            } else {
                Precio = ResulstBaseSKU.getPrecioPublico();
            }
            String SKURevisar = baseListaSKU.getSKU();
            realm = Realm.getDefaultInstance();
            try {
                ResulstBaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", SKURevisar).findAll();
                BaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", SKURevisar).findFirst();
                Glide.with(context).load(BaseImagenes.getLink()).into(vh.ImagenProducto);
            } catch (Exception e) {
                vh.ImagenProducto.setImageResource(R.drawable.ic_question);
                // Toast.makeText(context,"No se encontraron imagenes",Toast.LENGTH_LONG).show();
            }
            String Cantidad = ResulstBaseSKU.getCantidad();
            String Titulo = baseListaSKU.getSKU();
            String Descripcion = ResulstBaseSKU.getDescripcion();
            vh.Cantidad.setText("Cantidad: " + Cantidad);
            vh.Precio.setText("Precio: " + Precio);
            vh.Descripcion.setText(Titulo);
            vh.Titulo.setText(Descripcion);
        }catch (Exception e){
            vh.Cantidad.setText("Cantidad: 0");
            vh.Titulo.setText("Articulo no encontrado...");
            vh.Descripcion.setText("Sin SKU");
            vh.Precio.setText("Precio: $0.0");
        }

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
