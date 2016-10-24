/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.pedidos;

import com.frontino.serv.logica.seguridad.Authenticator;
import com.frontino.serv.logica.seguridad.Conector;
import com.frontinoadm.logica.configuracion.zona.rutas.Ruta;
import com.frontinoadm.logica.configuracion.zona.rutas.RutaDto;
import com.frontinoadm.logica.configuracion.zona.zonas.ZonasDto;
import com.frontinoadm.logica.ingresos.ProcesoIngresos;
import com.frontinoadm.logica.ingresos.doumento.Doc008dDto;
import com.frontinoadm.logica.ingresos.pedidos.Pedidos;
import com.frontinoadm.logica.ingresos.pedidos.PedidosDetalleDto;
import com.frontinoadm.logica.ingresos.pedidos.PedidosDto;
import com.frontinoadm.logica.parametro.ValorParametro;
import com.frontinoadm.logica.producto.familias.FamiliaProducto;
import com.frontinoadm.logica.producto.familias.FamiliaProductoDto;
import com.frontinoadm.logica.tercero.tercero.TerceroDto;
import com.frontinoseg.logica.empresa.EmpresaDto;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import com.google.gson.Gson;
import com.sy.fechas.Fechas;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author casc
 */
@Stateless
@Path("pedidos")
public class PedidosREST {

    private Conector conector = null;

