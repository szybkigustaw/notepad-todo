import java.util.Date;
import java.util.Objects;

/**
 * Reprezentuje pojedynczą notatkę.
 * Notatka może być notatką ukrytą, zawiera tekst oraz daty utworzenia/ostatniej modyfikacji;
 *
 * @version 1.1.0
 * @author Michał Mikuła
 */
public class Note {
    /**
     * Statyczna stała definiująca typ notatki
     */
    static final int NOTE = 1;
    /**
     * Statyczna stała definiująca typ notatki
     */
    static final int TODO_NOTE = 2;
    /**
     * Definiuje treść notatki
     */
    private String text;
    /**
     * Definiuje etykietę notatki
     */
    private String label;
    /**
     * Definiuje datę utworzenia notatki
     */
    private Date create_date;
    /**
     * Definiuje datę ostatniej modyfikacji notatki
     */
    private Date mod_date;
    /**
     * Definiuje typ notatki
     */
    private int type = Note.NOTE;
    /**
     * Definiuje stan ukrycia notatki
     */
    boolean isHidden;

    /**
     * Zwraca tekst notatki.
     *
     * @return Tekst notatki.
     */
    public String getText(){
        return this.text;
    }

    /**
     * Przypisuje nowy tekst do notatki.
     * @param text Nowy tekst notatki.
     */
    public void setText(String text){
        this.text = text;
    }

    /**
     * Zwraca etykietę notatki.
     *
     * @return Etykieta notatki.
     */
    public String getLabel(){
        return this.label;
    }

    /**
     * Przypisuje nową etykietę do notatki.
     * @param label Nowa etykieta notatki.
     */
    public void setLabel(String label){
        this.label = label;
    }

    /**
     * Zwraca datę utworzenia notatki.
     * @return Data utworzenia notatki.
     */
    public Date getCreate_date(){
        return this.create_date;
    }
    /**
     * Przypisuje nową datę utworzenia do notatki.
     * @param create_date Nowa data utworzenia notatki.
     */
    public void setCreate_date(Date create_date){
        this.create_date = create_date;
    }

    /**
     * Zwraca datę ostatniej modyfikacji notatki.
     * @return Data ostatniej modyfikacji notatki.
     */
    public Date getMod_date(){
        return this.mod_date;
    }
    /**
     * Przypisuje nową datę ostatniej modyfikacji notatki.
     * @param mod_date Nowa data modyfikacji notatki.
     */
    public void setMod_date(Date mod_date){
        this.mod_date = mod_date;
    }

    /**
     * Zwraca typ notatki.
     * @return Typ notatki. (NOTE == 1, TODO_NOTE == 2)
     */
    public int getType() { return this.type; }

    /**
     * Przypisuje nowy typ notatki.
     * @param type Typ notatki. (NOTE == 1, TODO_NOTE == 2)
     */
    public void setType(int type) { this.type = type; }

    /**
     * Zwraca stan ukrycia notatki.
     * @return Stan ukrycia notatki.
     */
    public boolean getHidden(){
        return this.isHidden;
    }
    /**
     * Przypisuje stan ukrycia notatki.
     * @param isHidden Nowy stan ukrycia notatki.
     */
    public void setHidden(Boolean isHidden){
        this.isHidden = isHidden;
    }

    /**
     * Wyświetla w konsoli zawartość notatki oraz jej metadane
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
     * Dokonuje porównania zawartości i metadanych notatek.
     * @param first Pierwsza notatka
     * @param second Druga notatka
     * @return Wartość <i>true</i> jeśli notatki są równe. <i>false</i> jeśli nie są równe.
     */
    public static boolean areNotesEqual(Note first, Note second){
        if(!Objects.equals(first.getLabel(), second.getLabel())) return false;
        if(!Objects.equals(first.getText(), second.getText())) return false;
        if(!Objects.equals(first.getMod_date(), second.getMod_date())) return false;
        if(!Objects.equals(first.getCreate_date(), second.getCreate_date())) return false;
        return first.getHidden() == second.getHidden();
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
