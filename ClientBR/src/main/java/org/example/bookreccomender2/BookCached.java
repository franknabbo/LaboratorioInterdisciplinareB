package org.example.bookreccomender2;

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