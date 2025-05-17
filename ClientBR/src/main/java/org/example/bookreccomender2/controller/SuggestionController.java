// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookreccomender2.controller;

import org.example.bookreccomender2.Book;
import org.example.bookreccomender2.BookClient;
import org.example.bookreccomender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;


public class SuggestionController {

    public SuggestionController() {
    }

    public boolean addSuggestedBook(String userId, int idLibroReferenced, List<Book> idLibroSuggested) {
        //la stringa è composta da: ADD_SUGGESTED_BOOK:userId:idLibroReferenced:idLibroSuggested:idLibroSuggested2:idLibroSuggested3:
        StringBuilder message = new StringBuilder("ADD_SUGGESTED_BOOK:" + userId + ":" + idLibroReferenced);
        for (Book book : idLibroSuggested) {
            message.append(":").append(book.getId());
        }
        //invia la stringa al server
        try {
            // Invia la richiesta di aggiunta del libro suggerito
            SocketConnection.sendMessage(String.valueOf(message));
            BufferedReader in = SocketConnection.getIn();
            // Leggi la risposta del server
            String response = in.readLine();
            // Gestisci la risposta
            if (response.startsWith("SUGGESTION_SUCCESS")) {
                return true;
            } else {
                return false;
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> getSuggestedBooks(int idLibroReferenced) {
        //la stringa è composta da: GET_SUGGESTED_BOOK:idLibroReferenced
        String message = "GET_SUGGESTED_BOOKS:" + idLibroReferenced;
        List<Book> books = new java.util.ArrayList<>();
        //invia la stringa al server
        try {
            // Invia la richiesta di aggiunta del libro suggerito
            SocketConnection.sendMessage(message);
            BufferedReader in = SocketConnection.getIn();
            // Leggi la risposta del server
            String response = in.readLine();
            // Gestisci la risposta
            if (response.startsWith("INIZIO_LISTA_LIBRI")) {
                // Leggi i libri suggeriti
                String line;
                boolean reading = false;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("BOOK")) {
                        reading = true;
                    }

                    if (line.equals("END_BOOKS")) {
                        break;
                    }

                    if (reading && line.startsWith("BOOK:")) {
                        try {
                            // Formato corretto da server: BOOK:id|||titolo|||autore|||categoria|||editore|||anno_pubblicazione|||copertina
                            String[] parts = line.split("BOOK:|\\|\\|\\|");
                            if (parts.length >= 8) {
                                Book book = BookClient.getBook(parts);
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
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return books;
    }
}