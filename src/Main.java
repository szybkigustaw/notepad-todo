import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Main {
    public static FileHandler fh = new FileHandler("/home/plq/Documents/notatki.xml");
    public static NoteList noteList = new NoteList(new Note[0], NoteList.FULL);

    static public CardLayout lt = new CardLayout(25, 25);
    static public JFrame main_frame = new JFrame("Notepad");
    static public JPanel rp = new JPanel();
    static public HomeMenu hm = new HomeMenu();
    static public NoteListGUI nl = new NoteListGUI(noteList);
    static public ReadNote rn = new ReadNote();
    static public EditNote en = new EditNote();
    static public boolean hidden_mode = false;
    static public String current_window = "HomeMenu";

    static public String password = "";

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

    public static String hashString(String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(string.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hash);

        StringBuilder hex_string = new StringBuilder(number.toString(16));

        while(hex_string.length() < 64){
            hex_string.insert(0, '0');
        }

        return hex_string.toString();
    }
    
    public static void changePassword(){
        if(password != null) {
            try {
                String old_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj stare hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);
                old_password = hashString(old_password);
                if (Objects.equals(password, old_password)) {
                    String new_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);
                    new_password = hashString(new_password);
                    if (new_password.equals(old_password)) {
                        JOptionPane.showMessageDialog(Main.main_frame, "Hasło nie może być takie same jak poprzednie", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String new_password_retype = JOptionPane.showInputDialog(Main.main_frame, "Podaj jeszcze raz nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);
                        new_password_retype = hashString(new_password_retype);
                        if (!Objects.equals(new_password, new_password_retype)) {
                            JOptionPane.showMessageDialog(Main.main_frame, "Hasła są różne", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                        } else {
                            password = new_password;
                            JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie zmieniono hasło", "Zmiana hasła", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(Main.main_frame, "Błędne hasło", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NoSuchAlgorithmException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            try{
                String new_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);
                new_password = hashString(new_password);
                String new_password_retype = JOptionPane.showInputDialog(Main.main_frame, "Podaj jeszcze raz nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);
                new_password_retype = hashString(new_password_retype);
                if (!Objects.equals(new_password, new_password_retype)) {
                    JOptionPane.showMessageDialog(Main.main_frame, "Hasła są różne", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                } else {
                        password = new_password;
                        JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie dodano hasło", "Zmiana hasła", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NoSuchAlgorithmException ex){
                System.out.println(ex.getMessage());
            }
        }
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
                    fh.setXml_file(fc.getSelectedFile());
                    fh.setFile_path(fc.getSelectedFile().getPath());
                    fh.parseXml();
                    NoteList fetched_notes = fh.parseDocToNotes();
                    if (fetched_notes != null) {
                        noteList.setNoteList(fh.parseDocToNotes().getNoteList());
                        Main.reloadApp(true);
                        JOptionPane.showMessageDialog(main_frame, "Pomyślnie wczytano notatki z pliku", "Wczytywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (password == null){
                        int password_doChange = JOptionPane.showConfirmDialog(main_frame, "Wygląda na to że hasło nie zostało ustawione. Czy chcesz je ustawić?", "Brak hasła", JOptionPane.YES_NO_OPTION);
                        if(password_doChange == JOptionPane.YES_OPTION){
                            changePassword();
                        } 
                    }
            }
        });

        save.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
            int i = fc.showSaveDialog(main_frame);
            if(i == JFileChooser.APPROVE_OPTION){
                    fh.setXml_file(fc.getSelectedFile());
                    fh.setFile_path(fc.getSelectedFile().getPath());
                    fh.parseToFile(noteList);
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
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

        JMenuItem password_change = new JMenuItem("Dodaj / Zmień hasło");
        password_change.addActionListener(e -> changePassword());

        JMenuItem password_remove = new JMenuItem("Usuń hasło");
        password_remove.addActionListener(e -> {
            try{
                if(password == null){
                    JOptionPane.showMessageDialog(main_frame, "Hasło już nie istnieje", "Nie można usunąć hasła", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                NoteList temp = new NoteList(noteList.getNoteList(), NoteList.FULL);
                for(int i = 0; i < temp.getListLength(); i++){
                    if(temp.getNote(i).getHidden()){
                        throw new SecurityException("Dalej istnieją ukryte notatki. Usuń je lub upublicznij przed usunięciem hasła.");
                    }
                }
                password = null;
                JOptionPane.showMessageDialog(main_frame, "Hasło zostało pomyślnie usunięte", "Kasowanie hasła", JOptionPane.INFORMATION_MESSAGE);

            } catch (SecurityException ex){
                JOptionPane.showMessageDialog(main_frame, ex.getMessage(), "Nie można usunąć hasła", JOptionPane.ERROR_MESSAGE);
            }
        });

        notes.add(sort_menu); notes.add(password_change); notes.add(password_remove);

        mb.add(file); mb.add(notes);
        main_frame.setJMenuBar(mb);
    }
}