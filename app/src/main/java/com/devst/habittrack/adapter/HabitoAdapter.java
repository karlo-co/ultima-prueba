package com.devst.habittrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devst.habittrack.R;
import com.devst.habittrack.model.Habito;
import java.util.List;

public class HabitoAdapter extends RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder> {

    public interface OnHabitoActionListener {
        void onEditar(Habito habito);
        void onEliminar(Habito habito);
    }

    private final List<Habito> lista;
    private final OnHabitoActionListener listener;

    public HabitoAdapter(List<Habito> lista, OnHabitoActionListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habito, parent, false);
        return new HabitoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitoViewHolder holder, int position) {
        Habito h = lista.get(position);
        holder.tvNombre.setText(h.getNombre());
        holder.tvDetalle.setText(h.getCategoria() + " • " + h.getHoraRecordatorio() + " hrs");
        holder.tvEstado.setText("Estado: " + h.getEstado());

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(h));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(h));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class HabitoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalle, tvEstado;
        Button btnEditar, btnEliminar;

        public HabitoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreHabito);
            tvDetalle = itemView.findViewById(R.id.tvDetalleHabito);
            tvEstado = itemView.findViewById(R.id.tvEstadoHabito);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}