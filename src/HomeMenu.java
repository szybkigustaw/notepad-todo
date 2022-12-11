import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        //Tworzenie obiektów reprezentujących układ oraz wartości układu
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(layout);

        //Tworzenie etykiety - nagłówek pierwszy
        JLabel l1 = new JLabel("Witaj użytkowniku!");
        l1.setFont(new Font("Arial", Font.PLAIN, 48));

        //Przydzielanie wartości układu: rozszerzenie pola do wartości pożądanej, dodawanie wewnętrznych odstępów oraz przydzielanie miejsca w kolumnie pierwszej i wierszu pierwszym
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

        //Tworzenie przycisku pierwszego
        JButton b1 = new JButton("Stworzyć nową notatkę / listę zadań");
        b1.setAlignmentX(CENTER_ALIGNMENT);
        b1.setSize(p.getWidth(), 75);
        b1.setFont(new Font("Arial", Font.PLAIN, 32));

        //Tworzenie przycisku drugiego
        JButton b2 = new JButton("Edytować istniejącą");
        b2.setAlignmentX(CENTER_ALIGNMENT);
        b2.setSize(p.getWidth(), 75);
        b2.setFont(new Font("Arial", Font.PLAIN, 32));

        //Tworzenie przycisku trzeciego
        JButton b3 = new JButton("Podejrzeć już istniejące");
        b3.setAlignmentX(CENTER_ALIGNMENT);
        b3.setSize(p.getWidth(), 75);
        b3.setFont(new Font("Arial", Font.PLAIN, 32));

        //Dodawanie akcji do przycisku trzeciego - przejście do widoku listy notatek
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.lt.show(Main.rp, "NoteList");
            }
        });

        //Dodanie przycisków do panelu
        p.add(b1); p.add(b2); p.add(b3);
        //Dodanie panelu do panelu głównego - znowu pozycja jest automatycznie dedukowana
        add(p, gbc);

        //Ustawianie rozmiaru panelu głównego
        setSize(1150, 750);
    }
}
