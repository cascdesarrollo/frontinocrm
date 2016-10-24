/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.logica.tareas;

import com.frontino.serv.logica.email.Correos;
import com.frontino.serv.logica.seguridad.Authenticator;
import com.frontino.serv.logica.seguridad.Conector;
import com.sy.sentencias.listar.Listar;
import com.sy.sentencias.modificar.Modificar;
import java.sql.ResultSet;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author casc
 */
@Stateless
public class TareasCorreos {

    private Conector conector = null;

    private Conector getConector() {
        if (conector == null) {
            conector = new Conector();
        }
        return conector;
    }

    @Schedule(hour = "00-23", minute = "00-59")
    public void correosActivacion() {
        Listar sel = new Listar(getConector().getConDirectCloud());
        ResultSet rs;
        Modificar upd = new Modificar(getConector().getConDirectCloud());
        try {
            sel.setSentencia("SELECT id_cli, nom_emp, nom_ter, ema_ter "
                    + " FROM cli002d WHERE sta_ter='P' AND (cod_act IS NULL  OR trim(cod_act)='' )");
            rs = sel.ejecutar();
            /*
             Dejar while si se vuelve loco enviando muchos correos colocamos que 
             evalue cada minuto y que lo haga con un if
             */
            String codigo;
            while (rs.next()) {
                codigo = Authenticator.genearCodigoActivacion();
                Correos cor = new Correos();
                cor.enviarCorreoNuevoCliente(rs.getString("nom_emp"),
                        rs.getString("nom_ter"),
                        codigo,
                        rs.getString("ema_ter")
                );
                upd.ejecutarUpdate(true, "UPDATE cli002d set cod_act='" + codigo
                        + "'  WHERE id_cli=" + rs.getInt("id_cli")
                        + " AND sta_ter='P' ");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
