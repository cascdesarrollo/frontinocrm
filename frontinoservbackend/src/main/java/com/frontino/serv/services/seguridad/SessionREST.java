/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.seguridad;

import com.frontino.serv.logica.email.Correos;
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
            String authToken = autenticador.login(getConector().getConDirectCloud(), username, password);

            List<Object> arr = autenticador.listadoEmpresas(getConector().getConDirectCloud(), authToken);
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
                getConector().matarConexionDirectCloud();
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

            SesionDto dto = autenticador.obtenerDatos(getConector().getConDirectCloud(), valores[0], valores[1],
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
                getConector().matarConexionDirectCloud();
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

    @POST
    @Path("cambiopass")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response cambiarpass(
            @FormParam("token") String _token,
            @FormParam("pold") String _pold,
            @FormParam("pnew") String _pnew,
            @FormParam("prenew") String _prenew
    ) {
        if (!_prenew.equals(_pnew)) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("error", true);
            jsonObjBuilder.add("des_error", "Contraseña no coincide con confirmacion");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
        }
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        SesionDto sesion = null;
        try {
            sesion = autenticador.obtenerDatos(getConector().getConDirectCloud(), valores[0], valores[1],
                    Integer.parseInt(valores[2]), Integer.parseInt(valores[3]));
        } catch (Exception ex) {
            Logger.getLogger(SessionREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (sesion != null) {
            try {
                if (autenticador.cambiarPassword(getConector().getConDirectCloud(), valores[0], _pold, _pnew //por ahora para que siempre haga commit
                        , true)) {
                    SessionDataServiceDto data = autenticador.prepararData(getConector().getCon(sesion.getConexName()), sesion, Integer.parseInt(valores[2]), Integer.parseInt(valores[3]));
                    new Correos().enviarCorreoCambioContrasena(
                            data.getNomEmp(), data.getNomTer(),
                            valores[0], _pnew);

                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("success", true);
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString())
                            .build();
                } else {
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("error", true);
                    jsonObjBuilder.add("des_error", "No se pudo Cambiar Contraseña");
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
                }

            } catch (Exception ex) {
                String[] cadError = ex.getMessage().split(":");
                if (cadError[0].equalsIgnoreCase("GENERAL")) {
                    System.out.println(cadError[3]);
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("error", true);
                    jsonObjBuilder.add("des_error", cadError[3]);
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
                } else {
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("error", true);
                    jsonObjBuilder.add("des_error", ex.getMessage());
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
                }
            } finally {
                try {
                    //Se mata aqui porque hace 2 consultas
                    getConector().matarConexionDirectCloud();
                } catch (SQLException ex) {
                    Logger.getLogger(SessionREST.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("error", true);
            jsonObjBuilder.add("des_error", "No ha iniciado session");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        }
    }

    @POST
    @Path("activar")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response activarNuevoUsuario(
            @FormParam("emailactiva") String email,
            @FormParam("password") String password,
            @FormParam("codigo") String codigo
    ) {

        Authenticator autenticador = Authenticator.getInstance();
        try {
            int[] datosId = autenticador.activarNuevoUsuario(getConector().getConDirectCloud(),
                    email, password, codigo, true);
            if (datosId[0] > 0) {
                //Iniciar sesion y cargar datos
                String authToken = autenticador.login(getConector().getConDirectCloud(), email, password);
                SesionDto sesion = new SesionDto();
                String[] datosNombres = autenticador.consultarEmpresa(getConector().getConDirectCloud(), email, datosId[0], datosId[1]);
                new Correos().enviarCorreoActivacion(
                        datosNombres[0],
                        datosNombres[1],
                        email, password);
                List<Object> arr = autenticador.listadoEmpresas(getConector().getConDirectCloud(), authToken);
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
            } else {
                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("error", true);
                jsonObjBuilder.add("des_error", "No se pudo Cambiar Contraseña");
                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
            }

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
                getConector().matarConexionDirectCloud();
            } catch (SQLException ex) {
                Logger.getLogger(SessionREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
