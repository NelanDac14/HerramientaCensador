package nelandac.app.herramientacensador.sync;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nelandac.app.herramientacensador.modelos.Visita;
import nelandac.app.herramientacensador.modelos.VisitaDAO;
import nelandac.app.herramientacensador.modelos.Seguimiento;

public class SyncManager {

    private Context context;
    private VisitaDAO dao;

    public SyncManager(Context context) {
        this.context = context;
        this.dao = new VisitaDAO(context);
    }

    public void sincronizar() {

        List<Visita> pendientes = dao.obtenerPendientesSync();

        if (pendientes.isEmpty()) {
            Log.d("SYNC", "No hay datos pendientes");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Visita v : pendientes) {

            Map<String, Object> data = new HashMap<>();

            // 🔥 TODOS LOS CAMPOS
            data.put("uuid", v.getUuid());
            data.put("id_local", v.getId());

            data.put("pais", v.getPais());
            data.put("prospector", v.getProspector());
            data.put("tipo_cliente", v.getTipoCliente());

            data.put("nombre_comercial", v.getNombreComercial());
            data.put("nombre_cliente", v.getNombreCliente());

            data.put("tipo_identificacion", v.getTipoIdentificacion());
            data.put("numero_identificacion", v.getNumeroIdentificacion());

            data.put("coordenadas", v.getCoordenadas());
            data.put("latitud", v.getLatitud());
            data.put("longitud", v.getLongitud());

            data.put("clasificacion_negocio", v.getClasificacionNegocio());
            data.put("telefono", v.getTelefono());

            data.put("link_google_maps", v.getLinkGoogleMaps());
            data.put("modulo", v.getModulo());

            data.put("dia_visita", v.getDiaVisita());
            data.put("solicita_apoyo_supervisor", v.getSolicitaApoyoSupervisor());
            data.put("fecha_coordinada", v.getFechaCoordinada());

            data.put("cliente_con_venta", v.getClienteConVenta());
            data.put("cliente_nuevo", v.getClienteNuevo());
            data.put("cliente_tiene_codigo", v.getClienteTieneCodigo());

            data.put("estado_sync", v.getEstadoSync());
            data.put("fecha_registro", v.getFechaRegistro());

            // 🔥 VALIDACIÓN REAL DE IMAGEN
            String pathFoto = v.getFotoNegocio();

            if (pathFoto != null &&
                    !pathFoto.isEmpty() &&
                    !pathFoto.startsWith("http")) {

                File file = new File(pathFoto);

                if (file.exists()) {

                    // 📸 Subir imagen válida
                    subirFotoYObtenerUrl(pathFoto, new Callback<String>() {
                        @Override
                        public void onComplete(String url) {

                            data.put("foto_negocio", url);

                            guardarVisitaYSeguimientos(db, v, data);
                        }

                        @Override
                        public void onError(Exception e) {

                            Log.e("SYNC", "Error subiendo imagen, se sube sin foto", e);

                            // 🔥 FALLBACK: subir SIN imagen
                            data.put("foto_negocio", null);
                            guardarVisitaYSeguimientos(db, v, data);
                        }
                    });

                } else {

                    // ❌ Archivo no existe
                    Log.e("SYNC", "Archivo no existe: " + pathFoto);

                    data.put("foto_negocio", null);
                    guardarVisitaYSeguimientos(db, v, data);
                }

            } else {

                // 🌐 Ya es URL o no tiene foto
                data.put("foto_negocio", pathFoto);
                guardarVisitaYSeguimientos(db, v, data);
            }
        }
    }

