import java.util.Date;

/**
 * Klasa reprezentująca pojedyńczą notatkę.
 * Notatka może być notatką ukrytą, zawiera tekst oraz daty utworzenia/ostatniej modyfikacji;
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class Note {
    private String text;
    private String label;
    private Date create_date;
    private Date mod_date;
    boolean isHidden;

    /**
     * Metoda zwracająca tekst notatki.
     *
     * @return Tekst notatki.
     */
    public String getText(){
        return this.text;
    };

    /**
     * Metoda przypisująca nowy tekst notatki.
     * @param text Nowy tekst notatki.
     */
    public void setText(String text){
        this.text = text;
    }

    /**
     * Metoda zwracająca etykietę notatki.
     *
     * @return Etykieta notatki.
     */
    public String getLabel(){
        return this.label;
    };

    /**
     * Metoda przypisująca nową etykietę notatki.
     * @param label Nowa etykieta notatki.
     */
    public void setLabel(String label){
        this.label = label;
    }

    /**
     * Metoda zwracająca datę utworzenia notatki.
     * @return Data utworzenia notatki.
     */
    public Date getCreate_date(){
        return this.create_date;
    }
    /**
     * Metoda przypisująca nową datę utworzenia notatki.
     * @param create_date Nowy tekst notatki.
     */
    public void setCreate_date(Date create_date){
        this.create_date = create_date;
    }

    /**
     * Metoda zwracająca datę ostatniej modyfikacji notatki.
     * @return Data ostatniej modyfikacji notatki.
     */
    public Date getMod_date(){
        return this.mod_date;
    }
    /**
     * Metoda przypisująca nową datę ostatniej modyfikacji notatki.
     * @param mod_date Nowy tekst notatki.
     */
    public void setMod_date(Date mod_date){
        this.mod_date = mod_date;
    }

    /**
     * Metoda wyświetlająca w konsoli zawartość notatki oraz jej metadane
     */
    public void showNote(){
        System.out.printf("\n%s: \n\n", this.getLabel());
        System.out.printf("Hidden: %b \n", this.getHidden());
        System.out.printf("Creation date: %s\n", this.getCreate_date().toString());
        System.out.printf("Modification date: %s\n\n", this.getMod_date().toString());
        System.out.printf("Value: %s\n", this.getText());
        System.out.print("--------------------");
    }

    /**
     * Metoda zwracająca stan ukrycia notatki.
     * @return Stan ukrycia notatki.
     */
    public boolean getHidden(){
        return this.isHidden;
    }
    /**
     * Metoda zmieniająca stan ukrycia notatki.
     * @param isHidden Nowy stan ukrycia notatki.
     */
    public void setHidden(Boolean isHidden){
        this.isHidden = isHidden;
    }

    /**
     * Konstruktor domyślny. Tworzy nową, nieukrytą notatkę z tekstem "Sample text" i etykietą "Sample label".
     */
    Note(){
        this.setLabel("Sample label");
        this.setText("Sample text");
        this.setHidden(false);
        this.setMod_date(new Date());
        this.setCreate_date(new Date());
    }

    /**
     * Konstruktor parametryczny. Tworzy nową notatkę z własną etykietą, własnym tekstem oraz zdefiniowanym stanem ukrycia.
     * @param label Etykieta nowej notatki.
     * @param text Tekst nowej notatki.
     * @param isHidden Stan ukrycia nowej notatki.
     */
    Note(String label, String text, boolean isHidden){
        this.setLabel(label);
        this.setText(text);
        this.setHidden(isHidden);
        this.setMod_date(new Date());
        this.setCreate_date(new Date());
    }
}
