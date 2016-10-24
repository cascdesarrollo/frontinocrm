/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frontino.serv.logica.email;

import com.frontinoseg.logica.seguridad.sesion.ClienteDto;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author casc
 */
public class Correos {

    final String username = "frontinosoft@gmail.com";
    final String password = "frontino2013";
    final String fromEmail = "frontinosoft@gmail.com";
    final String urlCRM = "https://frontinosoft.com";

    Properties props;
    Session session;

    private void inicializarParametrosSsl() {
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("username", "password");
                    }
                });
    }

    private void inicializarParametros() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public boolean enviarCorreoNuevoRegistro(ClienteDto _dto) {
        boolean result = false;
        inicializarParametros();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(_dto.getEmaCli()));
            message.setSubject("Activación de Cuenta");
            //OJO COLOCARmessage.setText(this.mensajeCambioPassword(fromEmail, password));

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Envio de Correo cuando se ha hecho un cambio de contraseña
     *
     * @param _email
     * @param _password
     * @return
     */
    public boolean enviarCorreoCambioContrasena(String _empresa, String _tercero,
            String _email, String _password) {
        boolean result = false;
        inicializarParametros();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(_email));
            message.setSubject("Cambio de Password");
            message.setContent(this.mensajeCambioPassword(_empresa, _tercero, _password), "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            System.out.println("Error enviando Correo " + e.getMessage());
        }
        return result;
    }

    /**
     *
     * @param _empresa
     * @param _tercero
     * @param _email
     * @param _password
     * @return
     */
    public boolean enviarCorreoActivacion(String _empresa,
            String _tercero,
            String _email, String _password) {
        boolean result = false;
        inicializarParametros();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(_email));
            message.setSubject("Activación de Usuario");
            message.setContent(this.mensajeActivacion(_empresa, _tercero, _password), "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            System.out.println("Error enviando Correo " + e.getMessage());
        }
        return result;
    }

    public boolean enviarCorreoNuevoCliente(String _empresa,
            String _tercero,
            String _codigo,
            String _email) {
        boolean result = false;
        inicializarParametros();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(_email));
            message.setSubject("Nueva Cuenta ");
            message.setContent(this.mensajeNuevaCuenta(_empresa, _tercero, _codigo), "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            System.out.println("Error enviando Correo " + e.getMessage());
        }
        return result;
    }
    
    
    private String mensajeNuevaCuenta(String _empresa, String _tercero, String _codigo) {
        return "<html><body>"
                + "<div style='background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:100%!important'> "
                + "<br><br>"
                + "<div style='background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:95%!important'> "
                + "<div style='overflow:hidden;color:transparent;width:0;font-size:0;min-height:0'></div>"
                + tablaCabecera(_empresa, _tercero)
                + "<table border='0' align='center' border='0' cellspacing='0' cellpadding='0' width='100%' bgcolor='#EDF0F3' style='background-color:#edf0f3;table-layout:fixed'>"
                + "<tr><td style='font-family:Source Sans Pro,sans-serif,Helvetica,Arial;color:#43484f;font-weight:400;font-size:20px;padding:0px 5px 3px 15px;text-align:left' colspan='2'><br>"
                + "<span style='font-family:Source Sans Pro,sans-serif,Helvetica,Arial;color:#ffffff;font-weight:400;font-size:30px;padding:0px 5px 3px 15px;text-align:left\'>"
                + "Te Damos la Bienvenida a Nuestro Portal"
                + "</span>"
                + "<br/>"
                + " Ingresa y Activa tu cuenta con tu email y el siguiente codigo de Activacion "
                +"<br>"
                + _codigo
                + "</td></tr><tr><td align='center' colspan='2'>"
                + "<a href='" + urlCRM + "' style='background:#78c259;border:1px solid #78c259;color:#ffffff;display:inline-block;font-family:Source Sans Pro,sans-serif,Helvetica,Arial;font-size:14px;font-weight:bold;line-height:50px;text-align:center;text-decoration:none;width:300px;text-transform:uppercase' target='_blank' >Ir a Portal!</a>"
                + "<br><br></td></tr></table>"
                + "</div><div style='background-color:#ffffff;padding:0;margin:0 auto;font-weight:200;width:95%!important'> <div style='overflow:hidden;color:transparent;width:0;font-size:0;min-height:0'></div>"
                //Tabla Final
                + tablaFinal();
    }

    private String mensajeActivacion(String _empresa, String _tercero, String _password) {
        return "<html><body>"
                + "<div style='background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:100%!important'> "
                + "<br><br>"
                + "<div style='background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:95%!important'> "
                + "<div style='overflow:hidden;color:transparent;width:0;font-size:0;min-height:0'></div>"
                + tablaCabecera(_empresa, _tercero)
                + "<table border='0' align='center' border='0' cellspacing='0' cellpadding='0' width='100%' bgcolor='#EDF0F3' style='background-color:#edf0f3;table-layout:fixed'>"
                + "<tr><td style='font-family:Source Sans Pro,sans-serif,Helvetica,Arial;color:#43484f;font-weight:400;font-size:20px;padding:0px 5px 3px 15px;text-align:left' colspan='2'><br>"
                + "Se ha realizado la activación de su cuenta a travez de nuestro portal "
                + "<br/>"
                + " Su Contraseña es " + _password
                + "</td></tr><tr><td align='center' colspan='2'>"
                + "<a href='" + urlCRM + "' style='background:#78c259;border:1px solid #78c259;color:#ffffff;display:inline-block;font-family:Source Sans Pro,sans-serif,Helvetica,Arial;font-size:14px;font-weight:bold;line-height:50px;text-align:center;text-decoration:none;width:300px;text-transform:uppercase' target='_blank' >Ir a Portal!</a>"
                + "<br><br></td></tr></table>"
                + "</div><div style='background-color:#ffffff;padding:0;margin:0 auto;font-weight:200;width:95%!important'> <div style='overflow:hidden;color:transparent;width:0;font-size:0;min-height:0'></div>"
                //Tabla Final
                + tablaFinal();
    }

    private String mensajeCambioPassword(String _empresa, String _tercero, String _password) {
        return "<html><body>"
                + "<div style='background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:100%!important'> "
                + "<br><br>"
                + "<div style='background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:95%!important'> "
                + "<div style='overflow:hidden;color:transparent;width:0;font-size:0;min-height:0'></div>"
                + tablaCabecera(_empresa, _tercero)
                + "<table border='0' align='center' border='0' cellspacing='0' cellpadding='0' width='100%' bgcolor='#EDF0F3' style='background-color:#edf0f3;table-layout:fixed'>"
                + "<tr><td style='font-family:Source Sans Pro,sans-serif,Helvetica,Arial;color:#43484f;font-weight:400;font-size:20px;padding:0px 5px 3px 15px;text-align:left' colspan='2'><br>"
                + "Se ha realizado cambio de Contraseña a travez de nuestro portal "
                + "<br/>"
                + " Su nueva Contraseña es " + _password
                + "</td></tr><tr><td align='center' colspan='2'>"
                + "<a href='" + urlCRM + "' style='background:#78c259;border:1px solid #78c259;color:#ffffff;display:inline-block;font-family:Source Sans Pro,sans-serif,Helvetica,Arial;font-size:14px;font-weight:bold;line-height:50px;text-align:center;text-decoration:none;width:300px;text-transform:uppercase' target='_blank' >Ir a Portal!</a>"
                + "<br><br></td></tr></table>"
                + "</div><div style='background-color:#ffffff;padding:0;margin:0 auto;font-weight:200;width:95%!important'> <div style='overflow:hidden;color:transparent;width:0;font-size:0;min-height:0'></div>"
                //Tabla Final
                + tablaFinal();
    }

    private String tablaCabecera(String _empresa, String _tercero) {
        String result
                = "<table border='0' align='center' border='0' cellspacing='0' cellpadding='0' width='100%' bgcolor='#EDF0F3' style='background-color:#edf0f3;table-layout:fixed;padding-top: 10px;'>"
                + "<tr><td bgcolor='#2791bd' align='center' style='background-color:#2791bd;padding:18px 24px;text-align:center' colspan='2'> "
                + "<span style='font-family:Source Sans Pro,sans-serif,Helvetica,Arial;color:#ffffff;font-weight:400;font-size:30px;padding:0px 5px 3px 15px;text-align:left'>"
                + _empresa
                + "</span></td></tr></table>";
        if (_tercero != null) {
            result += "<table border='0' align='center' border='0' cellspacing='0' cellpadding='0' width='100%' bgcolor='#EDF0F3' style='background-color:#edf0f3;table-layout:fixed'>"
                    + "<tr><td style='font-family:Source Sans Pro,sans-serif,Helvetica,Arial;color:#43484f;font-weight:400;font-size:30px;padding:0px 5px 3px 15px;text-align:left' colspan='2'><br>"
                    + "Hola " + _tercero + "</td>"
                    + "</tr></table>";
        }
        return result;
    }

    private String tablaFinal() {
        return "<table border='0' cellspacing='0' cellpadding='0' style='font-weight:200;font-family:Helvetica,Arial,sans-serif;height: 50px;' width='100%' >"
                + "<tbody> <tr> <td align='center' width='100%' style='padding:0 0 12px 0;font-size:12px;line-height:16px'>"
                + "<br><span dir='ltr'> 2016 FrontinoSoft C.A. <small>J-40855845-9</small>, Barrio Obrero Carrera 18 en Calles 9 y 10. San Cristobal-Estado Tachira. Venezuela</span></td> </tr> </tbody> </table>"
                + "</div><br><br></div></body></html>";
    }

}
