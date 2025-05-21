// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2.controller;

import org.example.bookrecommender2.Book;
import org.example.bookrecommender2.BookClient;
import org.example.bookrecommender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;


public class SuggestionController {

    public SuggestionController() {
    }

    public boolean addSuggestedBook(String userId, int idLibroReferenced, List<Integer> idLibroSuggested) {
        //la stringa è composta da: ADD_SUGGESTED_BOOK:userId:idLibroReferenced:idLibroSuggested:idLibroSuggested2:idLibroSuggested3:
        StringBuilder message = new StringBuilder("ADD_SUGGESTED_BOOK:" + userId + ":" + idLibroReferenced);
        //aggiungo i libri suggeriti
        for (int i = 0; i < idLibroSuggested.size(); i++) {
            message.append(":").append(idLibroSuggested.get(i));
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
            // Gestisci la risposta
            //INIZIO_LISTA_LIBRI
            //BOOK:882|||100 Greatest Pitchers|||By Kelley, Brent||||||Crescent|||1988|||https://covers.openlibrary.org/b/id/9519394-L.jpg
            //BOOK:2906|||1000 Reasons You Are the Perfect Mom (1000 Hints, Tips and Ideas)|||By Powell, Michael||| Family & Relationships , Parenting , Motherhood|||M Q Publications|||2005|||https://covers.openlibrary.org/b/id/2015189-L.jpg
            //BOOK:4662|||100 Years 100 Stories|||By Burns, George||| Biography & Autobiography , Entertainment & Performing Arts|||Putnam Adult|||1996|||https://covers.openlibrary.org/b/id/258454-L.jpg
            //END_BOOKS

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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return books;
    }
}