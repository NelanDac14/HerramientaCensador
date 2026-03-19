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

public class MapaVisitasActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private VisitaDAO dao;
    private Chip chipTodo, chipHoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_visitas);


        chipHoy = findViewById(R.id.chipHoyMapa);
        chipTodo = findViewById(R.id.chipTodoMapa);
        dao = new VisitaDAO(this);

        chipHoy.setOnClickListener(v -> {
            cargarMarcadores(dao.obtenerHoy());
        });

        chipTodo.setOnClickListener(v -> {
            cargarMarcadores(dao.obtenerVisitas());
        });

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // UBICACIÓN ACTUAL (PONLO AQUÍ)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // 1. Intentar recibir datos de una sola visita
        double lat = getIntent().getDoubleExtra("LAT", 0);
        double lng = getIntent().getDoubleExtra("LNG", 0);
        String nombre = getIntent().getStringExtra("NOMBRE");

        // CASO 1: viene desde el adapter (1 sola visita)
        if (lat != 0 && lng != 0) {

            LatLng ubicacion = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions()
                    .position(ubicacion)
                    .title(nombre));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 17));

        } else {

            // CASO 2: cargar TODAS las visitas (como ya tenías)
            List<Visita> lista = dao.obtenerVisitas();

            for (Visita v : lista) {

                if (v.getLatitud() != 0 && v.getLongitud() != 0) {

                    LatLng ubicacion = new LatLng(v.getLatitud(), v.getLongitud());

                    mMap.addMarker(new MarkerOptions()
                            .position(ubicacion)
                            .title(v.getNombreComercial())
                            .snippet(v.getTelefono()));
                }
            }

            // Mover cámara solo una vez (mejor UX)
            if (!lista.isEmpty()) {
                Visita primera = lista.get(0);
                LatLng inicio = new LatLng(primera.getLatitud(), primera.getLongitud());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicio, 12));
            }
        }
    }

    private void cargarMarcadores(List<Visita> lista) {

        mMap.clear();

        for (Visita v : lista) {

            if (v.getLatitud() != 0 && v.getLongitud() != 0) {

                LatLng pos = new LatLng(v.getLatitud(), v.getLongitud());

                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(v.getNombreComercial()));
            }
        }
    }
}