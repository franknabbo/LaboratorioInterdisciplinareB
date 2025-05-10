package org.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    //getRatingsFromBook
    public List<Rating> getRatingsFromBook(int bookId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM valutazioniLibri WHERE id_libro = ?";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Rating rating = new Rating(
                        rs.getString("user_id"),
                        rs.getInt("id_libro"),
                        rs.getInt("stile"),
                        rs.getInt("contenuto"),
                        rs.getInt("gradevolezza"),
                        rs.getInt("originalita"),
                        rs.getInt("edizione"),
                        rs.getInt("votoFinale"),
                        rs.getString("recensione")
                );
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ratings;
    }

}
