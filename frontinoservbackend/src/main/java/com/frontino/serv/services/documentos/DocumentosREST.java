/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.documentos;

import com.frontino.serv.logica.seguridad.Authenticator;
import com.frontino.serv.logica.seguridad.Conector;
import com.frontinoadm.logica.ingresos.ProcesoIngresos;
import com.frontinoadm.logica.ingresos.doumento.Doc007mDto;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import com.sy.fechas.Fechas;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author casc
 */
@Path("documentos")
public class DocumentosREST {

    @Context
    private UriInfo context;

    private Conector conector = null;

    private Conector getConector() {
        if (conector == null) {
            conector = new Conector();
        }
        return conector;
    }

    @POST
    @Path("consultar")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<Doc007mDto> consultarDocumentos(
            @FormParam("token") String _token,
            @FormParam("fecIni") long _fecIni,
            @FormParam("fecFin") long _fecFin,
            @FormParam("tipo") String _tipo,
            @FormParam("tipoSaldo") String _tipoSaldo
    ) {
        List<Doc007mDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            if (sesion != null) {
                ProcesoIngresos proce = new ProcesoIngresos(
                        Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu(),
                        getConector().getCon(sesion.getConexName()));
                String desde = null, hasta = null;
                if (_fecIni > 0 && _fecFin > 0) {
                    Fechas fec = new Fechas();
                    desde = fec.fechaToString(new Date(_fecIni), "dd/MM/yyyy");
                    hasta = fec.fechaToString(new Date(_fecFin), "dd/MM/yyyy");
                }
                arr = proce.consultarDocumentos(Integer.parseInt(valores[3]),
                        desde, hasta,
                        _tipoSaldo.charAt(0), _tipo.charAt(0));
            }
        } catch (Exception ex) {
            String[] cadError = ex.getMessage().split(":");
            if (cadError[0].equalsIgnoreCase("GENERAL")) {
                System.out.println(cadError[3]);

            } else {
                System.out.println(ex.getMessage());
            }
        } finally {
            try {
                getConector().matarConexion();
            } catch (SQLException ex) {
                Logger.getLogger(DocumentosREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arr;
    }
}
