// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender.db;

/**
 * La classe {@code Library} rappresenta una libreria appartenente a un utente nel sistema.
 * Ogni libreria è identificata da un ID univoco e associata a un utente tramite il suo user ID.
 * Contiene inoltre il nome della libreria.
 */
public class Library {
    private int idLibreria;
    private String user_id;
    private String nome;

    /**
     * Costruttore per creare un oggetto {@code Library} con i parametri specificati.
     *
     * @param idLibreria l'identificativo univoco della libreria
     * @param user_id l'identificativo dell'utente proprietario della libreria
     * @param nome il nome della libreria
     */
    public Library(int idLibreria, String user_id, String nome) {
        this.idLibreria = idLibreria;
        this.user_id = user_id;
        this.nome = nome;
    }

    /**
     * Restituisce l'ID della libreria.
     *
     * @return l'identificativo della libreria
     */
    public int getIdLibreria() {
        return idLibreria;
    }

    /**
     * Restituisce l'ID dell'utente proprietario della libreria.
     *
     * @return l'identificativo dell'utente
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * Restituisce il nome della libreria.
     *
     * @return il nome della libreria
     */
    public String getNome() {
        return nome;
    }
}
