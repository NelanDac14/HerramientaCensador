package nelandac.app.herramientacensador.vistas_usuario;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Seguimiento;
import nelandac.app.herramientacensador.modelos.SeguimientoAdapter;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;

/**
 * Pantalla de Gestión: DetalleClienteActivity
 * 
 * Esta actividad proporciona una vista consolidada (Expediente 360°) del cliente.
 * Su responsabilidad es combinar la información estática del censo original con 
 * la dinámica de la bitácora de seguimientos cronológicos.
 * 
 * Características Senior:
 * - Carga de datos bajo demanda mediante el ID del cliente maestro.
 * - Integración de lista histórica mediante SeguimientoAdapter.
 * - Optimización de UI para respeto de Insets del sistema.
 */
public class DetalleClienteActivity extends AppCompatActivity {

    /** Componentes visuales para la cabecera informativa. */
    private TextView txtNombre, txtInfo;
    
    /** Componente de lista para el despliegue del historial de visitas. */
    private RecyclerView recyclerHistorial;
    
    /** Orquestador de la visualización de la bitácora. */
    private SeguimientoAdapter adapter;
    
    /** Capa de persistencia. */
    private VisitaDAO dao;
    
    /** Estado: Identificador único del cliente seleccionado. */
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Habilitación de renderizado Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_cliente);

        // Recuperación de la identidad del cliente desde el Intent
        idCliente = getIntent().getIntExtra("VISITA_ID", -1);
        if (idCliente == -1) {
            Toast.makeText(this, "Error: Identificación de cliente no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dao = new VisitaDAO(this);
        initVistas();
        cargarDatosMaestros();
        cargarHistorial();

        // Gestión de márgenes del sistema para adaptabilidad de pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** Vinculación de objetos Java con elementos del diseño XML. */
    private void initVistas() {
        txtNombre = findViewById(R.id.txtDetalleNombre);
        txtInfo = findViewById(R.id.txtDetalleInfo);
        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Recupera y visualiza la información estática (Nombre, Teléfono, Coordenadas) 
     * del registro maestro.
     */
    private void cargarDatosMaestros() {
        Visita v = dao.getVisitaById(idCliente);
        if (v != null) {
            txtNombre.setText(v.getNombreComercial());
            String info = String.format("Cliente: %s\nTeléfono: %s\nCoordenadas: %s\nRegistrado: %s", 
                    v.getNombreCliente(), 
                    v.getTelefono(), 
                    v.getCoordenadas(),
                    v.getFechaRegistro());
            txtInfo.setText(info);
        }
    }

    /**
     * Recupera la bitácora cronológica de seguimientos vinculada a este cliente.
     * Actualiza el RecyclerView con el historial de visitas pasadas.
     */
    private void cargarHistorial() {
        List<Seguimiento> historial = dao.getSeguimientosPorCliente(idCliente);
        if (historial.isEmpty()) {
            Toast.makeText(this, "Aún no hay seguimientos para este cliente", Toast.LENGTH_SHORT).show();
        }
        adapter = new SeguimientoAdapter(this, historial);
        recyclerHistorial.setAdapter(adapter);
    }
}
