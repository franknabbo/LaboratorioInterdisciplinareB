package org.example.bookreccomender2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UtenteDAO {
    private final DataBaseConnection db;

    public UtenteDAO() {
        this.db = new DataBaseConnection();
    }

    public boolean registraUtente(Utente utente) {
        String sql = "INSERT INTO UtentiRegistrati(nome, cognome, codice_fiscale, mail, crypted_pass) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getCodiceFiscale());
            stmt.setString(4, utente.getEmail());
            stmt.setString(5, hashPassword(utente.getPassword()));

            int righe = stmt.executeUpdate();
            return righe > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUtente(String email, String password) {
        String sql = "SELECT * FROM UtentiRegistrati WHERE mail = ? AND crypted_pass = ?";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashPassword(password));

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true se trovato
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }
}

