package com.frontino.serv.logica.seguridad;

import com.frontinoadm.logica.tercero.tercero.Tercero;
import com.frontinoadm.logica.tercero.tercero.TerceroDto;
import com.frontinoseg.logica.empresa.Empresa;
import com.frontinoseg.logica.empresa.EmpresaDto;
import com.frontinoseg.logica.seguridad.sesion.SesionDto;
import com.frontinoseg.logica.seguridad.sesion.SessionDataServiceDto;
import com.sy.sentencias.listar.Listar;
import com.sy.sentencias.modificar.Modificar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;

public final class Authenticator {

    private static Authenticator authenticator = null;

    // A service key storage which stores <service_key, username>
    private final Map<String, String> serviceKeysStorage = new HashMap();

    // An authentication token storage which stores <service_key, auth_token>.
    private final Map<String, SesionDto> authorizationTokensStorage = new HashMap();

    private Authenticator() {

    }

    public static Authenticator getInstance() {
        if (authenticator == null) {
            authenticator = new Authenticator();
        }

        return authenticator;
    }

    /**
     *
     * @param _con
     * @param username
     * @param password
     * @return
     * @throws LoginException
     */
    public String login(Connection _con, String username, String password) throws LoginException {
        ResultSet rs;
        Listar sel = new Listar(_con);
        try {
            sel.setSentencia(new StringBuilder()
                    .append("SELECT id_cli FROM cli002d ")
                    .append(" WHERE ema_ter='").append(username)
                    .append("' and pas_ter=md5('").append(password).append("')")
                    .toString());
            rs = sel.ejecutar();
            if (rs.next()) {
                SesionDto dto = new SesionDto();
                dto.getUsuarioDto().setLogUsu(username);
                String authToken = UUID.randomUUID().toString();
                authorizationTokensStorage.put(authToken, dto);
                return authToken;
            } else {
                throw new LoginException("Don't Come Here Again!");
            }
        } catch (Exception ex) {
            throw new LoginException(ex.getMessage());
        }
    }

