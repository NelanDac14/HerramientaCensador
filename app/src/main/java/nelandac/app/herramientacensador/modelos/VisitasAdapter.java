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
import nelandac.app.herramientacensador.vistas_usuario.DetalleClienteActivity;
import nelandac.app.herramientacensador.vistas_usuario.MapaVisitasActivity;

/**
 * Adaptador Maestro: VisitasAdapter
 * 
 * Orquesta la representación visual de la cartera de clientes (Visitas) en el listado principal.
 * Implementa una arquitectura de eventos mediante la interfaz OnItemActionListener para
 * desacoplar la lógica de la UI de las acciones de negocio.
 * 
 * Responsabilidades:
 * - Renderizado de items con soporte para imágenes asíncronas (Glide).
 * - Gestión de navegación hacia el expediente detallado del cliente.
 * - Implementación de menús contextuales para acciones rápidas (WhatsApp, Maps, Seguimiento).
 */
public class VisitasAdapter extends RecyclerView.Adapter<VisitasAdapter.ViewHolder> {

    /** Fuente de datos: Lista de clientes registrados. */
    private List<Visita> lista;
    
    /** Contexto de la aplicación para navegación y acceso a servicios. */
    private Context context;

    /**
     * Interfaz de comunicación para acciones externas al adaptador.
     */
    public interface OnItemActionListener {
        /** Gatillo para edición de datos maestros. */
        void onEditar(Visita visita, int position);

        /** Gatillo para el registro de un nuevo evento de bitácora. */
        void onSeguimiento(Visita visita);
    }

    private OnItemActionListener listener;

    /**
     * Constructor parametrizado.
     */
    public VisitasAdapter(Context context, List<Visita> lista, OnItemActionListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflado del layout de item predefinido
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_visita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Visita visita = lista.get(position);

        // Binding de datos textuales
        holder.txtNumero.setText((position + 1) + ".");
        holder.txtNombre.setText(visita.getNombreComercial());
        holder.txtCliente.setText(visita.getNombreCliente());
        holder.txtTelefono.setText(visita.getTelefono());
        holder.txtDia.setText(visita.getDiaVisita());

        // Visualización de coordenadas geográficas capturadas
        String coords = visita.getLatitud() + ", " + visita.getLongitud();
        holder.txtCoordenadas.setText(coords);

        // Carga optimizada de evidencia fotográfica
        if (visita.getFotoNegocio() != null && !visita.getFotoNegocio().isEmpty()) {
            Glide.with(context)
                    .load(visita.getFotoNegocio())
                    .centerCrop()
                    .into(holder.imgFoto);
        }

        if (visita.getModulo() != null && visita.getModulo().contains("Seguimientos:")) {
            holder.txtSeguimientos.setText(visita.getModulo());
        } else {
            holder.txtSeguimientos.setText(R.string.seguimientos_0);
        }

        /**
         * Evento de clic en el contenedor: Navegación al expediente del cliente.
         */
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleClienteActivity.class);
            intent.putExtra("VISITA_ID", visita.getId());
            context.startActivity(intent);
        });

        /**
         * Configuración del menú OverFlow (Opciones rápidas).
         */
        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.inflate(R.menu.menu_visita);

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menuSeguimiento) {
                    if (listener != null) listener.onSeguimiento(visita);
                    return true;
                }

                if (id == R.id.menuEditar) {
                    if (listener != null) listener.onEditar(visita, holder.getAdapterPosition());
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

                if (id == R.id.menuCopiar) {
                    copiarAlPortapapeles(construirTexto(visita));
                    Toast.makeText(context, "Información copiada", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (id == R.id.menuCompartir) {
                    compartirTexto(construirTexto(visita));
                    return true;
                }
                
                if (id == R.id.menuMapaItem) {
                    abrirMapaIndividual(visita);
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

    /**
     * Cache de vistas para optimización de rendimiento.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto, btnMenu;
        TextView txtNumero, txtNombre, txtCliente, txtTelefono, txtDia, txtCoordenadas, txtSeguimientos;

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
            txtSeguimientos = itemView.findViewById(R.id.txtSeguimientos);
        }
    }

    /** Lógica para navegación GPS mediante aplicación externa. */
    private void abrirMaps(Visita visita) {
        String uri = "google.navigation:q=" + visita.getLatitud() + "," + visita.getLongitud();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }

    /** Apertura de canal de comunicación WhatsApp. */
    private void enviarWhatsApp(Visita visita) {
        String telefono = visita.getTelefono();
        String url = "https://wa.me/506" + telefono;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /** Eliminación de registro con confirmación de usuario e integridad en cascada. */
    private void borrarRegistro(Visita visita) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar visita")
                .setMessage("¿Desea eliminar este registro?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    VisitaDAO dao = new VisitaDAO(context);
                    dao.deleteVisita(visita.getId());
                    int position = lista.indexOf(visita);
                    lista.remove(position);
                    Toast.makeText(context, "Visita eliminada", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String construirTexto(Visita v) {
        String linkMaps = "https://maps.google.com/?q=" + v.getLatitud() + "," + v.getLongitud();
        return "📍 *INFORMACIÓN DE VISITA COMERCIAL*\n\n" +
                "🏪 *Nombre:* " + v.getNombreComercial() + "\n" +
                "👤 *Cliente:* " + v.getNombreCliente() + "\n" +
                "📞 *Tel:* " + v.getTelefono() + "\n" +
                "📅 *Día:* " + v.getDiaVisita() + "\n\n" +
                "📌 *Ubicación:*\n" + linkMaps;
    }

    private void copiarAlPortapapeles(String texto) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("visita", texto);
        clipboard.setPrimaryClip(clip);
    }

    private void compartirTexto(String texto) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        context.startActivity(Intent.createChooser(intent, "Compartir vía"));
    }

    private void abrirMapaIndividual(Visita visita) {
        Intent intent = new Intent(context, MapaVisitasActivity.class);
        intent.putExtra("LAT", visita.getLatitud());
        intent.putExtra("LNG", visita.getLongitud());
        intent.putExtra("NOMBRE", visita.getNombreComercial());
        context.startActivity(intent);
    }
}
