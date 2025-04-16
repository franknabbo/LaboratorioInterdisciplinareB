package org.example.db;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private int publicationYear;
    private String coverUrl; // Nuovo campo per la copertina

    public Book(int id, String title, String author, String category, String publisher, int publicationYear, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.coverUrl = coverUrl;
    }

    // Costruttore senza copertina per retrocompatibilità
    public Book(int id, String title, String author, String category, String publisher, int publicationYear) {
        this(id, title, author, category, publisher, publicationYear, null);
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getPublisher() { return publisher; }
    public int getPublicationYear() { return publicationYear; }
    public String getCoverUrl() { return coverUrl; }

    // Per la richiesta del client, creiamo una descrizione formattata
    public String getDescription() {
        return "Categoria: " + category +
                ", Editore: " + publisher +
                ", Anno: " + publicationYear;
    }
}