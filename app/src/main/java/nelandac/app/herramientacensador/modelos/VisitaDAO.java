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

        v.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
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

        // 🔥 ASEGURAR UUID SIEMPRE
        if (visita.getUuid() == null || visita.getUuid().isEmpty()) {
            visita.setUuid(java.util.UUID.randomUUID().toString());
        }

        values.put("uuid", visita.getUuid());

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

        if (resultado == -1) {
            android.util.Log.e("DB", "❌ ERROR INSERT VISITA");
        } else {
            android.util.Log.d("DB", "✅ VISITA INSERTADA ID: " + resultado);
        }

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

    public List<Visita> obtenerPendientesSync() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE estado_sync = 0", null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public void marcarComoSincronizado(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("estado_sync", 1);

        db.update("visitas", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Seguimiento> obtenerSeguimientosPorVisita(int idVisita) {
        List<Seguimiento> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM bitacora WHERE id_visita_maestro = ? ORDER BY fecha_seguimiento DESC",
                new String[]{String.valueOf(idVisita)}
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

    public boolean existeVisitaPorUUID(String uuid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM visitas WHERE uuid = ? LIMIT 1",
                new String[]{uuid}
        );

        boolean existe = cursor.moveToFirst();

        cursor.close();
        db.close();

        return existe;
    }

    public void resetearSync() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE visitas SET estado_sync = 0");
        db.close();
    }

    public void generarUUIDsFaltantes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM visitas WHERE uuid IS NULL OR uuid = ''", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String uuid = java.util.UUID.randomUUID().toString();

                ContentValues values = new ContentValues();
                values.put("uuid", uuid);

                db.update("visitas", values, "id=?", new String[]{String.valueOf(id)});

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    public List<Visita> obtenerAltas() {
        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM visitas WHERE cliente_nuevo = 'Sí'",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(mapCursorToVisita(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    /**
     * Obtiene todas las visitas junto con la cantidad de seguimientos asociados.
     * Esto permite:
     * - Mostrar indicador en UI
     * - Filtrar visitas con actividad
     */
    public List<Visita> obtenerVisitasConSeguimientos() {

        List<Visita> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
                "SELECT v.*, " +
                        "COUNT(b.id_seguimiento) AS total_seguimientos " +
                        "FROM visitas v " +
                        "LEFT JOIN bitacora b ON v.id = b.id_visita_maestro " +
                        "GROUP BY v.id " +
                        "ORDER BY v.fecha_registro DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Visita v = mapCursorToVisita(cursor);

                // 🔥 NUEVO CAMPO DINÁMICO
                int totalSeguimientos = cursor.getInt(cursor.getColumnIndexOrThrow("total_seguimientos"));

                // Usamos el módulo como campo auxiliar (sin romper modelo)
                v.setModulo(v.getModulo() + " | Seg: " + totalSeguimientos);

                lista.add(v);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    /**
     * 🔥 NIVEL PRO
     * Retorna un dataset unificado de VISITAS + SEGUIMIENTOS según filtro de fecha
     */
    public List<ReporteItem> obtenerReportePorRango(String fechaInicio, String fechaFin) {

        List<ReporteItem> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // =====================================================
        // 🔵 SEGUIMIENTOS EN RANGO
        // =====================================================
        String querySeg =
                "SELECT b.*, v.* FROM bitacora b " +
                        "INNER JOIN visitas v ON v.id = b.id_visita_maestro " +
                        "WHERE DATE(b.fecha_seguimiento) BETWEEN DATE(?) AND DATE(?)";

        Cursor cSeg = db.rawQuery(querySeg, new String[]{fechaInicio, fechaFin});

        if (cSeg.moveToFirst()) {
            do {
                Seguimiento s = new Seguimiento();
                s.setIdSeguimiento(cSeg.getInt(cSeg.getColumnIndexOrThrow("id_seguimiento")));
                s.setFechaSeguimiento(cSeg.getString(cSeg.getColumnIndexOrThrow("fecha_seguimiento")));
                s.setComentarios(cSeg.getString(cSeg.getColumnIndexOrThrow("comentarios")));
                s.setEstadoVenta(cSeg.getString(cSeg.getColumnIndexOrThrow("estado_venta")));

                Visita v = mapCursorToVisita(cSeg);

                lista.add(new ReporteItem(ReporteItem.TIPO_SEGUIMIENTO, v, s));

            } while (cSeg.moveToNext());
        }
        cSeg.close();

        // =====================================================
        // 🟢 VISITAS NUEVAS EN RANGO
        // =====================================================
        String queryVis =
                "SELECT * FROM visitas " +
                        "WHERE DATE(fecha_registro) BETWEEN DATE(?) AND DATE(?)";

        Cursor cVis = db.rawQuery(queryVis, new String[]{fechaInicio, fechaFin});

        if (cVis.moveToFirst()) {
            do {
                Visita v = mapCursorToVisita(cVis);

                lista.add(new ReporteItem(ReporteItem.TIPO_VISITA, v, null));

            } while (cVis.moveToNext());
        }
        cVis.close();

        db.close();
        return lista;
    }

    /**
     * 🔥 NUEVO - FILTRO POR FECHA EXACTA
     */
    public List<Seguimiento> obtenerSeguimientosPorVisitaYFecha(int idVisita, String fecha) {

        List<Seguimiento> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM bitacora " +
                        "WHERE id_visita_maestro = ? " +
                        "AND DATE(fecha_seguimiento) = DATE(?) " +
                        "ORDER BY fecha_seguimiento DESC",
                new String[]{String.valueOf(idVisita), fecha}
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