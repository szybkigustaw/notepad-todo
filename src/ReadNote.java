import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa reprezentująca panel pozwalający podglądać wybraną z listy notatkę. Z tego panelu też można ją dodać do listy ukrytych notatek.
 */
public class ReadNote extends JPanel {

    /**
     * Konstruktor domyślny. Tworzy panel z podglądem notatki przy użyciu notatki domyślnej.
     */
    ReadNote(){
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);
        setSize(1150, 750);

        JLabel label = new JLabel(new Note().getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        JTextArea text = new JTextArea(new Note().getText());
        text.setSize(1150, 626);

        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 626);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(sp, gbc);
    }

    /**
     * Konstruktor parametryczny. Tworzy panel z podglądem notatki, pobierając dane z podanej notatki.
     * @param note Notatka podglądana w panelu.
     */
    ReadNote(Note note){
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);
        setSize(1150, 750);

        JLabel label = new JLabel(note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        JTextField text = new JTextField(note.getText(), 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1150, 512);
        text.setEditable(false);

        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 512);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(sp, gbc);

        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);

        JButton go_back = new JButton("Wróć do listy");
        go_back.setSize(200, 48);

        go_back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.lt.show(Main.rp, "NoteList");
            }
        });

        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(go_back, ob_gbc);

        JButton hide = new JButton("Ukryj");
        hide.setSize(200, 48);

        ob_gbc.fill = GridBagConstraints.HORIZONTAL;
        ob_gbc.gridx = 4;
        ob_gbc.gridy = 0;
        option_bar.add(hide, ob_gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(option_bar, gbc);
    }
}
