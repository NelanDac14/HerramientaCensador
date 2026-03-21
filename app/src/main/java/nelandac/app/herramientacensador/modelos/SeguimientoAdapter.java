package nelandac.app.herramientacensador.modelos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nelandac.app.herramientacensador.R;

/**
 * Adaptador de UI: SeguimientoAdapter
 * 
 * Esta clase es la responsable de la gestión y representación visual de la bitácora de seguimientos
 * dentro de un componente RecyclerView. Implementa el patrón ViewHolder para optimizar el reciclaje
 * de vistas y garantizar un desplazamiento fluido de la lista (60 FPS).
 * 
 * Responsabilidades:
 * - Vinculación de metadatos temporales y comerciales de cada visita de seguimiento.
 * - Carga asíncrona y eficiente de evidencia fotográfica mediante la librería Glide.
 * - Manejo de estados de visualización para comentarios opcionales.
 */
public class SeguimientoAdapter extends RecyclerView.Adapter<SeguimientoAdapter.ViewHolder> {

    /** Colección de datos de tipo Seguimiento a ser renderizada. */
    private List<Seguimiento> lista;
    
    /** Contexto de la actividad para inflado de layouts y gestión de recursos del sistema. */
    private Context context;

    /**
     * Constructor del adaptador.
     * 
     * @param context Contexto de ejecución.
     * @param lista Fuente de datos con el historial cronológico de seguimientos.
     */
    public SeguimientoAdapter(Context context, List<Seguimiento> lista) {
        this.context = context;
        this.lista = lista;
    }

    /**
     * Infla el diseño XML definido en R.layout.item_seguimiento para crear nuevas instancias de ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seguimiento, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Realiza la vinculación (binding) entre los datos del modelo y los componentes visuales de la fila.
     * 
     * @param holder Contenedor de vistas de la fila actual.
     * @param position Índice del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Seguimiento s = lista.get(position);

        // Actualización de componentes textuales con datos de persistencia
        holder.txtFecha.setText(s.getFechaSeguimiento());
        holder.txtEstado.setText(String.format("Estado de Venta: %s", s.getEstadoVenta()));
        
        // Lógica de presentación para observaciones de campo
        holder.txtComentarios.setText(s.getComentarios().isEmpty() ? "Sin comentarios registrados." : s.getComentarios());

        /**
         * Gestión multimedia mediante Glide.
         * Se aplica transformación de recorte central (centerCrop) para uniformidad visual
         * y se define un recurso de respaldo (placeholder) ante ausencias de imagen.
         */
        if (s.getFotoSeguimiento() != null && !s.getFotoSeguimiento().isEmpty()) {
            Glide.with(context)
                    .load(s.getFotoSeguimiento())
                    .centerCrop()
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    /**
     * Retorna el tamaño total de la colección de datos.
     */
    @Override
    public int getItemCount() {
        return lista.size();
    }

    /**
     * Patrón ViewHolder para Seguimientos.
     * Mantiene las referencias a los widgets de la UI para evitar búsquedas repetitivas en el árbol de vistas.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtEstado, txtComentarios;
        ImageView imgFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.txtFechaSeguimientoItem);
            txtEstado = itemView.findViewById(R.id.txtEstadoVentaItem);
            txtComentarios = itemView.findViewById(R.id.txtComentariosItem);
            imgFoto = itemView.findViewById(R.id.imgFotoSeguimientoItem);
        }
    }
}
