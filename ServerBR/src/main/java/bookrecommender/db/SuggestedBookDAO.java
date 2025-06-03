// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione dei suggerimenti di libri associati a un libro di riferimento
 * da parte degli utenti.
 */
public class SuggestedBookDAO {
    private final DataBaseConnection db;

    /**
     * Costruttore che inizializza la connessione al database.
     */
    public SuggestedBookDAO() {
        this.db = new DataBaseConnection();
    }

    /**
     * Controlla quante volte un utente ha già suggerito libri per un dato libro di riferimento.
     *
     * @param userId            ID dell'utente.
     * @param idLibroReferenced ID del libro di riferimento.
     * @return Numero di suggerimenti già presenti, o -1 in caso di errore.
     */
    private int isSuggestedBookAlreadyAdded(String userId, int idLibroReferenced) {
        String sql = "SELECT * FROM consiglilibri WHERE user_id = ? AND id_libro_referenced = ?";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, idLibroReferenced);
            var exc = pstmt.executeQuery();
            int i = 0;
            while (exc.next()) {
                i++;
            }
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Aggiunge o aggiorna i libri suggeriti da un utente per un dato libro di riferimento.
     * Se esistono già suggerimenti, questi vengono eliminati prima di salvare i nuovi.
     *
     * @param userId            ID dell'utente.
     * @param idLibroReferenced ID del libro di riferimento.
     * @param suggestedBooks    Lista di ID dei libri suggeriti.
     * @return true se l'operazione è andata a buon fine, false altrimenti.
     */
    public boolean addSuggestedBook(String userId, int idLibroReferenced, List<Integer> suggestedBooks) {
        int i = isSuggestedBookAlreadyAdded(userId, idLibroReferenced);
        if (i > 0) {
            String sql = "DELETE FROM consiglilibri WHERE user_id = ? AND id_libro_referenced = ?";
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, userId);
                pstmt.setInt(2, idLibroReferenced);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (int idLibroSuggested : suggestedBooks) {
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

        return true;
    }

    /**
     * Restituisce una lista degli ID dei libri più consigliati per un determinato libro di riferimento.
     * Vengono restituiti al massimo 3 libri suggeriti, ordinati per frequenza decrescente.
     *
     * @param idLibroReferenced ID del libro di riferimento.
     * @return Lista di ID dei libri suggeriti, o null in caso di errore.
     */
    public List<Integer> getSuggestedBooks(int idLibroReferenced) {
        String sql = "SELECT id_libro_suggested, COUNT(*) as count FROM consiglilibri " +
                "WHERE id_libro_referenced = ? GROUP BY id_libro_suggested " +
                "ORDER BY count DESC LIMIT 3";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idLibroReferenced);
            var exc = pstmt.executeQuery();
            List<Integer> suggestedBooks = new ArrayList<>();
            while (exc.next()) {
                suggestedBooks.add(exc.getInt("id_libro_suggested"));
            }
            return suggestedBooks;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
