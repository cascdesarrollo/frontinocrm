/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.services.cobranzas;

import com.frontino.serv.logica.seguridad.Conector;
import com.frontinoadm.logica.ingresos.ProcesoIngresos;
import com.frontinoadm.logica.ingresos.doumento.Doc007mDto;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author casc
 */
@Path("cobranzas")
public class CobranzasREST {

    @Context
    private UriInfo context;
    
    private Conector conector = null;

    private Conector getConector() {
        if (conector == null) {
            conector = new Conector();
        }
        return conector;
    }

    
}
