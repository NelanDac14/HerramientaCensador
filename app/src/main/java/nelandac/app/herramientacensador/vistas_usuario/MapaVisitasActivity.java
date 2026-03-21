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
 * Clase MapaVisitasActivity
 * 
 * Esta actividad proporciona una interfaz geográfica utilizando el SDK de Google Maps para Android.
 * Su propósito principal es visualizar la ubicación de los puntos de interés (visitas) mediante
 * marcadores interactivos. Soporta la visualización de un registro individual o la carga masiva
 * de registros históricos con capacidades de filtrado temporal.
 */
public class MapaVisitasActivity extends FragmentActivity implements OnMapReadyCallback {

    // Instancia del controlador de Google Maps
    private GoogleMap mMap;
    
    // Objeto de acceso a datos para recuperación de registros geolocalizados
    private VisitaDAO dao;
    
    // Componentes de la interfaz para el control de capas de datos
    private Chip chipTodo, chipHoy;

    /**
     * Ciclo de vida: onCreate
     * Inicializa la interfaz de usuario, los componentes de datos y el fragmento del mapa.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_visitas);

        // Vinculación de componentes de filtrado
        chipHoy = findViewById(R.id.chipHoyMapa);
        chipTodo = findViewById(R.id.chipTodoMapa);
        
        // Inicialización de la capa de persistencia
        dao = new VisitaDAO(this);

        /**
         * Listeners para el filtrado dinámico de marcadores en el mapa.
         * Realizan una limpieza de la capa visual y recargan los datos según el criterio seleccionado.
         */
        chipHoy.setOnClickListener(v -> cargarMarcadores(dao.obtenerHoy()));

        chipTodo.setOnClickListener(v -> cargarMarcadores(dao.obtenerVisitas()));

        // Inicialización asíncrona del fragmento de mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Callback onMapReady
     * Se ejecuta cuando el objeto GoogleMap está disponible para su manipulación.
     * Configura los parámetros iniciales del mapa, gestiona permisos de ubicación y
     * determina la lógica de carga inicial basada en los parámetros del Intent.
     * 
     * @param googleMap Instancia del mapa configurado.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Validación de privilegios de acceso a la ubicación del dispositivo en tiempo de ejecución
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        // Habilitación de la capa de ubicación propia del usuario y controles de UI
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        /**
         * Lógica de determinación de estado inicial:
         * El mapa puede ser invocado para mostrar una visita específica o el listado global.
         */
        double lat = getIntent().getDoubleExtra("LAT", 0);
        double lng = getIntent().getDoubleExtra("LNG", 0);
        String nombre = getIntent().getStringExtra("NOMBRE");

        // Caso 1: Visualización de un registro específico (Proviene del adaptador de visitas)
        if (lat != 0 && lng != 0) {

            LatLng ubicacion = new LatLng(lat, lng);

            // Generación de marcador único con título identificador
            mMap.addMarker(new MarkerOptions()
                    .position(ubicacion)
                    .title(nombre));

            // Transición de cámara con nivel de zoom de alta proximidad
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 17));

        } else {

            // Caso 2: Carga masiva de registros históricos
            List<Visita> lista = dao.obtenerVisitas();

            for (Visita v : lista) {
                // Validación de integridad de coordenadas antes del renderizado del marcador
                if (v.getLatitud() != 0 && v.getLongitud() != 0) {
                    LatLng ubicacion = new LatLng(v.getLatitud(), v.getLongitud());

                    mMap.addMarker(new MarkerOptions()
                            .position(ubicacion)
                            .title(v.getNombreComercial())
                            .snippet(v.getTelefono()));
                }
            }

            // Establece un punto de inicio visual basado en el primer registro de la colección
            if (!lista.isEmpty()) {
                Visita primera = lista.get(0);
                LatLng inicio = new LatLng(primera.getLatitud(), primera.getLongitud());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicio, 12));
            }
        }
    }

    /**
     * Gestiona la actualización masiva de la capa de marcadores del mapa.
     * Realiza una limpieza atómica del estado visual previo antes de iterar sobre la nueva colección.
     * 
     * @param lista Colección de objetos Visita con datos geográficos.
     */
    private void cargarMarcadores(List<Visita> lista) {

        // Limpieza de todos los marcadores, polilíneas y superposiciones existentes
        mMap.clear();

        for (Visita v : lista) {
            // Verificación de coordenadas válidas (distintas del origen 0,0)
            if (v.getLatitud() != 0 && v.getLongitud() != 0) {

                LatLng pos = new LatLng(v.getLatitud(), v.getLongitud());

                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(v.getNombreComercial()));
            }
        }
    }
}
