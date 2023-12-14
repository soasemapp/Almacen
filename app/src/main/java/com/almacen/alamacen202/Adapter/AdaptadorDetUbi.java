package com.almacen.alamacen202.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.DetUbi;

import java.util.ArrayList;

public class AdaptadorDetUbi extends RecyclerView.Adapter<AdaptadorDetUbi.ViewHolderDetUbi> {

    private ArrayList<DetUbi> datos;
    private int index;
    public AdaptadorDetUbi(ArrayList<DetUbi> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdaptadorDetUbi.ViewHolderDetUbi onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_det_ubi,
                null, false);
        return new AdaptadorDetUbi.ViewHolderDetUbi(view);
    }//oncreateViewHolder


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(AdaptadorDetUbi.ViewHolderDetUbi holder, int position) {
        holder.tvUb.setText(datos.get(position).getUbicacion());
        holder.tvMax.setText(datos.get(position).getMax());
        holder.tvMin.setText(datos.get(position).getMin());
        holder.tvCant.setText(datos.get(position).getCant());

        if(index==position){
            holder.lyUbi.setBackgroundResource(R.color.ColorTenue);
        }else{
            holder.lyUbi.setBackgroundColor(0);
        }
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

    public static class ViewHolderDetUbi extends RecyclerView.ViewHolder {
        TextView tvUb,tvMax, tvMin,tvCant,tvFol;
        LinearLayout lyUbi;
        public ViewHolderDetUbi (View itemView) {
            super(itemView);
            tvUb= itemView.findViewById(R.id.tvUb);
            tvMax =  itemView.findViewById(R.id.tvMax);
            tvMin =  itemView.findViewById(R.id.tvMin);
            tvCant = itemView.findViewById(R.id.tvCant);
            lyUbi  = itemView.findViewById(R.id.lyUbi);
        }//constructor
    }//ViewHolderDetUbi class
}//principal
