// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe DAO (Data Access Object) per la gestione degli utenti nel database.
 */
public class UtenteDAO {
    private final DataBaseConnection db;

    /**
     * Costruttore della classe UtenteDAO.
     * Inizializza la connessione al database.
     */
    public UtenteDAO() {
        this.db = new DataBaseConnection();
    }

    /**
     * Registra un nuovo utente nel database.
     *
     * @param utente Oggetto Utente da registrare.
     * @return Stringa che indica il risultato della registrazione.
     */
    public String registraUtente(Utente utente) {
        String sql = "INSERT INTO UtentiRegistrati(nome, cognome, codice_fiscale, mail, crypted_pass, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            String userId = generaUserId(utente.getNome(), utente.getCognome());
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getCodiceFiscale());
            stmt.setString(4, utente.getEmail());
            stmt.setString(5, utente.getPassword());
            stmt.setString(6, userId);
            int righe = stmt.executeUpdate();

            if (righe > 0) {
                return "REGISTRAZIONE OK:" + userId;
            } else {
                return "REGISTRAZIONE FALLITA:Nessuna riga inserita";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "REGISTRAZIONE FALLITA:" + e.getMessage();
        }
    }

    /**
     * Esegue il login dell'utente controllando le credenziali fornite.
     *
     * @param userId   ID utente.
     * @param password Password cifrata.
     * @return Stringa che indica il risultato del login.
     */
    public String loginUtente(String userId, String password) {
        String sql = "SELECT * FROM UtentiRegistrati WHERE user_id = ? AND crypted_pass = ?";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "LOGIN OK:" + userId;
            } else {
                return "LOGIN FAILED:Credenziali non valide";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "LOGIN FAILED:" + e.getMessage();
        }
    }

    /**
     * Chiude la connessione al database.
     */
    public void closeConnection() {
        db.closeConnection();
    }

    /**
     * Genera uno user ID unico basato sul nome e cognome forniti.
     *
     * @param nome    Nome dell'utente.
     * @param cognome Cognome dell'utente.
     * @return User ID univoco.
     */
    public String generaUserId(String nome, String cognome) {
        String baseUserId = (nome.charAt(0) + cognome).toLowerCase().replaceAll("\\s+", "");
        String userId = baseUserId;
        int counter = 1;

        String sql = "SELECT COUNT(*) FROM UtentiRegistrati WHERE user_id = ?";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            while (true) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    userId = baseUserId + String.format("%02d", counter++);
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la generazione dell'userId", e);
        }
        return userId;
    }
}
