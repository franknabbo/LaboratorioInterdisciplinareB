// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookrecommender2;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton che gestisce una cache locale dei libri mostrati nella home page.
 */
public class BookCached {
    private static BookCached instance;
    private List<Book> cachedHomeBooks;

    /**
     * Costruttore privato per singleton.
     */
    private BookCached() {
        cachedHomeBooks = new ArrayList<>();
    }

    /**
     * Restituisce l'istanza singleton di BookCached.
     * @return istanza singleton di BookCached
     */
    public static synchronized BookCached getInstance() {
        if (instance == null) {
            instance = new BookCached();
        }
        return instance;
    }

    /**
     * Restituisce la lista di libri cachetati per la home.
     * @return lista di libri cachetati
     */
    public List<Book> getCachedHomeBooks() {
        return cachedHomeBooks;
    }

    /**
     * Imposta la lista di libri cachetati per la home.
     * @param books lista di libri da cachetare
     */
    public void setCachedHomeBooks(List<Book> books) {
        this.cachedHomeBooks = books;
    }

    /**
     * Verifica se la cache contiene libri.
     * @return true se la cache contiene libri, false altrimenti
     */
    public boolean hasCachedHomeBooks() {
        return !cachedHomeBooks.isEmpty();
    }

    /**
     * Svuota la cache dei libri della home.
     */
    public void clearCache() {
        cachedHomeBooks.clear();
    }
}
