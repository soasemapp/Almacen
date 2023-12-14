package com.almacen.alamacen202.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Folios;

import java.util.ArrayList;

public class AdaptadorListaFolios3 extends RecyclerView.Adapter<AdaptadorListaFolios3.ViewHolderCarrito>{

    ArrayList<Folios> lista;

    public AdaptadorListaFolios3(ArrayList<Folios> listaProd) {
        this.lista= lista;
    }

    @Override
    public AdaptadorListaFolios3.ViewHolderCarrito onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_folios2, null, false);
        return new AdaptadorListaFolios3.ViewHolderCarrito(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorListaFolios3.ViewHolderCarrito holder, int position) {
        holder.tvFolio.setText(lista.get(position).getFolio());
        holder.tvFecha.setText(lista.get(position).getFecha());
        holder.tvHora.setText(lista.get(position).getHora());
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }


    public class ViewHolderCarrito extends RecyclerView.ViewHolder {
        TextView  tvFolio,tvFecha,tvHora;
        LinearLayout cardla;

        public ViewHolderCarrito(View itemView) {
            super(itemView);
            tvFolio = (TextView) itemView.findViewById(R.id.tvFolio);
            tvFecha = (TextView) itemView.findViewById(R.id.tvFecha);
            tvHora = (TextView) itemView.findViewById(R.id.tvHora);
            cardla = (LinearLayout)itemView.findViewById(R.id.cartable);
        }
    }
}
