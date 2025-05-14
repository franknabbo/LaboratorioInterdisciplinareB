package org.example.bookreccomender2.controller;

import org.example.bookreccomender2.Book;
import org.example.bookreccomender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class LibraryController {
    AlertController alertController = new AlertController();


    public LibraryController() {
    }

    public boolean createLibraryWithName(String libraryName) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {


            // Invia richiesta di creazione libreria
            out.println("CREATE_LIBRARY:" + UserManager.getUserId() + ":" + libraryName);

            // Gestisci la risposta
            String response = in.readLine();

            if (response.startsWith("LIBRARY_CREATED")) {
                alertController.showAlertSucces("Libreria creata", "La libreria '" + libraryName + "' è stata creata con successo.");
                return true;


            } else if (response.startsWith("LIBRARY_EXISTS")) {
                alertController.showAlert("Libreria già esistente", "La libreria '" + libraryName + "' esiste già.");
                return false;
            } else {
                String errorMessage = "Errore nella creazione della libreria.";
                if (response.contains(":")) {
                    errorMessage = response.split(":", 2)[1];
                }
                alertController.showAlert("Errore", errorMessage);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
            return false;
        }
    }

    public void addBookToSelectedLibrary(String libraryName, Book selectedBook) {
        if (selectedBook == null) {
            alertController.showAlert("Errore", "Nessun libro selezionato");
            return;
        }

        try {
            SocketConnection.sendMessage("ADD_BOOK_TO_LIBRARY:" + UserManager.getUserId() + ":" + libraryName + ":" + selectedBook.getId());

            BufferedReader in = SocketConnection.getIn();

            // Gestisci la risposta
            String response = in.readLine();

            if (response.startsWith("BOOK_ADDED")) {
                alertController.showAlertSucces("Libro aggiunto", "Il libro è stato aggiunto alla libreria '" + libraryName + "' con successo.");
            } else if (response.startsWith("BOOK_EXISTS")) {
                alertController.showAlert("Libro già presente", "Il libro è già presente nella libreria '" + libraryName + "'.");
            } else {
                String errorMessage = "Errore nell'aggiunta del libro.";
                if (response.contains(":")) {
                    errorMessage = response.split(":", 2)[1];
                }
                alertController.showAlert("Errore", errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
        }
    }

    public List<String> getLibraryList() {
        List<String> libraries = new ArrayList<>();
        try {
            // Invia richiesta di librerie
            SocketConnection.sendMessage("GET_LIBRARY:" + UserManager.getUserId());
            BufferedReader in = SocketConnection.getIn();
            // Leggi la risposta
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END_LIBRARIES")) {
                    break;
                }
                libraries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return libraries;
    }

}
