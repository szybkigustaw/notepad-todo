import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Okno podglądu wybranej z listy notatki.
 * <p>Umożliwia podgląd metadanych notatki oraz (jeśli typem notatki jest TODO-NOTE) odhaczanie zadań</p>
 * <p>Umożliwia ukrywanie oraz upublicznianie notatki (tylko, jeśli hasło jest ustawione)</p>
 */
public class ReadNote extends JPanel {
    /**
     * Notatka przechowywana w tym panelu.
     */
    private Note note;

    /**
     * Notatka z listą zadań przechowywana w tym panelu.
     */
    private ToDoNote todo_note;

    /**
     * Pozycja notatki na liście
     */
    private int index;

    /**
     * Zwraca notatkę aktualnie przechowywaną w panelu.
     * @return Notatka aktualnie przechowywana w panelu.
     */
    public Note getNote(){
        return this.note;
    }
    /**
     * Zwraca notatkę z listą zadań aktualnie przechowywaną w panelu.
     * @return Notatka z listą zadań aktualnie przechowywana w panelu.
     */
    public ToDoNote getTodo_note(){
        return this.todo_note;
    }
    /**
     * Ustawia w przechowywanej notatce z listą zadań stan odhaczenia konkretnego zadania.
     * @param checked Stan odhaczenia zadania z listy zadań.
     * @param index Pozycja zadania na liście.
     */
    public void setChecked(boolean checked, int index){
        this.todo_note.setChecked(checked, index);
    }

    /**
     * Zwraca pozycję na liście notatki przechowywanej w panelu.
     * @return Pozycja na liście notatki przechowywanej w panelu.
     */
    public int getIndex() { return this.index; }

