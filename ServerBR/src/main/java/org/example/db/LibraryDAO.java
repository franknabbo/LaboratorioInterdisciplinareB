// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe {@code LibraryDAO} gestisce le operazioni di accesso al database
 * relative alle librerie personali degli utenti, inclusa la creazione,
 * il recupero e l'associazione di libri.
 */
public class LibraryDAO {
    private final DataBaseConnection db;

    /**
     * Costruttore che inizializza la connessione al database.
     */
    public LibraryDAO() {
        this.db = new DataBaseConnection();
    }

    /**
     * Crea una nuova libreria per un utente specificato.
     *
     * @param userId      l'identificativo dell'utente
     * @param libraryName il nome della nuova libreria
     * @return un messaggio che indica l'esito dell'operazione
     */
    public String createLibrary(String userId, String libraryName) {
        if (userId.isEmpty() || libraryName.isEmpty()) {
            return "LIBRARY_CREATION_FAILED:UserId o nome libreria mancante";
        }

        if (libraryExists(userId, libraryName)) {
            return "LIBRARY_CREATION_FAILED:Esiste già una libreria con questo nome";
        }

        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(
                    "INSERT INTO librerie(user_id, nome) VALUES (?, ?)");
            stmt.setString(1, userId);
            stmt.setString(2, libraryName);
            int righe = stmt.executeUpdate();

            return righe > 0 ? "LIBRARY_CREATED:" + libraryName :
                    "LIBRARY_CREATION_FAILED:Nessuna riga inserita";
        } catch (SQLException e) {
            e.printStackTrace();
            return "LIBRARY_CREATION_FAILED:" + e.getMessage();
        }
    }

    /**
     * Restituisce la lista di libri contenuti in una libreria specifica.
     *
     * @param userId      l'identificativo dell'utente
     * @param libraryName il nome della libreria
     * @return una lista di oggetti {@code Book}
     */
    public List<Book> getLibraryBooks(String userId, String libraryName) {
        List<Book> books = new ArrayList<>();
        int idLibreria = getLibraryId(userId, libraryName);
        if (idLibreria == -1) return books;

        try {
            String sql = """
                SELECT Libri.* FROM Libri
                JOIN librerie_libri ON Libri.id_libro = librerie_libri.id_libro
                WHERE librerie_libri.id_libreria = ?
            """;
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idLibreria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id_libro"),
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getString("categoria"),
                        rs.getString("editore"),
                        rs.getInt("anno_pubblicazione"),
                        rs.getString("copertina")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Verifica se esiste già una libreria con lo stesso nome per un utente.
     *
     * @param user_id     l'identificativo dell'utente
     * @param libraryName il nome della libreria
     * @return {@code true} se esiste, altrimenti {@code false}
     */
    public boolean libraryExists(String user_id, String libraryName) {
        try {
            String sql = "SELECT COUNT(*) FROM Librerie WHERE user_id = ? AND nome = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, user_id);
            stmt.setString(2, libraryName);
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce tutte le librerie di un utente.
     *
     * @param userId l'identificativo dell'utente
     * @return una lista di oggetti {@code Library}
     */
    public List<Library> getUserLibraries(String userId) {
        List<Library> libraries = new ArrayList<>();
        try {
            String sql = "SELECT * FROM librerie WHERE user_id = ? ORDER BY nome";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                libraries.add(new Library(
                        rs.getInt("id_libreria"),
                        rs.getString("user_id"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return libraries;
    }

    /**
     * Aggiunge un libro a una libreria specifica.
     *
     * @param userId      l'identificativo dell'utente
     * @param libraryName il nome della libreria
     * @param idLibro     l'ID del libro da aggiungere
     * @return un messaggio che indica l'esito dell'operazione
     */
    public String addBookToLibrary(String userId, String libraryName, int idLibro) {
        if (userId.isEmpty() || libraryName.isEmpty() || idLibro == 0) {
            return "BOOK_ADD_FAILED:Parametri mancanti";
        }

        int idLibreria = getLibraryId(userId, libraryName);
        if (idLibreria == -1) return "BOOK_ADD_FAILED:Libreria non trovata";
        if (idLibro == -1) return "BOOK_ADD_FAILED:Libro non trovato nel catalogo";
        if (bookExistsInLibrary(idLibreria, idLibro)) return "BOOK_EXISTS:Libro già presente nella libreria";

        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(
                    "INSERT INTO librerie_libri(id_libreria, id_libro) VALUES (?, ?)");
            stmt.setInt(1, idLibreria);
            stmt.setInt(2, idLibro);
            int righe = stmt.executeUpdate();

            return righe > 0 ? "BOOK_ADDED:Libro aggiunto con successo" :
                    "BOOK_ADD_FAILED:Nessuna riga inserita";
        } catch (SQLException e) {
            e.printStackTrace();
            return "BOOK_ADD_FAILED:" + e.getMessage();
        }
    }

    /**
     * Ottiene l'ID di una libreria dato l'utente e il nome della libreria.
     *
     * @param userId      l'identificativo dell'utente
     * @param libraryName il nome della libreria
     * @return l'ID della libreria o -1 se non trovata
     */
    private int getLibraryId(String userId, String libraryName) {
        try {
            String sql = "SELECT id_libreria FROM Librerie WHERE user_id = ? AND nome = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, libraryName);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("id_libreria") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Verifica se un libro è già presente in una libreria.
     *
     * @param idLibreria l'ID della libreria
     * @param idLibro    l'ID del libro
     * @return {@code true} se il libro è già presente, altrimenti {@code false}
     */
    public boolean bookExistsInLibrary(int idLibreria, int idLibro) {
        try {
            String sql = "SELECT COUNT(*) FROM librerie_libri WHERE id_libreria = ? AND id_libro = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idLibreria);
            stmt.setInt(2, idLibro);
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Chiude la connessione al database.
     */
    public void closeConnection() {
        db.closeConnection();
    }
}
