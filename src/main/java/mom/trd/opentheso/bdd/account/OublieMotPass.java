/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.account;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.tools.MD5Password;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "oublie", eager = true)
@SessionScoped

public class OublieMotPass {

    private String email;
    private String nomUsu;
    private String passsansmd5;

    public void vide(HikariDataSource ds) {
        String nouvellePass = "";
        Statement stmt;
        ResultSet resultSet;
        if (email != null && nomUsu != null) {
            try {
                Connection conn = ds.getConnection();
                stmt = conn.createStatement();
                try {
                    String query = "Select id_user from users where mail ='" + email + "'";
                    resultSet = stmt.executeQuery(query);

                    nouvellePass = MD5Password.getEncodedPassword(genererNouvellePass());
                    insertNP(ds, nouvellePass);

                } finally {
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void insertNP(HikariDataSource ds, String nouvellePass) {
        Statement stmt;
        try {
            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            try {
                String queryAjouPass = "update users set motpasstemp ='" + nouvellePass + "' where mail = '" + email + "'AND username ='" + nomUsu + "'";
                stmt.executeQuery(queryAjouPass);
            } finally {
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String genererNouvellePass() {
        String code = "";
        int sum = 0;
        String[] alfa = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        while (sum < 10) {
            int numRandon = (int) Math.round(Math.random() * 36);
            code += alfa[numRandon];
            sum++;
        }
        passsansmd5 = code;
        return code;
    }

    public String getNomUsu() {
        return nomUsu;
    }

    public void setNomUsu(String nomUsu) {
        this.nomUsu = nomUsu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
