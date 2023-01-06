import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.GridBagConstraints.RELATIVE;

/**
 * Reprezentuje panel z menu domowym aplikacji.
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class HomeMenu extends JPanel{

    /**
     * Konstruktor domyślny. Tworzy obiekt reprezentujący panel menu powitalnego.
     */
    HomeMenu(){
        //Ustaw stan obecnego okna na okno menu głównego
        Main.current_window = "HomeMenu";

        //Stwórz obiekty reprezentujące układ oraz wartości układu
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(layout);

        //Stwórz etykietę — nagłówek pierwszy
        String username = System.getProperty("user.name");
        JLabel l1 = new JLabel(String.format("Witaj, %s!", username));
        l1.setFont(new Font("Arial", Font.PLAIN, 48));

        /*
            Przydziel wartości układu: rozszerz pola do wartości pożądanej,
            dodawaj wewnętrzne odstępy oraz przydziel miejsca w kolumnie pierwszej i wierszu pierwszym
         */
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0; gbc.gridy = RELATIVE;
        add(l1, gbc);

        //Stwórz etykietę — nagłówek drugi
        JLabel l2 = new JLabel("Co chcesz dzisiaj zrobić?");
        l2.setFont(new Font("Arial", Font.PLAIN, 28));

        //Dodaj do układu — pozycja w drugim wierszu jest automatycznie dedukowana
        add(l2, gbc);

        //Stwórz panel z opcjami i układem typu Box w osi pionowej
        JPanel p = new JPanel();
        p.setSize(1150, 750);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        //Ustaw koloru tła na jakiś losowy :3
        p.setBackground(new Color(243, 234, 234));

        //Dodaj czarną ramkę wokół panelu z opcjami.
        p.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                BorderFactory.createEmptyBorder(25, 25, 25 ,25)
        ));

        //Stwórz przycisk pierwszy i nadaj mu odpowiednie wartości rozmiaru i stylu tekstu.
        JButton b1 = new JButton("Stworzyć nową notatkę / listę zadań");
        b1.setAlignmentX(CENTER_ALIGNMENT);
        b1.setSize(p.getWidth(), 75);
        b1.setFont(new Font("Arial", Font.PLAIN, 32));

        //Dodaj akcję do przycisku pierwszego — przejście do widoku edycji notatki (Notatka pusta = nowa)
        b1.addActionListener(e -> {
            Main.en = new EditNote();
            Main.reloadApp(true);
        });

        //Stwórz przycisk drugi i nadaj mu odpowiednie wartości rozmiaru i stylu tekstu.
        JButton b2 = new JButton("Podejrzeć już istniejące");
        b2.setAlignmentX(CENTER_ALIGNMENT);
        b2.setSize(p.getWidth(), 75);
        b2.setFont(new Font("Arial", Font.PLAIN, 32));

        //Dodaj akcję do przycisku drugiego — przejście do widoku listy notatek
        b2.addActionListener(e -> {
                Main.nl = new NoteListGUI(Main.noteList);
                Main.reloadApp(true); //"Odświeżenie" aplikacji.
            });

        //Dodaj przyciski do panelu
        p.add(b1); p.add(b2);

        //Dodaj panelu do panelu głównego — znowu pozycja jest automatycznie dedukowana
        add(p, gbc);

        //Ustaw rozmiaru panelu głównego
        setSize(1150, 750);
    }
}
