/**
 * Wyjątek zwracany w momencie niepowodzenia zapisu listy notatek do pliku.
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class FailedToWriteToFileException extends Exception{

    /**
     * Konstruktor parametryczny. Tworzy wyjątek wraz z wiadomością o błędzie.
     * @param error_msg Wiadomość o błędzie, jaką ma przechowywać wyjątek.
     */
    FailedToWriteToFileException(String error_msg){
        super(error_msg);
    }
}