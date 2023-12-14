package com.almacen.alamacen202.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.CAJASSANDG;

import java.util.ArrayList;

public class AdapterListaCajas extends RecyclerView.Adapter<AdapterListaCajas.ViewHolderCarrito>{

    ArrayList<CAJASSANDG> listaCajas;
    private int index;

    public AdapterListaCajas(ArrayList<CAJASSANDG> listaCajas) {
        this.listaCajas = listaCajas;
    }

    @Override
    public AdapterListaCajas.ViewHolderCarrito onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_cajas_gen, null, false);
        return new AdapterListaCajas.ViewHolderCarrito(view);
    }

    @Override
    public void onBindViewHolder(AdapterListaCajas.ViewHolderCarrito holder, int position) {
        holder.tvCaja.setText(listaCajas.get(position).getClavedelProdcuto());
        holder.tvCant.setText(listaCajas.get(position).getCantidadUnidades());

        if(index==position){
            holder.lyCaja.setBackgroundResource(R.color.ColorTenue);

        }else{
            holder.lyCaja.setBackgroundColor(0);
        }
    }
    public int index(int index){
        this.index=index;
        return index;
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }//getItemType

    @Override
    public int getItemCount() {
        return listaCajas.size();
    }
    public class ViewHolderCarrito extends RecyclerView.ViewHolder {
        private TextView tvCaja,tvCant;
        private LinearLayout lyCaja;
        public ViewHolderCarrito(View itemView) {
            super(itemView);
            tvCaja = itemView.findViewById(R.id.tvCaja);
            tvCant = itemView.findViewById(R.id.tvCant);
            lyCaja = itemView.findViewById(R.id.lyCaja);
        }
    }
}
