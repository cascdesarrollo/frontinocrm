/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.seguridad;

import com.frontino.serv.logica.seguridad.Authenticator;
import com.frontino.serv.logica.seguridad.Conector;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import com.frontinoseg.logica.seguridad.sesion.SessionDataServiceDto;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author casc
 */
@Path("session")
public class SessionREST {

    private static final long serialVersionUID = -6663599014192066936L;

    @Context
    private HttpServletRequest request;

    /**
     * Creates a new instance of SessionREST
     */
    public SessionREST() {
    }

    @POST
    @Path("login")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(
            @FormParam("username") String username,
            @FormParam("password") String password
    ) {
        Authenticator autenticador = Authenticator.getInstance();
        try {
            String authToken = autenticador.login(getConector().getConMd5(),username, password);

            List<Object> arr = autenticador.listadoEmpresas(getConector().getConMd5(), authToken);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("auth_token", authToken);
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            for (Object objeto : arr) {
                String[] empresa = (String[]) objeto;
                jsonArrayBuilder.add(
                        Json.createObjectBuilder()
                        .add("ideEmp", empresa[0])
                        .add("nomEmp", empresa[1])
                        .add("ideTer", empresa[2])
                );
            }
            jsonObjBuilder.add("empresas", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString())
                    .build();
        } catch (final LoginException ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("error", true);
            jsonObjBuilder.add("des_error", "Credenciales Invalidas");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        } catch (final Exception ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("error", true);
            jsonObjBuilder.add("des_error", "Error Validando crenciales " + ex.getMessage());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        } finally {
            try {
                //Se mata aqui porque hace 2 consultas
                getConector().matarConexionMd5();
            } catch (SQLException ex) {
                Logger.getLogger(SessionREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param status
     * @return
     */
    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return Response.status(status).cacheControl(cc);
    }

    @POST
    @Path("logout")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logout(
            @FormParam("valida") String _token) {
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        try {
            autenticador.logout(valores[0], valores[1]);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(SessionREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        //autenticador.logout(null, null);
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "Executed demoPostMethod");
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @GET
    @Path("dataSession")
    @Produces({"application/json"})
    public SessionDataServiceDto datosSession(
            @QueryParam("valida") String _token) {
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        try {

            SesionDto dto = autenticador.obtenerDatos(getConector().getConMd5(), valores[0], valores[1],
                    Integer.parseInt(valores[2]), Integer.parseInt(valores[3]));

            return autenticador.prepararData(getConector().getCon(dto.getConexName()), dto, Integer.parseInt(valores[2]), Integer.parseInt(valores[3]));

        } catch (Exception ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("error", true);
            jsonObjBuilder.add("des_error", "Credenciales Invalidas");
            JsonObject jsonObj = jsonObjBuilder.build();
            //return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        } finally {
            try {
                //Se mata aqui porque hace 2 consultas
                getConector().matarConexionMd5();
                getConector().matarConexion();
            } catch (SQLException ex) {
                Logger.getLogger(SessionREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //autenticador.logout(null, null);
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "Executed demoPostMethod");
        JsonObject jsonObj = jsonObjBuilder.build();

        //return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        return null;
    }
    
    private Conector conector = null;

    private Conector getConector() {
        if (conector == null) {
            conector = new Conector();
        }
        return conector;
    }

}