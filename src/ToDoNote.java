import java.util.Date;
/**
 * Klasa reprezentująca pojedyńczą notatkę zawierającą listę zadań.
 * Notatka może być notatką ukrytą, zawiera tekst oraz daty utworzenia/ostatniej modyfikacji.
 * Lista zadań zawiera treści zadań oraz ich stany odhaczenia. Klasa pochodna klasy Note.
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class ToDoNote extends Note{
    private String[] todos;
    private boolean[] isChecked;
    private boolean isCompleted;

    /**
     * Metoda zwracająca treść listy zadań.
     * @return Treść listy zadań.
     */
    public String[] getTodo(){
        return this.todos;
    }

    /**
     * Metoda zwracająca pojedyńczy element z listy zadań.
     * @param index Pozycja na liście zadań.
     * @return Treść pojedyńczego zadania.
     */
    public String getTodo(int index){
        return this.todos[index];
    }

    /**
     * Metoda przypisująca do notatki nową treść listy zadań.
     * @param text Nowa treść listy zadań.
     */
    public void setTodo(String[] text){
        this.todos = text;
    }

    /**
     * Metoda przypisująca do listy zadań notatki nową wartość pojedyńczego zadania.
     * @param text Nowa treść zadania.
     * @param index Pozycja zadania na liście.
     */
    public void setTodo(String text, int index){
        this.todos[index] = text;
    }

    /**
     * Metoda zwracająca stany całej listy zadań.
     * @return Stany odhaczenia listy zadań.
     */
    public boolean[] getChecked(){
        return this.isChecked;
    }

    /**
     * Metoda zwracająca stan pojedyńczego zadania z listy.
     * @param index Pozycja zadania na liście.
     * @return Stan odhaczenia zadania.
     */
    public boolean getChecked(int index){
        return this.isChecked[index];
    }
    /**
     * Metoda przypisująca nowe stany całej listy zadań.
     * @param isChecked Lista nowych stanów zadań.
     */
    public void setChecked(boolean[] isChecked){
        this.isChecked = isChecked;
        this.verifyToDoCompletion();
    }

    /**
     * Metoda przypisująca nowy stan do pojedyńczego zadania.
     * @param isChecked Nowy stan odhaczenia zadaia.
     * @param index Pozycja zadania na liście.
     */
    public void setChecked(boolean isChecked, int index){
        this.isChecked[index] = isChecked;
        this.verifyToDoCompletion();
    }

    /**
     * Metoda zwraca stan ukończenia listy zadań.
     * @return Stan ukończenia listy zadań.
     */
    public boolean getCompleted(){ return this.isCompleted; }

    /**
     * Metoda przypisuje nowy stan ukończenia listy zadań.
     * @param isCompleted Nowy stan ukończenia listy zadań.
     */
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted;}

    /**
     * Metoda weryfikująca stan ukończenia notatki. Koryguje wpis o stanie zakończenia notatki adekwatnie do jej aktualnego stanu.
     */
    public void verifyToDoCompletion(){
        this.setCompleted(true);
        for(boolean isChecked : this.getChecked()){
            if(!isChecked){
                this.setCompleted(false);
                break;
            }
        }
    }

    /**
     * Metoda pobierająca tablicę ciągów znaków i poszerzającą ją o podaną ilość pozycji.
     * @param arr Tablica bazowa.
     * @param slots Ilość miejsc do dodania.
     * @return Nowa tablica, zawierająca informacje z tablicy starej, poszerzona o podaną ilość miejsc.
     */
    private String[] expandTextArray(String[] arr, int slots){
        String[] newTexts = new String[arr.length + slots];
        for(int i = 0; i < newTexts.length; i++){
            if(i < arr.length) {
                newTexts[i] = arr[i];
            } else newTexts[i] = "";
        }
        arr = newTexts;
        return arr;
    }
    /**
     * Metoda pobierająca tablicę wartości prawda/fałsz i poszerzającą ją o podaną ilość pozycji.
     * @param arr Tablica bazowa.
     * @param slots Ilość miejsc do dodania.
     * @return Nowa tablica, zawierająca informacje z tablicy starej, poszerzona o podaną ilość miejsc.
     */
    private boolean[] expandCheckedArray(boolean[] arr, int slots){
        boolean[] newBools = new boolean[arr.length + slots];
        for(int i = 0; i < newBools.length; i++){
            if(i < arr.length) newBools[i] = arr[i];
            else newBools[i] = false;
        }
        arr = newBools;
        return arr;
    }

    /**
     * Metoda dodająca do listy zadań w notatce nowe zadania. Automatycznie aktualizuje datę modyfikacji notatki;
     * @param texts Lista treści zadań
     * @param checkedStates Lista stanów odhaczenia zadań
     */
    public void addToDo(String[] texts, boolean[] checkedStates){
        if(texts.length != checkedStates.length){
            if(texts.length < checkedStates.length){
                int curTextsLength = texts.length;
                texts = this.expandTextArray(texts, (checkedStates.length - texts.length));
                for(int i = curTextsLength; i < texts.length; i++){
                    texts[i] = "Sample text";
                }
            } else if (checkedStates.length < texts.length){
                int curStatesLength = checkedStates.length;
                checkedStates = this.expandCheckedArray(checkedStates, (texts.length - checkedStates.length));
                for(int i = curStatesLength; i < checkedStates.length; i++){
                    checkedStates[i] = false;
                }
            }
        }
        int curTodosLength = this.todos.length;
        int curBoolsLength = this.isChecked.length;
        this.todos = this.expandTextArray(this.todos, texts.length);
        this.isChecked = this.expandCheckedArray(this.isChecked, checkedStates.length);
        System.arraycopy(texts,0, this.todos, curTodosLength, texts.length);
        System.arraycopy(checkedStates, 0, this.isChecked, curBoolsLength, checkedStates.length);
        this.setMod_date(new Date());
    }

    /**
     * Metoda wyświetlająca w konsoli zawartość notatki, listę zadań w niej zawartą oraz jej metadane.
     */
    public void showNote(){
        System.out.printf("\n%s: \n\n", this.getLabel());
        System.out.printf("Hidden: %b \n", this.getHidden());
        System.out.printf("Creation date: %s\n", this.getCreate_date().toString());
        System.out.printf("Modification date: %s\n\n", this.getMod_date().toString());
        System.out.printf("Value: %s\n", this.getText());
        System.out.println("To-Do values:");
        for(int i = 0; i < this.getTodo().length; i++){
            System.out.printf("\tT: %s \t C: %b\n", this.getTodo(i), this.getChecked(i));
        }
        System.out.print("--------------------");
    }

    /**
     * Konstruktor domyślny. Tworzy nową, nieukrytą notatkę z tekstem "Sample text" oraz listą zadań zawierającą 5 zadań o treści "Sample text", z których dwie ostatnie są odhaczone
     */
    ToDoNote(){
        super();
        this.setType(TODO_NOTE);
        this.setTodo(new String[]{"Sample text", "Sample text", "Sample text", "Sample text", "Sample text"});
        this.setChecked(new boolean[]{false,false,false,true,true});
        this.verifyToDoCompletion();
    }

    /**
     * Konstruktor parametryczny. Tworzy nową notatkę ze zdefiniowaną etykietą, zawartością oraz stanem ukrycia.
     * @param label Etykieta nowej notatki.
     * @param text Treść nowej notatki.
     * @param todos Lista zadań.
     * @param isChecked Lista stanów zadań.
     * @param isHidden Stan ukrycia nowej notatki.
     */
    ToDoNote(String label, String text, String[] todos, boolean[] isChecked, boolean isHidden) {
        super(label, text, isHidden);
        this.setType(TODO_NOTE);
        this.setTodo(todos);
        this.setChecked(isChecked);
        this.verifyToDoCompletion();
    }
}
