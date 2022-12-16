import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Klasa reprezentująca panel z listą notatek. Notatki z tego poziomu można kasować oraz podawać do edycji.
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class NoteListGUI extends JPanel{

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
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Wyświetlenie okna dialogowego pytającego o potwierdzenie operacji
                int a = JOptionPane.showConfirmDialog(Main.rp, "Jesteś pewien?");
                if(a == JOptionPane.YES_OPTION){
                    /*
                        Dość hakerski sposób odświeżania listy, polegający na przeładowaniu wszystkich komponentów
                        aplikacji po usunięciu notatki, a później szybkiego przełączenia użytkownika z powrotem do
                        menu listy notatek.
                    */
                    Main.noteList.removeNote(index);
                    Main.reloadApp();
                    Main.lt.show(Main.rp, "NoteList");
                }
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
                Main.rn = new ReadNote(Main.noteList.getNote(index));
                Main.reloadApp();
                Main.lt.show(Main.rp,"ReadNote");
            }

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
     */
    NoteListGUI(NoteList notes){

        //Utworzenie panelu z listą
        JPanel list_window = new JPanel();
        list_window.setLayout(new BoxLayout(list_window, BoxLayout.Y_AXIS));
        list_window.setSize(1100, 700);

        //Tworzenie i dodawanie elementów listy do panelu
        for(int i = 0; i < notes.getListLength(); i++){
            list_window.add(createListItem(notes.getNote(i), i), i);
        }

        //Wrzucenie listy do panelu ze scrollem - dodanie opcji scrollowania listy w przypadku nadmiarowej ilości elementów
        JScrollPane scrollPane = new JScrollPane(list_window, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setSize(1150, 750);

        //Dodanie panelu do panelu głównego, ustalenie rozmiarów i pustego układu (tzw. róbta co chceta)
        add(scrollPane);
        setSize(1150, 750);
        setLayout(null);
    }
}
