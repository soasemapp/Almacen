package com.almacen.alamacen202.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.DifUbiExist;
import com.almacen.alamacen202.SetterandGetters.ProdEtiq;

import java.util.ArrayList;

public class AdapterListProd extends RecyclerView.Adapter<AdapterListProd.ViewHolderListProd> {

    private ArrayList<ProdEtiq> datos;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int index;

    public AdapterListProd(ArrayList<ProdEtiq> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdapterListProd.ViewHolderListProd onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_prod,
                null, false);
        return new AdapterListProd.ViewHolderListProd(view);
    }//oncreateViewHolder

    @Override
    public void onBindViewHolder(AdapterListProd.ViewHolderListProd holder, int position) {
        holder.n.setText(datos.get(position).getNum());
        holder.tvP.setText(datos.get(position).getProd());
        holder.tvDescrip.setText(datos.get(position).getDescrip());

        if(index==position){
            holder.lyList.setBackgroundResource(R.color.ColorGris);
        }else{
            holder.lyList.setBackgroundColor(0);
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

    public static class ViewHolderListProd extends RecyclerView.ViewHolder {
        TextView n,tvP, tvDescrip;
        LinearLayout lyList;
        public ViewHolderListProd (View itemView) {
            super(itemView);
            n= itemView.findViewById(R.id.tvN);
            tvP =  itemView.findViewById(R.id.tvP);
            tvDescrip =  itemView.findViewById(R.id.tvDescrip);
            lyList = itemView.findViewById(R.id.lyList);
        }//constructor
    }//AdapterViewHolder class
    public void filterList(ArrayList<ProdEtiq> filterdProd) {
        this.datos = filterdProd;
        notifyDataSetChanged();
    }//filterList
}//principal
