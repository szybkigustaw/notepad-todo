import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Klasa reprezentująca panel pozwalający podglądać wybraną z listy notatkę. Z tego panelu też można ją dodać do listy ukrytych notatek.
 */
public class ReadNote extends JPanel {
    private Note note;
    private ToDoNote todo_note;
    private int index;

    /**
     * Metoda zwracająca notatkę aktualnie podglądaną w panelu.
     * @return Notatka aktualnie zapisana w panelu.
     */
    public Note getNote(){
        return this.note;
    }
    /**
     * Metoda zwracająca notatkę aktualnie podglądaną w panelu (Notatka z listą zadań).
     * @return Notatka aktualnie zapisana w panelu.
     */
    public ToDoNote getTodo_note(){
        return this.todo_note;
    }
    /**
     * Metoda ustawiająca w aktualnie zapisanej notatce z listą zadań stan odhaczenia konkretnego zadania.
     * @param checked Stan odhaczenia zadania z listy zadań.
     * @param index Pozycja zadania na liście.
     */
    public void setChecked(boolean checked, int index){
        this.todo_note.setChecked(checked, index);
    }

    /**
     * Metoda zwracająca pozycję notatki zapisanej w panelu w liście.
     * @return Pozycja notatki zapisanej w panelu w liście.
     */
    public int getIndex() { return this.index; }

    /**
     * Metoda tworząca pojedyńczy wiersz przedstawiający zadanie na liście zadań. Zawiera treść zadania oraz znacznik do odhaczania.
     * @param todo Notatka, z której dane o zadaniu są pobierane.
     * @param todo_index Pozycja zadania na liście zadań.
     * @return Wiersz reprezentujacy graficznie pojedyńcze zadanie na liście.
     */
    private JPanel createTodo(ToDoNote todo, int todo_index){
        //Tworzenie układu oraz wartości modelowych
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Tworzenie ciała - kontenera przechowującego resztę elementów
        JPanel item = new JPanel(layout);
        item.setSize(1100, 60);

        //Tworzenie combo-boxa - "ptaszka" symbolizującego stan odhaczenia zadania.
        JCheckBox cb = new JCheckBox("",todo.getChecked(todo_index));
        cb.setSize(50, 50);

        //Tworzenie logiki - reakcja na przełączenie "ptaszka"
        cb.addActionListener(e -> {
            setChecked(cb.isSelected(), todo_index); //Zmiana stanu odhaczenia zadania.
            todo_note.verifyToDoCompletion(); //Zmiana stanu ukończenia listy zadań.
        });

        //Wstawianie combo-boxa do kontenera.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        item.add(cb, gbc);

        //Tworzenie etykiety, przechowujacej treść zadania.
        JLabel text = new JLabel(todo.getTodo(todo_index));
        text.setFont(new Font("Arial", Font.PLAIN, 20));

        //Tworzenie logiki - reakcja etykiety na kliknięcie myszką
        text.addMouseListener(new MouseListener() {


            @Override
            public void mouseClicked(MouseEvent e) {
                cb.setSelected(!cb.isSelected()); //Zmiana stanu zaznaczenia combo-boxa.
                setChecked(cb.isSelected(), todo_index); //Zmiana stanu odhaczenia zadania.
                todo_note.verifyToDoCompletion(); //Zmiana stanu ukończenia listy zadań.
            }

            //Nic nie warty syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Wstawianie etykiety do kontenera.
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        item.add(text, gbc);

        //Zwrócenie gotowego kontenera.
        return item;
    }

    /**
     * Konstruktor domyślny. Tworzy panel z podglądem notatki przy użyciu notatki domyślnej.
     */
    ReadNote(){
        //Tworzenie układu i wartości modelowych.
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Przypisanie układu i ustalenie rozmiaru okna.
        setLayout(layout);
        setSize(1150, 750);

        //Tworzenie etykiety notatki, ustawienie jej rozmiaru oraz stylu oraz wstawienie do kontenera.
        JLabel label = new JLabel(new Note().getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        //Tworzenie pola tekstowego zawierającego treść notatki.
        JTextArea text = new JTextArea(new Note().getText(), 60, 50);

        /*
            Tworzenie "szyby" z paskiem przewijania - zabezpieczenie na wypadek pojawienia się notatki
            zbyt długiej do wyświetlenia w pojedyńczym oknie.
         */
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 626);

        //Wstawienie szyby do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);
    }

    /**
     * Konstruktor parametryczny. Tworzy panel z podglądem notatki, pobierając dane z podanej notatki.
     * @param note Notatka podglądana w panelu.
     */
    ReadNote(Note note){
        //Przypisanie do pola obiektu obecnej notatki.
        this.note = note;

        //Tworzenie układu i wartości modelowych.
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Przypisanie układu i ustalenie rozmiaru okna.
        setLayout(layout);
        setSize(1150, 750);

        //Tworzenie etykiety notatki, ustawienie jej rozmiaru oraz stylu oraz wstawienie do kontenera.
        JLabel label = new JLabel(this.note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        /*
            Tworzenie pola tekstowego zawierającego treść notatki oraz jego atrybutu (nieedytowalny).
         */
        JTextArea text = new JTextArea(this.note.getText(), 60, 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1100, 512);
        text.setEditable(false);

        /*
            Tworzenie "szyby" z paskiem przewijania - zabezpieczenie na wypadek pojawienia się notatki
            zbyt długiej do wyświetlenia w pojedyńczym oknie.
         */
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 512);

        //Wstawianie szyby do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        /*
            Tworzenie kontenera dla paska opcji, jego układu oraz wartości modelowych.
            Ustawianie tego układu oraz rozmiaru paska.
         */
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1100, 64);

        //Tworzenie przycisku służącego do powrotu do listy notatek.
        JButton go_back = new JButton("Wróć do listy");
        go_back.setSize(200, 48);

        //Dodanie funkcjonalności do przycisku.
        go_back.addActionListener(e -> Main.lt.show(Main.rp, "NoteList"));

        //Wstawianie przycisku do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(go_back, ob_gbc);

        //Tworzenie przycisku ukrywającego notatkę (dodającego do listy ukrytych).
        JButton hide = new JButton("Ukryj");
        hide.setSize(200, 48);

        //Wstawianie przycisku do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 4;
        ob_gbc.gridy = 0;
        option_bar.add(hide, ob_gbc);

        //Wstawianie paska opcji do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(option_bar, gbc);
    }

    /**
     * Konstruktor parametryczny. Tworzy panel z podglądem notatki, pobierając dane z podanej notatki. Implementuje listę zadań z tej notatki.
     * @param note Notatka z listą zadań podglądana w panelu.
     * @param index Pozycja notatki na liście.
     */
    ReadNote(ToDoNote note, int index){
        //Przypisanie do pól obiektu obecnej notatki oraz jej pozycji na liście
        this.todo_note = note;
        this.index = index;

        //Tworzenie układu i wartości modelowych.
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Przypisanie układu i ustalenie rozmiaru okna.
        setLayout(layout);
        setSize(1150, 750);

        //Tworzenie etykiety notatki, ustawienie jej rozmiaru oraz stylu oraz wstawienie do kontenera.
        JLabel label = new JLabel(this.todo_note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        add(label, gbc);

        /*
            Tworzenie pola tekstowego zawierającego treść notatki oraz jego atrybutu (nieedytowalny).
         */
        JTextArea text = new JTextArea(this.todo_note.getText(), 60, 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1100, 128);
        text.setEditable(false);

         /*
            Tworzenie "szyby" z paskiem przewijania - zabezpieczenie na wypadek pojawienia się notatki
            zbyt długiej do wyświetlenia w pojedyńczym oknie.
         */
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 128);

        //Wstawianie szyby do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        gbc.weightx = 1.0;
        add(sp, gbc);

        /*
            Tworzenie panelu z listą zadań, przypisanie do niego układu liniowego i utworzenie wokół
            niego obramowania czarnego.
         */
        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS));
        todos.setBorder(BorderFactory.createLineBorder(new Color(0,0,0), 1, true));

