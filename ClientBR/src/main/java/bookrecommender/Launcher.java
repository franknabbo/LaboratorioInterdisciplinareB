package bookrecommender;

/**
 * Classe di lancio dell'applicazione.
 * <p>
 * Questa classe serve per avviare l'applicazione JavaFX evitando problemi
 * con il module system. Non deve estendere {@link javafx.application.Application}.
 * </p>
 */
public class Launcher {
    /**
     * Metodo main che avvia l'applicazione richiamando il main della classe {@link Main}.
     *
     * @param args argomenti della riga di comando
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}