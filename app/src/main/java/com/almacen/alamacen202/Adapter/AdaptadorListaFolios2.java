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

public class AdaptadorListaFolios2 extends RecyclerView.Adapter<AdaptadorListaFolios2.ViewHolderCarrito> implements View.OnClickListener {

    ArrayList<Folios> listaProd;
    private View.OnClickListener listener;

    public AdaptadorListaFolios2(ArrayList<Folios> listaProd) {
        this.listaProd = listaProd;
    }

    @Override
    public AdaptadorListaFolios2.ViewHolderCarrito onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_folios2, null, false);
        view.setOnClickListener(this);
        return new AdaptadorListaFolios2.ViewHolderCarrito(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorListaFolios2.ViewHolderCarrito holder, int position) {
        holder.tvFolio.setText(listaProd.get(position).getFolio());
        holder.tvFecha.setText(listaProd.get(position).getFecha());
        holder.tvHora.setText(listaProd.get(position).getHora());
    }


    @Override
    public int getItemCount() {
        return listaProd.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;

    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);

        }
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
