package org.example.db;



public class Rating {
    private int idValutazione;
    private int idUtente;
    private int idLibro;
    private int stile;           // Valutazione da 1 a 5
    private int contenuto;       // Valutazione da 1 a 5
    private int gradevolezza;    // Valutazione da 1 a 5
    private int originalita;     // Valutazione da 1 a 5
    private int edizione;        // Valutazione da 1 a 5
    private int votoFinale;      // Calcolato automaticamente
    private String recensione;   // Max 256 caratteri


    // Costruttore
    public Rating(int idUtente, int idLibro, int stile, int contenuto, int gradevolezza,
                  int originalita, int edizione, String recensione) {
        this.idUtente = idUtente;
        this.idLibro = idLibro;
        setStile(stile);
        setContenuto(contenuto);
        setGradevolezza(gradevolezza);
        setOriginalita(originalita);
        setEdizione(edizione);
        setRecensione(recensione);
        calcolaVotoFinale();

    }

    // Metodi setter con validazione
    public void setStile(int stile) {
        validaVoto(stile);
        this.stile = stile;
    }

    public void setContenuto(int contenuto) {
        validaVoto(contenuto);
        this.contenuto = contenuto;
    }

    public void setGradevolezza(int gradevolezza) {
        validaVoto(gradevolezza);
        this.gradevolezza = gradevolezza;
    }

    public void setOriginalita(int originalita) {
        validaVoto(originalita);
        this.originalita = originalita;
    }

    public void setEdizione(int edizione) {
        validaVoto(edizione);
        this.edizione = edizione;
    }

    public void setRecensione(String recensione) {
        if (recensione != null && recensione.length() > 256) {
            throw new IllegalArgumentException("La recensione non può superare i 256 caratteri");
        }
        this.recensione = recensione;
    }

    // Getters
    public int getIdValutazione() {
        return idValutazione;
    }

    public int getIdUtente() {
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


    // Metodo per calcolare il voto finale
    private void calcolaVotoFinale() {
        double media = (stile + contenuto + gradevolezza + originalita + edizione) / 5.0;
        this.votoFinale = (int) Math.round(media);
    }

    // Validazione del voto (da 1 a 5)
    private void validaVoto(int voto) {
        if (voto < 1 || voto > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5");
        }
    }


}
