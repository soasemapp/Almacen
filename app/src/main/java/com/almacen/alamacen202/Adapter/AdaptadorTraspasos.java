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
import com.almacen.alamacen202.SetterandGetters.Traspasos;

import java.util.ArrayList;

public class AdaptadorTraspasos extends RecyclerView.Adapter<AdaptadorTraspasos.ViewHolderTraspasos> {

    private ArrayList<Traspasos> datos;
    private int index;
    public AdaptadorTraspasos(ArrayList<Traspasos> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdaptadorTraspasos.ViewHolderTraspasos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_traspaso,
                null, false);
        return new AdaptadorTraspasos.ViewHolderTraspasos(view);
    }//oncreateViewHolder

    @Override
    public void onBindViewHolder(AdaptadorTraspasos.ViewHolderTraspasos holder, int position) {
        holder.Producto.setText(datos.get(position).getProducto());
        holder.Cantidad.setText(datos.get(position).getCantidad());
        holder.n.setText(datos.get(position).getNum());
        holder.CantSurt.setText(datos.get(position).getCantSurt());
        holder.ubi.setText(datos.get(position).getUbic());
        holder.itExist.setText(datos.get(position).getExist());

        if(index==position){
            holder.lyaout.setBackgroundResource(R.color.colorSelec);//seleccion
            if(Integer.parseInt(datos.get(position).getCantSurt())>0 &&
                    Integer.parseInt(datos.get(position).getCantSurt())==
                            Integer.parseInt(datos.get(position).getCantidad())){
                holder.Producto.setTextColor(Color.parseColor("#32997C"));
                holder.Cantidad.setTextColor(Color.parseColor("#32997C"));
                holder.n.setTextColor(Color.parseColor("#32997C"));
                holder.CantSurt.setTextColor(Color.parseColor("#32997C"));
                holder.ubi.setTextColor(Color.parseColor("#32997C"));
                holder.itExist.setTextColor(Color.parseColor("#32997C"));
            }else if(datos.get(position).isSincronizado()==false){
                holder.Producto.setTextColor(Color.parseColor("#223CCA"));
                holder.Cantidad.setTextColor(Color.parseColor("#223CCA"));
                holder.n.setTextColor(Color.parseColor("#223CCA"));
                holder.CantSurt.setTextColor(Color.parseColor("#223CCA"));
                holder.ubi.setTextColor(Color.parseColor("#223CCA"));
                holder.itExist.setTextColor(Color.parseColor("#223CCA"));
            }else{
                holder.Producto.setTextColor(Color.parseColor("#000000"));
                holder.Cantidad.setTextColor(Color.parseColor("#B61B1B"));
                holder.n.setTextColor(Color.parseColor("#043B72"));
                holder.CantSurt.setTextColor(Color.parseColor("#043B72"));
                holder.ubi.setTextColor(Color.parseColor("#043B72"));
                holder.itExist.setTextColor(Color.parseColor("#1E739A"));
            }//else si ya se termino de escanear
        }else{
            if(Integer.parseInt(datos.get(position).getCantSurt())>0 &&
                    Integer.parseInt(datos.get(position).getCantSurt())==
                            Integer.parseInt(datos.get(position).getCantidad())){
                holder.lyaout.setBackgroundResource(R.color.ColorSinc);
            }else{
                holder.lyaout.setBackgroundColor(0);
            }
            if(datos.get(position).isSincronizado()){
                holder.Producto.setTextColor(Color.parseColor("#000000"));
                holder.Cantidad.setTextColor(Color.parseColor("#B61B1B"));
                holder.n.setTextColor(Color.parseColor("#043B72"));
                holder.CantSurt.setTextColor(Color.parseColor("#043B72"));
                holder.ubi.setTextColor(Color.parseColor("#043B72"));
                holder.itExist.setTextColor(Color.parseColor("#1E739A"));
            }else{
                holder.Producto.setTextColor(Color.parseColor("#223CCA"));
                holder.Cantidad.setTextColor(Color.parseColor("#223CCA"));
                holder.n.setTextColor(Color.parseColor("#223CCA"));
                holder.CantSurt.setTextColor(Color.parseColor("#223CCA"));
                holder.ubi.setTextColor(Color.parseColor("#223CCA"));
                holder.itExist.setTextColor(Color.parseColor("#223CCA"));
            }//else
        }//else no esta seleccionado
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

    public static class ViewHolderTraspasos extends RecyclerView.ViewHolder {
        TextView n,Producto, Cantidad,CantSurt,ubi,itExist;
        LinearLayout lyaout;
        public ViewHolderTraspasos (View itemView) {
            super(itemView);
            n= itemView.findViewById(R.id.tvN);
            Producto =  itemView.findViewById(R.id.Producto);
            Cantidad =  itemView.findViewById(R.id.Cantidad);
            CantSurt = itemView.findViewById(R.id.CantSurt);
            ubi = itemView.findViewById(R.id.ubi);
            itExist = itemView.findViewById(R.id.itExist);
            lyaout  = itemView.findViewById(R.id.lyaout);
        }//constructor
    }//AdapterTraspasosViewHolder class
}//principal
