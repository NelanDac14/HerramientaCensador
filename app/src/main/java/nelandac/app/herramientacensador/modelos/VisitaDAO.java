package nelandac.app.herramientacensador.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nelandac.app.herramientacensador.basedatos.BaseDatos;
import nelandac.app.herramientacensador.modelos.Visita;

public class VisitaDAO {


    private BaseDatos dbHelper;

    public VisitaDAO(Context context) {
        dbHelper = new BaseDatos(context);
    }

    // INSERTAR VISITA
    public long insertVisita(Visita visita) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("pais", visita.getPais());
        values.put("prospector", visita.getProspector());
        values.put("tipo_cliente", visita.getTipoCliente());
        values.put("nombre_comercial", visita.getNombreComercial());
        values.put("nombre_cliente", visita.getNombreCliente());
        values.put("tipo_identificacion", visita.getTipoIdentificacion());
        values.put("numero_identificacion", visita.getNumeroIdentificacion());
        values.put("coordenadas", visita.getCoordenadas());
        values.put("latitud", visita.getLatitud());
        values.put("longitud", visita.getLongitud());
        values.put("clasificacion_negocio", visita.getClasificacionNegocio());
        values.put("telefono", visita.getTelefono());
        values.put("link_google_maps", visita.getLinkGoogleMaps());
        values.put("modulo", visita.getModulo());
        values.put("foto_negocio", visita.getFotoNegocio());
        values.put("dia_visita", visita.getDiaVisita());
        values.put("solicita_apoyo_supervisor", visita.getSolicitaApoyoSupervisor());
        values.put("fecha_coordinada", visita.getFechaCoordinada());
        values.put("cliente_con_venta", visita.getClienteConVenta());
        values.put("cliente_nuevo", visita.getClienteNuevo());
        values.put("cliente_tiene_codigo", visita.getClienteTieneCodigo());
        values.put("estado_sync", visita.getEstadoSync());

        long resultado = db.insert("visitas", null, values);

        db.close();

        return resultado;
    }

    // OBTENER TODAS LAS VISITAS
    public List<Visita> getVisitas() {

        List<Visita> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        if (cursor.moveToFirst()) {
            do {

                Visita visita = new Visita();

                visita.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                visita.setPais(cursor.getString(cursor.getColumnIndexOrThrow("pais")));
                visita.setProspector(cursor.getString(cursor.getColumnIndexOrThrow("prospector")));
                visita.setTipoCliente(cursor.getString(cursor.getColumnIndexOrThrow("tipo_cliente")));
                visita.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                visita.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                visita.setTipoIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("tipo_identificacion")));
                visita.setNumeroIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("numero_identificacion")));
                visita.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
                visita.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                visita.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                visita.setClasificacionNegocio(cursor.getString(cursor.getColumnIndexOrThrow("clasificacion_negocio")));
                visita.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                visita.setLinkGoogleMaps(cursor.getString(cursor.getColumnIndexOrThrow("link_google_maps")));
                visita.setModulo(cursor.getString(cursor.getColumnIndexOrThrow("modulo")));
                visita.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
                visita.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                visita.setSolicitaApoyoSupervisor(cursor.getString(cursor.getColumnIndexOrThrow("solicita_apoyo_supervisor")));
                visita.setFechaCoordinada(cursor.getString(cursor.getColumnIndexOrThrow("fecha_coordinada")));
                visita.setClienteConVenta(cursor.getString(cursor.getColumnIndexOrThrow("cliente_con_venta")));
                visita.setClienteNuevo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_nuevo")));
                visita.setClienteTieneCodigo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_tiene_codigo")));
                visita.setEstadoSync(cursor.getInt(cursor.getColumnIndexOrThrow("estado_sync")));
                visita.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));

                lista.add(visita);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    // ELIMINAR VISITA
    public int deleteVisita(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete("visitas", "id=?", new String[]{String.valueOf(id)});

        Log.d("SQL_DELETE","Filas borradas: " +
                db.delete("visitas", "id = ?", new String[]{String.valueOf(id)}));

        db.close();

        return resultado;
    }

    // ACTUALIZAR VISITA
    public int updateVisita(Visita visita) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("pais", visita.getPais());
        values.put("prospector", visita.getProspector());
        values.put("tipo_cliente", visita.getTipoCliente());
        values.put("nombre_comercial", visita.getNombreComercial());
        values.put("nombre_cliente", visita.getNombreCliente());

        int resultado = db.update(
                "visitas",
                values,
                "id=?",
                new String[]{String.valueOf(visita.getId())}
        );

        db.close();

        return resultado;
    }

    public List<Visita> obtenerVisitas(){

        List<Visita> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        if(cursor.moveToFirst()){

            do{

                Visita v = new Visita();

                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));

                lista.add(v);

            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public List<Visita> obtenerVisitasPorFecha(String fecha){

        List<Visita> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE fecha_coordinada = ? ORDER BY id",
                new String[]{fecha});

        if(cursor.moveToFirst()){

            do{

                Visita v = new Visita();

                v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
                v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));

                lista.add(v);

            }while(cursor.moveToNext());
        }

        cursor.close();

        return lista;
    }

}
