package org.example.bookreccomender2;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private String publicationYear;
    private String coverUrl; // Nuovo campo per la copertina

    public Book(String title, String author, String category, String publisher, String publicationYear, String coverUrl) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.coverUrl = coverUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getPublisher() { return publisher; }
    public String getPublicationYear() { return publicationYear; }
    public String getCoverUrl() { return coverUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }

}