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
     * Tablica przechowująca terminy przypomnienia o dacie zakończenia zadania.
     */
    private Date[] remind_times;
    /**
     * Tablica przechowująca terminy zakończenia zadań.
     */
    private Date[] deadlines;
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
     * Zwraca tablicę wszystkich terminów przypomnienia o terminie zakończenia zadań.
     * @return Tablica wszystkich terminów przypomnienia o terminie zakończenia zadań.
     */
    public Date[] getRemindTimes(){ return this.remind_times; }

    /**
     * Zwraca pojedynczy termin przypomnienia o terminie zakończenia zadania.
     * @param index Pozycja zadania na liście zadań.
     * @return Termin przypomnienia o terminie zakończenia wybranego zadania.
     */
    public Date getRemindTime(int index){ return this.remind_times[index]; }

   /**
     * Przypisuje termin przypomnienia o terminie zakończenia zadania do wybranego zadania na liście zadań.
     * @param index Pozycja zadania na liście zadań.
     * @param remind_time Nowy termin przypomnienia o terminie zakończenia zadania.
     */
    public void setRemindTime(Date remind_time, int index){ this.remind_times[index] = remind_time; }

    /**
     * Przypisuje tablicę terminów przypomnienia o terminie zakończenia zadań do listy zadań
     * @param remind_times Nowa tablica terminów przypomnienia o terminie zakończenia zadań.
     */
    public void setRemindTimes(Date[] remind_times){ this.remind_times = remind_times; }

    /**
     * Zwraca tablicę wszystkich terminów zakończenia zadań.
     * @return Tablica wszystkich terminów zakończenia zadań.
     */
    public Date[] getDeadlines(){ return this.deadlines; }

    /**
     * Zwraca pojedynczy termin zakończenia zadania.
     * @param index Pozycja zadania na liście zadań.
     * @return Termin zakończenia wybranego zadania.
     */
    public Date getDeadline(int index){ return this.deadlines[index]; }

    /**
     * Przypisuje termin zakończenia zadania do wybranego zadania na liście zadań.
     * @param index Pozycja zadania na liście zadań.
     * @param deadline Nowy termin zakończenia zadania.
     */
    public void setDeadline(Date deadline, int index){ this.deadlines[index] = deadline; }

    /**
     * Przypisuje tablicę terminów zakończenia zadań do listy zadań
     * @param deadlines Nowa tablica terminów zakończenia zadań.
     */
    public void setDeadlines(Date[] deadlines){ this.deadlines = deadlines; }

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
        if(first.getDeadlines().length != second.getDeadlines().length) return false;
        if(first.getRemindTimes().length != second.getRemindTimes().length) return false;
        if(first.getTodo().length != second.getTodo().length) return false;
        if(first.getChecked().length != second.getChecked().length) return false;
        for(int i = 0; i < first.getTodo().length; i++){
            if(
                    (!Objects.equals(first.getTodo(i), second.getTodo(i))) ||
                    first.getChecked(i) != second.getChecked(i) ||
                    first.getRemindTime(i) != second.getRemindTime(i) ||
                    first.getDeadline(i) != second.getRemindTime(i)
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

    private Date[] expandDateArray(Date[] arr, int slots){

        //Stwórz nową tablicę o ilości miejsc (długość tablicy z parametrów metody
        // + dodatkowa ilość miejsc podana w parametrach metody)

        Date[] new_dates = new Date[arr.length + slots];

        //Dla każdego elementu w nowej tablicy
        for(int i = 0; i < new_dates.length; i++){

            //Wstaw do nowej tablicy datę ze starej tablicy pod indeksem iteratora
            if(i < arr.length) new_dates[i] = arr[i];
            else new_dates[i] = null; /* Jeśli iterator jest większy od długości tablicy podanej w parametrze,
                                         wstaw do nowej tablicy wartość pustą */
        }

        //Przypisz do tablicy podanej w parametrze wartość nowej tablicy i zwróć ją
        arr = new_dates;
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

    public void addDeadline(int index, Date deadline){
        if(this.deadlines.length < index){
            this.setDeadlines(this.expandDateArray(this.getDeadlines(), (index - this.deadlines.length)));
        }

        this.setDeadline(deadline, index);
    }

    public void addReminder(int index, Date remind_time){
        if(this.remind_times.length < index){
            this.setRemindTimes(this.expandDateArray(this.getRemindTimes(), (index - this.remind_times.length)));
        }

        this.setRemindTime(remind_time, index);
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
            System.out.printf("\tRT: %s \t DL: %s\n", this.getRemindTime(i).toString(), this.getDeadline(i).toString());
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
        this.setRemindTimes(new Date[]{new Date(), new Date(), new Date(), new Date(), new Date()});
        this.setDeadlines(new Date[]{new Date(), new Date(), new Date(), new Date(), new Date()});
        this.verifyToDoCompletion();
    }

    /**
     * Konstruktor parametryczny. Tworzy nową notatkę ze zdefiniowaną etykietą, zawartością oraz stanem ukrycia.
     * @param label Etykieta nowej notatki.
     * @param text Treść nowej notatki.
     * @param todos Tablica treści zadań
     * @param isChecked Tablica stanów odhaczenia zadań.
     * @param remind_times Tablica terminów przypomnienia o terminie zakończenia notatek.
     * @param deadlines Tablica terminów zakończenia notatek.
     * @param isHidden Stan ukrycia nowej notatki.
     */
    ToDoNote(String label, String text, String[] todos, boolean[] isChecked, Date[] remind_times, Date[] deadlines, boolean isHidden) {
        super(label, text, isHidden);
        this.setType(TODO_NOTE);
        this.setTodo(todos);
        this.setChecked(isChecked);
        this.setRemindTimes(remind_times);
        this.setDeadlines(deadlines);
        this.verifyToDoCompletion();
    }
}
