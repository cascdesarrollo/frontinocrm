/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.logica.seguridad;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author casc
 */
public class Conector {

    private Connection conDirectCloud;

    public Connection getConDirectCloud() {
        try {
            File f = new File(".");
            try {
                System.out.println(f.getCanonicalPath());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (conDirectCloud == null || conDirectCloud.isClosed()) {
                conDirectCloud = new com.sy.conexion.Conexion(null).getConexion();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conDirectCloud;
    }

    public void matarConexionDirectCloud() throws SQLException {
        if (conDirectCloud != null) {
            conDirectCloud.close();
        }
    }

    private Connection con;

    public Connection getCon(String[] _params) {
        try {
            if (con == null || con.isClosed()) {
                con = new com.sy.conexion.Conexion(_params).getConexion();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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