    // 🔥 MÉTODO CENTRAL (GUARDA VISITA + SEGUIMIENTOS)
    private void guardarVisitaYSeguimientos(FirebaseFirestore db, Visita v, Map<String, Object> data) {

        db.collection("visitas")
                .document(v.getUuid())
                .set(data)
                .addOnSuccessListener(unused -> {

                    dao.marcarComoSincronizado(v.getId());
                    Log.d("SYNC", "Visita subida ID: " + v.getId());

                    // 🔥 SUBIR SEGUIMIENTOS
                    List<Seguimiento> seguimientos = dao.obtenerSeguimientosPorVisita(v.getId());

                    for (Seguimiento s : seguimientos) {

                        Map<String, Object> dataSeg = new HashMap<>();
                        dataSeg.put("id_local", s.getIdSeguimiento());
                        dataSeg.put("fecha_seguimiento", s.getFechaSeguimiento());
                        dataSeg.put("comentarios", s.getComentarios());
                        dataSeg.put("estado_venta", s.getEstadoVenta());

                        if (s.getFotoSeguimiento() != null &&
                                !s.getFotoSeguimiento().isEmpty() &&
                                !s.getFotoSeguimiento().startsWith("http")) {

                            subirFotoYObtenerUrl(s.getFotoSeguimiento(), new Callback<String>() {
                                @Override
                                public void onComplete(String url) {
                                    dataSeg.put("foto_seguimiento", url);

                                    db.collection("visitas")
                                            .document(v.getUuid())
                                            .collection("seguimientos")
                                            .add(dataSeg);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("SYNC", "Error foto seguimiento", e);
                                }
                            });

                        } else {

                            dataSeg.put("foto_seguimiento", s.getFotoSeguimiento());

                            db.collection("visitas")
                                    .document(v.getUuid())
                                    .collection("seguimientos")
                                    .add(dataSeg);
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("SYNC", "Error subiendo visita", e);
                });
    }

    public void subirFotoYObtenerUrl(String pathLocal, Callback<String> callback) {

        try {

            File file = new File(pathLocal);

            if (!file.exists()) {
                callback.onError(new Exception("Archivo no existe: " + pathLocal));
                return;
            }

            Uri uri = Uri.fromFile(file);

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("fotos/" + System.currentTimeMillis() + "_" + file.getName());

            storageRef.putFile(uri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageRef.getDownloadUrl();
                    })
                    .addOnSuccessListener(uriDownload -> {
                        callback.onComplete(uriDownload.toString());
                    })
                    .addOnFailureListener(callback::onError);

        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public void descargarDesdeFirebase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("visitas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (var doc : queryDocumentSnapshots) {

                        String uuid = doc.getString("uuid");

                        if (uuid == null) continue;

                        // 🔥 VALIDAR SI YA EXISTE
                        if (dao.existeVisitaPorUUID(uuid)) {
                            continue;
                        }

                        Visita v = new Visita();

                        v.setUuid(uuid);
                        v.setPais(doc.getString("pais"));
                        v.setProspector(doc.getString("prospector"));
                        v.setTipoCliente(doc.getString("tipo_cliente"));
                        v.setNombreComercial(doc.getString("nombre_comercial"));
                        v.setNombreCliente(doc.getString("nombre_cliente"));
                        v.setTipoIdentificacion(doc.getString("tipo_identificacion"));
                        v.setNumeroIdentificacion(doc.getString("numero_identificacion"));
                        v.setCoordenadas(doc.getString("coordenadas"));

                        Double lat = doc.getDouble("latitud");
                        Double lng = doc.getDouble("longitud");

                        v.setLatitud(lat != null ? lat : 0);
                        v.setLongitud(lng != null ? lng : 0);

                        v.setClasificacionNegocio(doc.getString("clasificacion_negocio"));
                        v.setTelefono(doc.getString("telefono"));
                        v.setLinkGoogleMaps(doc.getString("link_google_maps"));
                        v.setModulo(doc.getString("modulo"));
                        v.setFotoNegocio(doc.getString("foto_negocio"));
                        v.setDiaVisita(doc.getString("dia_visita"));
                        v.setSolicitaApoyoSupervisor(doc.getString("solicita_apoyo_supervisor"));
                        v.setFechaCoordinada(doc.getString("fecha_coordinada"));
                        v.setClienteConVenta(doc.getString("cliente_con_venta"));
                        v.setClienteNuevo(doc.getString("cliente_nuevo"));
                        v.setClienteTieneCodigo(doc.getString("cliente_tiene_codigo"));
                        v.setEstadoSync(1);
                        v.setFechaRegistro(doc.getString("fecha_registro"));

                        long idLocal = dao.insertVisita(v);

                        // 🔥 DESCARGAR SEGUIMIENTOS
                        descargarSeguimientos(db, doc.getId(), (int) idLocal);
                    }

                    Log.d("SYNC", "Descarga completa");

                })
                .addOnFailureListener(e -> {
                    Log.e("SYNC", "Error descargando", e);
                });
    }

    private void descargarSeguimientos(FirebaseFirestore db, String uuid, int idLocal) {

        db.collection("visitas")
                .document(uuid)
                .collection("seguimientos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (var doc : queryDocumentSnapshots) {

                        Seguimiento s = new Seguimiento();

                        s.setIdVisitaMaestro(idLocal);
                        s.setFechaSeguimiento(doc.getString("fecha_seguimiento"));
                        s.setFotoSeguimiento(doc.getString("foto_seguimiento"));
                        s.setComentarios(doc.getString("comentarios"));
                        s.setEstadoVenta(doc.getString("estado_venta"));

                        dao.insertSeguimiento(s);
                    }

                });
    }
}