// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookreccomender2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BookClient {

    /**
     * Richiede un numero specifico di libri con copertine
     */
    public List<Book> getBooks(int number) throws IOException {
        List<Book> books = new ArrayList<>();

        // Invia la richiesta dei libri
        SocketConnection.sendMessage("GET_BOOKS:" + number);
        BufferedReader in = SocketConnection.getIn();
        // Legge la risposta
        String line;
        boolean reading = false;

        while ((line = in.readLine()) != null) {
            if (line.equals("INIZIO_LISTA_LIBRI")) {
                reading = true;
                continue;
            }

            if (line.equals("END_BOOKS")) {
                break;
            }

            if (reading && line.startsWith("BOOK:")) {
                try {
                    // Formato corretto da server: BOOK:id|||titolo|||autore|||categoria|||editore|||anno_pubblicazione|||copertina
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 8) {
                        Book book = getBook(parts);
                        books.add(book);
                    } else {
                        System.err.println("Formato libro non valido: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing dei dati del libro: " + e.getMessage());
                }
            }
        }

        return books;
    }

    public static Book getBook(String[] parts) {
        int id = Integer.parseInt(parts[1]);
        String title = parts[2];
        String author = parts[3];
        String category = parts[4];
        String publisher = parts[5];
        String publicationYear = parts[6];

        // La parte coverUrl è opzionale, verifica se esiste
        String coverUrl = "null";
        if (parts.length > 7) {
            coverUrl = parts[7];
        }

        // Crea il libro con il costruttore che accetta 7 parametri
        return new Book(id, title, author, category, publisher, publicationYear, coverUrl);
    }

    public List<Book> getLibraryBooks(String userId, String libraryName) throws IOException {
        List<Book> books = new ArrayList<>();

        // Invia la richiesta
        String request = "GET_LIBRARY_BOOKS:" + userId + ":" + libraryName;

        SocketConnection.sendMessage(request);
        BufferedReader in = SocketConnection.getIn();
        // Legge la risposta
        String line;
        boolean reading = false;

        while ((line = in.readLine()) != null) {
            if (line.equals("INIZIO_LISTA_LIBRI")) {
                reading = true;
                continue;
            }

            if (line.equals("END_BOOKS")) {
                break;
            }

            if (reading && line.startsWith("BOOK:")) {
                try {
                    // Formato corretto da server: BOOK:titolo|||autore|||categoria|||editore|||anno_pubblicazione|||copertina
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 8) {
                        int id = Integer.parseInt(parts[1]);
                        String title = parts[2];
                        String author = parts[3];
                        String category = parts[4];
                        String publisher = parts[5];
                        String publicationYear = parts[6];
                        String coverUrl = parts[7];

                        Book book = new Book(id, title, author, category, publisher, publicationYear, coverUrl);
                        books.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing: " + e.getMessage());
                }
            }
        }
        return books;

    }

    public List<Book> performSearch(String searchType, String searchTerm, String year) throws IOException {
        // Leggi il messaggio di benvenuto
        // Invia la richiesta di ricerca
        String request = "SEARCH:" + searchType + ":" + searchTerm;
        if (year != null && searchType.equals("AUTHOR_YEAR")) {
            request += ":" + year;
        }
        SocketConnection.sendMessage(request);
        return parseSearchResults();
    }

    private List<Book> parseSearchResults() throws IOException {
        List<Book> results = new ArrayList<>();
        String line;
        boolean reading = false;

        BufferedReader in = SocketConnection.getIn();

        while ((line = in.readLine()) != null) {
            if (line.equals("INIZIO_LISTA_LIBRI")) {
                reading = true;
                continue;
            }

            if (line.equals("END_BOOKS")) {
                break;
            }

            if (reading && line.startsWith("BOOK:")) {
                try {
                    // Formato corretto da server: BOOK:titolo|||autore|||categoria|||editore|||anno_pubblicazione|||copertina
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 8) {
                        int id = Integer.parseInt(parts[1]);
                        String title = parts[2];
                        String author = parts[3];
                        String category = parts[4];
                        String publisher = parts[5];
                        String publicationYear = parts[6];
                        String coverUrl = parts[7];

                        Book book = new Book(id, title, author, category, publisher, publicationYear, coverUrl);
                        results.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing: " + e.getMessage());
                }
            }
        }

        return results;
    }
}