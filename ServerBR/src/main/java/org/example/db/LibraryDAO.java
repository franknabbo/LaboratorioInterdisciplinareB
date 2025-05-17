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


public class LibraryDAO {
    private final DataBaseConnection db;

    public LibraryDAO() {
        this.db = new DataBaseConnection();
    }

    /**
     * Crea una nuova libreria per un utente
     */
    public String createLibrary(String userId, String libraryName) {
        // Verifica che userId e libraryName non siano vuoti
        if (userId.isEmpty() || libraryName.isEmpty()) {
            return "LIBRARY_CREATION_FAILED:UserId o nome libreria mancante";
        }

        // Verifica che non esista già una libreria con lo stesso nome
        if (libraryExists(userId, libraryName)) {
            return "LIBRARY_CREATION_FAILED:Esiste già una libreria con questo nome";
        }

        // Inserisci la nuova libreria nel database
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(
                    "INSERT INTO librerie(user_id, nome) VALUES (?, ?)");
            stmt.setString(1, userId);
            stmt.setString(2, libraryName);

            int righe = stmt.executeUpdate();

            if (righe > 0) {
                return "LIBRARY_CREATED:" + libraryName;
            } else {
                return "LIBRARY_CREATION_FAILED:Nessuna riga inserita";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "LIBRARY_CREATION_FAILED:" + e.getMessage();
        }
    }

    /**
     * Ottiene i libri di una libreria specifica
     */
    public List<Book> getLibraryBooks(String userId, String libraryName) {
        List<Book> books = new ArrayList<>();

        // Trova l'ID della libreria
        int idLibreria = getLibraryId(userId, libraryName);
        if (idLibreria == -1) {
            return books; // Libreria non trovata, ritorna lista vuota
        }

        try {
            String sql = "SELECT Libri.* FROM Libri " +
                    "JOIN librerie_libri ON Libri.id_libro = librerie_libri.id_libro " +
                    "WHERE librerie_libri.id_libreria = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idLibreria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id_libro"),
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getString("categoria"),
                        rs.getString("editore"),
                        rs.getInt("anno_pubblicazione"),
                        rs.getString("copertina")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    /**
     * Verifica se esiste già una libreria con lo stesso nome per l'utente
     */
    public boolean libraryExists(String user_id, String libraryName) {
        try {
            String sql = "SELECT COUNT(*) FROM Librerie WHERE user_id = ? AND nome = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, user_id);
            stmt.setString(2, libraryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Ottiene tutte le librerie di un utente
     */
    public List<Library> getUserLibraries(String userId) {
        List<Library> libraries = new ArrayList<>();
        System.out.println("getUserLibraries: " + userId);
        try {
            String sql = "SELECT * FROM librerie WHERE user_id = ? ORDER BY nome";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Library library = new Library(
                        rs.getInt("id_libreria"),
                        rs.getString("user_id"),
                        rs.getString("nome")
                );
                libraries.add(library);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getUserLibraries: " + libraries);

        return libraries;
    }

    /**
     * Aggiunge un libro a una libreria specifica
     */
    public String addBookToLibrary(String userId, String libraryName, int idLibro) {
        // Verifica che userId, libraryName e book non siano vuoti
        if (userId.isEmpty() || libraryName.isEmpty() || idLibro == 0) {
            return "BOOK_ADD_FAILED:Parametri mancanti";
        }


        // Trova l'ID della libreria
        int idLibreria = getLibraryId(userId, libraryName); // ?
        if (idLibreria == -1) {
            return "BOOK_ADD_FAILED:Libreria non trovata";
        }

        if (idLibro == -1) {
            return "BOOK_ADD_FAILED:Libro non trovato nel catalogo";
        }

        // Verifica che il libro non sia già nella libreria
        if (bookExistsInLibrary(idLibreria, idLibro)) {
            return "BOOK_EXISTS:Libro già presente nella libreria";
        }

        // Aggiungi il libro alla libreria
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(
                    "INSERT INTO librerie_libri(id_libreria, id_libro) VALUES (?, ?)");
            stmt.setInt(1, idLibreria);
            stmt.setInt(2, idLibro);

            int righe = stmt.executeUpdate();

            if (righe > 0) {
                return "BOOK_ADDED:Libro aggiunto con successo";
            } else {
                return "BOOK_ADD_FAILED:Nessuna riga inserita";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "BOOK_ADD_FAILED:" + e.getMessage();
        }
    }

    /**
     * Ottiene l'ID di una libreria dato il nome e l'ID dell'utente
     */
    private int getLibraryId(String userId, String libraryName) {
        try {
            String sql = "SELECT id_libreria FROM Librerie WHERE user_id = ? AND nome = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, libraryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_libreria");
            }
            return -1; // Libreria non trovata
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Verifica se un libro è già presente in una libreria
     */
    public boolean bookExistsInLibrary(int idLibreria, int idLibro) {
        try {
            String sql = "SELECT COUNT(*) FROM librerie_libri WHERE id_libreria = ? AND id_libro = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idLibreria);
            stmt.setInt(2, idLibro);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void closeConnection() {
        db.closeConnection();
    }
}