// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.db;

/**
 * Classe che rappresenta un utente del sistema.
 */
public class Utente {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String password; // password cifrata

    /**
     * Costruttore della classe Utente.
     *
     * @param nome          Nome dell'utente.
     * @param cognome       Cognome dell'utente.
     * @param codiceFiscale Codice fiscale univoco dell'utente.
     * @param email         Email dell'utente.
     * @param password      Password cifrata dell'utente.
     */
    public Utente(String nome, String cognome, String codiceFiscale, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
    }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return Nome dell'utente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return Cognome dell'utente.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Restituisce il codice fiscale dell'utente.
     *
     * @return Codice fiscale.
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Restituisce l'email dell'utente.
     *
     * @return Email dell'utente.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Restituisce la password cifrata dell'utente.
     *
     * @return Password cifrata.
     */
    public String getPassword() {
        return password;
    }
}
