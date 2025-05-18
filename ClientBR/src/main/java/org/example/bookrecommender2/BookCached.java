// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookrecommender2;

import java.util.ArrayList;
import java.util.List;


public class BookCached {
    private static BookCached instance;
    private List<Book> cachedHomeBooks;

    private BookCached() {
        cachedHomeBooks = new ArrayList<>();
    }

    public static synchronized BookCached getInstance() {
        if (instance == null) {
            instance = new BookCached();
        }
        return instance;
    }

    public List<Book> getCachedHomeBooks() {
        return cachedHomeBooks;
    }

    public void setCachedHomeBooks(List<Book> books) {
        this.cachedHomeBooks = books;
    }

    public boolean hasCachedHomeBooks() {
        return !cachedHomeBooks.isEmpty();
    }

    public void clearCache() {
        cachedHomeBooks.clear();
    }
}