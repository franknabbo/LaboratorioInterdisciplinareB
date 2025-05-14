package org.example.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SuggestedBookDAO {
    private final DataBaseConnection db;

    public SuggestedBookDAO() {
        this.db = new DataBaseConnection();
    }

    //Metodo per ottenere i libri suggeriti di un determinato libro, contanto quelli che sonos stati consigliati piu volte


    //Metodo per inserire i libri suggeriti di uno specifico libro
    //CREATE TABLE IF NOT EXISTS public.consiglilibri
    //(
    //    user_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    //    id_libro_referenced integer NOT NULL,
    //    id_libro_suggested integer NOT NULL,
    //    CONSTRAINT consiglilibri_pkey PRIMARY KEY (user_id, id_libro_referenced, id_libro_suggested)
    //);
    private int isSuggestedBookAlreadyAdded(String userId, int idLibroReferenced) {
        String sql = "SELECT * FROM consiglilibri WHERE user_id = ? AND id_libro_referenced = ?";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, idLibroReferenced);
            var exc = pstmt.executeQuery();
            int i= 0;
            while (exc.next()) {
                i++;
            }
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addSuggestedBook(String userId, int idLibroReferenced, List<Integer> suggestedBooks) {
        // Verifica se esiste già questa esatta combinazione
        int i = isSuggestedBookAlreadyAdded(userId, idLibroReferenced);
        if (i>0) {
            //fai la delete di tutti i libri suggeriti con idLibroReferenced e userId
            String sql = "DELETE FROM consiglilibri WHERE user_id = ? AND id_libro_referenced = ?";
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, userId);
                pstmt.setInt(2, idLibroReferenced);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Inserisce un nuovo record
            for(int idLibroSuggested : suggestedBooks) {
                // Inserisce un nuovo record
                String sql2 = "INSERT INTO consiglilibri (user_id, id_libro_referenced, id_libro_suggested) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql2)) {
                    pstmt.setString(1, userId);
                    pstmt.setInt(2, idLibroReferenced);
                    pstmt.setInt(3, idLibroSuggested);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } else {
            // Se non esiste, inserisce un nuovo record
            for(int idLibroSuggested : suggestedBooks) {
                // Inserisce un nuovo record
                String sql = "INSERT INTO consiglilibri (user_id, id_libro_referenced, id_libro_suggested) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
                    pstmt.setString(1, userId);
                    pstmt.setInt(2, idLibroReferenced);
                    pstmt.setInt(3, idLibroSuggested);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
}
