// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender.db;

/**
 * Rappresenta una valutazione (rating) di un libro da parte di un utente.
 * Contiene punteggi su vari aspetti e un commento opzionale.
 */
public class Rating {
    private String idUtente;
    private int idLibro;
    private int stile;
    private int contenuto;
    private int gradevolezza;
    private int originalita;
    private int edizione;
    private int votoFinale;
    private String recensione;

    /**
     * Costruttore completo della classe Rating.
     *
     * @param idUtente      ID dell'utente che ha fatto la valutazione
     * @param idLibro       ID del libro valutato
     * @param stile         Punteggio per lo stile (1-5)
     * @param contenuto     Punteggio per il contenuto (1-5)
     * @param gradevolezza  Punteggio per la gradevolezza (1-5)
     * @param originalita   Punteggio per l'originalità (1-5)
     * @param edizione      Punteggio per l'edizione (1-5)
     * @param votoFinale    Voto finale complessivo
     * @param recensione    Recensione testuale (max 256 caratteri)
     */
    public Rating(String idUtente, int idLibro, int stile, int contenuto, int gradevolezza,
                  int originalita, int edizione, int votoFinale, String recensione ) {
        this.idUtente = idUtente;
        this.idLibro = idLibro;
        setStile(stile);
        setContenuto(contenuto);
        setGradevolezza(gradevolezza);
        setOriginalita(originalita);
        setEdizione(edizione);
        setRecensione(recensione);
        setVotoFinale(votoFinale);
    }

    /**
     * Costruttore vuoto.
     */
    public Rating() {}

    /**
     * Imposta il punteggio dello stile.
     *
     * @param stile valore da 1 a 5
     */
    public void setStile(int stile) {
        validaVoto(stile);
        this.stile = stile;
    }

    /**
     * Imposta il punteggio del contenuto.
     *
     * @param contenuto valore da 1 a 5
     */
    public void setContenuto(int contenuto) {
        validaVoto(contenuto);
        this.contenuto = contenuto;
    }

    /**
     * Imposta il punteggio della gradevolezza.
     *
     * @param gradevolezza valore da 1 a 5
     */
    public void setGradevolezza(int gradevolezza) {
        validaVoto(gradevolezza);
        this.gradevolezza = gradevolezza;
    }

    /**
     * Imposta il punteggio dell'originalità.
     *
     * @param originalita valore da 1 a 5
     */
    public void setOriginalita(int originalita) {
        validaVoto(originalita);
        this.originalita = originalita;
    }

    /**
     * Imposta il punteggio dell'edizione.
     *
     * @param edizione valore da 1 a 5
     */
    public void setEdizione(int edizione) {
        validaVoto(edizione);
        this.edizione = edizione;
    }

    /**
     * Imposta la recensione testuale.
     *
     * @param recensione testo fino a 256 caratteri
     * @throws IllegalArgumentException se la recensione supera i 256 caratteri
     */
    public void setRecensione(String recensione) {
        if (recensione != null && recensione.length() > 256) {
            throw new IllegalArgumentException("La recensione non può superare i 256 caratteri");
        }
        this.recensione = recensione;
    }

    /**
     * Imposta il voto finale.
     *
     * @param votoFinale voto complessivo
     */
    public void setVotoFinale(int votoFinale) {
        this.votoFinale = votoFinale;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public int getStile() {
        return stile;
    }

    public int getContenuto() {
        return contenuto;
    }

    public int getGradevolezza() {
        return gradevolezza;
    }

    public int getOriginalita() {
        return originalita;
    }

    public int getEdizione() {
        return edizione;
    }

    public int getVotoFinale() {
        return votoFinale;
    }

    public String getRecensione() {
        return recensione;
    }

    /**
     * Valida che un voto sia compreso tra 1 e 5.
     *
     * @param voto voto da validare
     * @throws IllegalArgumentException se il voto non è nel range valido
     */
    private void validaVoto(int voto) {
        if (voto < 1 || voto > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5");
        }
    }
}
