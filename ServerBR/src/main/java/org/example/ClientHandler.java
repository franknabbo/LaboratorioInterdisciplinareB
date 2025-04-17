package org.example;

import org.example.db.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final BookDAO bookDAO;
    private final UtenteDAO utenteDAO;
    private LibraryDAO libraryDAO;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.bookDAO = new BookDAO();
        this.utenteDAO = new UtenteDAO();
        this.libraryDAO = new LibraryDAO();
    }

    @Override
    public void run() {
        try {
            // Inizializzazione canali di comunicazione
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Messaggio di benvenuto
            out.println("BENVENUTO al servizio BookRecommender!");

            // Ciclo di gestione richieste
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processRequest(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Errore I/O con il client: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private String processRequest(String request) {
        if (request.startsWith("REGISTER:")) {
            return handleRegistration(request);
        } else if (request.startsWith("LOGIN:")) {
            return handleLogin(request);
        } else if (request.startsWith("GET_BOOKS")) {
            return handleGetBook(request);
        } else if (request.startsWith("SEARCH:")) {
            return handleSearch(request);
        } else if (request.startsWith("CREATE_LIBRARY:")) {
            return handleCreateLibrary(request);
        } else if(request.startsWith("GET_LIBRARY:")) {
            return handleGetLibrary(request);
        }else if(request.startsWith("ADD_BOOK_TO_LIBRARY")) {
            return handleAddBookToLibrary(request);
        }
        else if (request.startsWith("GET_LIBRARY_BOOKS:")) {
            return handleGetLibraryBooks(request);
        } else { return "ERRORE:Comando non riconosciuto";}
    }

    private String handleGetLibrary(String request) {
        // Parsa la richiesta per ottenere userId
        String[] parts = request.split(":", 2);
        if (parts.length < 2) {
            return "LIBRARY_RETRIEVAL_FAILED:Formato richiesta non valido";
        }
        String userId = parts[1];
        // Utilizza il LibraryDAO per ottenere la libreria
        List<Library> libraries = libraryDAO.getUserLibraries(userId);

        // Converti la lista di librerie in una stringa formattata
        StringBuilder response = new StringBuilder("INIZIO_LISTA_LIBRERIE\n");
        for (Library library : libraries) {
            response.append("LIBRARY:")
                    .append(library.getIdLibreria()).append("|||")
                    .append(library.getNome()).append("|||")
                    .append(library.getIdUtente()).append("\n");
        }
        response.append("END_LIBRARIES");
        return response.toString();
    }

    private String handleGetLibraryBooks(String request) {
        // Parsa la richiesta per ottenere userId e nome libreria
        String[] parts = request.split(":", 3);
        if (parts.length < 3) {
            return "LIBRARY_BOOKS_RETRIEVAL_FAILED:Formato richiesta non valido";
        }

        String userId = parts[1];
        String libraryName = parts[2];

        // Utilizza il LibraryDAO per ottenere i libri della libreria
        List<Book> books = libraryDAO.getLibraryBooks(userId, libraryName);

        // Converti la lista di libri in una stringa formattata
        StringBuilder response = new StringBuilder("INIZIO_LISTA_LIBRI_LIBRERIA\n");
        for (Book book : books) {
            response.append("BOOK:")
                    .append(book.getTitle()).append("|||")
                    .append(book.getAuthor()).append("|||")
                    .append(book.getCategory()).append("|||")
                    .append(book.getPublisher()).append("|||")
                    .append(book.getPublicationYear()).append("|||")
                    .append(book.getCoverUrl()).append("\n");
        }
        response.append("END_LIBRARY_BOOKS");
        return response.toString();
    }

    private String handleRegistration(String request) {
        try {
            // Formato: REGISTER:nome:cognome:codiceFiscale:email:passwordEncrypted
            String[] parts = request.split(":", 6);
            if (parts.length < 6) {
                return "REGISTRAZIONE FALLITA:Parametri insufficienti";
            }

            String nome = parts[1];
            String cognome = parts[2];
            String codiceFiscale = parts[3];
            String email = parts[4];
            String password = parts[5];

            Utente utente = new Utente(nome, cognome, codiceFiscale, email, password);
            return utenteDAO.registraUtente(utente);
        } catch (Exception e) {
            return "REGISTRAZIONE FALLITA:" + e.getMessage();
        }
    }

    private String handleLogin(String request) {
        try {
            // Formato: LOGIN:userId:passwordEncrypted
            String[] parts = request.split(":", 3);
            if (parts.length < 3) {
                return "LOGIN FAILED:Parametri insufficienti";
            }

            String userId = parts[1];
            String password = parts[2];

            return utenteDAO.loginUtente(userId, password);
        } catch (Exception e) {
            return "LOGIN FAILED:" + e.getMessage();
        }
    }

    private String handleGetBook(String request) {
        try {
            // Formato: GET_BOOKS_:number
            String[] parts = request.split(":");
            if (parts.length < 2) {
                return "ERRORE:Parametri insufficienti";
            }

            int number = Integer.parseInt(parts[1]);
            List<Book> books = bookDAO.getBooks(number);
            //stampa books
            System.out.println("Libri recuperati: " + books.size());
            for (Book book : books) {
                System.out.println("Titolo: " + book.getTitle());
            }
            return getString(books);
        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String handleSearch(String request) {
        try {
            // Formato: SEARCH:tipo:termine[:anno]
            String[] parts = request.split(":");
            if (parts.length < 3) {
                return "ERRORE:Parametri insufficienti";
            }

            String searchType = parts[1];
            String searchTerm = parts[2];
            Integer year = null;
            System.out.println("Tipo di ricerca: " + searchType);
            System.out.println("Termine di ricerca: " + searchTerm);


            if (searchType.equals("AUTHOR_YEAR") && parts.length >= 4) {
                try {
                    year = Integer.parseInt(parts[3]);
                } catch (NumberFormatException e) {
                    return "ERRORE:Anno non valido";
                }
            }

            List<Book> books = bookDAO.searchBooks(searchType, searchTerm, year);

            return getString(books);
        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String handleCreateLibrary(String request) {
        // Parsa la richiesta per ottenere userId e nome libreria
        String[] parts = request.split(":", 3);
        if (parts.length < 3) {
            return "LIBRARY_CREATION_FAILED:Formato richiesta non valido";
        }

        String userId = parts[1];
        String libraryName = parts[2];

        // Utilizza il LibraryDAO per creare la libreria
        return libraryDAO.createLibrary(userId, libraryName);
    }

    private String handleAddBookToLibrary(String request) {
        // Parsa la richiesta: ADD_BOOK_TO_LIBRARY:userId:libraryName:titolo|||autore|||...
        String[] parts = request.split(":", 4);
        if (parts.length < 4) {
            return "BOOK_ADD_FAILED:Formato richiesta non valido";
        }

        String userId = parts[1];
        String libraryName = parts[2];
        String bookData = parts[3];

        // Parsa i dati del libro
        String[] bookParts = bookData.split("\\|\\|\\|");
        if (bookParts.length < 6) {
            return "BOOK_ADD_FAILED:Dati libro non validi";
        }

        String title = bookParts[0];
        String author = bookParts[1];
        String category = bookParts[2];
        String publisher = bookParts[3];
        String year = bookParts[4];
        String coverUrl = bookParts[5];

        // Crea l'oggetto libro
        Book book = new Book(0, title, author, category, publisher, Integer.parseInt(year), coverUrl);

        // Aggiungi il libro alla libreria
        return libraryDAO.addBookToLibrary(userId, libraryName, book);
    }

    private void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();

            // Chiudi le connessioni ai DAO
            if (bookDAO != null) bookDAO.closeConnection();
            if (utenteDAO != null) utenteDAO.closeConnection();
            if (libraryDAO != null) libraryDAO.closeConnection();

            System.out.println("Connessione con il client chiusa");
        } catch (IOException e) {
            System.err.println("Errore nella chiusura delle risorse: " + e.getMessage());
        }
    }

    private String getString(List<Book> books) {
        StringBuilder response = new StringBuilder("INIZIO_LISTA_LIBRI\n");
        for (Book book : books) {
            response.append("BOOK:")
                    .append(book.getTitle()).append("|||")
                    .append(book.getAuthor()).append("|||")
                    .append(book.getCategory()).append("|||")
                    .append(book.getPublisher()).append("|||")
                    .append(book.getPublicationYear()).append("|||")
                    .append(book.getCoverUrl()).append("\n");
        }
        response.append("END_BOOKS");
        return response.toString();
    }

}