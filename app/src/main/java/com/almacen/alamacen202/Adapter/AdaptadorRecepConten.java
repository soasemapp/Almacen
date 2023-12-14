package com.almacen.alamacen202.Adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.RecepConten;
import com.almacen.alamacen202.SetterandGetters.Traspasos;

import java.util.ArrayList;

public class AdaptadorRecepConten extends RecyclerView.Adapter<AdaptadorRecepConten.ViewHolderRecepConten> {

    private ArrayList<RecepConten> datos;
    private int index;
    public AdaptadorRecepConten(ArrayList<RecepConten> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdaptadorRecepConten.ViewHolderRecepConten onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_recep_conten,
                null, false);
        return new AdaptadorRecepConten.ViewHolderRecepConten(view);
    }//oncreateViewHolder


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(AdaptadorRecepConten.ViewHolderRecepConten holder, int position) {
        holder.tvPr.setText(datos.get(position).getProducto());
        holder.tvC.setText(datos.get(position).getCantidad());
        holder.tvNu.setText(datos.get(position).getNum());
        holder.tvFol.setText(datos.get(position).getFolio());
        holder.tvPal.setText(datos.get(position).getPalet());

        if(index==position){
            holder.lyRec.setBackgroundResource(R.color.ColorTenue);
        }else{
            holder.lyRec.setBackgroundColor(0);
        }

        if(datos.get(position).getPrioridad().contains("U")){
            holder.tvNu.setTextColor(Color.RED);
            holder.tvPr.setTextColor(Color.RED);
            holder.tvC.setTextColor(Color.RED);
            holder.tvPal.setTextColor(Color.RED);
        }else{
            holder.tvNu.setTextColor(Color.BLACK);
            holder.tvPr.setTextColor(Color.BLACK);
            holder.tvC.setTextColor(Color.BLACK);
            holder.tvPal.setTextColor(Color.BLACK);
        }

        if(datos.get(position).getFolio().equals("")){
            holder.tvFol.setVisibility(View.GONE);
        }else{
            holder.tvFol.setVisibility(View.VISIBLE);
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

    public static class ViewHolderRecepConten extends RecyclerView.ViewHolder {
        TextView tvNu,tvPr, tvC,tvPal,tvFol;
        LinearLayout lyRec;
        public ViewHolderRecepConten (View itemView) {
            super(itemView);
            tvNu= itemView.findViewById(R.id.tvNu);
            tvPr =  itemView.findViewById(R.id.tvPr);
            tvC =  itemView.findViewById(R.id.tvC);
            tvPal = itemView.findViewById(R.id.tvPal);
            lyRec  = itemView.findViewById(R.id.lyRec);
            tvFol = itemView.findViewById(R.id.tvFol);
        }//constructor
    }//ViewHolderRecepConten class
}//principal
