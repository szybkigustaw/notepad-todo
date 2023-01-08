import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Objects;
import static java.awt.GridBagConstraints.RELATIVE;

/**
 * Okno edycji notatki.
 * <p>Umożliwia zmianę etykiety oraz treści notatki.</p>
 * <p>Umożliwia również dodanie jak i usunięcie listy zadań, a także modyfikację jej elementów.</p>
 * <p>Z poziomu tego okna można również ukryć i ujawnić notatkę. Do działania tej funkcji wymagane jest ustawione hasło dostępowe.</p>
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class EditNote extends JPanel {
    /**
     * Notatka przechowywana w tym obiekcie
     */
    private final ToDoNote note;
    /**
     * Notatka przechowywana w tym obiekcie. Stanowi punkt odniesienia przy sprawdzaniu dokonanych zmian w notatce.
     */
    private final ToDoNote read_note;
    /**
     * Wartość domyślna treści zadania
     */
    private final String DEFAULT_TODO = "Zjedz psa (Wprowadź coś lepszego)";
    /**
     * Wartość określająca, czy notatka przechowywana w tym obiekcie posiada listę zadań
     */
    private boolean todo_note;
    private final int note_index;
    /**
     * Ciąg znaków określający poprzednie wyświetlane okno
     */
    private final String prev_window;

    /**
     * Zapisuje notatkę mimo nieukończonego procesu edytowania jej przez użytkownika.
     */
    public void forceSave(){

        //Jeśli notatka ma swój indeks na liście
        if(this.note_index != -1){

            //Nadpisz notatkę pod tym indeksem na liście
            Main.noteList.setNote(this.note, this.note_index);
        }

        //Jeśli nie
        else {

            //Dodaj nową notatkę
            Main.noteList.addNote(this.note);
        }

        //Przełącz okno na poprzednie okno
        Main.current_window = prev_window;

        //Przeładuj aplikację
        Main.reloadApp(true);

        //Wyzeruj wartość okna edycji notatek
        Main.en = null;
    }

    /**
     * Kasuje okno edycji notatki mimo niezakończonego procesu edycji notatki.
     */
    public void forceDelete(){

        //Wyświetl poprzednie okno, przeładuj aplikację i skasuj okno edycji notatki
        Main.current_window = prev_window;
        Main.reloadApp(true);
        Main.en = null;
    }

    /**
     * Sprawdza, czy notatka została edytowana.
     * @return Wartość <i>true</i>, jeśli została edytowana.
     */
    public boolean hasNoteChanged(){
        System.out.println(ToDoNote.areNotesEqual(this.read_note, this.note));
        return !ToDoNote.areNotesEqual(this.read_note, this.note);
    }

    /**
     * Tworzy graficzną reprezentację pojedynczego zadania na liście w postaci panelu z treścią zadania i checkbox-em definiującym stan odhaczenia zadania.
     * <p>W panelu tym możliwa jest edycja treści zadania. Jeśli nic nie zostało wprowadzone, stosowana zostaje wartość {@link #DEFAULT_TODO}</p>
     * <p>Powstałe wartości stanu odhaczenia oraz treści zadania są wartościami domyślnymi.</p>
     * @param index Indeks zadania na liście
     * @return Gotowy panel
     */
    private JPanel createTodo(int index){
        this.note.setType(Note.TODO_NOTE);

        //Stwórz układu oraz wartości modelowe
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Stwórz ciało — kontener przechowujący resztę elementów
        JPanel item = new JPanel(layout);
        item.setSize(1100, 60);

        //Stwórz combo-boxa - "ptaszka" symbolizującego stan odhaczenia zadania.
        JCheckBox cb = new JCheckBox("",false);

        //Domyślne ustawienie zadania — fałsz
        note.setChecked(false, index);

        //Ustaw rozmiar combo-boxa
        cb.setSize(50, 50);


        //Dodaj logikę — reakcja na przełączenie "ptaszka"
        cb.addActionListener(e -> {
            note.setChecked(cb.isSelected(), index); //Ustaw stan odhaczenia zadania na stan zaznaczenia combo-boxa
        });

        //Wstaw combo-boxa do kontenera.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        item.add(cb, gbc);

        //Stwórz etykietę przechowującą treść zadania.
        JTextField text = new JTextField();
        text.setText(this.DEFAULT_TODO); //Nadaj jej wartość domyślną
        text.setFont(new Font("Arial", Font.PLAIN, 20));
        text.setHorizontalAlignment(JTextField.CENTER);
        text.setEditable(true); //Ustaw ją na edytowalną

        //Dodaj logikę do etykiety — reakcję na wciśnięcie myszy
        text.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(text.getText().equals(DEFAULT_TODO)) text.setText(""); //Jeśli tekst w etykiecie jest równy wartości domyślnej, ustaw ją na ciąg pusty
            }

            //Zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do etykiety — reakcję na zmianę stanu kursora tekstowego
        text.addCaretListener(e -> {

            //Jeśli treść zadania będzie równa pustemu ciągowi, ustaw treść zadania na domyślny tekst/
            if(Objects.equals(text.getText(), "")) note.setTodo("Sample todo", index);
            else note.setTodo(text.getText(), index); //Jeśli nie, przypisz ją do treści zadania w przechowywanej w obiekcie notatce
        });

        //Wstaw etykietę do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        item.add(text, gbc);

        //Zwróć gotowy kontener.
        return item;
    }

    /**
     * Tworzy graficzną reprezentację pojedynczego zadania na liście w postaci panelu z treścią zadania i checkbox-em definiującym stan odhaczenia zadania.
     * <p>W panelu tym możliwa jest edycja treści zadania. Jeśli nic nie zostało wprowadzone, stosowana zostaje wartość {@link #DEFAULT_TODO}</p>
     * <p>Do zbudowania panelu wykorzystuje dane podane w parametrach.</p>
     * @param index Indeks zadania na liście
     * @param todo Treść zadania
     * @param checked_state Stan odhaczenia zadania
     * @return Gotowy panel
     */
    private JPanel createTodo(String todo, boolean checked_state, int index){

        //Ustaw typ notatki przechowywanej w obiekcie na TODO-MOTE
        this.note.setType(Note.TODO_NOTE);

        //Stwórz układ oraz wartości modelowe
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Stwórz ciało — kontener przechowujący resztę elementów
        JPanel item = new JPanel(layout);
        item.setSize(1100, 60);

        //Stwórz combo-boxa - "ptaszka" symbolizującego stan odhaczenia zadania.
        JCheckBox cb = new JCheckBox("",checked_state);
        cb.setSize(50, 50);

        //Dodaj logikę — reakcja na przełączenie "ptaszka"
        cb.addActionListener(e -> note.setChecked(cb.isSelected(), index)); //Przestaw stan odhaczenia zadania na stan zaznaczenia combo-boxa

        //Wstaw combo-boxa do kontenera.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        item.add(cb, gbc);

        //Stwórz etykietę przechowującą treść zadania.
        JTextField text = new JTextField();
        text.setText(todo); //Ustaw jej treść na treść notatki podaną w parametrze
        text.setFont(new Font("Arial", Font.PLAIN, 20));
        text.setHorizontalAlignment(JTextField.CENTER);
        text.setEditable(true); //Ustaw etykietę na edytowalną

        //Dodaj logikę do etykiety — reakcję na przycisk myszy
        text.addMouseListener(new MouseListener() {
            @Override
            //Jeśli treść etykiety jest równa domyślnej treści zadania, ustaw ją na pustą
            public void mouseClicked(MouseEvent e) {
                if(text.getText().equals(DEFAULT_TODO)) text.setText("");
            }

            //Zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę etykiety — reakcję na zmianę stanu kursora tekstowego
        text.addCaretListener(e -> {

            //Jeśli treść etykiety jest równa pustemu ciągowi, ustaw treść zadania na domyślny tekst
            if(Objects.equals(text.getText(), "")) note.setTodo("Sample todo", index);

            //Jeśli jest inna, ustaw treść zadania na tę treść
            else note.setTodo(text.getText(), index);
        });

        //Wstaw etykietę do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        item.add(text, gbc);

        //Zwróć gotowy kontener
        return item;
    }

    /**
     * Konstruktor domyślny. Tworzy nowe okno edycji notatki, tworząc jednocześnie nową notatkę i umieszczając ją w obiekcie.
     */
    EditNote(){

        //Pobierz ciąg znaków reprezentujący obecnie wyświetlane okno i przypisz je do obiektu
        prev_window = Main.current_window;

        //Ustaw stan obecnego okna na okno dodania notatki.
        Main.current_window = "EditNote";

        this.note_index = -1;

        //Stwórz nową notatkę, przypisz jej typ NOTE oraz przypisz tablicom określającym stan zadań zerową długość
        note = new ToDoNote();
        note.setType(Note.NOTE);
        this.note.setTodo(new String[0]);
        this.note.setChecked(new boolean[0]);

        this.read_note = new ToDoNote(this.note.getLabel(), this.note.getText(), this.note.getTodo(), this.note.getChecked(), this.note.getHidden());
        this.read_note.setMod_date(this.note.getMod_date());
        this.read_note.setCreate_date(this.note.getCreate_date());
        this.read_note.setCompleted(this.note.getCompleted());

        //Zdefiniuj wartości domyślne pól tekstowych
        String DEFAULT_LABEL = "Wprowadź etykietę notatki.";
        String DEFAULT_TEXT = "Lorem ipsum blablabla. \n Wprowadź coś lepszego";

        //Stwórz układ oraz wartości modelowe
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        //Ustaw układ oraz rozmiar okna
        setLayout(layout);
        setSize(1150, 750);

        //Stwórz etykietę
        JTextField label = new JTextField();
        label.setText(DEFAULT_LABEL); //Przypisz do jej wartości domyślną etykietę
        label.setHorizontalAlignment(JTextField.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        label.setBackground(Main.rp.getBackground());
        label.setEditable(true); //Ustaw ją na edytowalną

        //Dodaj logikę do etykiety — reakcję na klawisz myszy
        label.addMouseListener(new MouseListener() {
            @Override
            //Ustaw treść etykiety na ciąg pusty, jeśli wartość jest równa wartości domyślnej
            public void mouseClicked(MouseEvent e) {
                if(Objects.equals(label.getText(), DEFAULT_LABEL)) label.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj reakcję na zmianę stanu kursora tekstowego w etykiecie — ustaw wartość etykiety notatki na wartość etykiety
        label.addCaretListener(e -> note.setLabel(label.getText()));

        //Wstaw etykietę do kontenera
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridwidth = RELATIVE;
        gbc.gridy = 0;
        add(label, gbc);

        //Stwórz pole tekstowe
        JTextArea text = new JTextArea( 60, 50);
        text.setText(DEFAULT_TEXT); //Przypisz do niego domyślną treść
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        if(!todo_note) text.setSize(1100, 512); //Jeśli jest notatka typu NOTE, jej pole tekstowe ma być większe
        else text.setSize(1100, 128);
        text.setEditable(true); //Ustaw pole na edytowalne

        //Dodaj logikę do pola tekstowego — reakcję na klawisz myszy
        text.addMouseListener(new MouseListener() {
            @Override
            //Ustaw treść pola tekstowego na ciąg pusty, jeśli wartość pola tekstowego równa się tekstowi domyślnemu
            public void mouseClicked(MouseEvent e) {
                if(Objects.equals(text.getText(), DEFAULT_TEXT)) text.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do stanu kursora pola tekstowego — ustaw treść notatki na treść pola tekstowego
        text.addCaretListener(e -> note.setText(text.getText()));

        //Umieść pole tekstowe w "szybie" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się notatki dłuższej od wielkości pola tekstowego
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        if(!todo_note) text.setSize(1100, 512);
        else text.setSize(1100, 128);

        //Umieść szybę w kontenerze
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        //Stwórz panel z listą zadań
        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS)); //Przypisz mu układ liniowy w osi Y

        //Dodaj czarną i pustą ramkę
        todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Dodaj etykietę-przycisk odpowiedzialny za usuwanie listy notatek
        JLabel remove_todos = new JLabel("Usuń listę zadań", SwingConstants.CENTER);
        remove_todos.setFont(new Font("Arial", Font.BOLD, 20));
        remove_todos.setHorizontalAlignment(SwingConstants.CENTER);
        remove_todos.setAlignmentX(CENTER_ALIGNMENT);
        remove_todos.setBackground(new Color(246, 173, 173));
        remove_todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        //Dodaj etykietę-przycisk odpowiedzialną za dodawanie nowych zadań do listy
        JLabel alt_todo = new JLabel("Dodaj zadanie",SwingConstants.CENTER);
        alt_todo.setFont(new Font("Arial", Font.BOLD, 20));
        alt_todo.setHorizontalAlignment(SwingConstants.CENTER);
        alt_todo.setAlignmentX(CENTER_ALIGNMENT);
        alt_todo.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

        //Dodaj logikę do etykiety — kasowanie listy
        remove_todos.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //Usuń wszystkie panele z zadaniami oraz etykieto-przyciski
                todos.removeAll();

                //Ustaw typ notatki przechowywanej w obiekcie na NOTE oraz ustaw długość tablic ze stanem zadań na zero
                note.setTodo(new String[0]);
                note.setChecked(new boolean[0]);
                note.setType(Note.NOTE);

                //Umieść z powrotem w panelu przycisk dodawania zadań do listy
                todos.add(alt_todo);

                //Przeładuj aplikację
                Main.reloadApp(false);
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do etykiety-notatki — dodawanie nowych zadań
        alt_todo.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                //Jeśli zadań w notatce jest mniej niż jedna, dodaj do panelu etykietę-przycisk kasowania zadań
                if(note.getTodo().length < 1) todos.add(remove_todos);

                //Usuń etykieto-przycisk dodawania notatek
                todos.remove(alt_todo);

                //Dodaj do notatki nowe zadanie
                note.addToDo(new String[1], new boolean[1]);

                //Dodaj do panelu reprezentację graficzną zadania
                todos.add(createTodo(note.getTodo().length - 1));

                //Dodaj do panelu przycisk dodawania notatek
                todos.add(alt_todo);

                //Przeładuj aplikację
                Main.reloadApp(false);
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj etykietę-przycisk
        todos.add(alt_todo);

        //Dodaj panel z listą zadań do "szyby" z paskiem przewijania.
        JScrollPane todos_sp = new JScrollPane(todos, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        todos_sp.setSize(new Dimension(1150, 384));

        //Umieść szyb
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.weightx = 1.0;
        add(todos_sp, gbc);

        //Stwórz układ oraz wartość modelowe dla paska opcji
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();

        //Stwórz pasek opcji, ustaw jego rozmiar, kolor tła oraz ramki
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);
        option_bar.setBackground(new Color(217, 217, 217));
        option_bar.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Stwórz check-boxa odpowiedzialnego za kontrolę stanu ukrycia notatki
        JCheckBox hidden_state = new JCheckBox("Ukryj notatkę",false);
        hidden_state.addActionListener(e -> note.setHidden(hidden_state.isSelected())); //Ustaw stan ukrycia notatki na stan check-boxa

        //Umieść check-boxa w pasku opcji
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 0;
        ob_gbc.gridy = 0;
        option_bar.add(hidden_state,ob_gbc);

        //Stwórz przycisk zapisywania notatki
        JButton save = new JButton("Zapisz");
        save.setSize(200, 48);

        //Dodaj logikę do przycisku
        save.addActionListener(e -> {

            //Jeśli notatka ma stan ukryty oraz hasło jest nieustawione
            if(note.getHidden() && Main.settings.get("access_password") == null){

                //Wyświetl o tym informację i zapytaj użytkownika, czy chce ustawić hasło
                int add_password_now = JOptionPane.showConfirmDialog(Main.main_frame, "Właśnie próbujesz przypisać notatce stan ukryty. " +
                        "Jednakże obecnie hasło dostępowe nie jest ustawione.\nMoże to sprawić, iż ta notatka będzie niedostępna do czasu, aż ustawisz hasło." +
                        " Czy chcesz ustawić je teraz?", "Brak zapisanego hasła", JOptionPane.YES_NO_OPTION);

                    //Jeśli tak, wywołaj okno kontekstowe ze zmianą hasła
                    if(add_password_now == JOptionPane.YES_OPTION){
                        Main.changePassword();
                    }

                }

                this.note.setMod_date(new Date());

                //Dodaj notatkę do listy notatek
                Main.noteList.addNote(note);

                //Wróć do poprzedniego okna
                Main.current_window = prev_window;
                Main.reloadApp(true);
        });

        //Wstaw przycisk do paska
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(save,ob_gbc);

        //Stwórz przycisk anulujący edycję notatki
        JButton cancel = new JButton("Anuluj");
        cancel.setSize(200, 48);

        //Dodaj logikę do przycisku
        cancel.addActionListener(e -> {

            if(hasNoteChanged()) {
                //Wyświetl informację o możliwej utracie danych, jeśli użytkownik ją zaakceptuje, wróć do poprzedniego okna
                if (JOptionPane.showConfirmDialog(Main.rp, "Na pewno chcesz odrzucić notatkę?" +
                                " Stracisz wszystkie zapisane w niej dane.", "Anulowanie tworzenia notatki",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    Main.current_window = prev_window;
                    Main.reloadApp(false);
                }
            } else {
                Main.current_window = prev_window;
                Main.reloadApp(false);
            }
        });

        //Wstaw przycisk do paska
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 2;
        ob_gbc.gridy = 0;
        option_bar.add(cancel,ob_gbc);

        //Wstaw pasek do kontenera
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }

    /**
     * Konstruktor parametryczny. Tworzy nowe okno edycji notatki. Do notatki wewnątrz okna przypisuje dane pobrane z notatki podanej w parametrach.
     * @param loaded_note Notatka, która ma być edytowana
     * @param note_index Pozycja notatki na liście
     */
    EditNote(Note loaded_note, int note_index){

        //Pobierz ciąg znaków reprezentujący obecnie wyświetlane okno i przypisz je do obiektu
        prev_window = Main.current_window;

        //Ustaw stan obecnego okna na okno dodania notatki.
        Main.current_window = "EditNote";

        this.note_index = note_index;

        //Stwórz nową notatkę, przypisz jej typ NOTE oraz przypisz tablicom określającym stan zadań zerową długość
        this.note = new ToDoNote();
        this.note.setLabel(loaded_note.getLabel());
        this.note.setText(loaded_note.getText());
        this.note.setHidden(loaded_note.getHidden());
        this.note.setMod_date(loaded_note.getMod_date());
        this.note.setCreate_date(loaded_note.getCreate_date());
        this.note.setType(Note.NOTE);
        this.note.setTodo(new String[0]);
        this.note.setChecked(new boolean[0]);

        this.read_note = new ToDoNote(this.note.getLabel(), this.note.getText(), this.note.getTodo(), this.note.getChecked(), this.note.getHidden());
        this.read_note.setMod_date(this.note.getMod_date());
        this.read_note.setCreate_date(this.note.getCreate_date());
        this.read_note.setCompleted(this.note.getCompleted());

        //Zdefiniuj wartości domyślne pól tekstowych
        String DEFAULT_LABEL = "Wprowadź etykietę notatki.";
        String DEFAULT_TEXT = "Lorem ipsum blablabla. \n Wprowadź coś lepszego";

        //Stwórz układ oraz wartości modelowe
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        //Ustaw układ oraz rozmiar okna
        setLayout(layout);
        setSize(1150, 750);

        //Stwórz etykietę
        JTextField label = new JTextField();
        label.setText(note.getLabel()); //Przypisz do jej wartości domyślną etykietę
        label.setHorizontalAlignment(JTextField.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        label.setBackground(Main.rp.getBackground());
        label.setEditable(true); //Ustaw ją na edytowalną

        //Dodaj logikę do etykiety — reakcję na klawisz myszy
        label.addMouseListener(new MouseListener() {
            @Override
            //Ustaw treść etykiety na ciąg pusty, jeśli wartość jest równa wartości domyślnej
            public void mouseClicked(MouseEvent e) {
                if(Objects.equals(label.getText(), DEFAULT_LABEL)) label.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj reakcję na zmianę stanu kursora tekstowego w etykiecie — ustaw wartość etykiety notatki na wartość etykiety
        label.addCaretListener(e -> note.setLabel(label.getText()));

        //Wstaw etykietę do kontenera
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridwidth = RELATIVE;
        gbc.gridy = 0;
        add(label, gbc);

        //Stwórz pole tekstowe
        JTextArea text = new JTextArea( 60, 50);
        text.setText(note.getText()); //Przypisz do niego domyślną treść
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        if(!todo_note) text.setSize(1100, 512); //Jeśli jest notatka typu NOTE, jej pole tekstowe ma być większe
        else text.setSize(1100, 128);
        text.setEditable(true); //Ustaw pole na edytowalne

        //Dodaj logikę do pola tekstowego — reakcję na klawisz myszy
        text.addMouseListener(new MouseListener() {
            @Override
            //Ustaw treść pola tekstowego na ciąg pusty, jeśli wartość pola tekstowego równa się tekstowi domyślnemu
            public void mouseClicked(MouseEvent e) {
                if(Objects.equals(text.getText(), DEFAULT_TEXT)) text.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do stanu kursora pola tekstowego — ustaw treść notatki na treść pola tekstowego
        text.addCaretListener(e -> note.setText(text.getText()));

        //Umieść pole tekstowe w "szybie" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się notatki dłuższej od wielkości pola tekstowego
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        if(!todo_note) text.setSize(1100, 512);
        else text.setSize(1100, 128);

        //Umieść szybę w kontenerze
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        //Stwórz panel z listą zadań
        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS)); //Przypisz mu układ liniowy w osi Y

        //Dodaj czarną i pustą ramkę
        todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Dodaj etykietę-przycisk odpowiedzialny za usuwanie listy notatek
        JLabel remove_todos = new JLabel("Usuń listę zadań", SwingConstants.CENTER);
        remove_todos.setFont(new Font("Arial", Font.BOLD, 20));
        remove_todos.setHorizontalAlignment(SwingConstants.CENTER);
        remove_todos.setAlignmentX(CENTER_ALIGNMENT);
        remove_todos.setBackground(new Color(246, 173, 173));
        remove_todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        //Dodaj etykietę-przycisk odpowiedzialną za dodawanie nowych zadań do listy
        JLabel alt_todo = new JLabel("Dodaj zadanie",SwingConstants.CENTER);
        alt_todo.setFont(new Font("Arial", Font.BOLD, 20));
        alt_todo.setHorizontalAlignment(SwingConstants.CENTER);
        alt_todo.setAlignmentX(CENTER_ALIGNMENT);
        alt_todo.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

        //Dodaj logikę do etykiety — kasowanie listy
        remove_todos.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //Usuń wszystkie panele z zadaniami oraz etykieto-przyciski
                todos.removeAll();

                //Ustaw typ notatki przechowywanej w obiekcie na NOTE oraz ustaw długość tablic ze stanem zadań na zero
                note.setTodo(new String[0]);
                note.setChecked(new boolean[0]);
                note.setType(Note.NOTE);

                //Umieść z powrotem w panelu przycisk dodawania zadań do listy
                todos.add(alt_todo);

                //Przeładuj aplikację
                Main.reloadApp(false);
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do etykiety-notatki — dodawanie nowych zadań
        alt_todo.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //Jeśli zadań w notatce jest mniej niż jedna, dodaj do panelu etykietę-przycisk kasowania zadań
                if(note.getTodo().length < 1) todos.add(remove_todos);

                //Usuń etykieto-przycisk dodawania notatek
                todos.remove(alt_todo);

                //Dodaj do notatki nowe zadanie
                note.addToDo(new String[1], new boolean[1]);

                //Dodaj do panelu reprezentację graficzną zadania
                todos.add(createTodo(note.getTodo().length - 1));

                //Dodaj do panelu przycisk dodawania notatek
                todos.add(alt_todo);

                //Przeładuj aplikację
                Main.reloadApp(false);
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj etykietę-przycisk
        todos.add(alt_todo);

        //Dodaj panel z listą zadań do "szyby" z paskiem przewijania.
        JScrollPane todos_sp = new JScrollPane(todos, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        todos_sp.setSize(new Dimension(1150, 384));

        //Umieść szyb
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.weightx = 1.0;
        add(todos_sp, gbc);

        //Stwórz układ oraz wartość modelowe dla paska opcji
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();

        //Stwórz pasek opcji, ustaw jego rozmiar, kolor tła oraz ramki
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);
        option_bar.setBackground(new Color(217, 217, 217));
        option_bar.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Stwórz check-boxa odpowiedzialnego za kontrolę stanu ukrycia notatki
        JCheckBox hidden_state = new JCheckBox("Ukryj notatkę",false);
        hidden_state.addActionListener(e -> note.setHidden(hidden_state.isSelected())); //Ustaw stan ukrycia notatki na stan check-boxa

        //Umieść check-boxa w pasku opcji
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 0;
        ob_gbc.gridy = 0;
        option_bar.add(hidden_state,ob_gbc);

        //Stwórz przycisk zapisywania notatki
        JButton save = new JButton("Zapisz");
        save.setSize(200, 48);

        //Dodaj logikę do przycisku
        save.addActionListener(e -> {

            //Zweryfikuj wykonanie zadań, jeśli notatka jest typu TOOO_NOTE
            if(this.note.getType() == Note.TODO_NOTE) this.note.verifyToDoCompletion();

            //Jeśli notatka ma stan ukryty oraz hasło jest nieustawione
            if(note.getHidden() && Main.settings.get("access_password") == null){

                //Wyświetl o tym informację i zapytaj użytkownika, czy chce ustawić hasło
                int add_password_now = JOptionPane.showConfirmDialog(Main.main_frame, "Właśnie próbujesz przypisać notatce stan ukryty. " +
                        "Jednakże obecnie hasło dostępowe nie jest ustawione.\nMoże to sprawić, iż ta notatka będzie niedostępna do czasu, aż ustawisz hasło." +
                        " Czy chcesz ustawić je teraz?", "Brak zapisanego hasła", JOptionPane.YES_NO_OPTION);

                    //Jeśli tak, wywołaj okno kontekstowe ze zmianą hasła
                    if(add_password_now == JOptionPane.YES_OPTION){
                        Main.changePassword();
                    }
                }

                this.note.setMod_date(new Date());

                //Dodaj notatkę do listy notatek
                Main.noteList.setNote(this.note, note_index);

                //Wróć do poprzedniego okna
                Main.current_window = prev_window;
                Main.reloadApp(true);
        });

        //Wstaw przycisk do paska
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(save,ob_gbc);

        //Stwórz przycisk anulujący edycję notatki
        JButton cancel = new JButton("Anuluj");
        cancel.setSize(200, 48);

        //Dodaj logikę do przycisku
        cancel.addActionListener(e -> {

            if(hasNoteChanged()) {
                //Wyświetl informację o możliwej utracie danych, jeśli użytkownik ją zaakceptuje, wróć do poprzedniego okna
                if (JOptionPane.showConfirmDialog(Main.rp, "Na pewno chcesz odrzucić notatkę?" +
                                " Stracisz wszystkie zapisane w niej dane.", "Anulowanie tworzenia notatki",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    Main.current_window = prev_window;
                    Main.reloadApp(false);
                }
            } else {
                Main.current_window = prev_window;
                Main.reloadApp(false);
            }
        });

        //Wstaw przycisk do paska
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 2;
        ob_gbc.gridy = 0;
        option_bar.add(cancel,ob_gbc);

        //Wstaw pasek do kontenera
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }

    /**
     * Konstruktor parametryczny. Tworzy nowe okno edycji notatki. Do notatki wewnątrz okna przypisuje dane pobrane z notatki podanej w parametrach.
     * @param loaded_note Notatka, która ma być edytowana
     * @param note_index Pozycja notatki na liście
     */
    EditNote(ToDoNote loaded_note, int note_index){

        //Pobierz ciąg znaków reprezentujący obecnie wyświetlane okno i przypisz je do obiektu
        prev_window = Main.current_window;

        //Ustaw stan obecnego okna na okno dodania notatki.
        Main.current_window = "EditNote";

        this.note_index = note_index;

        //Stwórz nową notatkę, przypisz jej typ TODO_NOTE
        this.note = loaded_note;
        this.note.setType(Note.TODO_NOTE);

         this.read_note = new ToDoNote(this.note.getLabel(), this.note.getText(), this.note.getTodo(), this.note.getChecked(), this.note.getHidden());
         this.read_note.setMod_date(this.note.getMod_date());
         this.read_note.setCreate_date(this.note.getCreate_date());
         this.read_note.setCompleted(this.note.getCompleted());

        //Zdefiniuj wartości domyślne pól tekstowych
        String DEFAULT_LABEL = "Wprowadź etykietę notatki.";
        String DEFAULT_TEXT = "Lorem ipsum blablabla. \n Wprowadź coś lepszego";

        //Stwórz układ oraz wartości modelowe
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        //Ustaw układ oraz rozmiar okna
        setLayout(layout);
        setSize(1150, 750);

        //Stwórz etykietę
        JTextField label = new JTextField();
        label.setText(this.note.getLabel()); //Przypisz do jej wartości domyślną etykietę
        label.setHorizontalAlignment(JTextField.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        label.setBackground(Main.rp.getBackground());
        label.setEditable(true); //Ustaw ją na edytowalną

        //Dodaj logikę do etykiety — reakcję na klawisz myszy
        label.addMouseListener(new MouseListener() {
            @Override
            //Ustaw treść etykiety na ciąg pusty, jeśli wartość jest równa wartości domyślnej
            public void mouseClicked(MouseEvent e) {
                if(Objects.equals(label.getText(), DEFAULT_LABEL)) label.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj reakcję na zmianę stanu kursora tekstowego w etykiecie — ustaw wartość etykiety notatki na wartość etykiety
        label.addCaretListener(e -> note.setLabel(label.getText()));

        //Wstaw etykietę do kontenera
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridwidth = RELATIVE;
        gbc.gridy = 0;
        add(label, gbc);

        //Stwórz pole tekstowe
        JTextArea text = new JTextArea( 60, 50);
        text.setText(this.note.getText()); //Przypisz do niego domyślną treść
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        if(!todo_note) text.setSize(1100, 512); //Jeśli jest notatka typu NOTE, jej pole tekstowe ma być większe
        else text.setSize(1100, 128);
        text.setEditable(true); //Ustaw pole na edytowalne

        //Dodaj logikę do pola tekstowego — reakcję na klawisz myszy
        text.addMouseListener(new MouseListener() {
            @Override
            //Ustaw treść pola tekstowego na ciąg pusty, jeśli wartość pola tekstowego równa się tekstowi domyślnemu
            public void mouseClicked(MouseEvent e) {
                if(Objects.equals(text.getText(), DEFAULT_TEXT)) text.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do stanu kursora pola tekstowego — ustaw treść notatki na treść pola tekstowego
        text.addCaretListener(e -> note.setText(text.getText()));

        //Umieść pole tekstowe w "szybie" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się notatki dłuższej od wielkości pola tekstowego
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        if(!todo_note) text.setSize(1100, 512);
        else text.setSize(1100, 128);

        //Umieść szybę w kontenerze
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        //Stwórz panel z listą zadań
        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS)); //Przypisz mu układ liniowy w osi Y

        //Dodaj czarną i pustą ramkę
        todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Dodaj etykietę-przycisk odpowiedzialny za usuwanie listy notatek
        JLabel remove_todos = new JLabel("Usuń listę zadań", SwingConstants.CENTER);
        remove_todos.setFont(new Font("Arial", Font.BOLD, 20));
        remove_todos.setHorizontalAlignment(SwingConstants.CENTER);
        remove_todos.setAlignmentX(CENTER_ALIGNMENT);
        remove_todos.setBackground(new Color(246, 173, 173));
        remove_todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        //Dodaj etykietę-przycisk odpowiedzialną za dodawanie nowych zadań do listy
        JLabel alt_todo = new JLabel("Dodaj zadanie",SwingConstants.CENTER);
        alt_todo.setFont(new Font("Arial", Font.BOLD, 20));
        alt_todo.setHorizontalAlignment(SwingConstants.CENTER);
        alt_todo.setAlignmentX(CENTER_ALIGNMENT);
        alt_todo.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

        //Dodaj logikę do etykiety — kasowanie listy
        remove_todos.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //Usuń wszystkie panele z zadaniami oraz etykieto-przyciski
                todos.removeAll();

                //Ustaw typ notatki przechowywanej w obiekcie na NOTE oraz ustaw długość tablic ze stanem zadań na zero
                note.setTodo(new String[0]);
                note.setChecked(new boolean[0]);
                note.setType(Note.NOTE);

                //Umieść z powrotem w panelu przycisk dodawania zadań do listy
                todos.add(alt_todo);

                //Przeładuj aplikację
                Main.reloadApp(false);
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Dodaj logikę do etykiety-notatki — dodawanie nowych zadań
        alt_todo.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //Jeśli zadań w notatce jest mniej niż jedna, dodaj do panelu etykietę-przycisk kasowania zadań
                if(note.getTodo().length < 1) todos.add(remove_todos);

                //Usuń etykieto-przycisk dodawania notatek
                todos.remove(alt_todo);

                //Dodaj do notatki nowe zadanie
                note.addToDo(new String[1], new boolean[1]);

                //Dodaj do panelu reprezentację graficzną zadania
                todos.add(createTodo(note.getTodo().length - 1));

                //Dodaj do panelu przycisk dodawania notatek
                todos.add(alt_todo);

                //Przeładuj aplikację
                Main.reloadApp(false);
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });


        //Uzupełnij panel następującymi elementami
        //Etykieta-przycisk usuwający listę zadań
        //Lista paneli z zadaniami
        //Etykieta-przycisk dodający nowe zadanie
        todos.add(remove_todos);
        for(int i = 0; i < this.note.getTodo().length; i++){
                todos.add(createTodo(this.note.getTodo(i), this.note.getChecked(i), i));
        }
        todos.add(alt_todo);


        //Dodaj panel z listą zadań do "szyby" z paskiem przewijania.
        JScrollPane todos_sp = new JScrollPane(todos, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        todos_sp.setSize(new Dimension(1150, 384));

        //Umieść szyb
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.weightx = 1.0;
        add(todos_sp, gbc);

        //Stwórz układ oraz wartość modelowe dla paska opcji
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();

        //Stwórz pasek opcji, ustaw jego rozmiar, kolor tła oraz ramki
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);
        option_bar.setBackground(new Color(217, 217, 217));
        option_bar.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Stwórz check-boxa odpowiedzialnego za kontrolę stanu ukrycia notatki
        JCheckBox hidden_state = new JCheckBox("Ukryj notatkę",false);
        hidden_state.addActionListener(e -> note.setHidden(hidden_state.isSelected())); //Ustaw stan ukrycia notatki na stan check-boxa

        //Umieść check-boxa w pasku opcji
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 0;
        ob_gbc.gridy = 0;
        option_bar.add(hidden_state,ob_gbc);

        //Stwórz przycisk zapisywania notatki
        JButton save = new JButton("Zapisz");
        save.setSize(200, 48);

        //Dodaj logikę do przycisku
        save.addActionListener(e -> {

            //Zweryfikuj wykonanie zadań, jeśli notatka jest typu TOOO_NOTE
            if(this.note.getType() == Note.TODO_NOTE) this.note.verifyToDoCompletion();

            //Jeśli notatka ma stan ukryty oraz hasło jest nieustawione
            if(note.getHidden() && Main.settings.get("access_password") == null){

                //Wyświetl o tym informację i zapytaj użytkownika, czy chce ustawić hasło
                int add_password_now = JOptionPane.showConfirmDialog(Main.main_frame, "Właśnie próbujesz przypisać notatce stan ukryty. " +
                        "Jednakże obecnie hasło dostępowe nie jest ustawione.\nMoże to sprawić, iż ta notatka będzie niedostępna do czasu, aż ustawisz hasło." +
                        " Czy chcesz ustawić je teraz?", "Brak zapisanego hasła", JOptionPane.YES_NO_OPTION);

                    //Jeśli tak, wywołaj okno kontekstowe ze zmianą hasła
                    if(add_password_now == JOptionPane.YES_OPTION){
                        Main.changePassword();
                    }

                }

                this.note.setMod_date(new Date());

                //Dodaj notatkę do listy notatek
                Main.noteList.setNote(this.note, note_index);

                //Wróć do poprzedniego okna
                Main.current_window = prev_window;
                Main.reloadApp(true);
        });

        //Wstaw przycisk do paska
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(save,ob_gbc);

        //Stwórz przycisk anulujący edycję notatki
        JButton cancel = new JButton("Anuluj");
        cancel.setSize(200, 48);

        //Dodaj logikę do przycisku
        cancel.addActionListener(e -> {

            if(hasNoteChanged()){
                //Wyświetl informację o możliwej utracie danych, jeśli użytkownik ją zaakceptuje, wróć do poprzedniego okna
                if (JOptionPane.showConfirmDialog(Main.rp, "Na pewno chcesz odrzucić notatkę?" +
                                " Stracisz wszystkie zapisane w niej dane.", "Anulowanie tworzenia notatki",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    Main.current_window = prev_window;
                    Main.reloadApp(false);
                }
            } else {
                Main.current_window = prev_window;
                Main.reloadApp(false);
            }
        });

        //Wstaw przycisk do paska
        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 2;
        ob_gbc.gridy = 0;
        option_bar.add(cancel,ob_gbc);

        //Wstaw pasek do kontenera
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }
}
