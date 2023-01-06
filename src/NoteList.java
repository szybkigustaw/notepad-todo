import java.text.Collator;
import java.util.*;

/**
 * Reprezentuje listę notatek. Przyjmuje obiekty klasy Note i jej subklas.
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class NoteList {
    /**
     * Definiuje tryb pełnego przypisania notatek. Wszystkie notatki są przypisywane.
     */
    static final int FULL = 0;
    /**
     * Definiuje tryb przypisania ukrytego. Tylko notatki ukryte (z ustawionym stanem ukrycia na true) znajdą się w ciele listy.
     * @see Note
     */
    static final int HIDDEN = 1;
    /**
     * Definiuje tryb przypisania publicznego. Tylko notatki jawne (z ustawionym stanem ukrycia na false) znajdą się w ciele listy.
     * @see Note
     */
    static final int PUBLIC = 2;

    /**
     * Definiuje tryb sortowania notatek na podstawie ich daty modyfikacji
     */
    static final int BY_MOD_DATE = 0;

    /**
     * Definiuje tryb sortowania notatek na podstawie ich daty utworzenia
     */
    static final int BY_CREATE_DATE = 1;

    /**
     * Definiuje tryb sortowania notatek na podstawie ich etykiet (alfabetycznie: od A do Z)
     */
    static final int BY_LABEL = 2;

    /**
     * Definiuje tryb sortowania notatek na podstawie ich stanu ukończenia
     * (Jeśli notatka nie posiada listy zadań, automatycznie umieszczana jest na końcu listy)
     */
    static final int BY_COMPLETION = 3;

    /**
     * Definiuje tryb sortowania notatek na podstawie ich typu.
     * Standardowo najpierw wyświetlane są notatki TODO-NOTE
     * @see Note
     */
    static final int BY_TYPE = 4;

    /**
     * Tablica obiektów przechowująca notatki w ciele obiektu.
     */
    private Note[] noteList;

    /**
     * Zwraca listę notatek.
     * @return Lista notatek.
     */
    public Note[] getNoteList(){
        return this.noteList;
    }

    /**
     * Zwraca pojedynczą notatkę z listy.
     * @param index Pozycja notatki na liście (Rozpoczyna się od 0).
     * @return Notatka z listy notatek znajdująca się na podanej pozycji na liście.
     */
    public Note getNote(int index){
        return this.noteList[index];
    }

    /**
     * Zwraca długość listy notatek.
     * @return Długość listy notatek.
     */
    public int getListLength(){ return this.getNoteList().length; }

    /**
     * Nadpisuje obecnie przechowywaną tablicę notatek nową wartością.
     * @param noteList Nowa lista notatek.
     */
    public void setNoteList(Note[] noteList){
        this.noteList = noteList;
    }

    /**
     * Przypisuje nową wartość do wyznaczonej notatki.
     * @param note Nowa notatka
     * @param index Pozycja na liście (Rozpoczyna się od 0)
     */
    public void setNote(Note note, int index){
        this.noteList[index] = note;
    }

    /**
     * Poszerza tablicę notatek przechowywaną w ciele tego obiektu o podaną ilość miejsc
     * @param slots Ilość miejsc, o którą zostanie poszerzona lista notatek.
     */
    private void expandNoteList(int slots){
        Note[] newList = new Note[this.getNoteList().length + slots];
        for(int i = 0; i < newList.length; i++){
            if(i < this.getNoteList().length) newList[i] = this.getNote(i);
            else newList[i] = new Note();
        }
        this.setNoteList(newList);
    }

    /**
     * Zwraca pozycję notatki na liście. Jeśli notatka nie znajduje się na liście, metoda zwraca wartość ujemną.
     * @param note Notatka, której pozycję na liście chcemy uzyskać.
     * @return Pozycja notatki na liście lub wartość ujemna w przypadku braku notatki na liście.
     */
    public int getNoteIndex(Note note) {
        for(int i = 0; i < this.getListLength(); i++){
            if(this.getNote(i).equals(note)){
                return i;
            }
        }
        return -1;
    }


    /**
     * Dodaje do listy notatek nową notatkę. Zawsze dodawana jest na koniec listy.
     * @param note Notatka do dodania na listę.
     */
    public void addNote(Note note){
        int curLength = this.getNoteList().length;
        this.expandNoteList(1);
        System.arraycopy(new Note[]{note}, 0, this.getNoteList(), curLength, 1);
    }

    /**
     * Dodaje do listy notatek nowe notatki. Zawsze dodawane są na koniec listy.
     * @param notes Tablica notatek do dodania.
     */
    public void addNote(Note[] notes){
        int curLength = this.getNoteList().length;
        this.expandNoteList(notes.length);
        System.arraycopy(notes, 0, this.getNoteList(), curLength, notes.length);
    }

    /**
     * Kasuje notatki na wyznaczonych pozycjach listy.
     * @param indexes Tablica pozycji, na których znajdują się notatki do skasowania (Indeksowanie zaczyna się od 0).
     */
    public void removeNote(int[] indexes){
        Note[] newList = new Note[this.getNoteList().length - indexes.length];
        Integer[] acceptedIndexes = new Integer[this.getNoteList().length - indexes.length];
        int cnt = 0;
        for(int i = 0; i < this.getNoteList().length; i++){
            boolean isAccepted = true;
            for (int index : indexes) {
                if (i == index) {
                    isAccepted = false;
                    break;
                }
                if (isAccepted) {
                    acceptedIndexes[cnt] = i;
                    cnt++;
                }
            }
        }
        for(int i = 0; i < newList.length; i++){
            newList[i] = this.getNote(acceptedIndexes[i]);
        }
        this.setNoteList(newList);
    }

    /**
     * Kasuje notatkę znajdującą się na wyznaczonym miejscu listy.
     * @param index Pozycja notatki na liście (Indeksowanie rozpoczyna się od 0).
     */
    public void removeNote(int index){
        Note[] newList = new Note[this.getNoteList().length - 1];
        Integer[] acceptedIndexes = new Integer[this.getNoteList().length - 1];
        int cnt = 0;
        for(int i = 0; i < this.getNoteList().length; i++){
            boolean isAccepted = true;
            if(i == index){
                isAccepted = false;
            }
            if(isAccepted){
                acceptedIndexes[cnt] = i;
                cnt++;
            }
        }
        for(int i = 0; i < newList.length; i++){
            newList[i] = this.getNote(acceptedIndexes[i]);
        }
        this.setNoteList(newList);
    }

    /**
     * Sortuje listę notatek przechowywaną w tym obiekcie oraz przypisuje do niego nową wartość listy.
     *
     * <p>Sortowanie może być przeprowadzone w kilku trybach, w zależności od przekazanej wartości trybu.
     * Możliwe tryby sortowania (sortowanie według): </p>
     * <ol>
     *     <li><b>Daty modyfikacji</b></li>
     *     <li><b>Daty utworzenia</b></li>
     *     <li><b>Etykiety</b> - kolejność alfabetyczna</li>
     *     <li><b>Typu notatki</b> - notatki typu <i>TODO-NOTE</i> wyświetlane są jako pierwsze</li>
     *     <li><b>Stanu ukończenia zadań</b> - procentu ukończenia wszystkich zadań na liście (nie dotyczy notatek typu <i>NOTE</i> - zawsze trafiają na koniec listy)</li>
     * </ol>
     *
     * @param sort_mode tryb sortowania (wyrażany poprzez liczbę całkowitą, do wykorzystania jedna ze stałych klasy)
     * @param descending wartość określająca kierunek sortowania (wartość true dla sortowania malejąco)
     */
    public void sortNote(int sort_mode, boolean descending) {

        //Stwórz kopię listy notatek z przechowywanej w obiekcie wartości
        NoteList temp = new NoteList(this.noteList, FULL);

        //Stwórz listę wyjściową
        NoteList output = new NoteList();

        //Wypełnij ją tyloma pustymi notatkami, ile notatek ma lista przechowywana w obiekcie
        output.setNoteList(new Note[temp.getListLength()]);

        //Sprawdź, który tryb sortowania jest żądany
        switch (sort_mode) {

            //Jeśli żądano sortowania według daty modyfikacji
            case BY_MOD_DATE -> {

                //Dla każdej notatki na liście tymczasowej
                for (int i = 0; i < temp.getListLength(); i++) {

                    //Dla każdej notatki na liście tymczasowej
                    for (int j = 0; j < temp.getListLength(); j++) {

                        /* Jeśli sortowanie ma się odbyć malejąco oraz data modyfikacji
                        w notatce pod indeksem i jest mniejsza od daty w notatce pod indeksem j */
                        if (descending && temp.getNote(i).getMod_date().getTime() < temp.getNote(j).getMod_date().getTime()) {

                            //Stwórz tymczasowe notatki
                            Note temp_note_less, temp_note_more;

                            //Przypisz im odpowiednie notatki z listy
                            temp_note_less = temp.getNote(i);
                            temp_note_more = temp.getNote(j);

                            //Na pozycji j (dalszej) przypisz notatkę wcześniejszą
                            temp.setNote(temp_note_less, j);

                            //Na pozycji i (wcześniejszej) przypisz notatkę późniejszą
                            temp.setNote(temp_note_more, i);

                        /* Jeśli sortowanie ma się odbyć rosnąco oraz data modyfikacji
                        w notatce pod indeksem i jest większa od daty w notatce pod indeksem j */
                        } else if (!descending && temp.getNote(i).getMod_date().getTime() > temp.getNote(j).getMod_date().getTime()) {

                            //Stwórz notatki tymczasowe
                            Note temp_note_less, temp_note_more;

                            //Przypisz im odpowiednie wartości
                            temp_note_less = temp.getNote(j);
                            temp_note_more = temp.getNote(i);

                            //Na pozycji i (wcześniejszej) przypisz notatkę wcześniejszą
                            temp.setNote(temp_note_less, i);

                            //Na pozycji j (dalszej) przypisz notatkę późniejszą
                            temp.setNote(temp_note_more, j);
                        }
                    }

                    //Przypisz do listy wyjściowej gotową listę tymczasową
                    output.setNoteList(temp.getNoteList());
                }
            }


            //Jeśli zażądano sortowania według daty utworzenia
            case BY_CREATE_DATE -> {
                //Dla każdej notatki na liście tymczasowej
                for (int i = 0; i < temp.getListLength(); i++) {

                    //Dla każdej notatki na liście tymczasowej
                    for (int j = 0; j < temp.getListLength(); j++) {

                        /* Jeśli sortowanie ma się odbyć malejąco oraz data utworzenia
                        w notatce pod indeksem i jest mniejsza od daty w notatce pod indeksem j */
                        if (descending && temp.getNote(i).getCreate_date().getTime() < temp.getNote(j).getCreate_date().getTime()) {

                            //Stwórz tymczasowe notatki
                            Note temp_note_less, temp_note_more;

                            //Przypisz im odpowiednie notatki z listy
                            temp_note_less = temp.getNote(i);
                            temp_note_more = temp.getNote(j);

                            //Na pozycji j (dalszej) przypisz notatkę wcześniejszą
                            temp.setNote(temp_note_less, j);

                            //Na pozycji i (wcześniejszej) przypisz notatkę późniejszą
                            temp.setNote(temp_note_more, i);

                        /* Jeśli sortowanie ma się odbyć rosnąco oraz data utworzenia
                        w notatce pod indeksem i jest większa od daty w notatce pod indeksem j */
                        } else if (!descending && temp.getNote(i).getCreate_date().getTime() > temp.getNote(j).getCreate_date().getTime()) {

                            //Stwórz notatki tymczasowe
                            Note temp_note_less, temp_note_more;

                            //Przypisz im odpowiednie wartości
                            temp_note_less = temp.getNote(j);
                            temp_note_more = temp.getNote(i);

                            //Na pozycji i (wcześniejszej) przypisz notatkę wcześniejszą
                            temp.setNote(temp_note_less, i);

                            //Na pozycji j (dalszej) przypisz notatkę późniejszą
                            temp.setNote(temp_note_more, j);
                        }
                    }

                    //Przypisz do listy wyjściowej gotową listę tymczasową
                    output.setNoteList(temp.getNoteList());
                }
            }


            //Jeśli zarządzano sortowania według etykiety
            case BY_LABEL -> {

                /* Stwórz tablicę wartości String o długości
                listy tymczasowej i przypisz im etykiety notatek */
                String[] labels = new String[temp.getListLength()];
                for (int i = 0; i < temp.getListLength(); i++) {
                    labels[i] = temp.getNote(i).getLabel();
                }

                //Stwórz sekwencję etykiet z tablicy
                List<String> label_list = Arrays.asList(labels);

                //Stwórz obiekt reprezentujący region systemu i przypisz do niego region Polski
                Locale locale = Locale.of("pl");

                /* Stwórz obiekt zawierający narzędzia do porównywania ciągów znaków na podstawie regionu systemu */
                Collator collator = Collator.getInstance(locale);

                //Ustaw siłę porównywania na wartość drugą (uwzględnia akcenty - "ogonki")
                collator.setStrength(Collator.SECONDARY);

                //Posortuj sekwencję etykiet, wykorzystując wcześniej utworzony kolator
                label_list.sort(collator);

                //Dla każdej etykiety w tablicy
                for (int i = 0; i < labels.length; i++) {

                    //Dla każdej notatki na liście tymczasowej
                    for (int j = 0; j < temp.getListLength(); j++) {

                        //Jeśli etykieta w notatce równa się etykiecie w tablicy
                        if (Objects.equals(temp.getNote(j).getLabel(), label_list.get(i))) {

                            //Na pozycji etykiety w liście wyjściowej zapisz notatkę o indeksie j
                            output.setNote(temp.getNote(j), i);

                            //Usuń notatkę z listy tymczasowej pod indeksem j
                            temp.removeNote(j);
                        }
                    }
                }

                //Jeśli notatki mają zostać posortowane w kolejności rosnącej
                if (!descending) {

                    //Stwórz sekwencję notatek z tablicy notatek, uzyskanej z listy wyjściowej
                    List<Note> output_list = Arrays.asList(output.getNoteList());

                    //Dokonaj odwrócenia kolejności elementów w sekwencji
                    Collections.reverse(output_list);

                    //Zwróć z powrotem do listy wyjściowej listę notatek z sekwencji notatek
                    output.setNoteList(output_list.toArray(new Note[0]));
                }
            }


            //Jeśli zażądano posortowania na podstawie stanu ukończenia notatek
            case BY_COMPLETION -> {

                //Ustaw listę wyjściową jako listę zeroelementową.
                output.setNoteList(new Note[0]);

                //Zadeklaruj sekwencje Notatek typu NOTE i typu TODO-NOTE
                List<Note> notes_list = new ArrayList<>();
                List<ToDoNote> todo_notes_list = new ArrayList<>();

                //Dla każde notatki na liście tymczasowej
                for (int i = 0; i < temp.getListLength(); i++) {

                    //Jeśli notatka pod indeksem i ma typ TODO-NOTE
                    if (temp.getNote(i).getType() == Note.TODO_NOTE) {

                        //Dodaj notatkę do sekwencji notatek z listą zadań
                        todo_notes_list.add((ToDoNote) temp.getNote(i));
                    } else {

                        //Jeśli nie, dodaj notatkę do sekwencji notatek
                        notes_list.add(temp.getNote(i));
                    }
                }

                //Pobierz długość sekwencji notatek z listą zadań
                int list_size = todo_notes_list.size();

                //Dla każdej notatki z sekwencji notatek TODO-NOTE
                for (int i = 0; i < list_size; i++) {

                    //Zdefiniuj maksymalną wartość proporcji zadań ukończonych do ilości wszystkich zadań w notatce
                    float max_value = 0f;

                    //Zdefiniuj indeks, pod którym ta wartość się znajduje
                    int max_value_at = 0;

                    //Dla każdej notatki z sekwencji notatek TODO-NOTE
                    for (int j = 0; j < todo_notes_list.size(); j++) {

                        //Pobierz ilość zadań w notatce
                        int todos_count = todo_notes_list.get(j).getTodo().length;

                        //Zdefiniuj licznik ukończonych zadań
                        int todo_completed_count = 0;

                        //Dla każdego zadania na liście zadań w notatce
                        for (int k = 0; k < todo_notes_list.get(j).getChecked().length; k++) {

                            //Jeśli jest zadanie odhaczone, zwiększ licznik
                            if (todo_notes_list.get(j).getChecked(k)) todo_completed_count++;
                        }

                        //Zinicjalizuj wartość proporcji ukończenia zadań
                        float todo_completion = (float) (todo_completed_count) / (float) (todos_count);

                        //Jeśli wartość proporcji jest wyższa od obecnej wartości maksymalnej
                        if (todo_completion > max_value) {

                            //Przypisz do wartości maksymalnej wartość proporcji
                            max_value = todo_completion;

                            //Przypisz do indeksu maksymalnej wartości indeks j
                            max_value_at = j;
                        }
                    }

                    /* Pod indeksem najwyższej wartości proporcji na liście wyjściowej
                    zapisz notatkę TODO-NOTE z sekwencji pod indeksem najwyższej wartości proporcji */
                    output.addNote(todo_notes_list.get(max_value_at));

                    //Usuń z sekwencji notatkę pod indeksem najwyższej wartości proporcji
                    todo_notes_list.remove(max_value_at);
                }

                //Stwórz tablicę notatek z sekwencji notatek i dodaj tę tablicę do listy wyjściowej
                Note[] notes_array;
                notes_array = notes_list.toArray(new Note[0]);
                output.addNote(notes_array);

                //Jeśli notatki mają zostać posortowane w kolejności rosnącej
                if (!descending) {

                    //Stwórz sekwencję notatek z tablicy notatek, uzyskanej z listy wyjściowej
                    List<Note> output_list = Arrays.asList(output.getNoteList());

                    //Dokonaj odwrócenia kolejności elementów w sekwencji
                    Collections.reverse(output_list);

                    //Zwróć z powrotem do listy wyjściowej listę notatek z sekwencji notatek
                    output.setNoteList(output_list.toArray(new Note[0]));
                }

            }


            //Jeśli zażądano posortowania notatek według typu notatki
            case BY_TYPE -> {

                //Zadeklaruj sekwencje notatek obydwu typów
                List<Note> notes_list = new ArrayList<>();
                List<ToDoNote> todo_notes_list = new ArrayList<>();

                //Dla każdej notatki na liście tymczasowej
                for (int i = 0; i < temp.getListLength(); i++) {

                    //Jeśli notatka pod indeksem i ma typ TODO-NOTE
                    if (temp.getNote(i).getType() == Note.TODO_NOTE) {

                        /* Dodaj do sekwencji notatek z listą zadań
                        notatkę pod indeksem i */
                        todo_notes_list.add((ToDoNote) temp.getNote(i));
                    }

                    //Jeśli nie, dodaj do sekwencji notatek notatkę pod indeksem i
                    else notes_list.add(temp.getNote(i));
                }

                //Stwórz tablicę notatek i umieść w niej notatki z sekwencji
                Note[] notes_array;
                notes_array = notes_list.toArray(new Note[0]);

                //Stwórz tablicę notatek z listą zadań i umieść w niej notatki z sekwencji
                ToDoNote[] todo_notes_array;
                todo_notes_array = todo_notes_list.toArray(new ToDoNote[0]);

                //Ustaw listę notatek w liście tymczasowej tak, aby miała długość 0
                output.setNoteList(new Note[0]);

                //Dodaj do listy wyjściowej tablice notatek w kolejności: notatki TODO-NOTE, notatki NOTE
                output.addNote(todo_notes_array);
                output.addNote(notes_array);

                //Jeśli notatki mają zostać posortowane w kolejności rosnącej
                if (!descending) {

                    //Stwórz sekwencję notatek z tablicy notatek, uzyskanej z listy wyjściowej
                    List<Note> output_list = Arrays.asList(output.getNoteList());

                    //Dokonaj odwrócenia kolejności elementów w sekwencji
                    Collections.reverse(output_list);

                    //Zwróć z powrotem do listy wyjściowej listę notatek z sekwencji notatek
                    output.setNoteList(output_list.toArray(new Note[0]));
                }


            }
        }

        //Ustaw listę notatek przechowywaną w obiekcie, pobierając dane z listy wyjściowej
        this.setNoteList(output.getNoteList());
    }


    /**
     * Porównuje dwie listy notatek ze sobą.
     * @param first_list Pierwsza lista notatek do porównania.
     * @param second_list Druga lista notatek do porównania.
     * @return Wartość true, jeśli listy są takie same. Wartość false, jeśli listy się różnią.
     */
    public static boolean areNoteListsEqual(NoteList first_list, NoteList second_list){

        //Jeśli obydwie listy są puste, zwróć wartość true
        if(first_list == null && second_list == null) return true;

        //Jeśli tylko jedna z list jest pusta, zwróć wartość false
        if(first_list != second_list &&
               (first_list == null || second_list == null)) return false;

        //Jeśli długości list się różnią, zwróć wartość false
        if(first_list.getListLength() != second_list.getListLength()) return false;

        //Dla każdej notatki
        for(int i = 0; i < first_list.getListLength(); i++){

            //Jeśli którekolwiek z danych są różne pomiędzy notatkami z obydwu list, zwróć wartość false
            if(!Objects.equals(first_list.getNote(i).getLabel(), second_list.getNote(i).getLabel())) return false;
            if(!Objects.equals(first_list.getNote(i).getText(), second_list.getNote(i).getText())) return false;
            if(!Objects.equals(first_list.getNote(i).getHidden(), second_list.getNote(i).getHidden())) return false;
            if(!Objects.equals(first_list.getNote(i).getCreate_date(), second_list.getNote(i).getCreate_date())) return false;
            if(!Objects.equals(first_list.getNote(i).getMod_date(), second_list.getNote(i).getMod_date())) return false;

            //Jeśli typy notatek z obydwu list się różnią, zwróć wartość false
            if(first_list.getNote(i).getType() != second_list.getNote(i).getType()) return false;

            //Jeśli typem notatki jest TODO_NOTE
            if(Objects.equals(first_list.getNote(i).getType(), Note.TODO_NOTE)) {

                //Jeśli długości tablic przechowujących treści zadań oraz stany odhaczenia zadań notatek z obydwu list są różne, zwróć wartość false
                if (
                        ((ToDoNote) first_list.getNote(i)).getTodo().length != ((ToDoNote) second_list.getNote(i)).getTodo().length ||
                        ((ToDoNote) first_list.getNote(i)).getChecked().length != ((ToDoNote) second_list.getNote(i)).getChecked().length
                ) {
                    return false;

                //Jeśli są tej samej długości
                } else {

                    //Dla każdego zadania na liście zadań
                    for (int j = 0; j < ((ToDoNote) first_list.getNote(i)).getTodo().length; j++) {

                        //Jeśli treści zadań lub ich stany odhaczenia w notatkach z obydwu list się różnią, zwróć wartość false
                        if (
                                !Objects.equals(((ToDoNote) first_list.getNote(i)).getTodo(j), ((ToDoNote) second_list.getNote(i)).getTodo(j)) ||
                                ((ToDoNote) first_list.getNote(i)).getChecked(j) != ((ToDoNote) second_list.getNote(i)).getChecked(j)
                        ) return false;
                    }
                }
            }
        }

        //Jeśli mimo wszystko kod dotarł do tej linijki bez zwracania żadnej wartości, listy muszą być równe.
        //Zwróć wartość true
        return true;
    }

    /**
     * Konstruktor domyślny. Tworzy nową, pustą listę notatek.
     */
    NoteList(){
        this.setNoteList(
                new Note[0]
        );
    }

    /**
     * Konstruktor parametryczny. Tworzy nową listę notatek opartą o podany zbiór notatek.
     * @param noteList Zbiór notatek.
     * @param list_mode Tryb przypisania listy. (Pełna = 0, tylko ukryte = 1, tylko jawne = 2)
     */
    NoteList(Note[] noteList, int list_mode) {

        //Jeśli trybem przypisania listy jest tryb notatek ukrytych
        if (list_mode == HIDDEN) {

            //Zdefiniuj licznik notatek ukrytych
            int hidden_count = 0;

            //Dla każdej notatki na liście z parametrów konstruktora
            for (Note value : noteList) {

                //Jeśli notatka jest ukryta
                if (value.getHidden()) {

                    //Zwiększ licznik
                    hidden_count++;
                }
            }

            /* Zadeklaruj nową tablicę notatek o długości
            równej wartości licznika notatek ukrytych. */
            Note[] hidden = new Note[hidden_count];

            //Zdefiniuj licznik
            int cnt = 0;

            //Dla każdej notatki na liście z parametrów konstruktora
            for (Note note : noteList) {

                //Jeśli notatka jest ukryta
                if (note.getHidden()) {

                    //Przypisz do tablicy notatek pod indeksem równym wartości licznika notatkę z pętli
                    hidden[cnt] = note;

                    //Zwiększ licznik
                    cnt++;
                }
            }

            //Przypisz do listy notatek w obiekcie wartość uzyskanej tablicy
            this.setNoteList(hidden);

        //Jeśli trybem przypisania listy jest tryb publicznego przypisania
        } else if(list_mode == PUBLIC){

            //Zdefiniuj licznik notatek jawnych
            int public_count = 0;

            //Dla każdej notatki na liście z parametrów konstruktora
            for (Note note : noteList) {

                //Jeśli notatka jest jawna
                if (!note.getHidden()) {

                    //Zwiększ wartość licznika
                    public_count++;
                }
            }

            //Zadeklaruj nową tablicę notatek o długości równej wartości licznika
            Note[] public_list = new Note[public_count];

            //Zadeklaruj licznik
            int cnt = 0;

            //Dla każdej notatki na liście notatek z parametrów konstruktora
            for (Note note : noteList) {

                //Jeśli notatka jest jawna
                if (!note.getHidden()) {

                    //Przypisz do tablicy notatek pod indeksem równym wartości licznika notatkę z pętli
                    public_list[cnt] = note;

                    //Zwiększ wartość licznika
                    cnt++;
                }
            }

            //Przypisz do listy notatek przechowywanej w obiekcie wartość tablicy notatek
            this.setNoteList(public_list);

        //Jeśli trybem przypisania jest tryb pełnego przypisania
        } else if(list_mode == FULL){

            //Przypisz listę z parametrów konstruktora do listy notatek w obiekcie.
            this.setNoteList(noteList);
        }
    }
}
