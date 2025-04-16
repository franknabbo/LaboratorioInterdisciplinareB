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

//    public String getBookCover(String title, String author) {
//
//        try {
//            // Prima controlla se esiste nel database
//            String sql = "SELECT copertina FROM Libri WHERE LOWER(titolo) = LOWER(?) AND LOWER(autore) = LOWER(?)";
//            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
//            stmt.setString(1, title);
//            stmt.setString(2, author);
//            ResultSet rs = stmt.executeQuery();
//
//            // Se trovata nel database, usa quella
//            if (rs.next()) {
//                String coverUrl = rs.getString("copertina");
//                if (coverUrl != null && !coverUrl.isEmpty()) {
//                    return coverUrl;
//                }
//            }
//
//
//            // Rimuovi "By " all'inizio del nome dell'autore, se presente
//            if (author != null && author.startsWith("By ")) {
//                author = author.substring(3);
//            }
//
//            // Codifica i parametri per l'URL
//            String encodedTitle = URLEncoder.encode(title, "UTF-8");
//            String encodedAuthor = URLEncoder.encode(author, "UTF-8");
//
//            // API di ricerca di OpenLibrary
//            String searchUrl = "https://openlibrary.org/search.json?title=" +
//                    encodedTitle + "&author=" + encodedAuthor;
//
//            // Effettua la richiesta di ricerca
//            URL url = new URL(searchUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//
//            int responseCode = connection.getResponseCode();
//
//            if (responseCode == 200) {
//                // Leggi la risposta JSON
//                StringBuilder response = new StringBuilder();
//                try (BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(connection.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                }
//
//                // Analizza il JSON
//                JSONObject jsonResponse = new JSONObject(response.toString());
//                JSONArray docs = jsonResponse.getJSONArray("docs");
//
//                // Verifica se sono stati trovati risultati
//                if (docs.length() > 0) {
//                    JSONObject firstBook = docs.getJSONObject(0);
//
//                    // Controlla se il libro ha un cover_i (ID copertina)
//                    if (firstBook.has("cover_i")) {
//                        int coverId = firstBook.getInt("cover_i");
//                        String coverUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
//
//                        // Opzionale: aggiorna il database con l'URL della copertina trovata
//                        updateCoverUrl(title, author, coverUrl);
//
//                        return coverUrl;
//                    }
//                    // Controlla se ha ISBN
//                    else if (firstBook.has("isbn")) {
//                        String isbn = firstBook.getJSONArray("isbn").getString(0);
//                        String coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
//
//                        // Opzionale: aggiorna il database con l'URL della copertina trovata
//                        updateCoverUrl(title, author, coverUrl);
//
//                        return coverUrl;
//                    }
//                }
//            }
//
//            connection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    /**
     * Aggiorna l'URL della copertina nel database per un libro specifico
     */
    private void updateCoverUrl(String title, String author, String coverUrl) {
        try {
            String sql = "UPDATE Libri SET copertina = ? WHERE LOWER(titolo) = LOWER(?) AND LOWER(autore) = LOWER(?)";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, coverUrl);
            stmt.setString(2, title);
            stmt.setString(3, author);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
            }
        } catch (SQLException e) {
        }
    }

    /**
     * Metodo unificato per la ricerca di libri secondo vari criteri
     *
     * @param searchType il tipo di ricerca ("TITLE", "AUTHOR", "AUTHOR_YEAR")
     * @param searchTerm il termine di ricerca (titolo o autore)
     * @param year       l'anno di pubblicazione (usato solo per "AUTHOR_YEAR")
     * @return lista di libri che corrispondono ai criteri di ricerca
     */
    public List<Book> searchBooks(String searchType, String searchTerm, Integer year) {
        List<Book> books = new ArrayList<>();
        String sql;

        try {
            PreparedStatement stmt;

            switch (searchType) {
                case "TITLE":
                    sql = "SELECT * FROM Libri WHERE LOWER(titolo) LIKE LOWER(?) ORDER BY id_libro";
                    stmt = db.getConnection().prepareStatement(sql);
                    stmt.setString(1, "%" + searchTerm + "%");
                    break;

                case "AUTHOR":
                    sql = "SELECT * FROM Libri WHERE LOWER(autore) LIKE LOWER(?) ORDER BY id_libro";
                    stmt = db.getConnection().prepareStatement(sql);
                    stmt.setString(1, "%" + searchTerm + "%");
                    break;

                case "AUTHOR_YEAR":
                    sql = "SELECT * FROM Libri WHERE LOWER(autore) LIKE LOWER(?) AND anno_pubblicazione = ? ORDER BY id_libro";
                    stmt = db.getConnection().prepareStatement(sql);
                    stmt.setString(1, "%" + searchTerm + "%");
                    stmt.setInt(2, year);
                    break;

                default:
                    System.err.println("Tipo di ricerca non valido: " + searchType);
                    return books;
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

    public void closeConnection() {
        db.closeConnection();
    }
}