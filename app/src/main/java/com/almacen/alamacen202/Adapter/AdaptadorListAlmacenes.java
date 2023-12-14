package com.almacen.alamacen202.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Almacenes;
import com.almacen.alamacen202.SetterandGetters.ListLiberaSandG;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdaptadorListAlmacenes extends RecyclerView.Adapter<AdaptadorListAlmacenes.ViewHolderListAlmacenes>{

    ArrayList<Almacenes> listaAlm;

    public AdaptadorListAlmacenes(ArrayList<Almacenes> listAlm) {
        this.listaAlm = listAlm;
    }

    @Override
    public AdaptadorListAlmacenes.ViewHolderListAlmacenes onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_almacenes, null, false);
        return new AdaptadorListAlmacenes.ViewHolderListAlmacenes(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorListAlmacenes.ViewHolderListAlmacenes holder, int position) {
        holder.tvAlmacen.setText(listaAlm.get(position).getNomAlm());
        holder.tvExistAlm.setText(listaAlm.get(position).getExist());
    }

    private static String formatNumberCurrency(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(number));
    }

    @Override
    public int getItemCount() {
        return listaAlm.size();
    }


    public class ViewHolderListAlmacenes extends RecyclerView.ViewHolder {
        private TextView tvAlmacen,tvExistAlm;
        LinearLayout cambio;

        public ViewHolderListAlmacenes(View itemView) {
            super(itemView);
            tvAlmacen =itemView.findViewById(R.id.tvAlmacen);
            tvExistAlm = itemView.findViewById(R.id.tvExistAlm);
        }
    }
}
