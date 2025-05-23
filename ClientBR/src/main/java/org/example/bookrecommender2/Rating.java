// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2;

/**
 * Rappresenta una valutazione (rating) assegnata da un utente a un libro.
 * Contiene punteggi su vari aspetti del libro e una recensione testuale.
 */
public class Rating {

    /** Identificativo univoco dell'utente che ha assegnato la valutazione. */
    private String idUtente;

    /** Identificativo del libro valutato. */
    private int idLibro;

    /** Valutazione dello stile del libro (da 1 a 5). */
    private int stile;

    /** Valutazione del contenuto del libro (da 1 a 5). */
    private int contenuto;

    /** Valutazione della gradevolezza del libro (da 1 a 5). */
    private int gradevolezza;

    /** Valutazione dell'originalità del libro (da 1 a 5). */
    private int originalita;

    /** Valutazione dell'edizione del libro (da 1 a 5). */
    private int edizione;

    /** Voto finale calcolato automaticamente, somma o media di altri voti. */
    private int votoFinale;

    /** Recensione testuale, max 256 caratteri. */
    private String recensione;

    /**
     * Costruttore completo di Rating.
     *
     * @param idUtente     ID dell'utente valutatore
     * @param idLibro      ID del libro valutato
     * @param stile        Valutazione dello stile (1-5)
     * @param contenuto    Valutazione del contenuto (1-5)
     * @param gradevolezza Valutazione della gradevolezza (1-5)
     * @param originalita  Valutazione dell'originalità (1-5)
     * @param edizione     Valutazione dell'edizione (1-5)
     * @param votoFinale   Voto finale calcolato
     * @param recensione   Recensione testuale (max 256 caratteri)
     * @throws IllegalArgumentException se uno dei voti non è tra 1 e 5 o recensione è troppo lunga
     */
    public Rating(String idUtente, int idLibro, int stile, int contenuto, int gradevolezza,
                  int originalita, int edizione, int votoFinale, String recensione) {
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
    public Rating() {
    }

    /**
     * Restituisce il valore del voto finale.
     *
     * @return il voto finale
     */
    public int getValue() {
        return votoFinale;
    }

    /**
     * Imposta la valutazione dello stile.
     *
     * @param stile voto da 1 a 5
     * @throws IllegalArgumentException se voto non valido
     */
    public void setStile(int stile) {
        validaVoto(stile);
        this.stile = stile;
    }

    /**
     * Imposta la valutazione del contenuto.
     *
     * @param contenuto voto da 1 a 5
     * @throws IllegalArgumentException se voto non valido
     */
    public void setContenuto(int contenuto) {
        validaVoto(contenuto);
        this.contenuto = contenuto;
    }

    /**
     * Imposta la valutazione della gradevolezza.
     *
     * @param gradevolezza voto da 1 a 5
     * @throws IllegalArgumentException se voto non valido
     */
    public void setGradevolezza(int gradevolezza) {
        validaVoto(gradevolezza);
        this.gradevolezza = gradevolezza;
    }

    /**
     * Imposta la valutazione dell'originalità.
     *
     * @param originalita voto da 1 a 5
     * @throws IllegalArgumentException se voto non valido
     */
    public void setOriginalita(int originalita) {
        validaVoto(originalita);
        this.originalita = originalita;
    }

    /**
     * Imposta la valutazione dell'edizione.
     *
     * @param edizione voto da 1 a 5
     * @throws IllegalArgumentException se voto non valido
     */
    public void setEdizione(int edizione) {
        validaVoto(edizione);
        this.edizione = edizione;
    }

    /**
     * Imposta la recensione testuale.
     *
     * @param recensione testo massimo 256 caratteri
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
     * @param votoFinale valore finale della valutazione
     */
    public void setVotoFinale(int votoFinale) {
        this.votoFinale = votoFinale;
    }

    /**
     * Restituisce l'ID utente che ha valutato.
     *
     * @return id utente
     */
    public String getIdUtente() {
        return idUtente;
    }

    /**
     * Restituisce l'ID del libro valutato.
     *
     * @return id libro
     */
    public int getIdLibro() {
        return idLibro;
    }

    /**
     * Alias per ottenere l'ID utente.
     *
     * @return id utente
     */
    public String getUserId() {
        return idUtente;
    }

    /**
     * Restituisce la valutazione dello stile.
     *
     * @return voto stile
     */
    public int getStile() {
        return stile;
    }

    /**
     * Restituisce la valutazione del contenuto.
     *
     * @return voto contenuto
     */
    public int getContenuto() {
        return contenuto;
    }

    /**
     * Restituisce la valutazione della gradevolezza.
     *
     * @return voto gradevolezza
     */
    public int getGradevolezza() {
        return gradevolezza;
    }

    /**
     * Restituisce la valutazione dell'originalità.
     *
     * @return voto originalità
     */
    public int getOriginalita() {
        return originalita;
    }

    /**
     * Restituisce la valutazione dell'edizione.
     *
     * @return voto edizione
     */
    public int getEdizione() {
        return edizione;
    }

    /**
     * Restituisce il voto finale.
     *
     * @return voto finale
     */
    public int getVotoFinale() {
        return votoFinale;
    }

    /**
     * Restituisce la recensione testuale.
     *
     * @return recensione
     */
    public String getRecensione() {
        return recensione;
    }

    /**
     * Rappresentazione testuale dell'oggetto Rating.
     *
     * @return stringa con tutti i campi del rating
     */
    @Override
    public String toString() {
        return "Rating{" +
                "idUtente='" + idUtente + '\'' +
                ", idLibro=" + idLibro +
                ", stile=" + stile +
                ", contenuto=" + contenuto +
                ", gradevolezza=" + gradevolezza +
                ", originalita=" + originalita +
                ", edizione=" + edizione +
                ", votoFinale=" + votoFinale +
                ", recensione='" + recensione + '\'' +
                '}';
    }

    /**
     * Controlla che il voto sia compreso tra 1 e 5.
     *
     * @param voto valore da validare
     * @throws IllegalArgumentException se il voto è fuori dal range 1-5
     */
    private void validaVoto(int voto) {
        if (voto < 1 || voto > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5");
        }
    }
}
