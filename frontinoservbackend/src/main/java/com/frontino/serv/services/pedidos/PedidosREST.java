/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.pedidos;

import com.frontino.serv.logica.seguridad.Authenticator;
import com.frontino.serv.logica.seguridad.Conector;
import com.frontinoadm.logica.ingresos.ProcesoIngresos;
import com.frontinoadm.logica.ingresos.doumento.Doc008dDto;
import com.frontinoadm.logica.ingresos.pedidos.Pedidos;
import com.frontinoadm.logica.ingresos.pedidos.PedidosDetalleDto;
import com.frontinoadm.logica.ingresos.pedidos.PedidosDto;
import com.frontinoseg.logica.empresa.EmpresaDto;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import com.sy.fechas.Fechas;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
            Pedidos ped = new Pedidos(getConector().getCon(sesion.getConexName()),
                    Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu());
            if (sesion != null) {
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
    public List<Doc008dDto> listarArticulos(
            @QueryParam("token") String _token
    ) {
        List<Doc008dDto> arr = new ArrayList<>();
        Authenticator autenticador = Authenticator.getInstance();
        Fechas fec = new Fechas();
        String[] valores = autenticador.valoresCadena(_token);
        try {
            SesionDto sesion = autenticador.getSession(valores[0], valores[1]);
            if (sesion != null) {
                ProcesoIngresos proce = new ProcesoIngresos(Integer.parseInt(valores[2]), 0, sesion.getUsuarioDto().getLogUsu(),
                        getConector().getCon(sesion.getConexName()));
                arr = proce.listarProductosVenta(Integer.parseInt(valores[3]), new Fechas().fechaActual("dd/MM/yyyy"));
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

}
