    package org.example.bookreccomender2;

    import java.io.*;
    import java.net.Socket;
    import java.util.ArrayList;
    import java.util.List;

    public class BookClient {
        private static final String SERVER_ADDRESS = "localhost";
        private static final int SERVER_PORT = 8080;

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Crea una nuova connessione al server
         */
        public BookClient() throws IOException {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        }

        /**
         * Richiede un numero specifico di libri con copertine
         */
        public List<Book> getBookCovers(int number) throws IOException {
            List<Book> books = new ArrayList<>();

            // Invia la richiesta
            String request = "GET_BOOK_COVERS:" + number;
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
                        // Formato: BOOK:titolo:autore:descrizione:coverUrl
                        String[] parts = line.split(":", 5);
                        if (parts.length >= 5) {
                            String title = parts[1];
                            String author = parts[2];
                            String description = parts[3];
                            String coverUrl = parts[4];

                            // Crea il libro
                            Book book = new Book(title, author, description);

                            // Imposta la copertina solo se è disponibile
                            if (coverUrl != null && !coverUrl.equals("null")) {
                                book.setCoverUrl(coverUrl);
                            }

                            books.add(book);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing dei dati del libro: " + e.getMessage());
                    }
                }
            }

            return books;
        }
        /**
         * Chiude la connessione al server
         */
        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                in.close();
                out.close();
                socket.close();
                System.out.println("Connessione al server chiusa");
            }
        }
    }