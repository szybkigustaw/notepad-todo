import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static FileHandler fh;
    public static File settings_file;
    public static NoteList noteList;
    public static NoteList readNoteList;

    static public CardLayout lt = new CardLayout(25, 25);
    static public JFrame main_frame = new JFrame("Notepad");
    static public JPanel rp = new JPanel();
    static public HomeMenu hm;
    static public NoteListGUI nl;
    static public ReadNote rn;
    static public EditNote en;
    static public boolean hidden_mode = false;
    static public String current_window = null;
    static public String password = null;
    public static String default_path = "";

    static public void reloadApp(boolean reloadList){
        String temp_current_window = current_window;
        rp.removeAll();

        if(reloadList) {
                NoteList hold = new NoteList(noteList.getNoteList(), hidden_mode ? NoteList.HIDDEN : NoteList.PUBLIC);
                nl = new NoteListGUI(hold);
        }

        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);
        rp.add(hm, 0);
        if(nl != null) rp.add(nl, 1);
        if(rn != null) rp.add(rn, rp.getComponentCount());
        if(en != null) rp.add(en, rp.getComponentCount());
        lt.addLayoutComponent(hm, "HomeMenu");
        if(nl != null) lt.addLayoutComponent(nl, "NoteList");
        if(rn != null) lt.addLayoutComponent(rn, "ReadNote");
        if(en != null) lt.addLayoutComponent(en, "EditNote");
        main_frame.add(rp);
        main_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        main_frame.setVisible(true);
        lt.show(rp, temp_current_window);
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
                            Main.reloadApp(true);
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
                        Main.reloadApp(true);
                }
            } catch (NoSuchAlgorithmException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void loadDefault(){
        settings_file = new File(String.format("%s%s.settings.txt", System.getProperty("user.home"), System.getProperty("file.separator")));
        if(!settings_file.exists()){
            JOptionPane.showMessageDialog(main_frame, "Poczekaj. Przygotowuję aplikację po raz pierwszy...", "Pierwsze uruchomienie", JOptionPane.PLAIN_MESSAGE);
            noteList = new NoteList(new Note[0], NoteList.FULL);
            try{
                if(settings_file.createNewFile()){
                    JOptionPane.showMessageDialog(
                            main_frame,
                            "Utworzono nowy plik konfiguracyjny. Możesz zacząć korzystać z aplikacji.\nHave fun!",
                            "Pierwsze uruchomienie",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } catch(IOException ex){
                JOptionPane.showMessageDialog(
                        main_frame,
                        ex.getMessage(),
                        "Wewnętrzny błąd aplikacji 1",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            if (!settings_file.canRead()) {
                JOptionPane.showMessageDialog(
                        main_frame,
                        "Nie można odczytać pliku konfiguracyjnego. Przywracam ustawienia domyślne.",
                        "Wewnętrzny błąd aplikacji 2",
                        JOptionPane.ERROR_MESSAGE
                );
                try {
                    if(settings_file.createNewFile()){
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Przywrócono ustawienia domyślne",
                                "Odczyt ustawień",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Wewnętrzny błąd aplikacji 3",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                try {
                    FileReader fr = new FileReader(settings_file);
                    Scanner fs = new Scanner(fr);
                    if (fs.hasNextLine()) {
                        default_path = fs.nextLine();
                    }
                    if(default_path.equals("")){
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Ścieżka dostępu do pliku jest pusta. Musisz sam załadować swoje notatki.",
                                "Wczytywanie domyślnego pliku notatek",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }

                    fh = new FileHandler(new File(default_path));
                    noteList = new NoteList(fh.parseDocToNotes().getNoteList(), NoteList.FULL);
                    readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Wewnętrzny błąd aplikacji 4",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    public static void handleClose(){
        if(!NoteList.areNoteListsEqual(noteList, readNoteList )){
            int to_save = JOptionPane.showConfirmDialog(
                    Main.main_frame,
                    "Masz niezapisane dane, które przy wyjściu zostaną utracone. Czy chcesz je zapisać?",
                    "Wyjdź z aplikacji",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            if(to_save == JOptionPane.YES_OPTION){
                if(noteList.getListLength() < 1){
                    JOptionPane.showMessageDialog(main_frame, "Nie ma nic do zapisania", "Zapisywanie pliku", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
                int i = fc.showSaveDialog(main_frame);
                if(i == JFileChooser.APPROVE_OPTION){
                    try {
                        fh = new FileHandler(fc.getSelectedFile(), true);
                        fh.parseToFile(noteList);
                        FileWriter settings_writer = new FileWriter(settings_file);
                        settings_writer.write(default_path);
                        settings_writer.close();
                    } catch(FailedToWriteToFileException ex){
                        JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Zapisywanie pliku",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    } catch(IOException ex) {
                        JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Zapisywanie pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Doszło do błędu w trakcie zapisu domyślnego pliku notatek. " +
                                        "Przy następnym uruchomieniu konieczne będzie ręczne załadowanie" +
                                        "pliku z notatkami.",
                                "Zapisywanie pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                }
                System.exit(0);
            } else if(to_save == JOptionPane.NO_OPTION){
                System.exit(0);
            }
        } else {
            try{
                FileWriter settings_writer = new FileWriter(settings_file);
                settings_writer.write(default_path);
                settings_writer.close();
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(
                        main_frame,
                        ex.getMessage(),
                        "Zapisywanie pliku domyślnego",
                        JOptionPane.ERROR_MESSAGE
                );
                JOptionPane.showMessageDialog(
                        main_frame,
                        "Doszło do błędu w trakcie zapisu domyślnego pliku notatek. " +
                                "Przy następnym uruchomieniu konieczne będzie ręczne załadowanie" +
                                "pliku z notatkami.",
                        "Zapisywanie pliku domyślnego",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        loadDefault();
        hm = new HomeMenu();

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("Plik");
        JMenuItem open = new JMenuItem("Otwórz plik");
        JMenuItem save = new JMenuItem("Zapisz do pliku");

        open.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
            int i = fc.showOpenDialog(main_frame);
            if(i==JFileChooser.APPROVE_OPTION){
                fh = new FileHandler(fc.getSelectedFile());
                NoteList fetched_notes = fh.parseDocToNotes();
                if (fetched_notes != null) {
                    noteList.setNoteList(fh.parseDocToNotes().getNoteList());
                    Main.reloadApp(true);
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie wczytano notatki z pliku", "Wczytywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                    int save_path_to_default = JOptionPane.showConfirmDialog(
                        main_frame,
                        "Czy chcesz zapisać ten plik z notatkami jako plik domyślny?",
                        "Domyślny plik z notatkami",
                        JOptionPane.YES_NO_OPTION
                    );
                    if(save_path_to_default == JOptionPane.YES_OPTION){
                        default_path = fh.getFile_path();
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Zapisano",
                                "Zapisywanie domyślnego pliku",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
                if (fetched_notes != null && password == null){
                    int password_doChange = JOptionPane.showConfirmDialog(main_frame, "Wygląda na to że hasło nie zostało ustawione. Czy chcesz je ustawić?", "Brak hasła", JOptionPane.YES_NO_OPTION);
                    if(password_doChange == JOptionPane.YES_OPTION){
                        changePassword();
                    }
                }
            }
        });

        save.addActionListener(e -> {
            if(noteList.getListLength() < 1){
                JOptionPane.showMessageDialog(main_frame, "Nie ma nic do zapisania", "Zapisywanie pliku", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
            int i = fc.showSaveDialog(main_frame);
            if(i == JFileChooser.APPROVE_OPTION){
                try {
                    fh = new FileHandler(fc.getSelectedFile(), true);
                    fh.parseToFile(noteList);
                    FileWriter settings_writer = new FileWriter(settings_file);
                    settings_writer.write(default_path);
                    settings_writer.close();
                } catch(FailedToWriteToFileException ex){
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Zapisywanie pliku",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                } catch(IOException ex) {
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Zapisywanie pliku domyślnego",
                            JOptionPane.ERROR_MESSAGE
                    );
                    JOptionPane.showMessageDialog(
                            main_frame,
                            "Doszło do błędu w trakcie zapisu domyślnego pliku notatek. " +
                                    "Przy następnym uruchomieniu konieczne będzie ręczne załadowanie" +
                                    "pliku z notatkami.",
                            "Zapisywanie pliku domyślnego",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenuItem select_default = new JMenuItem("Wybierz domyślny plik");
        select_default.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));

            File new_default = fc.getSelectedFile();
            default_path = new_default.getPath();
            JOptionPane.showMessageDialog(main_frame,
                    "Zapisano nowy domyślny plik",
                    "Zapisywanie domyślnego pliku",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem exit = new JMenuItem("Wyjdź");
        exit.addActionListener(e -> {
            handleClose();
        });

        file.add(open); file.add(save); file.add(new JSeparator(JSeparator.HORIZONTAL)); file.add(select_default);
        file.add(new JSeparator(JSeparator.HORIZONTAL)); file.add(exit);

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

        JMenuItem add_note = new JMenuItem("Dodaj notatkę");
        add_note.addActionListener(e -> {
            en = new EditNote();
            reloadApp(true);
        });

        notes.add(add_note); notes.add(sort_menu);

        JMenu security = new JMenu("Zabezpieczenia");

        JMenuItem password_change = new JMenuItem("Dodaj / Zmień hasło");
        password_change.addActionListener(e -> changePassword());

        JMenuItem password_remove = new JMenuItem("Usuń hasło");
        password_remove.addActionListener(e -> {
            String temp_current_window = Main.current_window;
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
                hidden_mode = false;

                Main.current_window = temp_current_window;
                Main.reloadApp(true);
                JOptionPane.showMessageDialog(main_frame, "Hasło zostało pomyślnie usunięte", "Kasowanie hasła", JOptionPane.INFORMATION_MESSAGE);

            } catch (SecurityException ex){
                JOptionPane.showMessageDialog(main_frame, ex.getMessage(), "Nie można usunąć hasła", JOptionPane.ERROR_MESSAGE);
            }
        });

        security.add(password_change); security.add(password_remove);


        JMenu info = new JMenu("Informacje");

        JMenuItem author = new JMenuItem("O autorze");
        author.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    "Stworzone przez: \n\n Michał Mikuła",
                    "O autorze",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JMenuItem version = new JMenuItem("O tej wersji");
        version.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    "Notepad-Todo \n\n Wersja 1.0.0 \n Copyright Michał Mikuła 2023",
                    "O aplikacji",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JMenuItem github_repo = new JMenuItem("Open Source - Repozytorium");
        github_repo.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/Polaczeq22/notepad-todo"));
            } catch(IOException | URISyntaxException ex){
                JOptionPane.showMessageDialog(
                        Main.main_frame,
                        ex.getMessage(),
                        "Wewnętrzny błąd aplikacji",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        info.add(author); info.add(version); info.add(github_repo);

        mb.add(file); mb.add(notes); mb.add(security); mb.add(info);
        main_frame.setJMenuBar(mb);

        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);

        rp.add(hm, 0);
        lt.addLayoutComponent(hm, "HomeMenu");
        main_frame.add(rp);
        main_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        main_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                handleClose();
            }
        });

        main_frame.setVisible(true);
    }
}