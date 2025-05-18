// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2;


public class Rating {
    private String idUtente;
    private int idLibro;
    private int stile;           // Valutazione da 1 a 5
    private int contenuto;       // Valutazione da 1 a 5
    private int gradevolezza;    // Valutazione da 1 a 5
    private int originalita;     // Valutazione da 1 a 5
    private int edizione;        // Valutazione da 1 a 5
    private int votoFinale;      // Calcolato automaticamente
    private String recensione;   // Max 256 caratteri


    // Costruttore
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
    public Rating(){

    }

    public int getValue(){

        return  votoFinale;

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

    public void setVotoFinale(int votoFinale) {
        this.votoFinale = votoFinale;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public String getUserId(){

        return idUtente;

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

    // Validazione del voto (da 1 a 5)
    private void validaVoto(int voto) {
        if (voto < 1 || voto > 5) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 1 e 5");
        }
    }



}
