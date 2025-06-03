// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package bookrecommender.db;

/**
 * Rappresenta un libro con informazioni di base quali titolo, autore, categoria, editore, anno di pubblicazione e copertina.
 */
public class Book {
    /** Identificativo univoco del libro. */
    private int id;

    /** Titolo del libro. */
    private String title;

    /** Autore del libro. */
    private String author;

    /** Categoria o genere del libro. */
    private String category;

    /** Editore del libro. */
    private String publisher;

    /** Anno di pubblicazione del libro. */
    private int publicationYear;

    /** URL della copertina del libro. */
    private String coverUrl;

    /**
     * Costruisce un oggetto Book con tutte le informazioni specificate.
     *
     * @param id identificativo univoco del libro
     * @param title titolo del libro
     * @param author autore del libro
     * @param category categoria o genere del libro
     * @param publisher editore del libro
     * @param publicationYear anno di pubblicazione del libro
     * @param coverUrl URL della copertina del libro
     */
    public Book(int id, String title, String author, String category, String publisher, int publicationYear, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.coverUrl = coverUrl;
    }

    /**
     * Restituisce l'identificativo univoco del libro.
     *
     * @return id del libro
     */
    public int getId() { return id; }

    /**
     * Restituisce il titolo del libro.
     *
     * @return titolo del libro
     */
    public String getTitle() { return title; }

    /**
     * Restituisce l'autore del libro.
     *
     * @return autore del libro
     */
    public String getAuthor() { return author; }

    /**
     * Restituisce la categoria o genere del libro.
     *
     * @return categoria del libro
     */
    public String getCategory() { return category; }

    /**
     * Restituisce l'editore del libro.
     *
     * @return editore del libro
     */
    public String getPublisher() { return publisher; }

    /**
     * Restituisce l'anno di pubblicazione del libro.
     *
     * @return anno di pubblicazione
     */
    public int getPublicationYear() { return publicationYear; }

    /**
     * Restituisce l'URL della copertina del libro.
     *
     * @return URL copertina libro
     */
    public String getCoverUrl() { return coverUrl; }

}