    private Conector getConector() {
        if (conector == null) {
            conector = new Conector();
        }
        return conector;
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    public void create(EmpresaDto entity) {
        //super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Long id, EmpresaDto entity) {
        //super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        //super.remove(super.find(id));
    }

    //@GET
    @GET
    @Path("listado")
    @Produces({"application/json"})
    public List<PedidosDto> consultarPedidos(
            @QueryParam("token") String _token,
            @QueryParam("fecIni") String _fecIni,
            @QueryParam("fecFin") String _fecFin,
            @QueryParam("staPed") String _staPed
    ) throws Exception {
        List<PedidosDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        Fechas fec = new Fechas();
        _fecIni = fec.fechaToString(new Date(Long.parseLong(_fecIni)), "dd/MM/yyyy");
        _fecFin = fec.fechaToString(new Date(Long.parseLong(_fecFin)), "dd/MM/yyyy");
        String[] valores = autenticador.valoresCadena(_token);
        /*if(true){
         throw  new Exception("HOLA");
         }*/
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            if (sesion != null) {
                Pedidos ped = new Pedidos(getConector().getCon(sesion.getConexName()),
                        Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu());

                arr = ped.consultarPedidosBasico(_fecIni, _fecFin, Integer.parseInt(valores[3]), _staPed);
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
                Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return arr;
    }

    @GET
    @Path("detalle")
    @Produces({"application/json"})
    public List<PedidosDetalleDto> consultarDetallePedidos(
            @QueryParam("token") String _token,
            @QueryParam("id") String _idePed
    ) {
        List<PedidosDetalleDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            Pedidos ped = new Pedidos(getConector().getCon(sesion.getConexName()),
                    Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu());
            if (sesion != null) {
                arr = ped.consultarDetallePedido(_idePed);
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
                Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arr;
    }

    @GET
    @Path("articulospedido")
    @Produces({"application/json"})
    public Response listarArticulos(
            @QueryParam("token") String _token,
            @QueryParam("familia") int _familia,
            @QueryParam("filtro") String _filtro,
            @QueryParam("pagina") int _pagina
    ) {
        List<Doc008dDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        Fechas fec = new Fechas();
        String[] valores = autenticador.valoresCadena(_token);
        int registrosPorPagina = 20;
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            if (sesion != null) {
                int desde = 0, hasta = registrosPorPagina;
                ProcesoIngresos proce = new ProcesoIngresos(Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu(),
                        getConector().getCon(sesion.getConexName()));
                arr = proce.listarProductosVenta(Integer.parseInt(valores[3]),
                        _familia,
                        new Fechas().fechaActual("dd/MM/yyyy"),
                        _filtro);
                int paginas = (int) (Math.ceil(arr.size() / registrosPorPagina));

                if (paginas == 0 && arr.size() > 0) {
                    _pagina = 1;
                    paginas = 1;
                }
                if (paginas == 0) {
                    _pagina = 1;
                    hasta = 0;
                } else {
                    if (_pagina > paginas) {
                        _pagina = 1;
                    }
                }
                if (_pagina > 1) {
                    desde += (registrosPorPagina * _pagina);
                    hasta += (registrosPorPagina * _pagina) + registrosPorPagina;
                }
                if (hasta > arr.size()) {
                    hasta = arr.size();
                }
                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("pagina", _pagina);
                jsonObjBuilder.add("paginas", paginas);
                jsonObjBuilder.add("registros", new Gson().toJson(arr.subList(desde, hasta)));
                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString())
                        .build();
            } else {
                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("error", true);
                jsonObjBuilder.add("des_error", "No ha iniciado session");
                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
            }

        } catch (Exception ex) {
            String[] cadError = ex.getMessage().split(":");
            if (cadError[0].equalsIgnoreCase("GENERAL")) {
                System.out.println(cadError[3]);
                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("error", true);
                jsonObjBuilder.add("des_error", cadError[3]);
                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
            } else {
                JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                jsonObjBuilder.add("error", true);
                jsonObjBuilder.add("des_error", ex.getMessage());
                JsonObject jsonObj = jsonObjBuilder.build();
                return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
            }
        } finally {
            try {
                getConector().matarConexion();
            } catch (SQLException ex) {
                Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //return arr;
    }

    @GET
    @Path("familias")
    @Produces({"application/json"})
    public List<FamiliaProductoDto> listarFamilias(
            @QueryParam("token") String _token
    ) {
        List<FamiliaProductoDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        Fechas fec = new Fechas();
        String[] valores = autenticador.valoresCadena(_token);
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            if (sesion != null) {
                FamiliaProducto familia = new FamiliaProducto(Integer.parseInt(valores[2]), sesion.getUsuarioDto().getLogUsu(),
                        getConector().getCon(sesion.getConexName()));

                arr = familia.arbolFamilias(2);
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
                Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arr;
    }

    @PUT
    @Path("guardar")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response guardarPedido(
            @QueryParam("token") String _token,
            List<Doc008dDto> detalle
    ) {
        List<Doc008dDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        Fechas fec = new Fechas();
        String[] valores = autenticador.valoresCadena(_token);
        SesionDto sesion = null;
        try {
            sesion = autenticador.getSession(valores[0], valores[1]);
        } catch (Exception ex) {
            Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (sesion != null) {
            Connection conact = getConector().getCon(sesion.getConexName());
            try {
                Pedidos ped = new Pedidos(getConector().getCon(sesion.getConexName()),
                        (Integer.parseInt(valores[2])), 0,
                        sesion.getUsuarioDto().getLogUsu());
                int ideEmp = (Integer.parseInt(valores[2]));
                int ideTer = (Integer.parseInt(valores[3]));
                String logUsu = sesion.getUsuarioDto().getLogUsu();

                String parZon = new ValorParametro(ideEmp, logUsu,
                        conact).consultaValorParamero("FRCLZ");
                if (parZon == null || parZon.trim().equals("")) {
                    throw new Exception(
                            "GENERAL:com.frontino.serv.services.pedidos:"
                            + "guardarPedido:"
                            + "Paramtro Zona de Pedidos Portal Clientes No Configurada FRCLZ ");
                }
                int ideZon = Integer.parseInt(parZon);

                PedidosDto ped001m = new PedidosDto();
                ped001m.setIde_ter(new TerceroDto());
                ped001m.getIde_ter().setIde_ter(ideTer);
                ped001m.setIde_emp(ideEmp);
                ped001m.setZonasDto(new ZonasDto());
                ped001m.getZonasDto().setIde_zon(ideZon);

                Ruta ruta = new Ruta(conact, logUsu, ideEmp, 0);
                RutaDto dtoRuta = ruta.consultarRegistro(ideZon, null);

                ped001m.setVen_ter(dtoRuta.getIdeTer());
                ped001m.setPos_lat(0);
                ped001m.setPos_lon(0);
                ped001m.setFec_ped(fec.fechaActual("dd/MM/yyyy"));
                ped001m.setAlmacenDto(dtoRuta.getAlmPed());
                ped001m.setSta_ped("0"); //Por defecto para quequede por verificar
                /*
                 * DETALLE
                 */
                List<PedidosDetalleDto> arrPed002d = new ArrayList<>();
                // PedidosDetalleDto[lstDoc004d.size()];
                for (Doc008dDto linea : detalle) {
                    PedidosDetalleDto detDoc = new PedidosDetalleDto();
                    detDoc.setIde_pro(linea.getIde_pro());
                    detDoc.setIde_und(linea.getIde_und());
                    detDoc.setCan_ped(linea.getCan_ddv());
                    arrPed002d.add(detDoc);
                }
                ped001m.setProAlms(arrPed002d);
                List<PedidosDto> arrPed = new ArrayList<>();
                arrPed.add(ped001m);
                Pedidos pedi = new Pedidos(conact, ideEmp, 0, logUsu);
                if (pedi.GenerarPedido(conact, arrPed)) {
                    conact.commit();
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("success", true);
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString())
                            .build();
                } else {
                    conact.rollback();
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("error", true);
                    jsonObjBuilder.add("des_error", "No se pudo generar pedido");
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
                }
            } catch (Exception ex) {
                try {
                    conact.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex1);
                }
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
                    getConector().matarConexion();
                } catch (SQLException ex) {
                    Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
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
    @Path("status")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response cambiarStatus(
            @QueryParam("token") String _token,
            @FormParam("ideped") String idePed,
            @FormParam("status") String status
    ) {
        Authenticator autenticador = Authenticator.getInstance();
        String[] valores = autenticador.valoresCadena(_token);
        SesionDto sesion = null;
        try {
            sesion = autenticador.getSession(valores[0], valores[1]);
        } catch (Exception ex) {
            Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (sesion != null) {
            Connection conact = getConector().getCon(sesion.getConexName());
            int ideEmp = (Integer.parseInt(valores[2]));
            String logUsu = sesion.getUsuarioDto().getLogUsu();
            try {
                Pedidos ped = new Pedidos(conact, ideEmp, 0, logUsu);
                if (ped.actualizarStatus(conact, idePed, status)) {
                    conact.commit();
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("success", true);
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString())
                            .build();
                } else {
                    conact.rollback();
                    JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
                    jsonObjBuilder.add("error", true);
                    jsonObjBuilder.add("des_error", "No se pudo modificar pedido");
                    JsonObject jsonObj = jsonObjBuilder.build();
                    return getNoCacheResponseBuilder(Response.Status.CONFLICT).entity(jsonObj.toString()).build();
                }
            } catch (Exception ex) {
                try {
                    conact.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex1);
                }
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
                    getConector().matarConexion();
                } catch (SQLException ex) {
                    Logger.getLogger(PedidosREST.class.getName()).log(Level.SEVERE, null, ex);
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

    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);
        return Response.status(status).cacheControl(cc);
    }
}
