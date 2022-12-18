import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.awt.GridBagConstraints.RELATIVE;

public class EditNote extends JPanel {
    private ToDoNote note;
    private final String DEFAULT_LABEL = "Wprowadź etykietę notatki.";
    private final String DEFAULT_TEXT = "Lorem ipsum blablabla. \n Wprowadź coś lepszego";
    private final String DEFAULT_TODO = "Zjedz psa (Wprowadź coś lepszego)";
    private boolean hidden_mode;
    private boolean todo_note;

    private JPanel createTodo(int index){
        this.note.setType(Note.TODO_NOTE);

        //Tworzenie układu oraz wartości modelowych
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        //Tworzenie ciała - kontenera przechowującego resztę elementów
        JPanel item = new JPanel(layout);
        item.setSize(1100, 60);

        //Tworzenie combo-boxa - "ptaszka" symbolizującego stan odhaczenia zadania.
        JCheckBox cb = new JCheckBox("",false);
        cb.setSize(50, 50);

        //Tworzenie logiki - reakcja na przełączenie "ptaszka"
        cb.addActionListener(e -> {
            note.setChecked(cb.isSelected(), index);
        });

        //Wstawianie combo-boxa do kontenera.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        item.add(cb, gbc);

        //Tworzenie etykiety, przechowujacej treść zadania.
        JTextField text = new JTextField();
        text.setText(this.DEFAULT_TODO);
        text.setFont(new Font("Arial", Font.PLAIN, 20));
        text.setHorizontalAlignment(JTextField.CENTER);
        text.setEditable(true);

        text.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(text.getText().equals(DEFAULT_TODO)) text.setText("");
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

        text.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if(text.getText() == "") note.setTodo("Sample todo", index);
                else note.setTodo(text.getText(), index);
            }
        });

        //Wstawianie etykiety do kontenera.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        item.add(text, gbc);

        //Zwrócenie gotowego kontenera.
        return item;
    }

    EditNote(){

        note = new ToDoNote();
        note.setType(Note.NOTE);
        this.note.setTodo(new String[0]);
        this.note.setChecked(new boolean[0]);


        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        setLayout(layout);
        setSize(1150, 750);

        JTextField label = new JTextField();
        label.setText(this.DEFAULT_LABEL);
        label.setHorizontalAlignment(JTextField.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setSize(364,116);
        label.setBackground(Main.rp.getBackground());
        label.setEditable(true);

        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        label.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                note.setLabel(label.getText());
            }
        });

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridwidth = RELATIVE;
        gbc.gridy = 0;
        add(label, gbc);

        JTextArea text = new JTextArea( 60, 50);
        text.setText(this.DEFAULT_TEXT);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        if(todo_note) text.setSize(1100, 512);
        else text.setSize(1100, 128);
        text.setEditable(true);

        text.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                text.setText("");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        text.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                note.setText(text.getText());
            }
        });

        JScrollPane sp = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        if(todo_note) text.setSize(1100, 128);
        else text.setSize(1100, 128);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        add(sp, gbc);

        JPanel todos = new JPanel();
        todos.setLayout(new BoxLayout(todos, BoxLayout.Y_AXIS));
        todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel remove_todos = new JLabel("Usuń listę zadań", SwingConstants.CENTER);
        remove_todos.setFont(new Font("Arial", Font.BOLD, 20));
        remove_todos.setHorizontalAlignment(SwingConstants.CENTER);
        remove_todos.setAlignmentX(CENTER_ALIGNMENT);
        remove_todos.setBackground(new Color(246, 173, 173));
        remove_todos.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel alt_todo = new JLabel("Dodaj zadanie",SwingConstants.CENTER);
        alt_todo.setFont(new Font("Arial", Font.BOLD, 20));
        alt_todo.setHorizontalAlignment(SwingConstants.CENTER);
        alt_todo.setAlignmentX(CENTER_ALIGNMENT);
        alt_todo.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

        remove_todos.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                todos.removeAll();
                note.setTodo(new String[0]);
                note.setChecked(new boolean[0]);
                note.setType(Note.NOTE);
                todos.add(alt_todo);

                Main.reloadApp(false, false);
                Main.lt.show(Main.rp, "EditNote");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        alt_todo.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(note.getTodo().length < 1) todos.add(remove_todos);
                todos.remove(alt_todo);
                note.addToDo(new String[1], new boolean[1]);
                todos.add(createTodo(note.getTodo().length - 1));
                todos.add(alt_todo);

                Main.reloadApp(false, false);
                Main.lt.show(Main.rp, "EditNote");
            }

            //zbędny syf
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        todos.add(alt_todo);

        JScrollPane todos_sp = new JScrollPane(todos, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        todos_sp.setSize(new Dimension(1150, 384));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.weightx = 1.0;
        add(todos_sp, gbc);

        GridBagLayout ob_layout = new GridBagLayout();
        GridBagConstraints ob_gbc = new GridBagConstraints();
        JPanel option_bar = new JPanel(ob_layout);
        option_bar.setSize(1150, 64);
        option_bar.setBackground(new Color(217, 217, 217));
        option_bar.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 5, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JCheckBox hidden_state = new JCheckBox("Ukryj notatkę",false);
        hidden_state.addActionListener(e -> {
            note.setHidden(hidden_state.isSelected());
        });

        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 0;
        ob_gbc.gridy = 0;
        option_bar.add(hidden_state,ob_gbc);

        JButton save = new JButton("Zapisz");
        save.setSize(200, 48);

        save.addActionListener(e -> {
                Main.noteList.addNote(note);
                Main.reloadApp(true, false);
                Main.lt.show(Main.rp, "HomeMenu");
        });

        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 1;
        ob_gbc.gridy = 0;
        option_bar.add(save,ob_gbc);

        JButton cancel = new JButton("Anuluj");
        cancel.setSize(200, 48);

        cancel.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(Main.rp,"Na pewno chcesz odrzucić notatkę?" +
            " Stracisz wszystkie zapisane w niej dane.","Anulowanie tworzenia notatki",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
                Main.reloadApp(false, false);
                Main.lt.show(Main.rp, "HomeMenu");
            }
        });

        ob_gbc.insets = new Insets(0, 128, 0, 128);
        ob_gbc.gridx = 2;
        ob_gbc.gridy = 0;
        option_bar.add(cancel,ob_gbc);


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        add(option_bar, gbc);
    }

    EditNote(Note note, boolean hidden_mode){

    }

    EditNote(ToDoNote note, boolean hidden_mode){

    }
}
