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

        // Recupera l'id_utente dal database usando userId
        int idUtente = getUserIdNumeric(userId);
        if (idUtente == -1) {
            return "LIBRARY_CREATION_FAILED:Utente non trovato";
        }

        // Verifica che non esista già una libreria con lo stesso nome
        if (libraryExists(idUtente, libraryName)) {
            return "LIBRARY_CREATION_FAILED:Esiste già una libreria con questo nome";
        }

        // Ottieni il prossimo ID disponibile per la libreria
        int nextLibraryId = getNextLibraryId();
        if (nextLibraryId == -1) {
            return "LIBRARY_CREATION_FAILED:Errore nella generazione dell'ID libreria";
        }

        // Inserisci la nuova libreria nel database
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(
                    "INSERT INTO librerie(id_libreria, id_utente, nome) VALUES (?, ?, ?)");

            stmt.setInt(1, nextLibraryId);
            stmt.setInt(2, idUtente);
            stmt.setString(3, libraryName);

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
        int idUtente = getUserIdNumeric(userId);

        if (idUtente == -1) {
            return books; // Utente non trovato, ritorna lista vuota
        }

        // Trova l'ID della libreria
        int idLibreria = getLibraryId(idUtente, libraryName);
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
     * Ottiene l'ID numerico dell'utente dal suo userId
     */
    public int getUserIdNumeric(String userId) {
        try {
            String sql = "SELECT id_utente FROM UtentiRegistrati WHERE user_id = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_utente");
            }
            return -1; // Utente non trovato
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Verifica se esiste già una libreria con lo stesso nome per l'utente
     */
    public boolean libraryExists(int idUtente, String libraryName) {
        try {
            String sql = "SELECT COUNT(*) FROM Librerie WHERE id_utente = ? AND nome = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idUtente);
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
     * Ottiene il prossimo ID disponibile per la libreria
     */
    private int getNextLibraryId() {
        try {
            PreparedStatement idStmt = db.getConnection().prepareStatement(
                    "SELECT COALESCE(MAX(id_libreria), 0) + 1 AS next_id FROM librerie");
            ResultSet idRs = idStmt.executeQuery();
            if (idRs.next()) {
                return idRs.getInt("next_id");
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Ottiene tutte le librerie di un utente
     */
    public List<Library> getUserLibraries(String userId) {
        List<Library> libraries = new ArrayList<>();
        int idUtente = getUserIdNumeric(userId);

        if (idUtente == -1) {
            return libraries; // Utente non trovato, ritorna lista vuota
        }

        try {
            String sql = "SELECT * FROM Librerie WHERE id_utente = ? ORDER BY nome";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idUtente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Library library = new Library(
                        rs.getInt("id_libreria"),
                        rs.getInt("id_utente"),
                        rs.getString("nome")
                );
                libraries.add(library);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return libraries;
    }

    /**
     * Aggiunge un libro a una libreria specifica
     */
    public String addBookToLibrary(String userId, String libraryName, Book book) {
        // Verifica che userId, libraryName e book non siano vuoti
        if (userId.isEmpty() || libraryName.isEmpty() || book == null) {
            return "BOOK_ADD_FAILED:Parametri mancanti";
        }

        // Recupera l'id_utente
        int idUtente = getUserIdNumeric(userId);
        if (idUtente == -1) {
            return "BOOK_ADD_FAILED:Utente non trovato";
        }

        // Trova l'ID della libreria
        int idLibreria = getLibraryId(idUtente, libraryName);
        if (idLibreria == -1) {
            return "BOOK_ADD_FAILED:Libreria non trovata";
        }

        // Verifica che il libro esista nel database
        int idLibro = getBookId(book.getTitle(), book.getAuthor());
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
    private int getLibraryId(int idUtente, String libraryName) {
        try {
            String sql = "SELECT id_libreria FROM Librerie WHERE id_utente = ? AND nome = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, idUtente);
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
     * Ottiene l'ID di un libro dato il titolo e l'autore
     */
    private int getBookId(String title, String author) {
        try {
            String sql = "SELECT id_libro FROM Libri WHERE titolo = ? AND autore = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_libro");
            }
            return -1; // Libro non trovato
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Verifica se un libro è già presente in una libreria
     */
    private boolean bookExistsInLibrary(int idLibreria, int idLibro) {
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

    // Metodo da aggiungere alla classe LibraryDAO
    public boolean verificaLibroInLibreriaUtente(int idUtente, int idLibro) {
        String sql = "SELECT COUNT(*) FROM librerie_personali lp " +
                "JOIN contenuto_librerie cl ON lp.id_libreria = cl.id_libreria " +
                "JOIN libri l ON cl.id_libro = l.id_libro " +
                "WHERE lp.id_utente = ? AND l.id_libro = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUtente);
            stmt.setInt(2, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void closeConnection() {
        db.closeConnection();
    }
}