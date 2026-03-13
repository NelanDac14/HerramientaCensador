package nelandac.app.herramientacensador.modelos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.vistas_usuario.Act_NuevaVisita;

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

        holder.txtNumero.setText((position + 1) + ".");
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

        //Manipula la función de cada menú OverFlow del item
        holder.btnMenu.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.inflate(R.menu.menu_visita);

            popup.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();

                if (id == R.id.menuEditar) {
                    editarRegistro(visita);
                    return true;
                }

                if (id == R.id.menuBorrar) {
                    borrarRegistro(visita);
                    return true;
                }

                if (id == R.id.menuWhatsapp) {
                    enviarWhatsApp(visita);
                    return true;
                }

                if (id == R.id.menuMaps) {
                    abrirMaps(visita);
                    return true;
                }

                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFoto, btnMenu;
        TextView txtNumero, txtNombre, txtCliente, txtTelefono, txtDia, txtCoordenadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFoto = itemView.findViewById(R.id.imgFoto);
            btnMenu = itemView.findViewById(R.id.btnMenu);

            txtNumero = itemView.findViewById(R.id.txtNumero);
            txtNombre = itemView.findViewById(R.id.txtNombreComercial);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            txtDia = itemView.findViewById(R.id.txtDiaVisita);
            txtCoordenadas = itemView.findViewById(R.id.txtCoordenadas);
        }
    }

    //Función para abrir google maps para llegar a la ubicación de la visita
    private void abrirMaps(Visita visita){

        String uri = "google.navigation:q=" +
                visita.getLatitud() + "," +
                visita.getLongitud();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        context.startActivity(intent);
    }

    //Función para enviar mensaje por WhatsApp al cliente en caso de tener número
    private void enviarWhatsApp(Visita visita){

        String telefono = visita.getTelefono();

        String url = "https://wa.me/506" + telefono;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        context.startActivity(intent);
    }

    //Función para editar un registro de visita
    private void editarRegistro(Visita visita){

        Intent intent = new Intent(context, Act_NuevaVisita.class);

        intent.putExtra("VISITA_ID", visita.getId());

        context.startActivity(intent);
    }

    private void borrarRegistro(Visita visita){

        new AlertDialog.Builder(context)
                .setTitle("Eliminar visita")
                .setMessage("¿Desea eliminar este registro?")
                .setIcon(android.R.drawable.ic_dialog_alert)

                .setPositiveButton("Eliminar", (dialog, which) -> {

                    VisitaDAO dao = new VisitaDAO(context);

                    Log.d("DELETE_VISITA","Intentando borrar ID: " + visita.getId());

                    dao.deleteVisita(visita.getId());

                    int position = lista.indexOf(visita);
                    lista.remove(position);

                    Toast.makeText(context,"Visita eliminada",Toast.LENGTH_SHORT).show();

                    notifyItemRemoved(position);

                })

                .setNegativeButton("Cancelar", null)

                .show();
    }



}
