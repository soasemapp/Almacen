package com.almacen.alamacen202.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.CAJASSANDG;
import com.almacen.alamacen202.SetterandGetters.CajaXProd;

import java.util.ArrayList;

public class AdaptadorCajaxProd extends RecyclerView.Adapter<AdaptadorCajaxProd.ViewHolderCajaXProd>{

    private ArrayList<CajaXProd> listaCajas;
    private int index;

    public AdaptadorCajaxProd(ArrayList<CajaXProd> listaCajas) {
        this.listaCajas = listaCajas;
    }

    @Override
    public AdaptadorCajaxProd.ViewHolderCajaXProd onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_cajaxprod,
                null, false);
        return new AdaptadorCajaxProd.ViewHolderCajaXProd(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorCajaxProd.ViewHolderCajaXProd holder, int position) {
        holder.tvCaja.setText(listaCajas.get(position).getCaja());
        holder.tvCant.setText(listaCajas.get(position).getCant());
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


    public class ViewHolderCajaXProd extends RecyclerView.ViewHolder {
        private  TextView tvCaja,tvCant;
        public ViewHolderCajaXProd(View itemView) {
            super(itemView);
            tvCaja = (TextView) itemView.findViewById(R.id.tvCaja);
            tvCant = (TextView) itemView.findViewById(R.id.tvCant);
        }
    }
}