    /**
     *
     * @param logUsu
     * @param authToken
     * @return
     */
    public boolean isAuthTokenValid(String logUsu, String authToken) {
        if (authorizationTokensStorage.containsKey(authToken)) {
            SesionDto sesion = authorizationTokensStorage.get(authToken);
            if (sesion.getUsuarioDto().getLogUsu().equals(logUsu)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param logUsu
     * @param authToken
     * @throws GeneralSecurityException
     */
    public void logout(String logUsu, String authToken) throws GeneralSecurityException {
        if (authorizationTokensStorage.containsKey(authToken)) {
            SesionDto sesion = authorizationTokensStorage.get(authToken);
            if (sesion.getUsuarioDto().getLogUsu().equals(logUsu)) {
                authorizationTokensStorage.remove(authToken);
                return;
            }
        }
        throw new GeneralSecurityException("Invalid service key and authorization token match.");
    }

    /**
     *
     * @param _con
     * @param _authToken
     * @return
     * @throws Exception
     */
    public List<Object> listadoEmpresas(Connection _con, String _authToken) throws Exception {
        SesionDto sesion = authorizationTokensStorage.get(_authToken);

        List<Object> arr = new ArrayList<>();
        Listar sel = new Listar(_con);
        ResultSet rs;
        try {
            sel.setSentencia(new StringBuilder()
                    .append("SELECT ide_emp, nom_emp, ide_ter")
                    .append(" FROM cli002d")
                    .append(" WHERE ema_ter='").append(sesion.getUsuarioDto().getLogUsu())
                    .append("'")
                    .toString());
            rs = sel.ejecutar();
            while (rs.next()) {
                String[] emp = {rs.getString("ide_emp"),
                    rs.getString("nom_emp"),
                    rs.getString("ide_ter")};
                arr.add(emp);
            }
            rs.close();
            sel.cerrar();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        return arr;
    }

    /**
     * Descompone token enviado desde frontEnd Devuelve arreglo de String con
     * valores en posiciones 0- Usuario 1- Token Real 2- Empresa 3- Tercero
     *
     * @param _valor
     * @return
     */
    public String[] valoresCadena(String _valor) {
        String usuario, authToken, empresa, tercero;
        try {
            String[] uno = _valor.split("/");
            usuario = uno[0];
            String[] dos = uno[1].split("e");
            empresa = dos[0];
            tercero = dos[1];
            authToken = uno[1].substring(
                    (empresa.length() + tercero.length() + 2),
                    uno[1].length()
            );
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            return null;
        }
        return new String[]{usuario, authToken, empresa, tercero};
    }

    /**
     *
     * @param _con
     * @param _logUsu
     * @param _authToken
     * @param _ideEmp
     * @param _ideTer
     * @return
     * @throws Exception
     */
    public SesionDto obtenerDatos(Connection _con, String _logUsu, String _authToken, int _ideEmp, int _ideTer) throws Exception {
        SesionDto dto;
        if (isAuthTokenValid(_logUsu, _authToken)) {
            if (authorizationTokensStorage.containsKey(_authToken)) {
                dto = authorizationTokensStorage.get(_authToken);
                if (dto.getConexName() == null) {
                    Listar sel = new Listar(_con);
                    ResultSet rs;
                    try {
                        sel.setSentencia(
                                new StringBuilder()
                                .append(" SELECT dip_bas, nom_bas, log_bas, pas_bas")
                                .append(" FROM bas001m, cli002d ")
                                .append(" WHERE bas001m.id_cli=cli002d.id_cli")
                                .append(" AND ide_emp=").append(_ideEmp)
                                .append(" AND ide_ter=").append(_ideTer)
                                .toString());
                        rs = sel.ejecutar();
                        if (rs.next()) {
                            String[] arr = new String[4];
                            arr[0] = "org.postgresql.Driver";
                            arr[1] = "jdbc:postgresql://" + rs.getString("dip_bas")
                                    + "/" + rs.getString("nom_bas");
                            arr[2] = rs.getString("log_bas");
                            arr[3] = rs.getString("pas_bas");
                            dto.setConexName(arr);
                        }
                        rs.close();
                        sel.cerrar();

                    } catch (Exception ex) {
                        throw new Exception(ex.getMessage());
                    }
                }
                return dto;
            }
        }

        return null;
    }

    /**
     *
     * @param _con
     * @param _dto
     * @param _ideEmp
     * @param _ideTer
     * @return
     * @throws Exception
     */
    public SessionDataServiceDto prepararData(Connection _con, SesionDto _dto, int _ideEmp, int _ideTer) throws Exception {
        SessionDataServiceDto data = new SessionDataServiceDto();

        try {
            Empresa emp = new Empresa(_dto.getUsuarioDto().getLogUsu(), _con);
            _dto.setEmpresaDto(new EmpresaDto());
            emp.consultarRegistro(_ideEmp, null, _dto.getEmpresaDto());
            TerceroDto dtoTer = new TerceroDto();
            Tercero ter = new Tercero(_ideEmp, _dto.getUsuarioDto().getLogUsu(), _con);
            ter.consultarRegistro(_ideTer, null, dtoTer);
            data.setNomEmp(_dto.getEmpresaDto().getNom_emp());
            data.setRifEmp(_dto.getEmpresaDto().getRif_emp());
            data.setDirEmp(_dto.getEmpresaDto().getDir_emp());
            data.setNomTer(dtoTer.getNom_ter());
            data.setRifTer(dtoTer.getIde_dfi().getRif_dfi());
            data.setRazTer(dtoTer.getIde_dfi().getRaz_dfi());
            data.setDirTer(dtoTer.getIde_dfi().getDir_dfi());
            data.setDieTer(dtoTer.getDir_ter());
            data.setTl1Ter(dtoTer.getTl1_ter());
            data.setTl2Ter(dtoTer.getTl2_ter());
        } catch (Exception ex2) {
            throw new Exception(ex2.getMessage());
        }
        return data;

    }

    /**
     * Obtener datos de session, este es mas que todo para la conexion
     *
     * @param _logUsu
     * @param _authToken
     * @return
     * @throws Exception
     */
    public SesionDto getSession(String _logUsu, String _authToken) throws Exception {
        SesionDto dto;
        if (isAuthTokenValid(_logUsu, _authToken)) {
            if (authorizationTokensStorage.containsKey(_authToken)) {
                dto = authorizationTokensStorage.get(_authToken);
                return dto;
            }
        }
        return null;
    }
    
    
    public boolean cambiarPassword(Connection _conact, String _username, String _password, String _newPassword, boolean _commit) 
            throws Exception {
        boolean  result = false;
        ResultSet rs;
        Listar sel = new Listar(_conact);
        try {
            sel.setSentencia(new StringBuilder()
                    .append("SELECT id_cli FROM cli002d ")
                    .append(" WHERE ema_ter='").append(_username)
                    .append("' and pas_ter=md5('").append(_password).append("')")
                    .toString());
            rs = sel.ejecutar();
            if (rs.next()) {
                Modificar upd =new Modificar(_conact);
                result  = upd.ejecutarUpdate(_commit, "UPDATE cli002d SET pas_ter=md5('"
                +_newPassword+"') WHERE id_cli="+rs.getInt("id_cli"));
                upd.cerrar();
            } else {
                throw new LoginException("Credenciales de Usuario Invalidas!");
            }
            rs.close();
            sel.cerrar();
        } catch (Exception ex) {
            throw new LoginException(ex.getMessage());
        }
        return result;
    }

}
