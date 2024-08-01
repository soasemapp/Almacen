package com.almacen.alamacen202.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Inventario;

import java.util.ArrayList;

public class AdapterInventario extends RecyclerView.Adapter<AdapterInventario.ViewHolderInventario> {

    private ArrayList<Inventario> datos;
    private int index;

    public AdapterInventario(ArrayList<Inventario> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdapterInventario.ViewHolderInventario onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_inventario,
                null, false);
        return new AdapterInventario.ViewHolderInventario(view);
    }//oncreateViewHolder

    @Override
    public void onBindViewHolder(AdapterInventario.ViewHolderInventario holder, int position) {
        holder.tvProd.setText(datos.get(position).getProducto());
        holder.tvCant.setText(datos.get(position).getCantidad());
        holder.tvEscan.setText(datos.get(position).getEscan());
        holder.Ub.setText(datos.get(position).getUbi());
        holder.n.setText(datos.get(position).getNum());

        if(index==position){
            holder.lyInv.setBackgroundResource(R.color.colorSelec);
            /*if(datos.get(position).isSincronizado()){
                holder.tvProd.setTextColor(Color.parseColor("#32997C"));
                holder.tvCant.setTextColor(Color.parseColor("#32997C"));
                holder.tvEscan.setTextColor(Color.parseColor("#32997C"));
                holder.Ub.setTextColor(Color.parseColor("#32997C"));
                holder.n.setTextColor(Color.parseColor("#32997C"));
            }else{
                holder.tvProd.setTextColor(Color.parseColor("#0E0E0E"));
                holder.tvCant.setTextColor(Color.parseColor("#1E739A"));
                holder.tvEscan.setTextColor(Color.parseColor("#043B72"));
                holder.Ub.setTextColor(Color.parseColor("#0E0E0E"));
                holder.n.setTextColor(Color.parseColor("#043B72"));
            }//else
            */
        }else{
            holder.lyInv.setBackgroundColor(0);
            /*if(datos.get(position).isSincronizado()){
                holder.lyInv.setBackgroundResource(R.color.ColorSinc);
                holder.tvProd.setTextColor(Color.parseColor("#32997C"));
                holder.tvCant.setTextColor(Color.parseColor("#32997C"));
                holder.tvEscan.setTextColor(Color.parseColor("#32997C"));
                holder.Ub.setTextColor(Color.parseColor("#32997C"));
                holder.n.setTextColor(Color.parseColor("#32997C"));
            }else{
                holder.lyInv.setBackgroundColor(0);
                holder.tvProd.setTextColor(Color.parseColor("#0E0E0E"));
                holder.tvCant.setTextColor(Color.parseColor("#1E739A"));
                holder.tvEscan.setTextColor(Color.parseColor("#043B72"));
                holder.Ub.setTextColor(Color.parseColor("#0E0E0E"));
                holder.n.setTextColor(Color.parseColor("#043B72"));
            }//
            */
        }//else
    }//onBindViewHolder

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
        return datos.size();
    }

    public static class ViewHolderInventario extends RecyclerView.ViewHolder {
        TextView n,tvProd,tvCant,tvEscan,Ub;
        LinearLayout lyInv;
        public ViewHolderInventario (View itemView) {
            super(itemView);
            n= itemView.findViewById(R.id.tvN);
            tvProd =  itemView.findViewById(R.id.Producto);
            tvCant = itemView.findViewById(R.id.Cantidad);
            tvEscan =  itemView.findViewById(R.id.Escan);
            Ub = itemView.findViewById(R.id.Ub);
            lyInv = itemView.findViewById(R.id.lyInv);
        }//constructor
    }//AdapterInventarioViewHolder class
}//principal
