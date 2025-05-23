// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Data Access Object (DAO) per la gestione dei libri nel database.
 * Permette di recuperare, cercare e ottenere dettagli sui libri.
 */

public class BookDAO {
    private final DataBaseConnection db;

    /**
     * Costruttore: inizializza la connessione al database.
     */
    public BookDAO() {
        this.db = new DataBaseConnection();
    }

    /**
     * Recupera una lista di libri dal database, con un limite opzionale.
     *
     * @param limit Numero massimo di libri da recuperare. Se 0, recupera tutti i libri.
     * @return Lista di oggetti Book recuperati dal database.
     */

    public List<Book> getBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql;
        if (limit == 0) {
            sql = "SELECT * FROM Libri ORDER BY titolo";
            try {
                PreparedStatement stmt = db.getConnection().prepareStatement(sql);
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

        } else {
            sql = "SELECT * FROM Libri ORDER BY titolo LIMIT ?";
            try {
                PreparedStatement stmt = db.getConnection().prepareStatement(sql);
                stmt.setInt(1, limit);
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

        }
        return books;
    }

    /**
     * Cerca libri nel database secondo diversi criteri e contesto di pagina.
     *
     * @param searchType Tipo di ricerca (TITLE, AUTHOR, AUTHOR_YEAR).
     * @param searchTerm Termine di ricerca (titolo o autore).
     * @param year       Anno di pubblicazione (opzionale, usato solo con AUTHOR_YEAR).
     * @param userId     ID utente per ricerche filtrate su librerie di un utente.
     * @param currentPage Identifica la pagina corrente per differenziare la query.
     * @param libraryName Nome della libreria (usato per ricerca specifica in libreria).
     * @return Lista di libri che soddisfano i criteri di ricerca.
     */

    public List<Book> searchBooks(String searchType, String searchTerm, Integer year, String userId, String currentPage, String libraryName) {
        List<Book> books = new ArrayList<>();
        String sql;

        try {
            PreparedStatement stmt = null;

            if (currentPage.contains("home")) {
                switch (searchType) {
                    case "TITLE":
                        sql = "SELECT * FROM Libri WHERE LOWER(titolo) LIKE LOWER(?) ORDER BY titolo";
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, "%" + searchTerm + "%");
                        break;

                    case "AUTHOR":
                        sql = "SELECT * FROM Libri WHERE LOWER(autore) LIKE LOWER(?) ORDER BY titolo";
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, "%" + searchTerm + "%");
                        break;

                    case "AUTHOR_YEAR":
                        sql = "SELECT * FROM Libri WHERE LOWER(autore) LIKE LOWER(?) AND anno_pubblicazione = ? ORDER BY titolo";
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, "%" + searchTerm + "%");
                        stmt.setInt(2, year);
                        break;

                    default:
                        System.err.println("Tipo di ricerca non valido: " + searchType);
                        return books;
                }
            } else if (currentPage.contains("suggested")) {
                switch (searchType) {
                    case "TITLE":
                        sql = """
                                    SELECT * 
                                    FROM librerie lb
                                    JOIN librerie_libri ll ON lb.id_libreria = ll.id_libreria
                                    JOIN libri l ON ll.id_libro = l.id_libro
                                    WHERE lb.user_id = ? AND LOWER(l.titolo) LIKE LOWER(?)
                                    ORDER BY l.titolo
                                """;
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, userId);
                        stmt.setString(2, "%" + searchTerm + "%");
                        break;

                    case "AUTHOR":
                        sql = """
                                    SELECT * 
                                    FROM librerie lb
                                    JOIN librerie_libri ll ON lb.id_libreria = ll.id_libreria
                                    JOIN libri l ON ll.id_libro = l.id_libro
                                    WHERE lb.user_id = ? AND LOWER(l.autore) LIKE LOWER(?)
                                    ORDER BY l.titolo
                                """;
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, userId);
                        stmt.setString(2, "%" + searchTerm + "%");
                        break;

                    case "AUTHOR_YEAR":
                        sql = """
                                    SELECT * 
                                    FROM librerie lb
                                    JOIN librerie_libri ll ON lb.id_libreria = ll.id_libreria
                                    JOIN libri l ON ll.id_libro = l.id_libro
                                    WHERE lb.user_id = ? AND LOWER(l.autore) LIKE LOWER(?) AND l.anno_pubblicazione = ?
                                    ORDER BY l.titolo
                                """;
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, userId);
                        stmt.setString(2, "%" + searchTerm + "%");
                        stmt.setInt(3, year);
                        break;

                    default:
                        System.err.println("Tipo di ricerca non valido: " + searchType);
                        return books;
                }
            } else if (currentPage.contains("library-books-view")) {
                switch (searchType) {
                    case "TITLE":
                        sql = """
                                    SELECT l.*
                                    FROM librerie lb
                                    JOIN librerie_libri ll ON lb.id_libreria = ll.id_libreria
                                    JOIN libri l ON ll.id_libro = l.id_libro
                                    WHERE lb.user_id = ? AND LOWER(lb.nome) = LOWER(?) AND LOWER(l.titolo) LIKE LOWER(?)
                                    ORDER BY l.titolo
                                """;
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, userId);
                        stmt.setString(2, libraryName);
                        stmt.setString(3, "%" + searchTerm + "%");
                        break;

                    case "AUTHOR":
                        sql = """
                                    SELECT l.*
                                    FROM librerie lb
                                    JOIN librerie_libri ll ON lb.id_libreria = ll.id_libreria
                                    JOIN libri l ON ll.id_libro = l.id_libro
                                    WHERE lb.user_id = ? AND LOWER(lb.nome) = LOWER(?) AND LOWER(l.autore) LIKE LOWER(?)
                                    ORDER BY l.titolo
                                """;
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, userId);
                        stmt.setString(2, libraryName);
                        stmt.setString(3, "%" + searchTerm + "%");
                        break;

                    case "AUTHOR_YEAR":
                        sql = """
                                    SELECT l.*
                                    FROM librerie lb
                                    JOIN librerie_libri ll ON lb.id_libreria = ll.id_libreria
                                    JOIN libri l ON ll.id_libro = l.id_libro
                                    WHERE lb.user_id = ? AND LOWER(lb.nome) = LOWER(?) AND LOWER(l.autore) LIKE LOWER(?) AND l.anno_pubblicazione = ?
                                    ORDER BY l.titolo
                                """;
                        stmt = db.getConnection().prepareStatement(sql);
                        stmt.setString(1, userId);
                        stmt.setString(2, libraryName);
                        stmt.setString(3, "%" + searchTerm + "%");
                        stmt.setInt(4, year);
                        break;

                    default:
                        System.err.println("Tipo di ricerca non valido: " + searchType);
                        return books;
                }

            }

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
            System.err.println("Errore nella ricerca: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Recupera i dettagli di un libro specifico dato il suo ID.
     *
     * @param bookId ID del libro da cercare.
     * @return Oggetto Book con i dettagli del libro, o null se non trovato.
     */

    public Book getBookDetails(int bookId) {
        Book book = null;
        String sql = "SELECT * FROM Libri WHERE id_libro = ?";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                book = new Book(
                        rs.getInt("id_libro"),
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getString("categoria"),
                        rs.getString("editore"),
                        rs.getInt("anno_pubblicazione"),
                        rs.getString("copertina")
                );
            }
        } catch (SQLException _) {
        }
        return book;
    }

    /**
     * Chiude la connessione al database.
     */

    public void closeConnection() {
        db.closeConnection();
    }
}