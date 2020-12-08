package com.ventas.havr.havrventas.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ventas.havr.havrventas.MaterialSKU;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.R;

import java.io.File;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterCotHolder extends BaseAdapter {

    public int Precio = 0;
    private static final String TAG = "CotHolder";
    private Context context;
    private int baseCotID;
    private List<BasePedidos> list;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private BaseSKU BaseSKU;
    private int layout;
    private int tipo;
    private int aumentar = 1;
    private int disminuir = 0;
    private int Dato = 0;
    private String PreciosCompleta;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseImagenes BaseImagenes;
    public Realm realm;
    private List<BaseSKU> ListaSKU;
    private BaseSKU baseSKU;
    private int NumCotizacion;
    public int NumPiezasAgregar = 0;
    private BaseCotizaciones baseCotizaciones;


    public AdapterCotHolder(Context context, List<BasePedidos> BaseDatos, int layout,
                            int baseCotID, String PreciosCompleta, int tipo){
        this.context = context;
        this.list = BaseDatos;
        this.layout = layout;
        this.baseCotID = baseCotID;
        this.PreciosCompleta = PreciosCompleta;
        this.tipo = tipo;
    }

    @Override
    public int getCount() {
        return list.size() ;
    }

    @Override
    public BasePedidos getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        realm.init(context);
        realm = Realm.getDefaultInstance();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout,null);
            vh = new ViewHolder();
            vh.Sku = (TextView) convertView.findViewById(R.id.tx_pedido_sku);
            vh.Descripcion = (TextView) convertView.findViewById(R.id.tx_pedido_descripcion);
            vh.Precio = (TextView) convertView.findViewById(R.id.tx_calidad);
            vh.BtEliminar = (Button) convertView.findViewById(R.id.bt_eliminar_objeto);
            vh.BtDisminuir = (Button) convertView.findViewById(R.id.bt_disminuir_piezas);
            vh.BtAumentar = (Button) convertView.findViewById(R.id.bt_agregar_piezas);
            vh.BtSubirBajar = convertView.findViewById(R.id.bt_subir_bajar);
            vh.Cantidad = (TextView) convertView.findViewById(R.id.tx_pedido_cantidad);
            vh.ImageComponentes = convertView.findViewById(R.id.imagen_pedidos);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        BasePedidos baseListaSKU = list.get(position);
        String Precio = "$0.0";
        String SKURevisar = baseListaSKU.getSKU();
        try {
            ResulstBaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", SKURevisar).findAll();
            BaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", SKURevisar).findFirst();
            try {
                if(BaseImagenes.getLink() != "NO")
                    Glide.with(context).load(BaseImagenes.getLink()).into(vh.ImageComponentes);
                else
                    Glide.with(context).load("https://imagizer.imageshack.com/v2/640x480q90/923/tKIHkA.png").into(vh.ImageComponentes);
            }catch (Exception e){
                Glide.with(context).load("https://imagizer.imageshack.com/v2/640x480q90/923/tKIHkA.png").into(vh.ImageComponentes);
            }
        }catch (Exception e){
            vh.ImageComponentes.setImageResource(R.drawable.ic_question);
        }

        vh.BtAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cantidad = ObtenerCantidad(position);
                String precio = ObtenerPrecio(position);
                String[] PrecioSt = precio.split(" ");
                PrecioSt[1] = PrecioSt[1].replace(",","");
                float floatPrecio = Float.parseFloat(PrecioSt[1]);
                int IntCantidad = Integer.parseInt(cantidad);
                BasePedidos DataBase = list.get(position);
                IntCantidad = CantidadDisponible(position, IntCantidad,aumentar,DataBase.getSKU());
                if(IntCantidad != 0)
                    GuardarBase(position,IntCantidad + "");
                String Total = String.format("%,.2f", PonerPrecio(floatPrecio,IntCantidad));
                vh.Precio.setText(precio + " ■ Total $ " + Total);
                vh.Cantidad.setText("Cantidad: "+IntCantidad);
            }
        });

        vh.BtDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cantidad = ObtenerCantidad(position);
                String precio = ObtenerPrecio(position);
                String[] PrecioSt = precio.split(" ");
                PrecioSt[1] = PrecioSt[1].replace(",","");
                int IntCantidad = Integer.parseInt(cantidad);
                float floatPrecio = Float.parseFloat(PrecioSt[1]);
                IntCantidad = IntCantidad - 1;
                if(IntCantidad == 0)
                    IntCantidad = 1;
                GuardarBase(position,IntCantidad + "");
                String Total = String.format("%,.2f", PonerPrecio(floatPrecio,IntCantidad));
                vh.Precio.setText(precio + " ■ Total $ " + Total);
                vh.Cantidad.setText("Cantidad: "+IntCantidad);
            }
        });

        vh.BtSubirBajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasePedidos DataBase = list.get(position);
                BaseSKU = realm.where(BaseSKU.class)
                        .contains("SKU",DataBase.getSKU(), Case.INSENSITIVE)
                        .findFirst();
                showAlertForCreatingCot(BaseSKU, "Carrito","¿Cuantas piezas necesita?",position);
            }
        });

        vh.BtEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogError("¿Seguro que desea eliminar el componente?",
                        "Pedidos", position);
            }
        });
        BasePedidos DataBase = list.get(position);
        String SKU = DataBase.getSKU();
        String descripcion = DataBase.getDescripcion();
        String Price = DataBase.getPrecio();
        String[] PrecioSt = Price.split(" ");
        PrecioSt[1] = PrecioSt[1].replace(",","");
        float floatPrecio = Float.parseFloat(PrecioSt[1]);
        String cantidad = DataBase.getCantidad();
        int Intcantidad = Integer.parseInt(cantidad);
        vh.Sku.setText(SKU);
        vh.Descripcion.setText(descripcion);
        String Total = String.format("%,.2f", PonerPrecio(floatPrecio,Intcantidad));
        vh.Precio.setText(Price + " ■ Total $ " + Total);
        vh.Cantidad.setText("Cantidad: "+cantidad);
        Log.e(TAG,"Información procesar:");
        RevisarExistencias(position,SKURevisar);
        return convertView;
    }

    private void RevisarExistencias(int position, String sku)
    {
        BaseSKU = realm.where(BaseSKU.class)
                .contains("SKU",sku, Case.INSENSITIVE)
                .findFirst();
        try {
            String cantidad = BaseSKU.getCantidad();
        }catch (Exception e ){
            BaseCotizaciones baseCotizaciones;
            baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",baseCotID).findFirst();
            realm.beginTransaction();
            baseCotizaciones.getBasePedidos().get(position).deleteFromRealm();
            realm.commitTransaction();
            Log.d(TAG,"Se elimino el artículo");
            Toast.makeText(context, "El artículo ya no se encuentra disponible", Toast.LENGTH_SHORT).show();
        }
    }
    private int CantidadDisponible(int position, int intCantidad, int subir_bajar, String sku) {
        BaseSKU = realm.where(BaseSKU.class)
                .contains("SKU",sku, Case.INSENSITIVE)
                .findFirst();
        try{
            String cantidad = BaseSKU.getCantidad();
            int cantidadInt = Integer.parseInt(cantidad);
            if(intCantidad >= cantidadInt)
                intCantidad = cantidadInt ;
            else
                intCantidad = intCantidad + 1;
            return intCantidad;
        }catch (Exception e){
            BaseCotizaciones baseCotizaciones;
            baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",baseCotID).findFirst();
            realm.beginTransaction();
            baseCotizaciones.getBasePedidos().get(position).deleteFromRealm();
            realm.commitTransaction();
            Log.d(TAG,"Se elimino el artículo");
            Toast.makeText(context, "El artículo ya no se encuentra disponible", Toast.LENGTH_SHORT).show();
            intCantidad = 0;
            return intCantidad;
        }
    }

    public class ViewHolder{
        TextView Sku;
        TextView Descripcion;
        TextView Precio;
        TextView Cantidad;
        Button BtEliminar;
        Button BtAumentar;
        Button BtDisminuir;
        Button BtSubirBajar;
        ImageView ImageComponentes;
    }

    private void showAlertForCreatingCot(BaseSKU baseSKU, String title, String message, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_componente, null);
        builder.setView(viewInflate);

        EditText input = (EditText) viewInflate.findViewById(R.id.editText_dialog_componentes);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String NumPiezas = input.getText().toString().trim();
                if (NumPiezas.length() > 0) {

                    try {
                        String piezasDisponiblesST = baseSKU.getCantidad();
                        int piezasDisponible = Integer.parseInt(piezasDisponiblesST);
                        NumPiezasAgregar = Integer.parseInt(NumPiezas);
                        if(NumPiezasAgregar > piezasDisponible){
                            NumPiezasAgregar = piezasDisponible;
                            Toast.makeText(context, "Se agregaron solo las piezas disponibles.", Toast.LENGTH_SHORT).show();
                        }
                        if(NumPiezasAgregar == 0)
                            NumPiezasAgregar = 1;
                        GuardarBase(position, NumPiezasAgregar + "");
                    }catch (Exception e){
                        Toast.makeText(context, "No se pudo agregar el componente.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "No se ha escrito un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void GuardarBase(int position, String cantidad){
        BaseCotizaciones baseCotizaciones;
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",baseCotID).findFirst();
        realm.beginTransaction();
        baseCotizaciones.getBasePedidos().get(position).setCantidad(cantidad);
        realm.copyToRealmOrUpdate(baseCotizaciones);
        realm.commitTransaction();
        Log.d(TAG,"Almacenanda cantidad");
    }

    private String ObtenerCantidad(int position){
        String cantidad = "0";
        BaseCotizaciones baseCotizaciones;
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",baseCotID).findFirst();
        cantidad = baseCotizaciones.getBasePedidos().get(position).getCantidad();
        Log.d(TAG,"Obtenemos cantidad");
        return cantidad;
    }

    private String ObtenerPrecio(int position){
        String precio = "0";
        BaseCotizaciones baseCotizaciones;
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",baseCotID).findFirst();
        precio = baseCotizaciones.getBasePedidos().get(position).getPrecio();
        Log.d(TAG,"Obtenemos precio");
        return precio;
    }

    private float PonerPrecio(float precio, int cantidad){
        return (float) precio*cantidad;
    }


    public void DialogError(String message, String title, int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BaseCotizaciones baseCotizaciones;
                        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",baseCotID).findFirst();
                        realm.beginTransaction();
                        baseCotizaciones.getBasePedidos().get(posicion).deleteFromRealm();
                        realm.commitTransaction();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setTitle(title)
                .setIcon(R.drawable.bt_delete)
                .setInverseBackgroundForced(true);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
