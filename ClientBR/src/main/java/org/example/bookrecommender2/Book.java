// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookrecommender2;

/**
 * Rappresenta un libro con i suoi dettagli come titolo, autore, categoria, editore, anno di pubblicazione e URL della copertina.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private String publicationYear;
    private String coverUrl; // Nuovo campo per la copertina

    /**
     * Costruttore di un libro.
     *
     * @param id identificativo univoco del libro
     * @param title titolo del libro
     * @param author autore del libro
     * @param category categoria o genere del libro
     * @param publisher casa editrice
     * @param publicationYear anno di pubblicazione
     * @param coverUrl URL dell'immagine di copertina
     */
    public Book(int id, String title, String author, String category, String publisher, String publicationYear, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.coverUrl = coverUrl;
    }

    // Getters

    /**
     * Restituisce l'id del libro.
     * @return id del libro
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il titolo del libro.
     * @return titolo del libro
     */
    public String getTitle() {
        return title;
    }

    /**
     * Restituisce l'autore del libro.
     * @return autore del libro
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Restituisce la categoria del libro.
     * @return categoria del libro
     */
    public String getCategory() {
        return category;
    }

    /**
     * Restituisce l'editore del libro.
     * @return casa editrice
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Restituisce l'anno di pubblicazione del libro.
     * @return anno di pubblicazione
     */
    public String getPublicationYear() {
        return publicationYear;
    }

    /**
     * Restituisce l'URL della copertina del libro.
     * @return URL immagine copertina
     */
    public String getCoverUrl() {
        return coverUrl;
    }

    // Setters

    /**
     * Imposta l'id del libro.
     * @param id nuovo id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Imposta il titolo del libro.
     * @param title nuovo titolo
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
