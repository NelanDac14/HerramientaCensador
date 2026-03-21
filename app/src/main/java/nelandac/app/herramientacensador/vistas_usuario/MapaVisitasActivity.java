package nelandac.app.herramientacensador.vistas_usuario;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.chip.Chip;

import java.util.List;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;

/**
 * Inteligencia Geográfica: MapaVisitasActivity
 * 
 * Esta actividad encapsula la lógica de visualización geoespacial de la cartera de clientes.
 * Provee una interfaz interactiva basada en Google Maps SDK, permitiendo la localización 
 * precisa de puntos comerciales y el análisis de cobertura mediante marcadores dinámicos.
 * 
 * Capacidades Senior:
 * - Renderizado dual: Vista de foco único (un cliente) o vista de conjunto (cartera completa).
 * - Integración de filtros temporales sobre la capa de datos geográficos.
 * - Gestión de permisos de ubicación de alta precisión en tiempo de ejecución.
 */
public class MapaVisitasActivity extends FragmentActivity implements OnMapReadyCallback {

    /** Controlador maestro de la instancia del mapa. */
    private GoogleMap mMap;
    
    /** Capa de persistencia para la recuperación de coordenadas. */
    private VisitaDAO dao;
    
    /** Controles de UI para la segmentación de datos en el mapa. */
    private Chip chipTodo, chipHoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_visitas);

        // Vinculación de componentes de filtrado dinámico
        chipHoy = findViewById(R.id.chipHoyMapa);
        chipTodo = findViewById(R.id.chipTodoMapa);
        
        dao = new VisitaDAO(this);

        /**
         * Listeners de filtrado:
         * Ejecutan un barrido de la capa visual (mMap.clear) antes de repoblar con el nuevo subconjunto.
         */
        chipHoy.setOnClickListener(v -> cargarMarcadores(dao.obtenerHoy()));
        chipTodo.setOnClickListener(v -> cargarMarcadores(dao.obtenerVisitas()));

        // Inicialización asíncrona del motor de mapas para evitar bloqueos del hilo principal
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Punto de configuración del mapa una vez cargado el motor.
     * Gestiona la habilitación de capas de ubicación y el posicionamiento inicial de la cámara.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Verificación de privilegios de acceso a la ubicación (Fine Location)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        // Activación de la capa de ubicación en tiempo real del usuario
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        /**
         * Lógica de enrutamiento inicial:
         * Determina si se visualiza un solo punto (proviniente de un item) 
         * o toda la base de datos instalada.
         */
        double lat = getIntent().getDoubleExtra("LAT", 0);
        double lng = getIntent().getDoubleExtra("LNG", 0);
        String nombre = getIntent().getStringExtra("NOMBRE");

        if (lat != 0 && lng != 0) {
            // Caso: Foco en un cliente específico
            LatLng ubicacion = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(ubicacion)
                    .title(nombre));

            // Posicionamiento de cámara con nivel de zoom urbano (Street Level)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 17));

        } else {
            // Caso: Visualización de cartera completa
            cargarMarcadores(dao.obtenerVisitas());
            
            // Auto-posicionamiento inicial basado en el primer registro disponible
            List<Visita> lista = dao.obtenerVisitas();
            if (!lista.isEmpty()) {
                Visita primera = lista.get(0);
                LatLng inicio = new LatLng(primera.getLatitud(), primera.getLongitud());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicio, 12));
            }
        }
    }

    /**
     * Orquestador de marcadores dinámicos.
     * Itera sobre una colección de Visitas y proyecta sus coordenadas en la capa del mapa.
     * 
     * @param lista Colección de registros con integridad geográfica validada.
     */
    private void cargarMarcadores(List<Visita> lista) {
        mMap.clear(); // Saneamiento de la capa visual

        for (Visita v : lista) {
            // Validación preventiva de coordenadas 0,0 para evitar marcadores erróneos en el mar
            if (v.getLatitud() != 0 && v.getLongitud() != 0) {
                LatLng pos = new LatLng(v.getLatitud(), v.getLongitud());
                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(v.getNombreComercial()));
            }
        }
    }
}
