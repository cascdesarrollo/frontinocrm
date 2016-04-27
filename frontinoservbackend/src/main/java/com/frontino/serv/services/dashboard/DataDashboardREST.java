/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.dashboard;


import com.frontino.serv.logica.seguridad.Authenticator;
import com.frontino.serv.logica.seguridad.Conector;
import com.frontinoadm.logica.ingresos.ProcesoIngresos;
import com.frontinoadm.logica.ingresos.pedidos.Pedidos;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import com.sy.fechas.Fechas;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author casc
 */
@Path("dashboard")
public class DataDashboardREST {

    private Conector conector = null;

    private Conector getConector() {
        if (conector == null) {
            conector = new Conector();
        }
        return conector;
    }

    @GET
    @Path("dashboardData")
    @Produces("application/json")
    public Response dataDashboard(
            @QueryParam("token") String _token
    ) {
        int vencidos = 0, pedidos = 0, mensajes = 0;
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            if (sesion != null) {
                int intentos = 0;
                while (intentos < 20
                        && (sesion.getConexName() == null || sesion.getConexName().length < 4)) {
                    System.out.println("Viene Intento " + intentos);
                    Thread.sleep(500);
                    intentos++;
                }
                ProcesoIngresos proce = new ProcesoIngresos(
                        Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu(),
                        getConector().getCon(sesion.getConexName()));
                Pedidos ped = new Pedidos(getConector().getCon(sesion.getConexName()),
                        Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu()
                );

                //Documentos Vencidos
                vencidos = proce.contarDocumentos(Integer.parseInt(valores[3]), true, 'D');
                //Pedidos Por Facturar
                Fechas fec = new Fechas();
                String hoy = fec.fechaActual("dd/MM/yyyy");
                String antes = fec.SumarFecha(hoy, -30, "dd/MM/yyyy");
                pedidos = ped.contarPedidos(hoy, antes, Integer.parseInt(valores[3]), "1");
                //TODO Consultar Mensajes

                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("vencidos", vencidos);
                jsonObjBuilder.add("pedidos", pedidos);
                jsonObjBuilder.add("mensajes", mensajes);

                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString())
                        .build();
            } else {
                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("error", true);
                jsonObjBuilder.add("des_error", "Error Consultando Datos para DashBoard");
                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
            }

        } catch (Exception ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("error", true);
            jsonObjBuilder.add("des_error", "Error Validando crenciales " + ex.getMessage());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        } finally {
            try {
                getConector().matarConexion();
            } catch (SQLException ex) {
                Logger.getLogger(DataDashboardREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return Response.status(status).cacheControl(cc);
    }

}
