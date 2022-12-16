import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Klasa reprezentująca panel pozwalający podglądać wybraną z listy notatkę. Z tego panelu też można ją dodać do listy ukrytych notatek.
 */
public class ReadNote extends JPanel {
    private Note note;
    private ToDoNote todo_note;

    public Note getNote(){
        return this.note;
    }

    public ToDoNote getTodo_note(){
        return this.todo_note;
    }


    private JPanel createTodo(String todo, boolean isChecked){
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel item = new JPanel(layout);
        item.setSize(1100, 60);

        JCheckBox cb = new JCheckBox("",isChecked);
        cb.setSize(50, 50);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        item.add(cb, gbc);

        JLabel text = new JLabel(todo);
        text.setFont(new Font("Arial", Font.PLAIN, 20));
        text.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cb.setSelected(!cb.isSelected());
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

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        item.add(text, gbc);

        return item;
    }

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

        JTextArea text = new JTextArea(new Note().getText(), 60, 50);

        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 626);

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
        this.note = note;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);
        setSize(1150, 750);

        JLabel label = new JLabel(this.note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        JTextArea text = new JTextArea(this.note.getText(), 60, 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1100, 512);
        text.setEditable(false);

        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 512);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1100, 64);

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

    /**
     * Konstruktor parametryczny. Tworzy panel z podglądem notatki, pobierając dane z podanej notatki. Implementuje listę zadań z tej notatki.
     * @param note Notatka z listą zadań podglądana w panelu.
     */
    ReadNote(ToDoNote note){
        this.todo_note = note;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);
        setSize(1150, 750);

        JLabel label = new JLabel(this.todo_note.getLabel(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        add(label, gbc);

        JTextArea text = new JTextArea(this.todo_note.getText(), 60, 50);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setSize(1100, 128);
        text.setEditable(false);

        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setSize(1150, 128);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        gbc.weightx = 1.0;
        add(sp, gbc);

        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS));
        todos.setBorder(BorderFactory.createLineBorder(new Color(0,0,0), 1, true));
        for(int i = 0; i < this.getTodo_note().getTodo().length; i++) {
            todos.add(createTodo(this.getTodo_note().getTodo(i), this.getTodo_note().getChecked(i)));
        }
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        gbc.weightx = 1.0;
        add(todos, gbc);

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
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }
}
