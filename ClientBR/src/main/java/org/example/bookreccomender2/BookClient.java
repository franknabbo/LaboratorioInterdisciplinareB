    package org.example.bookreccomender2;

    import java.io.*;
    import java.net.Socket;
    import java.util.ArrayList;
    import java.util.List;

    public class BookClient implements AutoCloseable {
        private static final String SERVER_ADDRESS = "localhost";
        private static final int SERVER_PORT = 8080;

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private SocketConnection socketConnection;

        /**
         * Crea una nuova connessione al server
         */

        public BookClient(){

        }

        /**
         * Richiede un numero specifico di libri con copertine
         */
        public List<Book> getBooks(int number) throws IOException {
            List<Book> books = new ArrayList<>();

            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Leggi il messaggio di benvenuto
                String welcome = in.readLine();

                // Invia la richiesta dei libri
                out.println("GET_BOOKS:" + number);

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
            }

            return books;
        }

        private static Book getBook(String[] parts) {
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

            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Invia la richiesta
                String request = "GET_LIBRARY_BOOKS:" + userId + ":" + libraryName;

                // Leggi il messaggio di benvenuto
                String welcome = in.readLine();

                out.println(request);
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
        }

        public List<Book> performSearch(String searchType, String searchTerm, String year) throws IOException {
            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Leggi il messaggio di benvenuto
                String welcome = in.readLine();

                // Invia la richiesta di ricerca
                String request = "SEARCH:" + searchType + ":" + searchTerm;
                if (year != null && searchType.equals("AUTHOR_YEAR")) {
                    request += ":" + year;
                }
                out.println(request);

                // Parsa i risultati
                return parseSearchResults(in);
            }
        }

        private List<Book> parseSearchResults(BufferedReader in) throws IOException {
            List<Book> results = new ArrayList<>();
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
                            results.add(book);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing: " + e.getMessage());
                    }
                }
            }

            return results;
        }

        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                in.close();
                out.close();
                socket.close();
                System.out.println("Connessione al server chiusa");
            }
        }
    }