        //Dodanie w pętli do panelu wierszy z zadaniami
        for(int i = 0; i < this.getTodo_note().getTodo().length; i++) {
            todos.add(createTodo(this.getTodo_note(), i));
        }

        //Wstawienie panelu do kontenera.
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        gbc.weightx = 1.0;
        add(todos, gbc);

        /*
            Tworzenie kontenera dla paska opcji, jego układu oraz wartości modelowych.
            Ustawianie tego układu oraz rozmiaru paska.
         */
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);

        //Tworzenie przycisku służącego do powrotu do listy notatek.
        JButton go_back = new JButton("Wróć do listy");
        go_back.setSize(200, 48);

        //Dodanie funkcjonalności do przycisku.
        go_back.addActionListener(e -> {
            Main.noteList.setNote(this.getTodo_note(), this.getIndex());
            Main.reloadApp(true);
            Main.lt.show(Main.rp, "NoteList");
        });

        //Wstawianie przycisku do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(go_back, ob_gbc);


        //Tworzenie przycisku ukrywającego notatkę (dodającego do listy ukrytych).
        JButton hide = new JButton("Ukryj");
        hide.setSize(200, 48);

        //Wstawianie przycisku do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 4;
        ob_gbc.gridy = 0;
        option_bar.add(hide, ob_gbc);

        //Wstawianie paska opcji do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }
}
