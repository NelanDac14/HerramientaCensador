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
import nelandac.app.herramientacensador.vistas_usuario.MapaVisitasActivity;

/**
 * Clase VisitasAdapter
 * 
 * Implementación de un RecyclerView. Adapter diseñado para gestionar la visualización y las acciones
 * de una lista de objetos de tipo Visita. Esta clase encapsula la lógica de inflado de vistas,
 * vinculación de datos (binding) y la gestión de eventos de usuario mediante un menú contextual.
 */
public class VisitasAdapter extends RecyclerView.Adapter<VisitasAdapter.ViewHolder> {

    // Fuente de datos para el adaptador
    private List<Visita> lista;
    
    // Contexto de ejecución para acceso a recursos y servicios del sistema
    private Context context;

    /**
     * Interfaz OnItemActionListener
     * 
     * Define el contrato para la comunicación entre el adaptador y la actividad/fragmento contenedor
     * para acciones que requieren procesamiento fuera del scope del adaptador (ej. navegación para edición).
     */
    public interface OnItemActionListener {
        /**
         * Notifica la intención del usuario de editar un registro específico.
         * @param visita Objeto Visita seleccionada.
         * @param position Posición del elemento en la lista.
         */
        void onEditar(Visita visita, int position);
    }

    // Instancia del escuchador de acciones
    private OnItemActionListener listener;

