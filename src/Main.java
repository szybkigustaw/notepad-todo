import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class Main {
    public static FileHandler fh = new FileHandler("/home/plq/Documents/notatki.xml");
    public static NoteList noteList = new NoteList(fh.parseDocToNotes().getNoteList(), NoteList.FULL);

    static public CardLayout lt = new CardLayout(25, 25);
    static public JFrame main_frame = new JFrame("Notepad");
    static public JPanel rp = new JPanel();
    static public HomeMenu hm = new HomeMenu();
    static public NoteListGUI nl = new NoteListGUI(noteList);
    static public ReadNote rn = new ReadNote();
    static public EditNote en = new EditNote();
    static public boolean hidden_mode = false;
    static public String current_window = "HomeMenu";

    static public String password = "essa123";

    static public void reloadApp(boolean reloadList){
        rp.removeAll();

        hm = new HomeMenu();
        if(reloadList) {
                NoteList hold = new NoteList(noteList.getNoteList(), hidden_mode ? NoteList.HIDDEN : NoteList.PUBLIC);
                nl = new NoteListGUI(hold);
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
        lt.show(rp, current_window);
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

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("Plik");
        JMenuItem open = new JMenuItem("Otwórz plik");
        JMenuItem save = new JMenuItem("Zapisz do pliku");

        open.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
            int i = fc.showOpenDialog(main_frame);
            if(i==JFileChooser.APPROVE_OPTION){
                try {
                    fh.setXml_file(fc.getSelectedFile());
                    fh.setFile_path(fc.getSelectedFile().getPath());
                    fh.parseXml();
                    noteList.setNoteList(fh.parseDocToNotes().getNoteList());
                    Main.reloadApp(true);
                } catch(Exception ex) {
                    System.out.println("Bruh");
                } finally {
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie wczytano notatki z pliku", "Wczytywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        save.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
            int i = fc.showSaveDialog(main_frame);
            if(i == JFileChooser.APPROVE_OPTION){
                try{
                    fh.setXml_file(fc.getSelectedFile());
                    fh.setFile_path(fc.getSelectedFile().getPath());
                    fh.parseToFile(noteList);
                } catch (Exception ex) {
                    System.out.println("bruh");
                } finally {
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        file.add(open); file.add(save);

        JMenu notes = new JMenu("Notatki");
        JMenu sort_menu = new JMenu("Sortuj");
        JMenuItem sort_by_createdate = new JMenuItem("wg daty utworzenia");
        JMenuItem sort_by_moddate = new JMenuItem("wg daty modyfikacji");
        JMenuItem sort_by_label = new JMenuItem("wg etykiety");
        JMenuItem sort_by_type = new JMenuItem("wg typu");
        JMenuItem sort_by_completion = new JMenuItem("wg stopnia ukończenia");
        JCheckBoxMenuItem sort_descending = new JCheckBoxMenuItem("Sortuj rosnąco", true);

        sort_by_createdate.addActionListener(e -> { noteList.sortNote(NoteList.BY_CREATE_DATE, sort_descending.getState()); reloadApp(true); });
        sort_by_moddate.addActionListener(e -> { noteList.sortNote(NoteList.BY_MOD_DATE, sort_descending.getState()); reloadApp(true); });
        sort_by_label.addActionListener(e -> { noteList.sortNote(NoteList.BY_LABEL, sort_descending.getState()); reloadApp(true); });
        sort_by_type.addActionListener(e -> { noteList.sortNote(NoteList.BY_TYPE, sort_descending.getState()); reloadApp(true); });
        sort_by_completion.addActionListener(e -> { noteList.sortNote(NoteList.BY_COMPLETION, sort_descending.getState()); reloadApp(true); });

        sort_menu.add(sort_by_createdate);
        sort_menu.add(sort_by_moddate);
        sort_menu.add(sort_by_label);
        sort_menu.add(sort_by_type);
        sort_menu.add(sort_by_completion);
        sort_menu.add(sort_descending);

        notes.add(sort_menu);

        mb.add(file); mb.add(notes);
        main_frame.setJMenuBar(mb);
    }
}