package org.example.bookreccomender2.controller;

import org.example.bookreccomender2.Book;
import org.example.bookreccomender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    }
