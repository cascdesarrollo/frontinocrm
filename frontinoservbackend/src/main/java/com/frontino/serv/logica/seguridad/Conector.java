/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.logica.seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author casc
 */
public class Conector {

    private Connection conMd5;

    public Connection getConMd5() {
        try {
            if (conMd5 == null || conMd5.isClosed()) {
                conMd5 = new com.sy.conexion.Conexion(false).ConexionMd5();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conMd5;
    }

    public void matarConexionMd5() throws SQLException {
        if (conMd5 != null) {
            conMd5.close();
        }
    }

    private Connection con;

    public Connection getCon(String[] _params) {
        try {
            if (con == null || con.isClosed()) {
                con = new com.sy.conexion.Conexion(_params).getConexion();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }

    public void matarConexion() throws SQLException {
        if (con != null) {
            con.close();
        }
    }

}
