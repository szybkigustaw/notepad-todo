import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Reprezentuje okno z listą notatek.
 *
 * <p>Umożliwia przejście do okna podglądu i edycji notatki. Z poziomu tego okna można również kasować notatki.</p>
 * <p>Zawiera przycisk odpowiedzialny za przełączanie między widokiem notatek jawnych oraz notatek ukrytych. Do tych drugich wymaga podania hasła.</p>
 * <p>Jeśli hasło nie jest ustawione, nie ma dostępu do listy notatek ukrytych</p>
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class NoteListGUI extends JPanel{

    /**
     * Tworzy nowy element listy w postaci panelu z logotypem określającym rodzaj notatki, jej etykietą oraz panelem opcji
     * @param note Notatka, z której będą pobierane dane.
     * @param index Pozycja notatki na liście notatek (dodawana w konstruktorze).
     * @return Obiekt reprezentujący element listy.
     */
    private JPanel createListItem(Note note, int index){

        //Przypisz zmiennej przechowującej informację o pozycji notatki na liście.
        int note_index = Main.noteList.getNoteIndex(note);

        //Stwórz panel — ciało elementu
        JPanel item = new JPanel();
        item.setBackground(new Color(0,255,0));
        item.setMaximumSize(new Dimension(1100, 50));

        //Stwórz obiekt układu oraz wartości modelowe
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Ustaw układ oraz ustal rozszerzanie się elementów do momentu osiągnięcia zamierzonego rozmiaru
        item.setLayout(grid);
        gbc.fill = GridBagConstraints.BOTH;

        //Stwórz element ikony
        JPanel icon = new JPanel();
        icon.setSize(50, 50);
        icon.setBackground(new Color(255,255,0));

        JLabel i = new JLabel((note.getType() == 1 ? "N" : "T"), SwingConstants.CENTER);
        i.setFont(new Font("Arial", Font.PLAIN, 20));
        icon.add(i);

        //Przydziel miejsce w pierwszym wierszu i pierwszej kolumnie
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;
        item.add(icon, gbc);

        //Stwórz etykietę z numerem porządkowym i etykietą notatki
        JLabel l = new JLabel(
               String.format("%d. %s",index+1,note.getLabel()),
                SwingConstants.CENTER
        );
        l.setFont(new Font("Arial", Font.PLAIN, 20));

        //Ustaw pozycję: pierwszy wiersz, druga kolumna, rozszerza się na trzy kolumny
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        item.add(l, gbc);

        //Stwórz panel z opcjami
        JPanel options = new JPanel();
        options.setSize(150, 32);
        options.setBackground(new Color(255,0,0));

        //Stwórz przycisk edycji notatki
        JButton edit = new JButton("E");
        edit.setSize(56,28);

        //Dodaj logikę do przycisku edycji — przekierowanie do okna edycji notatki
        edit.addActionListener(e -> {
            Main.en = new EditNote(Main.noteList.getNote(note_index), note_index);
            Main.reloadApp(false);
        });

        //Stwórz przycisk kasowania notatki
        JButton delete = new JButton("X");
        delete.setSize(56,28);

        //Dodaj logikę do przycisku kasowania
        delete.addActionListener(e -> {

            // Wyświetl okno dialogowe pytające o potwierdzenie operacji
            int a = JOptionPane.showConfirmDialog(Main.rp, "Jesteś pewien?");
            if(a == JOptionPane.YES_OPTION){
                //Usuń notatkę z listy i przeładuj okno
                Main.noteList.removeNote(note_index);
                Main.reloadApp(true);
            }
        });

        //Dodaj przyciski do panelu opcji
        options.add(edit); options.add(delete);

        //Ustal pozycję panelu: pierwszy wiersz, piąta kolumna, rozszerzenie na dwie kolumny
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        item.add(options, gbc);

        //Dodaj separator na spodzie elementu
        JSeparator sep = new JSeparator();
        sep.setSize(1150, 20);
        sep.setBackground(new Color (0,0, 255));

        //Ustal pozycję: drugi wiersz, pierwsza kolumna, rozciągnięcie na całą dostępną szerokość
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 6;
        item.add(sep, gbc);

        //Ustal rozmiar elementu
        item.setSize(1100, 50);

        //Dodaj logikę do elementu listy
        item.addMouseListener(new MouseListener() {

            //Po kliknięciu myszką
            @Override
            public void mouseClicked(MouseEvent e) {
                Main.rn = new ReadNote(Main.noteList.getNote(note_index)); //Utwórz nowe okno podglądu notatki
                Main.reloadApp(false); //Przeładowanie aplikacji
            }

            //Zbędny szajs
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        return item;
    }

    /**
     * Tworzy nowy element listy w postaci panelu z logotypem określającym rodzaj notatki, jej etykietą oraz panelem opcji
     * @param note Notatka, z której będą pobierane dane.
     * @param index Pozycja notatki na liście notatek (dodawana w konstruktorze).
     * @return Obiekt reprezentujący element listy.
     */
    private JPanel createListItem(ToDoNote note, int index){

        //Przypisz do zmiennej przechowującej informację o pozycji notatki na liście.
        int note_index = Main.noteList.getNoteIndex(note);

        //Stwórz panel — ciało elementu
        JPanel item = new JPanel();

        //Przypisz koloru w zależności od stopnia ukończenia zadań.
        if(note.getCompleted()) item.setBackground(new Color(255,255,0)); //Zadania ukończone.
        else item.setBackground(new Color(0,255,0)); //Zadania nieukończone.

        //Ustaw maksymalny rozmiaru elementu.
        item.setMaximumSize(new Dimension(1100, 50));

        //Stwórz układ oraz wartości modelowe
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Ustaw układ oraz ustal rozszerzanie się elementów do momentu osiągnięcia zamierzonego rozmiaru
        item.setLayout(grid);
        gbc.fill = GridBagConstraints.BOTH;

        //Stwórz element ikony
        JPanel icon = new JPanel();
        icon.setSize(50, 50);
        icon.setBackground(new Color(255,255,0));

        JLabel i = new JLabel((note.getType() == 1 ? "N" : "T"), SwingConstants.CENTER);
        i.setFont(new Font("Arial", Font.PLAIN, 20));
        icon.add(i);

        //Przydziel miejsce w pierwszym wierszu i pierwszej kolumnie
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;
        item.add(icon, gbc);

        //Stwórz etykietę z numerem porządkowym i etykietą notatki
        JLabel l;
        l = new JLabel(
                String.format("%d. %s",index+1,note.getLabel()),
                SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 20));


        //Ustaw pozycję: pierwszy wiersz, druga kolumna, rozszerza się na trzy kolumny
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        item.add(l, gbc);

        //Stwórz panel z opcjami
        JPanel options = new JPanel();
        options.setSize(150, 32);
        options.setBackground(new Color(255,0,0));

        //Stwórz przycisk edycji notatki
        JButton edit = new JButton("E");
        edit.setSize(56,28);

        //Dodaj logikę do przycisku edycji — utworzenie nowego okna edycji notatki oraz przeładowanie aplikacji
        edit.addActionListener(e -> {
            Main.en = new EditNote((ToDoNote)Main.noteList.getNote(note_index), note_index);
            Main.reloadApp(false);
        });

        //Stwórz przycisk kasowania notatki
        JButton delete = new JButton("X");
        delete.setSize(56,28);

        //Dodaj logikę do przycisku kasowania
        delete.addActionListener(e -> {

            // Wyświetl okno dialogowe pytające o potwierdzenie operacji
            int a = JOptionPane.showConfirmDialog(Main.rp, "Jesteś pewien?");
            if(a == JOptionPane.YES_OPTION){

                //Usuń notatkę i przeładuj aplikację
                Main.noteList.removeNote(note_index);
                Main.reloadApp(true);
            }
        });

        //Dodaj przyciski do panelu opcji
        options.add(edit); options.add(delete);

        //Ustal pozycję panelu: pierwszy wiersz, piąta kolumna, rozszerzenie na dwie kolumny
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        item.add(options, gbc);

        //Dodaj separator na spodzie elementu
        JSeparator sep = new JSeparator();
        sep.setSize(1150, 20);
        sep.setBackground(new Color (0,0, 255));

        //Ustal pozycję: drugi wiersz, pierwsza kolumna, rozciągnięcie na całą dostępną szerokość
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 6;
        item.add(sep, gbc);

        //Ustal rozmiar elementu
        item.setSize(1100, 50);

        //Dodaj logikę do całego elementu
        item.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Main.rn = new ReadNote((ToDoNote) Main.noteList.getNote(note_index)); //Utwórz nowe okno podglądu notatki
                Main.reloadApp(false); //Przeładuj aplikację
            }

            //Zbędny szajs
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        return item;
    }

    /**
     * Konstruktor domyślny. Tworzy nowe okno z menu listy notatek.
     *
     * @param notes Lista notatek, na której bazie powstanie panel.
     */
    NoteListGUI(NoteList notes){
        //Ustaw stan obecnego okna na okno listy notatek
        Main.current_window = "NoteList";

        notes.sortNote(Main.sort_type, Main.sort_descending);

        //Utwórz panel z listą
        JPanel list_window = new JPanel();
        list_window.setLayout(new BoxLayout(list_window, BoxLayout.Y_AXIS));
        list_window.setSize(1100, 700);

        //Sprawdź, czy lista notatek nie jest pusta
        if(notes.getListLength() < 1){

            //Ustaw układ kratowy + kolorek :3
            list_window.setLayout(new GridBagLayout());
            list_window.setBackground(new Color(0, 255, 0));

            //Stwórz i dodaj do panelu głównego etykiety z tekstem alternatywnym
            JLabel label = new JLabel("Świerszcze...");
            label.setFont(new Font("Arial", Font.BOLD, 36));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setAlignmentY(CENTER_ALIGNMENT);
            label.setAlignmentX(CENTER_ALIGNMENT);
            list_window.add(label);

        } else {

            //Stwórz i dodaj elementy listy do panelu
            for (int i = 0; i < notes.getListLength(); i++) {
                if (notes.getNote(i).getType() == Note.TODO_NOTE) {
                    list_window.add(createListItem((ToDoNote) notes.getNote(i), i), i);
                } else {
                    list_window.add(createListItem(notes.getNote(i), i), i);
                }
            }

        }

        //Wrzuć listy do panelu ze scrollem — dodanie opcji scrollowania listy w przypadku nadmiarowej ilości elementów
        JScrollPane scrollPane = new JScrollPane(list_window, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setSize(1150, 750);

        //Dodaj panel do panelu głównego
        add(scrollPane);


        //Stwórz układ i wartości modelowe dla paska opcji.
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        //Stwórz pasek opcji i nadaj mu maksymalny rozmiar.
        JPanel option_bar = new JPanel(layout);
        option_bar.setMaximumSize(new Dimension(1150, 64));

        //Dodaj do paska ramki złożonej z dwóch pustych obramowań i czarnej ramki.
        option_bar.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25),
                new CompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        ));

        //Stwórz przycisk powrotu do menu głównego.
        JButton go_back = new JButton("Wróć do menu głównego");
        go_back.setSize(256, 48);

        //Dodaj funkcjonalność do przycisku.
        go_back.addActionListener(e -> { Main.hm = new HomeMenu(); Main.reloadApp(false); });

        //Dodanie przycisku do paska.
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.4;
        gbc.gridy = 0;
        option_bar.add(go_back, gbc);

        //Stwórz przycisk przełączania widoku notatek ukrytych i jawnych
        JButton show_hidden = new JButton();
        if(!Main.hidden_mode) show_hidden.setText("Pokaż ukryte");
        else show_hidden.setText("Pokaż publiczne");
        show_hidden.setSize(256, 48);

        //Dodaj funkcjonalność
        show_hidden.addActionListener(e -> {

        //Jeśli nie jesteśmy aktualnie w trybie ukrytym
        if(!Main.hidden_mode){
            //Rozpoczęcie egzekucji kodu z prawdopodobnymi wyjątkami
            try {

                //Wyświetl okno z prośbą o podanie hasła
                String pass = JOptionPane.showInputDialog(Main.rp, "Podaj hasło dostępowe");

                //Zahaszowanie hasła
                pass = Main.hashString(pass);

                //Jeśli hasze się nie zgadzają
                if (!Objects.equals(pass, Main.settings.get("access_password"))) {

                    //Wyświetl okno z informacją o błędnym haśle
                    JOptionPane.showMessageDialog(Main.rp, "Błędne hasło!");

                //Jeśli są równe
                } else {

                    //Przełącz tryb widoku
                    Main.hidden_mode = !Main.hidden_mode;

                    //Przeładuj aplikację
                    Main.reloadApp(true);
                }
            //Złap wyjątek
            } catch (NoSuchAlgorithmException ex){

                //Wyrzuć jego wiadomość do konsoli
                System.out.println(ex.getMessage());
            }
        //Jeśli jesteśmy w trybie ukrytego widoku
        } else {

            //Przełącz go z powrotem na widok publiczny
            Main.hidden_mode = false;
            Main.reloadApp(true);
        }
        });

        //Jeśli hasło jest ustawione
        if(Main.settings.get("access_password") != null) {

            //Dodaj przycisk do paska.
            gbc.gridx = 4;
            gbc.gridwidth = 3;
            gbc.weightx = 0.4;
            gbc.gridy = 0;
            option_bar.add(show_hidden, gbc);
        }

        //Dodaj pasek do panelu głównego.
        add(option_bar);

        //Ustal rozmiar panelu głównego i jego układu — liniowego pionowego.
        setSize(1150, 750);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
}
