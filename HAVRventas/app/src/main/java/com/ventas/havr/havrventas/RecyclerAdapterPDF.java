package com.ventas.havr.havrventas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.ventas.havr.havrventas.Adaptadores.MyAdapter;
import com.ventas.havr.havrventas.Adaptadores.TutorialesVo;
import com.ventas.havr.havrventas.Interface.IProductClickListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterPDF extends  RecyclerView.Adapter<RecyclerAdapterPDF.ViewHolderTutoriales> implements MyAdapter.OnItemClickListener, View.OnClickListener {
    ArrayList<TutorialesVo> listaTutoriales;
    private View.OnClickListener listener;

    ActividadEscogerPDF actividadEscogerPDF;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;


    public RecyclerAdapterPDF(ActividadEscogerPDF actividadEscogerPDF, ArrayList<TutorialesVo> listaTutoriales, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.actividadEscogerPDF = actividadEscogerPDF;
        this.listaTutoriales = listaTutoriales;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public ViewHolderTutoriales onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(actividadEscogerPDF.getBaseContext()).inflate(R.layout.lista_tutoriales,null,false);
        view.setOnClickListener(this);
        return new ViewHolderTutoriales(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTutoriales holder, int position) {
        holder.TxTitulo.setText(skuDetailsList.get(position).getTitle());
        holder.TxDescripcion.setText(skuDetailsList.get(position).getDescription());
        holder.TxPrecio.setText(skuDetailsList.get(position).getPrice());
        // Product click
        holder.setiProductClickListener(new IProductClickListener() {
            @Override
            public void onProductClickListener(View view, int position) {
                // Launch billing folow
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(position))
                        .build();
                billingClient.launchBillingFlow(actividadEscogerPDF,billingFlowParams);
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
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

    public class ViewHolderTutoriales extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView TxTitulo, TxDescripcion, TxPrecio;
        ImageView IVfoto;
        IProductClickListener iProductClickListener;

        public void setiProductClickListener(IProductClickListener iProductClickListener) {
            this.iProductClickListener = iProductClickListener;
        }

        public ViewHolderTutoriales(View itemView) {
            super(itemView);
            TxTitulo = (TextView) itemView.findViewById(R.id.tx_titulo);
            TxPrecio = (TextView) itemView.findViewById(R.id.text_precio);
            TxDescripcion = (TextView) itemView.findViewById(R.id.tx_descripcioon);
            IVfoto = (ImageView) itemView.findViewById(R.id.imagen_list_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iProductClickListener.onProductClickListener(v,getAdapterPosition());
        }
    }
}