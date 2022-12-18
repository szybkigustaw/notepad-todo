import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

/**
 * Klasa reprezentująca panel z listą notatek. Notatki z tego poziomu można kasować oraz podawać do edycji.
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class NoteListGUI extends JPanel{

    private boolean hidden_mode;

    /**
     * Metoda tworzy nowy element listy w postaci panelu z logotypem określającym rodzaj notatki, jej etykietą oraz panelem opcji
     * @param note Notatka, z której będą pobierane dane.
     * @param index Pozycja notatki na liście notatek (dodawana w konstruktorze).
     * @return Obiekt reprezentujący element listy.
     */
    private JPanel createListItem(Note note, int index){

        //Tworzenie panelu - ciała
        JPanel item = new JPanel();
        item.setBackground(new Color(0,255,0));

        //Tworzenie układu oraz wartości modelowych
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Ustawianie układu oraz ustalenie rozszerzania się elementów do momentu osiągnięcia zamierzonego rozmiaru
        item.setLayout(grid);
        gbc.fill = GridBagConstraints.BOTH;

        //Tworzenie elementu ikony
        JPanel icon = new JPanel();
        icon.setSize(50, 50);
        icon.setBackground(new Color(255,255,0));

        JLabel i = new JLabel((note.getType() == 1 ? "N" : "T"), SwingConstants.CENTER);
        i.setFont(new Font("Arial", Font.PLAIN, 20));
        icon.add(i);

        //Przydzielanie miejsca w pierwszym wierszu i pierwszej kolumnie
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;
        item.add(icon, gbc);

        //Tworzenie etykiety z numerem porządkowym i etykietą notatki
        JLabel l = new JLabel(
               String.format("%d. %s",index+1,note.getLabel()),
                SwingConstants.CENTER
        );
        l.setFont(new Font("Arial", Font.PLAIN, 20));

        //Ustawianie pozycji: pierwszy wiersz, druga kolumna, rozszerza się na trzy kolumny
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        item.add(l, gbc);

        //Tworzenie panelu z opcjami
        JPanel options = new JPanel();
        options.setSize(150, 32);
        options.setBackground(new Color(255,0,0));

        //Tworzenie przycisku edycji notatki
        JButton edit = new JButton("E");
        edit.setSize(56,28);

        //Tworzenie przycisku kasowania notatki
        JButton delete = new JButton("X");
        delete.setSize(56,28);

        //Dodawanie logiki do przycisku kasowania
        delete.addActionListener(e -> {

            // Wyświetlenie okna dialogowego pytającego o potwierdzenie operacji
            int a = JOptionPane.showConfirmDialog(Main.rp, "Jesteś pewien?");
            if(a == JOptionPane.YES_OPTION){
                /*
                    Dość hakerski sposób odświeżania listy, polegający na przeładowaniu wszystkich komponentów
                    aplikacji po usunięciu notatki, a później szybkiego przełączenia użytkownika z powrotem do
                    menu listy notatek.
                */
                Main.noteList.removeNote(Main.noteList.getNoteIndex(note));
                Main.reloadApp(true, hidden_mode);
                Main.lt.show(Main.rp, "NoteList");
            }
        });

        //Dodanie przycisków do panelu opcji
        options.add(edit); options.add(delete);

        //Ustalenie pozycji panelu: pierwszy wiersz, piąta kolumna, rozszerzenie na dwie kolumny
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        item.add(options, gbc);

        //Dodanie separatora na spodzie elementu
        JSeparator sep = new JSeparator();
        sep.setSize(1150, 20);
        sep.setBackground(new Color (0,0, 255));

        //Ustalenie pozycji: drugi wiersz, pierwsza kolumna, rozciągnięcie na całą dostępną szerokość
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 6;
        item.add(sep, gbc);

        //Ustalenie rozmiaru elementu
        item.setSize(1100, 50);

        //Dodanie logiki do elementu listy
        item.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Main.rn = new ReadNote(Main.noteList.getNote(index)); //Stworzenie nowej reprezentacji listy notatek.
                Main.reloadApp(false, hidden_mode); //"Odświeżenie" aplikacji
                Main.lt.show(Main.rp,"ReadNote"); /*
                                                            Szybkie przełączenie na ekran odczytu notatki. Tak, żeby
                                                            użytkownik się nie zorientował :-)
                                                        */
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
     * Metoda tworzy nowy element listy w postaci panelu z logotypem określającym rodzaj notatki, jej etykietą oraz panelem opcji
     * @param note Notatka, z której będą pobierane dane.
     * @param index Pozycja notatki na liście notatek (dodawana w konstruktorze).
     * @return Obiekt reprezentujący element listy.
     */
    private JPanel createListItem(ToDoNote note, int index){

        //Tworzenie panelu - ciała
        JPanel item = new JPanel();

        //Przypisanie koloru w zależności od stopnia ukończenia zadań.
        if(note.getCompleted()) item.setBackground(new Color(255,255,0)); //Zadania ukończone.
        else item.setBackground(new Color(0,255,0)); //Zadania nieukończone.

        //Tworzenie układu oraz wartości modelowych
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Ustawianie układu oraz ustalenie rozszerzania się elementów do momentu osiągnięcia zamierzonego rozmiaru
        item.setLayout(grid);
        gbc.fill = GridBagConstraints.BOTH;

        //Tworzenie elementu ikony
        JPanel icon = new JPanel();
        icon.setSize(50, 50);
        icon.setBackground(new Color(255,255,0));

        JLabel i = new JLabel((note.getType() == 1 ? "N" : "T"), SwingConstants.CENTER);
        i.setFont(new Font("Arial", Font.PLAIN, 20));
        icon.add(i);

        //Przydzielanie miejsca w pierwszym wierszu i pierwszej kolumnie
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;
        item.add(icon, gbc);

        //Tworzenie etykiety z numerem porządkowym i etykietą notatki
        JLabel l;
        l = new JLabel(
                String.format("%d. %s",index+1,note.getLabel()),
                SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 20));


        //Ustawianie pozycji: pierwszy wiersz, druga kolumna, rozszerza się na trzy kolumny
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        item.add(l, gbc);

        //Tworzenie panelu z opcjami
        JPanel options = new JPanel();
        options.setSize(150, 32);
        options.setBackground(new Color(255,0,0));

        //Tworzenie przycisku edycji notatki
        JButton edit = new JButton("E");
        edit.setSize(56,28);

        //Tworzenie przycisku kasowania notatki
        JButton delete = new JButton("X");
        delete.setSize(56,28);

        //Dodawanie logiki do przycisku kasowania
        delete.addActionListener(e -> {

            // Wyświetlenie okna dialogowego pytającego o potwierdzenie operacji
            int a = JOptionPane.showConfirmDialog(Main.rp, "Jesteś pewien?");
            if(a == JOptionPane.YES_OPTION){
                /*
                    Dość hakerski sposób odświeżania listy, polegający na przeładowaniu wszystkich komponentów
                    aplikacji po usunięciu notatki, a później szybkiego przełączenia użytkownika z powrotem do
                    menu listy notatek.
                */
                Main.noteList.removeNote(Main.noteList.getNoteIndex(note));
                Main.reloadApp(true, hidden_mode);
                Main.lt.show(Main.rp, "NoteList");
            }
        });

        //Dodanie przycisków do panelu opcji
        options.add(edit); options.add(delete);

        //Ustalenie pozycji panelu: pierwszy wiersz, piąta kolumna, rozszerzenie na dwie kolumny
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        item.add(options, gbc);

        //Dodanie separatora na spodzie elementu
        JSeparator sep = new JSeparator();
        sep.setSize(1150, 20);
        sep.setBackground(new Color (0,0, 255));

        //Ustalenie pozycji: drugi wiersz, pierwsza kolumna, rozciągnięcie na całą dostępną szerokość
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 6;
        item.add(sep, gbc);

        //Ustalenie rozmiaru elementu
        item.setSize(1100, 50);

        item.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Main.rn = new ReadNote((ToDoNote) Main.noteList.getNote(index), index); //Stworzenie nowej reprezentacji listy notatek.
                Main.reloadApp(false, hidden_mode); //"Odświeżenie" aplikacji
                Main.lt.show(Main.rp,"ReadNote");  /*
                                                            Szybkie przełączenie na ekran odczytu notatki. Tak, żeby
                                                            użytkownik się nie zorientował :-)
                                                        */
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
     * Konstruktor domyślny. Tworzy nowy panel z menu listy notatek.
     * @param notes Lista notatek, na bazie której powstanie panel.
     * @param hidden_mode Stan trybu ukrytego (pokazywania tylko ukrytych notatek).
     */
    NoteListGUI(NoteList notes, boolean hidden_mode){
        //Przypisanie do pola obiektu o trybie ukrytym
        this.hidden_mode = hidden_mode;

        //Utworzenie panelu z listą
        JPanel list_window = new JPanel();
        list_window.setLayout(new BoxLayout(list_window, BoxLayout.Y_AXIS));
        list_window.setSize(1100, 700);

        //Sprawdzanie, czy lista notatek nie jest pusta
        if(notes.getListLength() < 1){

            //Ustawienie układu kratowego + kolorek :3
            list_window.setLayout(new GridBagLayout());
            list_window.setBackground(new Color(0, 255, 0));

            //Tworzenie i dodanie do panelu głównego etykiety z tekstem alternatywnym
            JLabel label = new JLabel("Świerszcze...");
            label.setFont(new Font("Arial", Font.BOLD, 36));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setAlignmentY(CENTER_ALIGNMENT);
            label.setAlignmentX(CENTER_ALIGNMENT);
            list_window.add(label);

        } else {

            //Tworzenie i dodawanie elementów listy do panelu
            for (int i = 0; i < notes.getListLength(); i++) {
                if (notes.getNote(i).getType() == Note.TODO_NOTE) {
                    list_window.add(createListItem((ToDoNote) notes.getNote(i), i), i);
                } else {
                    list_window.add(createListItem(notes.getNote(i), i), i);
                }
            }

        }

        //Wrzucenie listy do panelu ze scrollem - dodanie opcji scrollowania listy w przypadku nadmiarowej ilości elementów
        JScrollPane scrollPane = new JScrollPane(list_window, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setSize(1150, 750);

        //Dodanie panelu do panelu głównego
        add(scrollPane);


        //Tworzenie układu i wartości modelowych dla paska opcji.
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        //Stworzenie paska opcji i nadanie mu rozmiarów.
        JPanel option_bar = new JPanel(layout);
        option_bar.setMaximumSize(new Dimension(1150, 64));

        //Dodanie do paska ramki złożonej z dwóch pustych obramowań i czarnej ramki.
        option_bar.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25),
                new CompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        ));

        //Tworzenie przycisku powrotu do menu głównego.
        JButton go_back = new JButton("Wróć do menu głównego");
        go_back.setSize(256, 48);

        //Dodanie funkcjonalności przycisku.
        go_back.addActionListener(e -> Main.lt.show(Main.rp, "HomeMenu"));

        //Dodanie przycisku do paska.
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.4;
        gbc.gridy = 0;
        option_bar.add(go_back, gbc);

        //Tworzenie przycisku pokazu listy ukrytych notatek.
        JButton show_hidden = new JButton();
        if(!this.hidden_mode) show_hidden.setText("Pokaż ukryte");
        else show_hidden.setText("Pokaż publiczne");
        show_hidden.setSize(256, 48);

        //Dodanie funkcjonalności przycisku.
        show_hidden.addActionListener(e -> {
        if(!this.hidden_mode){
            //Prośba o podanie hasła
            String pass = JOptionPane.showInputDialog(Main.rp, "Podaj hasło dostępowe");
            if(!Objects.equals(pass, Main.password)){
                JOptionPane.showMessageDialog(Main.rp, "Błędne hasło!");
            } else {
                Main.reloadApp(true, !this.hidden_mode);
                Main.lt.show(Main.rp, "NoteList");
            }
        } else {
            Main.reloadApp(true, !this.hidden_mode);
            Main.lt.show(Main.rp, "NoteList");
        }
        });

        //Dodanie przycisku do paska.
        gbc.gridx = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 0.4;
        gbc.gridy = 0;
        option_bar.add(show_hidden, gbc);

        //Dodanie paska do panelu głównego.
        add(option_bar);

        //Ustalenie rozmiarów panelu głównego i jego układu — liniowego pionowego.
        setSize(1150, 750);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
}