    /**
     * Constructor de VisitasAdapter.
     * 
     * @param context Contexto de la aplicación.
     * @param lista Lista de visitas a ser renderizada.
     * @param listener Implementación de la interfaz de acciones de item.
     */
    public VisitasAdapter(Context context, List<Visita> lista, OnItemActionListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    /**
     * Crea nuevas instancias de ViewHolder inflando el diseño XML correspondiente a un item.
     * 
     * @param parent El ViewGroup en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista (no utilizado en esta implementación simple).
     * @return Una nueva instancia de ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflado del layout de item individual definido en R.layout.item_visita
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_visita, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Vincula los datos de un objeto Visita con los componentes visuales del ViewHolder.
     * También configura los escuchadores de eventos para las interacciones del item.
     * 
     * @param holder El ViewHolder que debe ser actualizado.
     * @param position La posición del elemento dentro de la fuente de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Obtención del objeto de datos según la posición solicitada
        Visita visita = lista.get(position);

        // Mapeo de información a los componentes de texto
        holder.txtNumero.setText((position + 1) + ".");
        holder.txtNombre.setText(visita.getNombreComercial());
        holder.txtCliente.setText(visita.getNombreCliente());
        holder.txtTelefono.setText(visita.getTelefono());
        holder.txtDia.setText(visita.getDiaVisita());

        // Formateo de coordenadas para visualización amigable
        String coords = visita.getLatitud() + ", " + visita.getLongitud();
        holder.txtCoordenadas.setText(coords);

        /**
         * Integración con Glide para la carga asíncrona de imágenes.
         * Realiza el procesamiento necesario (centerCrop) para optimizar la visualización en el ImageView.
         */
        if (visita.getFotoNegocio() != null && !visita.getFotoNegocio().isEmpty()) {
            Glide.with(context)
                    .load(visita.getFotoNegocio())
                    .centerCrop()
                    .into(holder.imgFoto);
        }

        /**
         * Configuración del menú contextual (PopupMenu) para el item.
         * Define las acciones disponibles para cada registro individual.
         */
        holder.btnMenu.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.inflate(R.menu.menu_visita);

            // Gestión de eventos de clic en las opciones del menú
            popup.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();

                // Acción de edición delegada al listener externo
                if (id == R.id.menuEditar) {
                    if (listener != null) {
                        listener.onEditar(visita, holder.getAdapterPosition());
                    }
                    return true;
                }

                // Eliminación local del registro previa confirmación
                if (id == R.id.menuBorrar) {
                    borrarRegistro(visita);
                    return true;
                }

                // Apertura de canal de comunicación mediante WhatsApp
                if (id == R.id.menuWhatsapp) {
                    enviarWhatsApp(visita);
                    return true;
                }

                // Navegación GPS mediante aplicación externa de Mapas
                if (id == R.id.menuMaps) {
                    abrirMaps(visita);
                    return true;
                }

                // Copia de información resumida al portapapeles del sistema
                if (id == R.id.menuCopiar) {
                    String info = construirTexto(visita);
                    copiarAlPortapapeles(info);
                    Toast.makeText(context, "Información copiada", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Acción de compartir información mediante Intents implícitos
                if (id == R.id.menuCompartir) {
                    String info = construirTexto(visita);
                    compartirTexto(info);
                    return true;
                }
                
                // Visualización en mapa interno de la aplicación
                if (id == R.id.menuMapaItem) {
                    abrirMapaIndividual(visita);
                    return true;
                }

                return false;
            });

            popup.show();
        });
    }

    /**
     * Devuelve el tamaño total de la fuente de datos.
     * @return Cantidad de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return lista.size();
    }

    /**
     * Clase interna ViewHolder
     * 
     * Contenedor que mantiene las referencias a las vistas de un item para evitar
     * llamadas costosas a findViewById durante el scroll del RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFoto, btnMenu;
        TextView txtNumero, txtNombre, txtCliente, txtTelefono, txtDia, txtCoordenadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Mapeo de componentes visuales del layout de item
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

    /**
     * Invoca una aplicación de mapas externa utilizando un Intent implícito con esquema de navegación.
     * @param visita Registro con los datos de geolocalización.
     */
    private void abrirMaps(Visita visita) {

        String uri = "google.navigation:q=" +
                visita.getLatitud() + "," +
                visita.getLongitud();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        context.startActivity(intent);
    }

    /**
     * Inicia una conversación de WhatsApp utilizando la API de enlaces de la plataforma.
     * @param visita Registro con el número de teléfono del cliente.
     */
    private void enviarWhatsApp(Visita visita) {

        String telefono = visita.getTelefono();

        // Construcción de URL con prefijo de país predefinido
        String url = "https://wa.me/506" + telefono;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        context.startActivity(intent);
    }

    /**
     * Gestiona la eliminación de un registro. Presenta un diálogo de confirmación,
     * ejecuta la baja en la base de datos mediante el DAO y actualiza el estado del adaptador.
     * @param visita Registro a eliminar.
     */
    private void borrarRegistro(Visita visita) {

        new AlertDialog.Builder(context)
                .setTitle("Eliminar visita")
                .setMessage("¿Desea eliminar este registro?")
                .setIcon(android.R.drawable.ic_dialog_alert)

                .setPositiveButton("Eliminar", (dialog, which) -> {

                    VisitaDAO dao = new VisitaDAO(context);

                    Log.d("DELETE_VISITA", "Intentando borrar ID: " + visita.getId());

                    // Persistencia: Baja lógica/física en la base de datos
                    dao.deleteVisita(visita.getId());

                    // Actualización de la estructura de datos local y notificación de cambio al adaptador
                    int position = lista.indexOf(visita);
                    lista.remove(position);

                    Toast.makeText(context, "Visita eliminada", Toast.LENGTH_SHORT).show();

                    notifyItemRemoved(position);

                })

                .setNegativeButton("Cancelar", null)

                .show();
    }

    /**
     * Genera una cadena de texto formateada para el intercambio de información.
     * @param v Objeto Visita.
     * @return Cadena de texto con formato enriquecido (Markdown).
     */
    private String construirTexto(Visita v) {

        String linkMaps = "https://maps.google.com/?q=" +
                v.getLatitud() + "," + v.getLongitud();

        return "📍 *INFORMACIÓN DE VISITA COMERCIAL*\n\n" +
                "🏪 *Nombre:* " + v.getNombreComercial() + "\n" +
                "👤 *Cliente:* " + v.getNombreCliente() + "\n" +
                "📞 *Tel:* " + v.getTelefono() + "\n" +
                "📅 *Día:* " + v.getDiaVisita() + "\n\n" +
                "📌 *Ubicación:*\n" + linkMaps;
    }

    /**
     * Utilidad para copiar una cadena de texto al portapapeles global del sistema operativo.
     * @param texto Cadena de caracteres a copiar.
     */
    private void copiarAlPortapapeles(String texto) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        android.content.ClipData clip =
                android.content.ClipData.newPlainText("visita", texto);

        clipboard.setPrimaryClip(clip);
    }

    /**
     * Ejecuta un Intent de envío global para compartir texto con otras aplicaciones instaladas.
     * @param texto Información a compartir.
     */
    private void compartirTexto(String texto) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);

        context.startActivity(Intent.createChooser(intent, "Compartir vía"));
    }

    /**
     * Navega hacia la actividad de visualización en mapa propia del sistema.
     * @param visita Registro con los parámetros de ubicación.
     */
    private void abrirMapaIndividual(Visita visita) {

        Intent intent = new Intent(context, MapaVisitasActivity.class);

        // Pasaje de parámetros primitivos para inicializar el mapa en la posición correcta
        intent.putExtra("LAT", visita.getLatitud());
        intent.putExtra("LNG", visita.getLongitud());
        intent.putExtra("NOMBRE", visita.getNombreComercial());

        context.startActivity(intent);
    }
}