    /**
     * Tworzy graficzną reprezentację zadania (poziomy panel z checkbox-em i treścią)
     *
     * <p>Treść zadania reaguje na kliknięcia myszką, jej klikanie przestawia stan odhaczenia zadania.</p>
     *
     * @param todo Notatka, z której dane o zadaniu są pobierane.
     * @param todo_index Pozycja zadania na liście zadań.
     * @return Graficzna reprezentacja zadania.
     */
    private JPanel createTodo(ToDoNote todo, int todo_index){
        //Stwórz układ oraz wartości modelowe
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Stwórz ciało — kontener przechowujący resztę elementów
        JPanel item = new JPanel(layout);
        item.setSize(1100, 60);

        //Stwórz combo-boxa - "ptaszka" symbolizującego stan odhaczenia zadania.
        JCheckBox cb = new JCheckBox("",todo.getChecked(todo_index));
        cb.setSize(50, 50);

        //Dodaj logikę — reakcja na przełączenie "ptaszka"
        cb.addActionListener(e -> {
            setChecked(cb.isSelected(), todo_index); //Zmień stan odhaczenia zadania.
            todo_note.verifyToDoCompletion(); //Zmień stan ukończenia listy zadań.
        });

        //Wstaw combo-boxa do kontenera.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        item.add(cb, gbc);

        //Stwórz etykietę przechowującą treść zadania.
        JLabel text = new JLabel(todo.getTodo(todo_index));
        text.setFont(new Font("Arial", Font.PLAIN, 20));

        //Dodaj logikę — reakcję panelu na kliknięcie myszką
        text.addMouseListener(new MouseListener() {


            @Override
            public void mouseClicked(MouseEvent e) {
                cb.setSelected(!cb.isSelected()); //Zmień stan zaznaczenia combo-boxa.
                setChecked(cb.isSelected(), todo_index); //Zmień stan odhaczenia zadania.
                todo_note.verifyToDoCompletion(); //Zmień stan ukończenia listy zadań.
            }

            //Nic niewart syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Wstaw etykietę do kontenera.
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        item.add(text, gbc);

        //Zwróć gotowy kontener.
        return item;
    }

    /**
     * Konstruktor domyślny. Tworzy panel z podglądem notatki przy użyciu notatki domyślnej.
     */
    ReadNote(){
        //Stwórz układ i wartości modelowe.
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Przypisz układ i ustal rozmiar okna.
        setLayout(layout);
        setSize(1150, 750);

        //Stwórz etykietę notatki, ustaw jej rozmiar oraz styl i wstaw ją do kontenera.
        JLabel label = new JLabel(new Note().getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        //Stwórz pole tekstowe zawierające treść notatki.
        JTextArea text = new JTextArea(new Note().getText(), 60, 50);

        /*
            Stwórz "szybę" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się notatki
            zbyt długiej do wyświetlenia w pojedynczym oknie.
         */
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 626);

        //Wstaw szybę do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);
    }

    /**
     * Konstruktor parametryczny. Tworzy panel z podglądem notatki, pobierając dane z podanej notatki.
     * @param note Notatka do przedstawienia w panelu.
     */
    ReadNote(Note note){
        //Ustaw stan obecnego okna na okno podglądu notatki
        Main.current_window = "ReadNote";

        //Przypisz do pola obiektu obecnej notatki.
        this.note = note;
        this.index = Main.noteList.getNoteIndex(this.getNote());

        //Stwórz układ i wartości modelowe.
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Przypisz układ i ustal rozmiar okna.
        setLayout(layout);
        setSize(1150, 750);

        //Stwórz etykietę notatki, ustaw jej rozmiar oraz styl, oraz wstaw ją do kontenera.
        JLabel label = new JLabel(this.note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        /*
            Stwórz pole tekstowe oraz przypisz mu rozmiar i styl. Uczyń go nieedytowalnym.
         */
        JTextArea text = new JTextArea(this.note.getText(), 60, 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1100, 512);
        text.setEditable(false);

        /*
            Stwórz "szybę" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się notatki
            zbyt długiej do wyświetlenia w pojedynczym oknie.
         */
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 512);

        //Wstaw tę szybę do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        /*
            Stwórz kontener dla paska opcji, jego układ oraz wartości modelowe.
            Ustaw ten układ oraz rozmiar paska.
         */
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1100, 64);

        //Stwórz przycisk służący do powrotu do listy notatek.
        JButton go_back = new JButton("Wróć do listy");
        go_back.setSize(200, 48);

        //Dodaj funkcjonalność do przycisku.
        go_back.addActionListener(e -> {

            //Przypisz z powrotem przechowywaną notatkę na swojej pozycji — tak na wszelki wypadek
            Main.noteList.setNote(this.getNote(), this.getIndex());

            //Przeładuj aplikację i wyświetl okno listy notatek
            Main.reloadApp(true);
            Main.lt.show(Main.rp, "NoteList");
        });

        //Wstaw przycisk do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(go_back, ob_gbc);

        //Stwórz przycisk ukrywający notatkę (dodający do listy ukrytych).
        JButton hide = new JButton();
        hide.setText(note.getHidden() ? "Upublicznij" : "Ukryj");
        hide.setSize(200, 48);

        //Dodaj funkcjonalność do przycisku.
        hide.addActionListener(e -> {

            //Jeśli notatka jest ukryta
            if(note.getHidden()){

                //Początek kodu z potencjalnymi wyjątkami
                try {

                    //Pobierz hasło od użytkownika
                    String pass = JOptionPane.showInputDialog(Main.rp, "Podaj hasło");

                    //Pobierz z niego hash
                    pass = Main.hashString(pass);

                    //Jeśli hasze haseł pasują
                    if (Objects.equals(Main.settings.get("access_password"), pass)) {

                        //Przestaw stan ukrycia notatki
                        note.setHidden(!(note.getHidden()));

                        //Wyświetl informację o wykonaniu operacji
                        JOptionPane.showMessageDialog(Main.rp, note.getHidden() ? "Notatkę ukryto!" : "Notatkę upubliczniono!");

                        //Zaktualizuj panel
                        Main.rn = new ReadNote(this.getNote());
                        Main.reloadApp(true);

                    //Jeśli hasze nie pasują
                    } else {

                        //Podaj informację o nieprawidłowym haśle
                        JOptionPane.showMessageDialog(Main.rp, "Błędne hasło!");
                    }

                //Jeśli wystąpi ten randomowy wyjątek
                } catch (NoSuchAlgorithmException ex){

                    //Wypluj do konsoli wiadomość z tego wyjątku
                    System.out.println(ex.getMessage());
                }

            //Jeśli notatka nie jest ukryta
            } else {

                //Ukryj notatkę (przestaw jej stan ukrycia)
                note.setHidden(!(note.getHidden()));

                //Wyświetl informację o powodzeniu operacji
                JOptionPane.showMessageDialog(Main.rp, note.getHidden() ? "Notatkę ukryto!" : "Notatkę upubliczniono!");

                //Zaktualizuj panel
                Main.rn = new ReadNote(this.getNote());
                Main.reloadApp(true);
            }
        });

        //Jeśli hasło jest ustawione
        if(Main.settings.get("access_password") != null) {

            //Wstaw przycisk do paska.
            ob_gbc.fill = GridBagConstraints.HORIZONTAL;
            ob_gbc.gridx = 4;
            ob_gbc.gridy = 0;
            option_bar.add(hide, ob_gbc);
        }


        //Stwórz przycisk pokazujący metadane przechowywanej notatki i ustaw jej rozmiar
        JButton show_metadata = new JButton("Pokaż metadane");
        show_metadata.setSize(200, 40);

        //Dodaj funkcjonalność do przycisku
        show_metadata.addActionListener(e -> {

            //Wyświetl okno przedstawiające metadane
            JOptionPane.showMessageDialog(Main.rp, String.format(
                    "Etykieta: %s \nData utworzenia %s \nData modyfikacji %s \nStan ukrycia %s",
                    this.getNote().getLabel(), this.getNote().getCreate_date().toString(), this.getNote().getMod_date().toString(),
                    this.getNote().getHidden() ? "Ukryta" : "Jawna"), "Metadane notatki", JOptionPane.INFORMATION_MESSAGE);
        });

        //Wstaw przycisku do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 7;
        ob_gbc.gridy = 0;
        option_bar.add(show_metadata, ob_gbc);

        //Wstaw pasek opcji do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(option_bar, gbc);

    }

    /**
     * Konstruktor parametryczny. Tworzy panel z podglądem notatki, pobierając dane z podanej notatki. Implementuje listę zadań z tej notatki.
     * @param note Notatka z listą zadań podglądana w panelu.
     */
    ReadNote(ToDoNote note){
        //Ustaw stan obecnego okna na okno podglądu notatki
        Main.current_window = "ReadNote";

        //Przypisz do pól tego obiektu obecnej notatki oraz jej pozycji na liście notatek
        this.todo_note = note;
        this.index = Main.noteList.getNoteIndex(this.getTodo_note());

        //Stwórz układ i wartości modelowe.
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Przypisz układ i ustalenie rozmiar okna.
        setLayout(layout);
        setSize(1150, 750);

        //Stwórz etykietę notatki, ustaw jej rozmiar oraz styl i wstaw do kontenera.
        JLabel label = new JLabel(this.todo_note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        add(label, gbc);

        /*
            Stwórz pole tekstowe zawierające treść notatki oraz jego atrybut (nieedytowalny).
         */
        JTextArea text = new JTextArea(this.todo_note.getText(), 60, 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1100, 128);
        text.setEditable(false);

         /*
            Stwórz "szybę" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się notatki
            zbyt długiej do wyświetlenia w pojedynczym oknie.
         */
        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 128);

        //Wstaw szybę do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        gbc.weightx = 1.0;
        add(sp, gbc);

        /*
            Stwórz panel z listą zadań, przypisz do niego układ liniowy i utwórz wokół
            niego obramowanie czarne.
         */
        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS));
        todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        //Dodaj w pętli do panelu wiersze z zadaniami
        for(int i = 0; i < this.getTodo_note().getTodo().length; i++) {
            todos.add(createTodo(this.getTodo_note(), i));
        }

