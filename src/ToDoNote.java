import java.util.Date;
import java.util.Objects;

/**
 * Reprezentująca pojedynczą notatkę zawierającą listę zadań.
 * Notatka może być notatką ukrytą, zawiera tekst oraz daty utworzenia/ostatniej modyfikacji.
 * Lista zadań zawiera treści zadań oraz ich stany odhaczenia. Klasa pochodna klasy Note.
 *
 * @version 1.1.0
 * @author Michał Mikuła
 * @see Note
 */
public class ToDoNote extends Note{
    /**
     * Tablica przechowująca treści notatek
     */
    private String[] todos;
    /**
     * Tablica przechowująca stany odhaczenia zadań
     */
    private boolean[] isChecked;
    /**
     * Zmienna przechowująca stan ukończenia listy zadań (wartość <i>true</i> dla ukończonych wszystkich zadań)
     */
    private boolean isCompleted;

    /**
     * Zwraca treść listy zadań.
     * @return Treść listy zadań.
     */
    public String[] getTodo(){
        return this.todos;
    }

    /**
     * Zwraca treść pojedynczego zadań.
     * @param index Pozycja na liście zadań.
     * @return Treść pojedynczego zadania.
     */
    public String getTodo(int index){
        return this.todos[index];
    }

    /**
     * Przypisuje do notatki nową tablicę reprezentującą treści zadań.
     * @param text Tablica przechowująca treści zadań
     */
    public void setTodo(String[] text){
        this.todos = text;
    }

    /**
     * Przypisuje nową treść do zadania znajdującego się na liście pod wskazanym indeksem.
     * @param text Nowa treść zadania.
     * @param index Pozycja zadania na liście.
     */
    public void setTodo(String text, int index){
        this.todos[index] = text;
    }

    /**
     * Zwraca tablicę stanów odhaczenia zadań dla listy zadań przechowywanej w notatce.
     * @return Tablica stanów odhaczenia listy zadań.
     */
    public boolean[] getChecked(){
        return this.isChecked;
    }

    /**
     * Zwraca stan odhaczenia zadania znajdującego się na liście pod wskazanym indeksem
     * @param index Pozycja zadania na liście.
     * @return Stan odhaczenia zadania.
     */
    public boolean getChecked(int index){
        return this.isChecked[index];
    }
    /**
     * Przypisuje tablicę ze stanami odhaczenia do listy zadań w notatce wewnątrz obiektu
     * @param isChecked Tablica zawierająca stany odhaczenia zadań.
     */
    public void setChecked(boolean[] isChecked){
        this.isChecked = isChecked;
        this.verifyToDoCompletion();
    }

    /**
     * Przypisuje stan odhaczenia do zadania z listy znajdującego się pod wskazanym indeksem
     * @param isChecked Nowy stan odhaczenia zadania.
     * @param index Pozycja zadania na liście.
     */
    public void setChecked(boolean isChecked, int index){
        this.isChecked[index] = isChecked;
        this.verifyToDoCompletion();
    }

    /**
     * Zwraca stan ukończenia listy zadań.
     * @return Stan ukończenia listy zadań. (<i>true</i> jeśli wszystkie zadania zostały ukończone)
     */
    public boolean getCompleted(){ return this.isCompleted; }

