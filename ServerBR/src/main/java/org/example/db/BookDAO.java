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


public class BookDAO {
    private final DataBaseConnection db;

    public BookDAO() {
        this.db = new DataBaseConnection();
    }

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
     * Metodo unificato per la ricerca di libri secondo vari criteri
     *
     * @param searchType il tipo di ricerca ("TITLE", "AUTHOR", "AUTHOR_YEAR")
     * @param searchTerm il termine di ricerca (titolo o autore)
     * @param year       l'anno di pubblicazione (usato solo per "AUTHOR_YEAR")
     * @return lista di libri che corrispondono ai criteri di ricerca
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

    // Metodo per ottenere i dettagli di un libro specifico
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


    public void closeConnection() {
        db.closeConnection();
    }
}