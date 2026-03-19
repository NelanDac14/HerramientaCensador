package nelandac.app.herramientacensador.vistas_usuario;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.List;

import nelandac.app.herramientacensador.R;
import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;

public class MapaVisitasActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private VisitaDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_visitas);

        dao = new VisitaDAO(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<Visita> lista = dao.obtenerVisitas();

        for (Visita v : lista) {

            if (v.getLatitud() != 0 && v.getLongitud() != 0) {

                LatLng ubicacion = new LatLng(v.getLatitud(), v.getLongitud());

                mMap.addMarker(new MarkerOptions()
                        .position(ubicacion)
                        .title(v.getNombreComercial())
                        .snippet(v.getTelefono()));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 12));
            }
        }
    }
}