import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Main {
    public static Note[] notes = {
            new Note(),
            new Note(),
            new ToDoNote(),
            new Note(
                    "Najs",
                    "Dzisiaj zjadłem psa. Nawet smaczny.",
                    false
            ),
            new ToDoNote(
                    "Essa",
                    "Oto zadania na dzisiejszy dzień :)",
                    new String[]{"Odkurz pokój", "Zjedz psa", "Zamów esse"},
                    new boolean[]{false, true, true},
                    false
            ),
            new Note(),
            new Note(),
            new ToDoNote(),
            new Note(
                    "Najs",
                    "Dzisiaj zjadłem psa. Nawet smaczny.",
                    false
            ),
            new ToDoNote(
                    "Essa",
                    "Oto zadania na dzisiejszy dzień :)",
                    new String[]{"Odkurz pokój", "Zjedz psa", "Zamów esse"},
                    new boolean[]{false, true, true},
                    false
            ),
            new Note(),
            new Note(),
            new ToDoNote(),
            new Note(
                    "Najs",
                    "Dzisiaj zjadłem psa. Nawet smaczny.",
                    false
            ),
            new ToDoNote(
                    "Essa",
                    "Oto zadania na dzisiejszy dzień :)",
                    new String[]{"Odkurz pokój", "Zjedz psa", "Zamów esse"},
                    new boolean[]{false, true, true},
                    false
            ),
            new Note(),
            new Note(),
            new ToDoNote(),
            new Note(
                    "Najs",
                    "Dzisiaj zjadłem psa. Nawet smaczny.",
                    false
            ),
            new ToDoNote(
                    "Essa",
                    "Oto zadania na dzisiejszy dzień :)",
                    new String[]{"Odkurz pokój", "Zjedz psa", "Zamów esse"},
                    new boolean[]{false, true, true},
                    false
            ),
            new Note(),
            new Note(),
            new ToDoNote(),
            new Note(
                    "Najs",
                    "Dzisiaj zjadłem psa. Nawet smaczny.",
                    false
            ),
            new ToDoNote(
                    "Essa",
                    "Oto zadania na dzisiejszy dzień :)",
                    new String[]{"Odkurz pokój", "Zjedz psa", "Zamów esse"},
                    new boolean[]{false, true, true},
                    false
            ),
            new Note(),
            new Note(),
            new ToDoNote(),
            new Note(
                    "Najs",
                    "Dzisiaj zjadłem psa. Nawet smaczny.",
                    false
            ),
            new ToDoNote(
                    "Essa",
                    "Oto zadania na dzisiejszy dzień :)",
                    new String[]{"Odkurz pokój", "Zjedz psa", "Zamów esse"},
                    new boolean[]{false, true, true},
                    false
            )
    };

    public static NoteList noteList = new NoteList(notes);

    static public CardLayout lt = new CardLayout(25, 25);
    static public JFrame main_frame = new JFrame("Notepad");
    static public JPanel rp = new JPanel();
    static public HomeMenu hm = new HomeMenu();
    static public NoteListGUI nl = new NoteListGUI(noteList);
    static public ReadNote rn;
    static public EditNote en;

    static public void reloadApp(){
        rp.removeAll();

        hm = new HomeMenu();
        nl = new NoteListGUI(noteList);

        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);
        rp.add(hm, 0);
        rp.add(nl, 1);
        lt.addLayoutComponent(hm, "HomeMenu");
        lt.addLayoutComponent(nl, "NoteList");
        main_frame.add(rp);
        main_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main_frame.setVisible(true);
    }


    public static void main(String[] args) {
        /*NoteList nl = new NoteList();
        System.out.println(nl.getListLength());
        nl.setNoteList(notes);
        System.out.println(nl.getListLength());

        for(Note note : nl.getNoteList()){
            note.showNote();
        }
        nl.removeNote(3);
        System.out.println(nl.getListLength());
        for(Note note : nl.getNoteList()){
            note.showNote();
        }
        nl.addNote(
                new Note(
                        "Bruh",
                        "Plater śmierdzi",
                        true
                )
        );
        System.out.println(nl.getListLength());
        for(Note note : nl.getNoteList()){
            note.showNote();
        }*/
        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);
        rp.add(hm, 0);
        rp.add(nl, 1);
        lt.addLayoutComponent(hm, "HomeMenu");
        lt.addLayoutComponent(nl, "NoteList");
        main_frame.add(rp);
        main_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main_frame.setVisible(true);


    }
}