        /*
            Stwórz "szybę" z paskiem przewijania — zabezpieczenie na wypadek pojawienia się listy zadań
            zbyt długiej do wyświetlenia w pojedynczym oknie.
         */
        JScrollPane todos_sp = new JScrollPane(todos, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //Wstaw panel do kontenera.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.weightx = 1.0;
        add(todos_sp, gbc);

        /*
            Stwórz kontener dla paska opcji, jego układ oraz wartości modelowe.
            Ustaw ten układ oraz rozmiar paska.
         */
        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);

        //Stwórz przycisk służący do powrotu do listy notatek.
        JButton go_back = new JButton("Wróć do listy");
        go_back.setSize(200, 48);

        //Dodaj funkcjonalności do przycisku.
        go_back.addActionListener(e -> {
            //Przypisz z powrotem na swojej pozycji przechowywaną notatkę — tak na wszelki wypadek:3
            Main.noteList.setNote(this.getTodo_note(), this.getIndex());

            //Przeładuj aplikację i zrób szybki przeskok do listy notatek
            Main.reloadApp(true);
            Main.lt.show(Main.rp, "NoteList");
        });

        //Wstaw przycisk do paska
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(go_back, ob_gbc);


        //Stwórz przycisk ukrywający notatkę (dodający do listy ukrytych).
        JButton hide = new JButton();
        hide.setText(todo_note.getHidden() ? "Upublicznij" : "Ukryj");
        hide.setSize(200, 48);

