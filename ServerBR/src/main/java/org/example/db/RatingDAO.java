package org.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RatingDAO {
    private final DataBaseConnection db;

    public RatingDAO() {
        this.db = new DataBaseConnection();
    }

    // Metodo per salvare la valutazione nel database
    public boolean salvaSuDatabase(Rating r) {
        String sql = "INSERT INTO valutazioniLibri (user_id, id_libro, stile, contenuto, gradevolezza, " +
                "originalita, edizione, votoFinale, recensione) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, r.getIdUtente());
            pstmt.setInt(2, r.getIdLibro());
            pstmt.setInt(3, r.getStile());
            pstmt.setInt(4, r.getContenuto());
            pstmt.setInt(5, r.getGradevolezza());
            pstmt.setInt(6, r.getOriginalita());
            pstmt.setInt(7, r.getEdizione());
            pstmt.setInt(8, r.getVotoFinale());
            pstmt.setString(9, r.getRecensione());


            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
