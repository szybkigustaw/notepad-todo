import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.GridBagConstraints.RELATIVE;

/**
 * Klasa reprezentująca panel z menu domowym aplikacji.
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class HomeMenu extends JPanel{

    /**
     * Konstruktor domyślny. Tworzy obiekt reprezentujący panel menu powitalnego.
     */
    HomeMenu(){
        //Ustawienie stanu obecnego okna na okno menu głównego
        Main.current_window = "HomeMenu";

        //Tworzenie obiektów reprezentujących układ oraz wartości układu
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(layout);

        //Tworzenie etykiety - nagłówek pierwszy
        JLabel l1 = new JLabel("Witaj użytkowniku!");
        l1.setFont(new Font("Arial", Font.PLAIN, 48));

        /*
            Przydzielanie wartości układu: rozszerzenie pola do wartości pożądanej,
            dodawanie wewnętrznych odstępów oraz przydzielanie miejsca w kolumnie pierwszej i wierszu pierwszym
         */
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0; gbc.gridy = RELATIVE;
        add(l1, gbc);

        //Tworzenie etykiety - nagłówek drugi
        JLabel l2 = new JLabel("Co chcesz dzisiaj zrobić?");
        l2.setFont(new Font("Arial", Font.PLAIN, 28));

        //Dodawanie do układu - pozycja w drugim wierszu jest automatycznie dedukowana
        add(l2, gbc);

        //Tworzenie panelu z opcjami i układem typu Box w osi pionowej
        JPanel p = new JPanel();
        p.setSize(1150, 750);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        //Ustawianie koloru tła na jakiś losowy :3
        p.setBackground(new Color(243, 234, 234));

        //Dodanie czarnej ramki wokół panelu z opcjami.
        p.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                BorderFactory.createEmptyBorder(25, 25, 25 ,25)
        ));

        //Tworzenie przycisku pierwszego
        JButton b1 = new JButton("Stworzyć nową notatkę / listę zadań");
        b1.setAlignmentX(CENTER_ALIGNMENT);
        b1.setSize(p.getWidth(), 75);
        b1.setFont(new Font("Arial", Font.PLAIN, 32));

        //Dodanie akcji do przycisku pierwszego - przejście do widoku edycji notatki (Notatka pusta = nowa)
        b1.addActionListener(e -> {
            Main.en = new EditNote();
            Main.reloadApp(false);
            Main.lt.show(Main.rp, "EditNote");
        });

        //Tworzenie przycisku drugiego
        JButton b2 = new JButton("Podejrzeć już istniejące");
        b2.setAlignmentX(CENTER_ALIGNMENT);
        b2.setSize(p.getWidth(), 75);
        b2.setFont(new Font("Arial", Font.PLAIN, 32));

        //Dodawanie akcji do przycisku drugiego - przejście do widoku listy notatek
        b2.addActionListener(e -> {
                Main.reloadApp(true); //"Odświeżenie" aplikacji.
                Main.lt.show(Main.rp, "NoteList"); //Szybki przeskok do listy notatek.
            });

        //Dodanie przycisków do panelu
        p.add(b1); p.add(b2);

        //Dodanie panelu do panelu głównego — znowu pozycja jest automatycznie dedukowana
        add(p, gbc);

        //Ustawianie rozmiaru panelu głównego
        setSize(1150, 750);
    }
}