    /**
     * Przypisuje nowy stan ukończenia listy zadań.
     * @param isCompleted Nowy stan ukończenia listy zadań.
     */
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted;}

    /**
     * Weryfikuje stan ukończenia notatki. Koryguje wpis o stanie zakończenia notatki adekwatnie do jej aktualnego stanu.
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
     * Dokonuje porównania zawartości i metadanych notatek.
     * @param first Pierwsza notatka
     * @param second Druga notatka
     * @return Wartość <i>true</i> jeśli notatki są równe. <i>false</i> jeśli nie są równe.
     */
    public static boolean areNotesEqual(ToDoNote first, ToDoNote second){
        if(!Objects.equals(first.getLabel(), second.getLabel())) return false;
        if(!Objects.equals(first.getText(), second.getText())) return false;
        if(!Objects.equals(first.getMod_date(), second.getMod_date())) return false;
        if(!Objects.equals(first.getCreate_date(), second.getCreate_date())) return false;
        if(first.getTodo().length != second.getTodo().length) return false;
        if(first.getChecked().length != second.getChecked().length) return false;
        for(int i = 0; i < first.getTodo().length; i++){
            if(
                    (!Objects.equals(first.getTodo(i), second.getTodo(i))) ||
                    first.getChecked(i) != second.getChecked(i)
            ) {
                return false;
            }
        }
        return first.getHidden() == second.getHidden();
    }

    /**
     * Pobiera tablicę treści zadań i poszerza ją o podaną ilość pozycji.
     * @param arr Tablica bazowa.
     * @param slots Ilość miejsc do dodania.
     * @return Nowa tablica, zawierająca informacje z tablicy starej, poszerzona o podaną ilość miejsc.
     */
    private String[] expandTextArray(String[] arr, int slots){

        //Stwórz nową tablicę o ilości miejsc (długość tablicy z parametrów metody
        // + dodatkowa ilość miejsc podana w parametrach metody)
        String[] newTexts = new String[arr.length + slots];

        //Dla każdego elementu w nowej tablicy
        for(int i = 0; i < newTexts.length; i++){

            //Jeśli iterator jest mniejszy od długości tablicy z parametrów funkcji
            if(i < arr.length) {

                //Wstaw do nowej tablicy treść zadania ze starej tablicy pod indeksem iteratora
                newTexts[i] = arr[i];
            } else newTexts[i] = ""; //Jeśli jest większy, wstaw pusty ciąg znaków w to miejsce
        }

        //Przypisz do tablicy podanej w parametrze wartość nowej tablicy i zwróć ją
        arr = newTexts;
        return arr;
    }
    /**
     * Pobiera tablicę stanów odhaczenia zadań i poszerza ją o podaną ilość pozycji.
     * @param arr Tablica bazowa.
     * @param slots Ilość miejsc do dodania.
     * @return Nowa tablica, zawierająca informacje z tablicy starej, poszerzona o podaną ilość miejsc.
     */
    private boolean[] expandCheckedArray(boolean[] arr, int slots){

        //Stwórz nową tablicę o ilości miejsc (długość tablicy z parametrów metody
        // + dodatkowa ilość miejsc podana w parametrach metody)

        boolean[] newBooleans = new boolean[arr.length + slots];

        //Dla każdego elementu w nowej tablicy
        for(int i = 0; i < newBooleans.length; i++){

            //Wstaw do nowej tablicy stan odhaczenia zadania ze starej tablicy pod indeksem iteratora
            if(i < arr.length) newBooleans[i] = arr[i];
            else newBooleans[i] = false; //Jeśli jest większy, wstaw wartość false w to miejsce
        }

        //Przypisz do tablicy podanej w parametrze wartość nowej tablicy i zwróć ją
        arr = newBooleans;
        return arr;
    }

    /**
     * Dodaje do listy zadań w notatce nowe zadania. Automatycznie aktualizuje datę modyfikacji notatki;
     * @param texts Tablica treści zadań
     * @param checkedStates Tablica stanów odhaczenia zadań
     */
    public void addToDo(String[] texts, boolean[] checkedStates){

        //Jeśli długość tablicy treści zadań nie równa się długości tablicy stanów zadań
        if(texts.length != checkedStates.length){

            //Jeśli ta pierwsza jest krótsza od drugiej
            if(texts.length < checkedStates.length){

                //Przechowaj informację o długości tablicy treści zadań
                int curTextsLength = texts.length;

                //Rozszerz tablicę o (długość tablicy stanów - długość tablicy treści zadań) pozycji
                texts = this.expandTextArray(texts, (checkedStates.length - texts.length));

                //Do każdej nowo dodanej treści zadań przypisz wartość "Sample text"
                for(int i = curTextsLength; i < texts.length; i++){
                    texts[i] = "Sample text";
                }
            }
            //Jeśli jest na odwrót
            else {

                //Przechowaj informację o długości tablicy stanów odhaczenia zadań
                int curStatesLength = checkedStates.length;

                //Rozszerz tablicę o (długość tablicy treści zadań - długość tablicy stanów odhaczenia zadań) pozycji
                checkedStates = this.expandCheckedArray(checkedStates, (texts.length - checkedStates.length));

                //Dla każdego nowo dodanego stanu przypisz wartość false
                for(int i = curStatesLength; i < checkedStates.length; i++){
                    checkedStates[i] = false;
                }
            }
        }

        //Przechowaj informację o obecnej długości tablic danych zadań
        int curTodosLength = this.todos.length;
        int curBooleansLength = this.isChecked.length;

        //Poszerz tablice o długości tablic podanych w parametrach
        this.todos = this.expandTextArray(this.todos, texts.length);
        this.isChecked = this.expandCheckedArray(this.isChecked, checkedStates.length);

        //Skopiuj zawartość tablic podanych w parametrach na końcu tablic danych notatek
        System.arraycopy(texts,0, this.todos, curTodosLength, texts.length);
        System.arraycopy(checkedStates, 0, this.isChecked, curBooleansLength, checkedStates.length);

        //Zaktualizuj datę modyfikacji
        this.setMod_date(new Date());
    }

    /**
     * Wyświetla w konsoli zawartość notatki, listę zadań w niej zawartą oraz jej metadane.
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
     * @param todos Tablica treści zadań
     * @param isChecked Tablica stanów odhaczenia zadań.
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
