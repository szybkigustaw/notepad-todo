import javax.swing.*;
import java.awt.*;

public class Main {
    public static FileHandler fh = new FileHandler("/home/plq/Documents/notatki.xml");
    public static NoteList noteList = new NoteList(fh.parseDocToNotes().getNoteList(), NoteList.FULL);

    static public CardLayout lt = new CardLayout(25, 25);
    static public JFrame main_frame = new JFrame("Notepad");
    static public JPanel rp = new JPanel();
    static public HomeMenu hm = new HomeMenu();
    static public NoteListGUI nl = new NoteListGUI(noteList, false);
    static public ReadNote rn = new ReadNote();
    static public EditNote en = new EditNote();

    static public String password = "essa123";

    static public void reloadApp(boolean reloadList, boolean reloadHidden){
        rp.removeAll();

        hm = new HomeMenu();
        if(reloadList) {
            if(!reloadHidden) {
                NoteList hold = new NoteList(noteList.getNoteList(), NoteList.PUBLIC);
                nl = new NoteListGUI(hold, false);
            }
            else {
                NoteList hold = new NoteList(noteList.getNoteList(), NoteList.HIDDEN);
                nl = new NoteListGUI(hold, true);
            }
        }

        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);
        rp.add(hm, 0);
        rp.add(nl, 1);
        rp.add(rn, 2);
        rp.add(en, 3);
        lt.addLayoutComponent(hm, "HomeMenu");
        lt.addLayoutComponent(nl, "NoteList");
        lt.addLayoutComponent(rn, "ReadNote");
        lt.addLayoutComponent(en, "EditNote");
        main_frame.add(rp);
        main_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        main_frame.setVisible(true);
    }


    public static void main(String[] args) {

        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);
        rp.add(hm, 0);
        rp.add(nl, 1);
        lt.addLayoutComponent(hm, "HomeMenu");
        lt.addLayoutComponent(nl, "NoteList");
        main_frame.add(rp);
        main_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        main_frame.setVisible(true);
    }
}