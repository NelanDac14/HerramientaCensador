package nelandac.app.herramientacensador.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nelandac.app.herramientacensador.basedatos.BaseDatos;

public class VisitaDAO {

    private BaseDatos dbHelper;

    public VisitaDAO(Context context) {
        dbHelper = new BaseDatos(context);
    }

    private String getLocalTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // 🔥 MAPPER CENTRALIZADO
    private Visita mapCursorToVisita(Cursor cursor) {
        Visita v = new Visita();

        v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        v.setPais(cursor.getString(cursor.getColumnIndexOrThrow("pais")));
        v.setProspector(cursor.getString(cursor.getColumnIndexOrThrow("prospector")));
        v.setTipoCliente(cursor.getString(cursor.getColumnIndexOrThrow("tipo_cliente")));
        v.setNombreComercial(cursor.getString(cursor.getColumnIndexOrThrow("nombre_comercial")));
        v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")));
        v.setTipoIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("tipo_identificacion")));
        v.setNumeroIdentificacion(cursor.getString(cursor.getColumnIndexOrThrow("numero_identificacion")));
        v.setCoordenadas(cursor.getString(cursor.getColumnIndexOrThrow("coordenadas")));
        v.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
        v.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
        v.setClasificacionNegocio(cursor.getString(cursor.getColumnIndexOrThrow("clasificacion_negocio")));
        v.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
        v.setLinkGoogleMaps(cursor.getString(cursor.getColumnIndexOrThrow("link_google_maps")));
        v.setModulo(cursor.getString(cursor.getColumnIndexOrThrow("modulo")));
        v.setFotoNegocio(cursor.getString(cursor.getColumnIndexOrThrow("foto_negocio")));
        v.setDiaVisita(cursor.getString(cursor.getColumnIndexOrThrow("dia_visita")));
        v.setSolicitaApoyoSupervisor(cursor.getString(cursor.getColumnIndexOrThrow("solicita_apoyo_supervisor")));
        v.setFechaCoordinada(cursor.getString(cursor.getColumnIndexOrThrow("fecha_coordinada")));
        v.setClienteConVenta(cursor.getString(cursor.getColumnIndexOrThrow("cliente_con_venta")));
        v.setClienteNuevo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_nuevo")));
        v.setClienteTieneCodigo(cursor.getString(cursor.getColumnIndexOrThrow("cliente_tiene_codigo")));
        v.setEstadoSync(cursor.getInt(cursor.getColumnIndexOrThrow("estado_sync")));
        v.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));

        return v;
    }

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
        values.put("fecha_registro", getLocalTimestamp());

        long resultado = db.insert("visitas", null, values);
        db.close();
        return resultado;
    }

    public List<Visita> getVisitas() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public int deleteVisita(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete("visitas", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado;
    }

    public int updateVisita(Visita visita) {
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

        int resultado = db.update("visitas", values, "id=?", new String[]{String.valueOf(visita.getId())});
        db.close();
        return resultado;
    }

    public List<Visita> obtenerVisitas() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas", null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public Visita getVisitaById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE id = ?", new String[]{String.valueOf(id)});

        Visita v = null;
        if (cursor.moveToFirst()) {
            v = mapCursorToVisita(cursor);
        }

        cursor.close();
        db.close();
        return v;
    }

    public List<Visita> obtenerHoy() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE DATE(fecha_registro) = DATE('now','localtime')", null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public List<Visita> obtenerPorFecha(String fecha) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE date(fecha_registro) = ?", new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public List<Visita> obtenerPorMes(String mes) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE strftime('%m', fecha_registro) = ?", new String[]{mes});

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public List<Visita> obtenerPorAnio(String anio) {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE strftime('%Y', fecha_registro) = ?", new String[]{anio});

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public long insertSeguimiento(Seguimiento s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id_visita_maestro", s.getIdVisitaMaestro());
        values.put("foto_seguimiento", s.getFotoSeguimiento());
        values.put("comentarios", s.getComentarios());
        values.put("estado_venta", s.getEstadoVenta());
        values.put("fecha_seguimiento", getLocalTimestamp());

        long id = db.insert("bitacora", null, values);
        db.close();
        return id;
    }

    public List<Seguimiento> getSeguimientosPorCliente(int idCliente) {
        List<Seguimiento> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM bitacora WHERE id_visita_maestro = ? ORDER BY fecha_seguimiento DESC",
                new String[]{String.valueOf(idCliente)}
        );

        if (cursor.moveToFirst()) {
            do {
                Seguimiento s = new Seguimiento();
                s.setIdSeguimiento(cursor.getInt(cursor.getColumnIndexOrThrow("id_seguimiento")));
                s.setIdVisitaMaestro(cursor.getInt(cursor.getColumnIndexOrThrow("id_visita_maestro")));
                s.setFechaSeguimiento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_seguimiento")));
                s.setFotoSeguimiento(cursor.getString(cursor.getColumnIndexOrThrow("foto_seguimiento")));
                s.setComentarios(cursor.getString(cursor.getColumnIndexOrThrow("comentarios")));
                s.setEstadoVenta(cursor.getString(cursor.getColumnIndexOrThrow("estado_venta")));
                lista.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }
}