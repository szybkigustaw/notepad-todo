import java.text.Collator;
import java.util.*;

/**
 * Klasa reprezentująca listę notatek. Przyjmuje obiekty klasy Note i jej subklas.
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class NoteList {
    static final int FULL = 0;
    static final int HIDDEN = 1;
    static final int PUBLIC = 2;

    static final int BY_MOD_DATE = 0;
    static final int BY_CREATE_DATE = 1;
    static final int BY_LABEL = 2;
    static final int BY_COMPLETION = 3;
    static final int BY_TYPE = 4;

    private Note[] noteList;

    /**
     * Metoda zwracająca listę notatek.
     * @return Lista notatek.
     */
    public Note[] getNoteList(){
        return this.noteList;
    }

    /**
     * Metoda zwracająca pojedyńczą notatkę z listy.
     * @param index Pozycja notatki na liście.
     * @return Notatka z listy notatek.
     */
    public Note getNote(int index){
        return this.noteList[index];
    }

    /**
     * Metoda zwracająca długość listy notatek.
     * @return Długość listy notatek.
     */
    public int getListLength(){
        return this.getNoteList().length;
    }

    /**
     * Metoda przypisująca nową listę notatek.
     * @param noteList Nowa lista notatek.
     */
    public void setNoteList(Note[] noteList){
        this.noteList = noteList;
    }

    /**
     * Metoda przypisująca nową wartość do wyznaczonej notatki.
     * @param note Nowa notatka
     * @param index Pozycja na liście
     */
    public void setNote(Note note, int index){
        this.noteList[index] = note;
    }

    /**
     * Metoda poszerzająca listę notatek o podaną ilość miejsc
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
     * Metoda zwracająca pozycję notatki na liście. Jeśli notatka nie znajduje się na liście, metoda zwraca wartość ujemną.
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
     * Metoda dodająca do listy notatek nową notatkę. Zawsze dodawana jest na koniec listy.
     * @param note Notatka do dodania na listę.
     */
    public void addNote(Note note){
        int curLength = this.getNoteList().length;
        this.expandNoteList(1);
        System.arraycopy(new Note[]{note}, 0, this.getNoteList(), curLength, 1);
    }

    /**
     * Metoda dodająca do listy notatek nowe notatki. Zawsze dodawane są na koniec listy.
     * @param notes Lista notatek do dodania.
     */
    public void addNote(Note[] notes){
        int curLength = this.getNoteList().length;
        this.expandNoteList(notes.length);
        System.arraycopy(notes, 0, this.getNoteList(), curLength, notes.length);
    }

    /**
     * Metoda kasująca notatki na wyznaczonych pozycjach listy.
     * @param indexes Lista pozycji, na których znajdują się notatki do skasowania.
     */
    public void removeNote(int[] indexes){
        Note[] newList = new Note[this.getNoteList().length - indexes.length];
        Integer[] acceptedIndexes = new Integer[this.getNoteList().length - indexes.length];
        int cnt = 0;
        for(int i = 0; i < this.getNoteList().length; i++){
            boolean isAccepted = true;
            for(int j = 0; j < indexes.length; j++){
                if(i == indexes[j]){
                    isAccepted = false;
                    break;
                }
                if(isAccepted){
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
     * Metoda kasująca notatkę znajdującą się na wyznaczonym miejscu listy.
     * @param index Numer pozycji notatki na liście.
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

    public void sortNote(int sort_mode, boolean descending) {
        NoteList temp = new NoteList(this.noteList, FULL);
        NoteList output = new NoteList();
        output.setNoteList(new Note[temp.getListLength()]);

        switch (sort_mode) {
            case BY_MOD_DATE: {
                for (int i = 0; i < temp.getListLength(); i++) {
                    for (int j = 0; j < temp.getListLength(); j++) {
                        if (descending && temp.getNote(i).getMod_date().getTime() < temp.getNote(j).getMod_date().getTime()) {
                            Note temp_note_less, temp_note_more;
                            temp_note_less = temp.getNote(i);
                            temp_note_more = temp.getNote(j);
                            temp.setNote(temp_note_less, j);
                            temp.setNote(temp_note_more, i);
                        } else if (!descending && temp.getNote(i).getMod_date().getTime() > temp.getNote(j).getMod_date().getTime()) {
                            Note temp_note_less, temp_note_more;
                            temp_note_less = temp.getNote(j);
                            temp_note_more = temp.getNote(i);
                            temp.setNote(temp_note_less, i);
                            temp.setNote(temp_note_more, j);
                        }
                    }

                    output.setNoteList(temp.getNoteList());
                }
            } break;
            case BY_CREATE_DATE: {
                for (int i = 0; i < temp.getListLength(); i++) {
                    for (int j = 0; j < temp.getListLength(); j++) {
                        if (descending && temp.getNote(i).getCreate_date().getTime() < temp.getNote(j).getCreate_date().getTime()) {
                            Note temp_note_less, temp_note_more;
                            temp_note_less = temp.getNote(i);
                            temp_note_more = temp.getNote(j);
                            temp.setNote(temp_note_less, j);
                            temp.setNote(temp_note_more, i);
                        } else if (!descending && temp.getNote(i).getCreate_date().getTime() > temp.getNote(j).getCreate_date().getTime()) {
                            Note temp_note_less, temp_note_more;
                            temp_note_less = temp.getNote(j);
                            temp_note_more = temp.getNote(i);
                            temp.setNote(temp_note_less, i);
                            temp.setNote(temp_note_more, j);
                        }
                    }
                }

                    output.setNoteList(temp.getNoteList());
                } break;
                case BY_LABEL: {
                    String[] labels = new String[temp.getListLength()];
                    for (int i = 0; i < temp.getListLength(); i++) {
                        labels[i] = temp.getNote(i).getLabel();
                    }

                    List<String> label_list = Arrays.asList(labels);

                    Locale locale = Locale.of("pl");
                    Collator collator = Collator.getInstance(locale);
                    collator.setStrength(Collator.SECONDARY);

                    Collections.sort(label_list, collator);

                    for (int i = 0; i < labels.length; i++) {
                        for (int j = 0; j < temp.getListLength(); j++) {
                            if (temp.getNote(j).getLabel() == label_list.get(i)) {
                                output.setNote(temp.getNote(j), i);
                                temp.removeNote(j);
                            }
                        }
                    }

                    if(!descending){
                        List<Note> output_list = Arrays.asList(output.getNoteList());
                        Collections.reverse(output_list);
                        output.setNoteList(output_list.toArray(new Note[0]));
                    }
                }
                break;
                case BY_COMPLETION: {
                    output.setNoteList(new Note[0]);
                    List<Note> notes_list = new ArrayList<>();
                    List<ToDoNote> todo_notes_list = new ArrayList<>();

                    for(int i = 0; i < temp.getListLength(); i++){
                        if(temp.getNote(i).getType() == Note.TODO_NOTE){
                            todo_notes_list.add((ToDoNote)temp.getNote(i));
                        } else {
                            notes_list.add(temp.getNote(i));
                        }
                    }

                    int list_size = todo_notes_list.size();

                    for(int i = 0; i < list_size; i++){
                        float max_value = 0f;
                        int max_value_at = 0;
                        for(int j = 0; j < todo_notes_list.size(); j++){
                            int todos_count = todo_notes_list.get(j).getTodo().length;
                            int todo_completed_count = 0;
                            for(int k = 0; k < todo_notes_list.get(j).getChecked().length; k++){
                                if(todo_notes_list.get(j).getChecked(k)) todo_completed_count++;
                            }
                            System.out.println(todos_count);
                            System.out.println(todo_completed_count);

                            float todo_completion = (float)(todo_completed_count) / (float)(todos_count);
                            System.out.println(todo_completion);
                            if(todo_completion > max_value){
                                max_value = todo_completion;
                                max_value_at = j;
                            }
                        }
                        System.out.println(todo_notes_list.get(max_value_at));
                        output.addNote(todo_notes_list.get(max_value_at));
                        todo_notes_list.remove(max_value_at);
                    }

                    Note[] notes_array;
                    notes_array = notes_list.toArray(new Note[0]);
                    output.addNote(notes_array);

                    if(!descending){
                        List<Note> output_list = Arrays.asList(output.getNoteList());
                        Collections.reverse(output_list);
                        output.setNoteList(output_list.toArray(new Note[0]));
                    }

                    for(Note note : output.getNoteList()){
                        note.showNote();
                    }
                }
                break;
                case BY_TYPE: {
                    List<Note> notes_list = new ArrayList<>();
                    List<ToDoNote> todo_notes_list = new ArrayList<>();

                    for(int i = 0; i < temp.getListLength(); i++){
                        if(temp.getNote(i).getType() == Note.TODO_NOTE){
                            todo_notes_list.add((ToDoNote) temp.getNote(i));
                        } else notes_list.add(temp.getNote(i));
                    }

                    Note[] notes_array;
                    notes_array = notes_list.toArray(new Note[0]);

                    ToDoNote[] todo_notes_array;
                    todo_notes_array = todo_notes_list.toArray(new ToDoNote[0]);

                    output.setNoteList(new Note[0]);
                    output.addNote(todo_notes_array);
                    output.addNote(notes_array);

                    if(!descending){
                        List<Note> output_list = Arrays.asList(output.getNoteList());
                        Collections.reverse(output_list);
                        output.setNoteList(output_list.toArray(new Note[0]));
                    }


                }
                break;
            }
            this.setNoteList(output.getNoteList());
        }

        public static boolean areNoteListsEqual(NoteList first_list, NoteList second_list){
            if(first_list == null && second_list == null) return true;
            if(first_list != second_list &&
               (first_list == null || second_list == null)) return false;
            if(first_list.getListLength() != second_list.getListLength()) return false;
            for(int i = 0; i < first_list.getListLength(); i++){
                if(!Objects.equals(first_list.getNote(i).getLabel(), second_list.getNote(i).getLabel())) return false;
                if(!Objects.equals(first_list.getNote(i).getText(), second_list.getNote(i).getText())) return false;
                if(!Objects.equals(first_list.getNote(i).getHidden(), second_list.getNote(i).getHidden())) return false;
                if(!Objects.equals(first_list.getNote(i).getCreate_date(), second_list.getNote(i).getCreate_date())) return false;
                if(!Objects.equals(first_list.getNote(i).getMod_date(), second_list.getNote(i).getMod_date())) return false;
                if(first_list.getNote(i).getType() != second_list.getNote(i).getType()) return false;
                if(Objects.equals(first_list.getNote(i).getType(), Note.TODO_NOTE)) {
                    if (
                            ((ToDoNote) first_list.getNote(i)).getTodo().length != ((ToDoNote) second_list.getNote(i)).getTodo().length ||
                                    ((ToDoNote) first_list.getNote(i)).getChecked().length != ((ToDoNote) second_list.getNote(i)).getChecked().length
                    ) {
                            return false;
                    } else {
                        for (int j = 0; j < ((ToDoNote) first_list.getNote(i)).getTodo().length; j++) {
                            if (
                                    !Objects.equals(((ToDoNote) first_list.getNote(i)).getTodo(j), ((ToDoNote) second_list.getNote(i)).getTodo(j)) ||
                                            ((ToDoNote) first_list.getNote(i)).getChecked(j) != ((ToDoNote) second_list.getNote(i)).getChecked(j)
                            ) return false;
                        }
                    }
                }
            }
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
        if (list_mode == HIDDEN) {
            int hidden_count = 0;
            for (int i = 0; i < noteList.length; i++) {
                if (noteList[i].getHidden()) {
                    hidden_count++;
                }
            }

            Note[] hidden = new Note[hidden_count];
            int cnt = 0;
            for (int i = 0; i < noteList.length; i++) {
                if (noteList[i].getHidden()) {
                    hidden[cnt] = noteList[i];
                    cnt++;
                }
            }

            this.setNoteList(hidden);
        } else if(list_mode == PUBLIC){
            int public_count = 0;
            for (int i = 0; i < noteList.length; i++) {
                if (!noteList[i].getHidden()) {
                    public_count++;
                }
            }

            Note[] public_list = new Note[public_count];
            int cnt = 0;
            for (int i = 0; i < noteList.length; i++) {
                if (!noteList[i].getHidden()) {
                    public_list[cnt] = noteList[i];
                    cnt++;
                }
            }

            this.setNoteList(public_list);
        } else if(list_mode == FULL){
            this.setNoteList(noteList);
        }
    }
}
