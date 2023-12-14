package com.almacen.alamacen202.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Activity.ActivityDifUbiExi;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.DifUbiExist;

import java.util.ArrayList;

public class AdapterDifUbiExi extends RecyclerView.Adapter<AdapterDifUbiExi.ViewHolderDifUbiExist> {

    private ArrayList<DifUbiExist> datos;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int index;

    public AdapterDifUbiExi(ArrayList<DifUbiExist> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdapterDifUbiExi.ViewHolderDifUbiExist onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dif_ubic_exi,
                null, false);
        return new AdapterDifUbiExi.ViewHolderDifUbiExist(view);
    }//oncreateViewHolder

    @Override
    public void onBindViewHolder(AdapterDifUbiExi.ViewHolderDifUbiExist holder, int position) {
        holder.n.setText(datos.get(position).getNum());
        holder.tvP.setText(datos.get(position).getProducto());
        /*holder.tvC.setText(datos.get(position).getCantidad());
        holder.tvE.setText(datos.get(position).getExistencia());
        holder.tvD.setText(datos.get(position).getDiferencia());
        holder.tvU.setText(datos.get(position).getUbicacion());*/
        holder.tvCC.setText(datos.get(position).getConteo());
        if(index==position){
            holder.lyDif.setBackgroundResource(R.color.colorSelec);//seleccion
            if(Integer.parseInt(datos.get(position).getConteo())>0){
                holder.n.setTextColor(Color.parseColor("#32997C"));
                holder.tvP.setTextColor(Color.parseColor("#32997C"));
                holder.tvCC.setTextColor(Color.parseColor("#32997C"));
            }else{
                holder.n.setTextColor(Color.parseColor("#043B72"));
                holder.tvP.setTextColor(Color.parseColor("#000000"));
                holder.tvCC.setTextColor(Color.parseColor("#043B72"));
            }//else
        }else{
            if(Integer.parseInt(datos.get(position).getConteo())>0){
                holder.lyDif.setBackgroundResource(R.color.ColorSinc);
            }else{
                holder.lyDif.setBackgroundColor(0);
            }
            holder.n.setTextColor(Color.parseColor("#043B72"));
            holder.tvP.setTextColor(Color.parseColor("#000000"));
            holder.tvCC.setTextColor(Color.parseColor("#043B72"));
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

    public static class ViewHolderDifUbiExist extends RecyclerView.ViewHolder {
        TextView n,tvP, tvC,tvE,tvD,tvU,tvCC;
        LinearLayout lyDif;
        public ViewHolderDifUbiExist (View itemView) {
            super(itemView);
            n= itemView.findViewById(R.id.tvN);
            tvP =  itemView.findViewById(R.id.tvP);
            /*tvC =  itemView.findViewById(R.id.tvC);
            tvE =  itemView.findViewById(R.id.tvE);
            tvD =  itemView.findViewById(R.id.tvD);
            tvU =  itemView.findViewById(R.id.tvU);*/
            tvCC =  itemView.findViewById(R.id.tvCC);
            lyDif = itemView.findViewById(R.id.lyDif);
        }//constructor
    }//AdapterSDifUbiExiViewHolder class
}//principal