        //Dodaj funkcjonalność do przycisku
        hide.addActionListener(e -> {

            //Jeśli notatka jest ukryta
            if(note.getHidden()){

                //Początek kodu z potencjalnymi wyjątkami
                try {

                    //Pobierz od użytkownika hasło
                    String pass = JOptionPane.showInputDialog(Main.rp, "Podaj hasło");

                    //Przypisz do hasła jego hash
                    pass = Main.hashString(pass);

                    //Jeśli hasze haseł się zgadzają
                    if (Objects.equals(Main.settings.get("access_password"), pass)) {

                        //Przestaw stan ukrycia notatki
                        note.setHidden(!(note.getHidden()));

                        //Wyświetl informację o powiedzionej operacji
                        JOptionPane.showMessageDialog(Main.rp, note.getHidden() ? "Notatkę ukryto!" : "Notatkę upubliczniono!");

                        //Odśwież okno
                        Main.rn = new ReadNote(this.getTodo_note());
                        Main.reloadApp(true);

                    //Jeśli hasło jest błędne
                    } else {

                        //Wyświetl informacje o tym fakcie
                        JOptionPane.showMessageDialog(Main.rp, "Błędne hasło!");
                    }

                //Jeśli wystąpi ten cosiowy wyjątek
                //(Serio, nie wiem co robi, bez jego wyłapania kompilator się popłacze)
                } catch (NoSuchAlgorithmException ex){
                    System.out.println(ex.getMessage());
                }

            //Jeśli notatka nie jest ukryta
            } else {

                //Przestaw jej stan ukrycia
                note.setHidden(!(note.getHidden()));

                //Wyświetl informację o wykonaniu operacji
                JOptionPane.showMessageDialog(Main.rp, note.getHidden() ? "Notatkę ukryto!" : "Notatkę upubliczniono!");

                //Przeładuj okno
                Main.rn = new ReadNote(this.getTodo_note());
                Main.reloadApp(true);
            }
        });

        //Jeśli hasło jest ustawione
        if(Main.settings.get("access_password") != null) {

            //Wstaw przycisk do paska.
            ob_gbc.fill = GridBagConstraints.HORIZONTAL;
            ob_gbc.gridx = 4;
            ob_gbc.gridy = 0;
            option_bar.add(hide, ob_gbc);
        }

        //Stwórz przycisk wyświetlający metadane notatki i ustaw jego rozmiar
        JButton show_metadata = new JButton("Pokaż metadane");
        show_metadata.setSize(200, 40);

        //Dodaj funkcjonalność do przycisku
        show_metadata.addActionListener(e -> {

            //Wyświetl okno przedstawiające metadane
            JOptionPane.showMessageDialog(Main.rp, String.format(
                    "Etykieta: %s \nData utworzenia %s \nData modyfikacji %s \nStan ukrycia %s",
                    this.getTodo_note().getLabel(), this.getTodo_note().getCreate_date().toString(), this.getTodo_note().getMod_date().toString(),
                    this.getTodo_note().getHidden() ? "Ukryta" : "Jawna"), "Metadane notatki", JOptionPane.INFORMATION_MESSAGE);
        });

        //Wstaw przycisk do paska.
        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 7;
        ob_gbc.gridy = 0;
        option_bar.add(show_metadata, ob_gbc);

        //Wstaw pasek opcji do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }
}
