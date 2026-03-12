package nelandac.app.herramientacensador.modelos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nelandac.app.herramientacensador.R;

public class VisitasAdapter extends RecyclerView.Adapter<VisitasAdapter.ViewHolder> {

    private List<Visita> lista;
    private Context context;

    public VisitasAdapter(Context context, List<Visita> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_visita, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Visita visita = lista.get(position);

        holder.txtNombre.setText(visita.getNombreComercial());
        holder.txtCliente.setText(visita.getNombreCliente());
        holder.txtTelefono.setText(visita.getTelefono());
        holder.txtDia.setText(visita.getDiaVisita());

        String coords = visita.getLatitud() + ", " + visita.getLongitud();
        holder.txtCoordenadas.setText(coords);

        // Cargar imagen con Glide
        if(visita.getFotoNegocio() != null && !visita.getFotoNegocio().isEmpty()){

            Glide.with(context)
                    .load(visita.getFotoNegocio())
                    .centerCrop()
                    .into(holder.imgFoto);

        }

        holder.btnMaps.setOnClickListener(v -> {

            String uri = "google.navigation:q=" +
                    visita.getLatitud() + "," +
                    visita.getLongitud();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFoto;
        TextView txtNombre, txtCliente, txtTelefono, txtDia, txtCoordenadas;
        Button btnMaps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFoto = itemView.findViewById(R.id.imgFoto);
            txtNombre = itemView.findViewById(R.id.txtNombreComercial);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            txtDia = itemView.findViewById(R.id.txtDiaVisita);
            txtCoordenadas = itemView.findViewById(R.id.txtCoordenadas);
            btnMaps = itemView.findViewById(R.id.btnMaps);
        }
    }
}
