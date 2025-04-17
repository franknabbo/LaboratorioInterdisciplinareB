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
        public List<Book> getBooks(int number) throws IOException {
            List<Book> books = new ArrayList<>();

            // Invia la richiesta
            String request = "GET_BOOKS:" + number;
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
                        if (parts.length >= 7) {
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
        private static Book getBook(String[] parts) {
            String title = parts[1];
            String author = parts[2];
            String category = parts[3];
            String publisher = parts[4];
            String publicationYear = parts[5];

            // La parte coverUrl è opzionale, verifica se esiste
            String coverUrl = "null";
            if (parts.length > 6) {
                coverUrl = parts[6];
            }

            // Crea il libro con il nuovo costruttore
            Book book = new Book(title, author, category, publisher, publicationYear, coverUrl);
            return book;
        }

        public List<Book> getLibraryBooks(String userId, String libraryName) throws IOException {
            List<Book> books = new ArrayList<>();

            // Invia la richiesta
            String request = "GET_LIBRARY_BOOKS:" + userId + ":" + libraryName;
            out.println(request);
            System.out.println("Richiesta inviata: " + request);

            // Legge la risposta
            String line;
            boolean reading = false;

            while ((line = in.readLine()) != null) {
                if (line.equals("INIZIO_LISTA_LIBRI_LIBRERIA")) {
                    reading = true;
                    continue;
                }

                if (line.equals("END_LIBRARY_BOOKS")) {
                    break;
                }

                if (reading && line.startsWith("BOOK:")) {
                    try {
                        String[] parts = line.split("BOOK:|\\|\\|\\|");
                        if (parts.length >= 7) {
                            Book book = getBook(parts);
                            books.add(book);
                            System.out.println("Libro aggiunto: " + book.getTitle());
                        } else {
                            System.err.println("Formato libro non valido: " + line);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing: " + e.getMessage());
                    }
                }
            }

            System.out.println("Libri ricevuti dalla libreria: " + books.size());
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