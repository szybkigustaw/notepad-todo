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

/**
 * Tworzy ramkę dla całej aplikacji oraz umieszcza w niej cztery okna. Kontroluje rozmiar okna alikacji, przechowuje listę notatek globalną dla całej aplikacji oraz informacje o ustawionym haśle.
 *
 * <p>Przechowuje dwie wersje listy notatek: obecną oraz ostatnią odczytaną z pliku (używanej przy determinowaniu, czy doszło do zmian w liście względem wczytanego pliku)</p>
 * <p>Wykorzystuje układ kartowy, który umożliwia wyświetlanie każdego okna aplikacji na całej powierzchni ramki.</p>
 * <p>Przechowuje również plik z ustawieniami domyślnymi, z których odczytuje ścieżkę do domyślnego pliku z notatkami.</p>
 *
 * @version 1.0
 * @author Michał Mikuła
 */
public class Main {
    /**
     * Obiekt odpowiedzialny za obsługę plików i odczyt notatek z nich
     */
    public static FileHandler fh;
    /**
     * Plik przechowujący dane konfiguracyjne: m.in. ścieżkę do domyślnego pliku z notatkami
     */
    public static File settings_file;
    /**
     * Globalna lista notatek dla całej aplikacji.
     */
    public static NoteList noteList;
    /**
     * Lista notatek stanowiąca punkt odniesienia dla sprawdzenia, czy doszło do zmiany listy notatek względem wczytanej z pliku.
     */
    public static NoteList readNoteList;
    /**
     * Obiekt reprezentujący układ kartowy. Stosowany do przełączania okien.
     */
    static public CardLayout lt = new CardLayout(25, 25);
    /**
     * Główna ramka aplikacji. Umieszczone są w niej okna przełączane przez układ kartowy.
     * @see #lt
     */
    static public JFrame main_frame = new JFrame("Notepad");
    /**
     * Panel stanowiący trzon wszystkich pozostałych okien. Stanowi ciało dla ramki, w którym umieszczone zostają okna.
     */
    static public JPanel rp = new JPanel();
    /**
     * Obiekt reprezentujący okno z menu głównym
     */
    static public HomeMenu hm;
    /**
     * Obiekt reprezentujący okno z listą notatek
     */
    static public NoteListGUI nl;
    /**
     * Obiekt reprezentujący okno podglądu notatki
     */
    static public ReadNote rn;
    /**
     * Obiekt reprezentujący okno edycji notatki
     */
    static public EditNote en;
    /**
     * Zmienna definiująca tryb widoku w aplikacji
     * (Wartość <i>true</i> dla <b>widoku ukrytego</b>)
     */
    static public boolean hidden_mode = false;
    /**
     * Ciąg znaków reprezentujący obecnie wyświetlane okno
     */
    static public String current_window = null;
    /**
     * Ciąg znaków przechowujący hasło (w formie zahaszowanej)
     */
    static public String password = null;
    /**
     * Ciąg znaków reprezentujący ścieżkę dostępu do domyślnego pliku z notatkami
     */
    public static String default_path = "";

    /**
     * Przeładowuje aplikację. Na żądanie użytkownika odświeża również listę notatek, implementując w niej tryb ukryty lub publiczny.
     * <p>Odświeżenie odbywa się poprzez:</p>
     * <ul>
     *     <li>Usunięcie wszystkich elementów z {@link #rp panelu głównego}</li>
     *     <li>Odbudowanie tego panelu</li>
     *     <li>Dodanie okien do niego w kolejności: {@link #hm menu główne}, {@link #nl lista notatek} oraz pozostałe okna</li>
     *     <li>Dodanie informacji o oknach i ich pozycjach do {@link #lt układu} (tu również przypisywane im są mnemoniczne ciągi znaków)</li>
     *     <li>Umieszczenie panelu głównego w {@link #main_frame ramce aplikacji} oraz przypisanie jej atrybutów</li>
     *     <li>Przełączenie okna w układzie na obecnie wyświetlane</li>
     * </ul>
     *
     * @param reloadList zmienna definiująća, czy lista notatek również ma zostać przeładowana przed przeładowaniem interfejsu (<i>true</i> jeśli tak)
     */
    static public void reloadApp(boolean reloadList){

        //Przechowanie informacji o obecnie otwartym oknie
        String temp_current_window = current_window;

        //Usunięcie wszystkich komponentów z panelu głównego
        rp.removeAll();

        //Jeśli zażądano przeładowania listy
        if(reloadList) {
                //Stwórz kopię listy notatek, implementując w niej tryb dostępu
                NoteList hold = new NoteList(noteList.getNoteList(), hidden_mode ? NoteList.HIDDEN : NoteList.PUBLIC);
                //Stwórz od nowa okno listy notatek na podstawie tej kopii
                nl = new NoteListGUI(hold);
        }


        //Ustaw układ dla panelu głównego
        rp.setLayout(lt);

        //Ustaw rozmiar okna
        rp.setBounds(0,0,1200,800);

        //Ustaw widoczność panelu
        rp.setVisible(true);

        //Dodaj okna panelu głównego
        rp.add(hm, 0);
        if(nl != null) rp.add(nl, 1);
        if(rn != null) rp.add(rn, rp.getComponentCount());
        if(en != null) rp.add(en, rp.getComponentCount());

        //Dodaj informację o istniejących oknach do układu i nadaj im mnemoniczne nazwy
        lt.addLayoutComponent(hm, "HomeMenu");
        if(nl != null) lt.addLayoutComponent(nl, "NoteList");
        if(rn != null) lt.addLayoutComponent(rn, "ReadNote");
        if(en != null) lt.addLayoutComponent(en, "EditNote");

        //Umieść panel główny w ramce
        main_frame.add(rp);

        //Ustaw domyślne zachowanie przy wyłączaniu aplikacji (aplikacja sama wykona potrzebne kroki do jej prawidłowego zamknięcia)
        main_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        //Ustaw widoczność ramki
        main_frame.setVisible(true);

        //Wyświetl obecne okno
        lt.show(rp, temp_current_window);
    }

    /**
     * Szyfruje podany w parametrach ciąg znaków.
     * @param string Ciąg znaków do zaszyfrowania
     * @return Hash ciągu znaków
     * @throws NoSuchAlgorithmException Błąd wywoływany w momencie, gdy podany w metodzie algorytm szyfrowania nie istnieje (w praktyce nigdy niewyrzucany błąd)
     */
    public static String hashString(String string) throws NoSuchAlgorithmException {

        //Stwórz obiekt klasy MessageDigest i przypisz mu algorytm SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        //Skonwertuj ciąg znaków do formy bajtowej i "straw" go przez algorytm
        byte[] hash = md.digest(string.getBytes(StandardCharsets.UTF_8));

        //Skonwertuj uzyskaną tablicę bitów na liczbę całkowitą dużego rozmiaru
        BigInteger number = new BigInteger(1, hash);

        /* Utwórz obiekt klasy StringBuilder, w której umieszczona zostanie wcześniej uzyskana liczba,
        skonwertowana na ciąg znaków, przy wykorzystaniu systemu szesnastkowego */
        StringBuilder hex_string = new StringBuilder(number.toString(16));

        //Dopóki długość tak uzyskanego ciągu znaków jest mniejsza niż 64
        while(hex_string.length() < 64){

            //Dodawaj zera na końcu
            hex_string.insert(0, '0');
        }

        //Zwróć gotowy ciąg znaków
        return hex_string.toString();
    }

    /**
     * Dokonuje zmiany hasła w aplikacji.
     *
     * <p>Wyświetla okno kontekstowe, w którym prosi użytkownika o podanie nowego hasłą. Dokonuje przy tym kilku sprawdzeń.</p>
     * <ul>
     *     <li>Czy hasło w ogóle jest ustawione (jeśli nie, tworzy zupełnie nowe hasło)</li>
     *     <li>Czy użytkownik zna wcześniejsze hasło</li>
     *     <li>Czy hasło już nie było wcześniej ustawione</li>
     *     <li>Czy nie doszło do pomyłki przy weryfikacji wpisanego nowego hasła</li>
     * </ul>
     */
    public static void changePassword(){

        //Jeśli hasło istnieje
        if(password != null) {

            //Początek kodu z prawdopodobnymi wyjątkami
            try {

                //Wyświetl komunikat proszący użytkownika o podanie starego hasła
                String old_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj stare hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                //Zahaszuj uzyskane hasło
                old_password = hashString(old_password);

                //Jeśli uzyskane hasło zgadza się z obecnie przechowywanym w aplikacji hasłem
                if (Objects.equals(password, old_password)) {

                    //Wyświetl komunikat proszący użytkownika o podanie nowego hasła
                    String new_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                    //Zahaszuj uzyskany wynik
                    new_password = hashString(new_password);

                    //Jeśli podane hasło jest równe obecnemu hasłu
                    if (new_password.equals(old_password)) {

                        //Wyświetl komunikat o tym fakcie
                        JOptionPane.showMessageDialog(Main.main_frame, "Hasło nie może być takie same jak poprzednie", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);

                    }

                    //Jeśli nie jest równe
                    else {

                        //Wyświetl komunikat proszący użytkownika o ponowne wprowadzenie nowego hasła
                        String new_password_retype = JOptionPane.showInputDialog(Main.main_frame, "Podaj jeszcze raz nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                        //Zahaszuj uzyskany wynik
                        new_password_retype = hashString(new_password_retype);

                        //Jeśli ponownie wprowadzone hasło nie zgadza się z poprzednio wpisanym
                        if (!Objects.equals(new_password, new_password_retype)) {

                            //Wyświetl komunikat o tym fakcie
                            JOptionPane.showMessageDialog(Main.main_frame, "Hasła są różne", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                        }

                        //Jeśli są równe
                        else {

                            //Ustaw nowe hasło
                            password = new_password;

                            //Wyświetl komunikat o powodzeniu operacji
                            JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie zmieniono hasło", "Zmiana hasła", JOptionPane.INFORMATION_MESSAGE);

                            //Przeładuj aplikację
                            Main.reloadApp(true);
                        }
                    }
                    //Jeśli wprowadzono błędne obecne hasło
                } else {

                    //Wyświetl o tym komunikat
                    JOptionPane.showMessageDialog(Main.main_frame, "Błędne hasło", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                }

                //Jeśli wystąpi wyjątek o błędynm algorytmie
            } catch (NoSuchAlgorithmException ex) {

                //Wyrzuć wiadomość wyjątku do konsoli
                System.out.println(ex.getMessage());
            }

            //Jeśli nie
        } else {

            //Początek kodu z prawdopodobnymi wyjątkami
            try{

                //Wyświetl okno, w którym użytkownik ma wpisać swoje hasło
                String new_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                //Zahaszuj je
                new_password = hashString(new_password);

                //Wyświetl okno, w którym użytkownik ma ponownie wpisać swoje hasło
                String new_password_retype = JOptionPane.showInputDialog(Main.main_frame, "Podaj jeszcze raz nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                //Zahaszuj i to hasło
                new_password_retype = hashString(new_password_retype);

                //Jeśli obydwa hasze się nie zgadzają
                if (!Objects.equals(new_password, new_password_retype)) {

                    //Wyświetl komunikat o tym fakcie
                    JOptionPane.showMessageDialog(Main.main_frame, "Hasła są różne", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);

                    //Jeśli są równe
                } else {

                    //Przypisz podaną wartość hasła
                    password = new_password;

                    //Wyświetl komunikat o powodzeniu akcji
                    JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie dodano hasło", "Zmiana hasła", JOptionPane.INFORMATION_MESSAGE);

                    //Przeładuj aplikację
                    Main.reloadApp(true);
                }
                //Jeśli wystąpi wyjątek o błędnym algorytmie
            } catch (NoSuchAlgorithmException ex){

                    //Przekaź wiadomość z błędu do konsoli
                    System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Ładuje wartości domyślne ustawień aplikacji. Wywołuje również odczyt pliku z domyślnej ścieżki.
     *
     * <p>Jeśli pliku z ustawieniami nie ma, sam tworzy nowy plik ustawień i uzupełnia go wartośiami domyślnymi</p>
     */
    public static void loadDefault(){

        //Zapisz reprezentację pliku z ustawieniami, uzyskanego z domyślnej ścieżki domowej użytkownika systemowego
        settings_file = new File(String.format("%s%s.settings.txt", System.getProperty("user.home"), System.getProperty("file.separator")));

        //Jeśli plik nie istnieje
        if(!settings_file.exists()){

            //Wyświetl komunikat o tworzeniu nowego pliku
            JOptionPane.showMessageDialog(main_frame, "Poczekaj. Przygotowuję aplikację po raz pierwszy...", "Pierwsze uruchomienie", JOptionPane.PLAIN_MESSAGE);

            //Stwórz puste listy notatek
            noteList = new NoteList(new Note[0], NoteList.FULL);
            readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);

            //Początek kodu z ewentualnymi wyjątkami
            try{

                //Jeśli plik udało się utworzyć
                if(settings_file.createNewFile()){

                    //Wyświetl komunikat o utworzeniu nowej konfiguracji
                    JOptionPane.showMessageDialog(
                            main_frame,
                            "Utworzono nowy plik konfiguracyjny. Możesz zacząć korzystać z aplikacji.\nHave fun!",
                            "Pierwsze uruchomienie",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                //Jeśli wystąpi wyjątek związany z operacjami I/O
            } catch(IOException ex){

                //Wyświetl okno z treścią wyjątku
                JOptionPane.showMessageDialog(
                        main_frame,
                        ex.getMessage(),
                        "Wewnętrzny błąd aplikacji",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            //Jeśli plik istnieje
        } else {

            //Jeśli plik jest nie do odczytu
            if (!settings_file.canRead()) {

                //Wyświetl o tym komunikat
                JOptionPane.showMessageDialog(
                        main_frame,
                        "Nie można odczytać pliku konfiguracyjnego. Przywracam ustawienia domyślne.",
                        "Wewnętrzny błąd aplikacji",
                        JOptionPane.ERROR_MESSAGE
                );

                //Początek kodu z prawdopodobnymi wyjątkami
                try {

                    //Jeśli utworzenie pliku powiodło się
                    if(settings_file.createNewFile()){

                        //Wyświetl o tym fakcie komunikat
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Przywrócono ustawienia domyślne",
                                "Odczyt ustawień",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                    //Jeśli wystąpi wyjątek związany z operacjami IO
                } catch (IOException ex) {

                    //Wyświetl komunikat z treścią błędu
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Wewnętrzny błąd aplikacji",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                //Jeśli plik jest zdatny do odczytu
            } else {

                //Początek kodu z prawdopodobnymi wyjątkami
                try {

                    //Utwórz instancje klas odczytującej plik oraz skanera
                    FileReader fr = new FileReader(settings_file);
                    Scanner fs = new Scanner(fr);

                    //Jeśli plik nie zawiera pustych linijek
                    if (fs.hasNextLine()) {

                        //Zwróć odczytaną ścieżkę
                        default_path = fs.nextLine();
                    }

                    //Jeśli odczytana ścieżka jest pusta
                    if(default_path.equals("")){

                        //Wyświetl komunikat o tym fakcie
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Ścieżka dostępu do pliku jest pusta. Konieczne ręczne wczytanie notatek.",
                                "Wczytywanie domyślnego pliku notatek",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }

                    //Jeśli pod odczytaną ścieżką nie ma żadnego pliku
                    if(!new File(default_path).exists()){

                        //Wyświetl komunikat o tym fakcie
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Wczytywanie pliku domyślnego zakończyło się powodzeniem. Konieczne jest ręczne wczytanie żądanego pliku.",
                                "Błąd odczytu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );

                        //Zakończ działanie metody
                        return;
                    }

                    //Utwórz instancję klasy obsługującej pliki z notatkami
                    fh = new FileHandler(new File(default_path));

                    //Jeśli notatki na tej liście są obecne, przypisz je do buforowej zmiennej. Jeśli nie, utwórz pustą listę
                    NoteList parsed_notes = new NoteList(fh.parseDocToNotes() != null ? fh.parseDocToNotes().getNoteList() : new Note[0], NoteList.FULL);

                    //Jeśli buforowa lista ma długość większą niż jeden, uzupełnij obydwie listy aplikacji jej danymi
                    noteList = parsed_notes.getListLength() > 0 ? parsed_notes : noteList;
                    readNoteList = parsed_notes.getListLength() > 0 ? new NoteList(noteList.getNoteList(), NoteList.FULL) : readNoteList;

                }

                //Jeśli wystąpi wyjątek związany z operacjami I/O
                catch (IOException ex) {

                    //Wyświetl komunikat o błędzie
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Wewnętrzny błąd aplikacji",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Sprawdza przed zamknięciem lekcji, czy są jakieś niezapisane dane. Jeśli są, pyta użytkownika, czy nie chce ich zapisać. Następnie zamyka aplikację.
     */
    public static void handleClose(){

        //Jeśli lista notatek i lista notatek odczytanych z ostatniego pliku nie są równe, ergo doszło do zmian
        if(!NoteList.areNoteListsEqual(noteList, readNoteList)){

            //Wyświetl komunikat z pytaniem, czy obecne zmiany mają zostać zapisane
            int to_save = JOptionPane.showConfirmDialog(
                    Main.main_frame,
                    "Masz niezapisane dane, które przy wyjściu zostaną utracone. Czy chcesz je zapisać?",
                    "Wyjdź z aplikacji",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            //Jeśli użytkownik wyrazi wolę zapisania danych
            if(to_save == JOptionPane.YES_OPTION){

                //Jeśli lista notatek jest pusta
                if(noteList.getListLength() < 1){

                    //Wyświetl komunikat o braku treści do zapisania
                    JOptionPane.showMessageDialog(main_frame, "Nie ma nic do zapisania", "Zapisywanie pliku", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Stwórz instancję klasy okna wyboru plików
                JFileChooser fc = new JFileChooser();

                //Ustaw filtr rozszerzeń plików na pliki XML
                fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));

                //Wyświetl okno zapisu pliku.
                int i = fc.showSaveDialog(main_frame);

                //Jeśli wybrano plik do zapisu
                if(i == JFileChooser.APPROVE_OPTION){
                    try {

                        //Stwórz nową instancję klasy do obsługi plików w trybie zapisu
                        fh = new FileHandler(fc.getSelectedFile(), true);

                        //Skonwertuj listę notatek do formy dokumentu XML i zapisz je w pliku przechowywanym w obiekcie
                        fh.parseToFile(noteList);

                        //Stwórz nową instancję klasy zapisującej do pliku i podaj jej ścieżkę do pliku konfiguracyjnego
                        FileWriter settings_writer = new FileWriter(settings_file);

                        //Zapisz w niej ścieżkę do domyślnego pliku z notatkami
                        settings_writer.write(default_path);
                        settings_writer.close();
                    }

                    //Jeśli wystąpi wyjątek nieudanego zapisu do pliku
                    catch(FailedToWriteToFileException ex){

                        //Wyświetl komunikat z wiadomością błędu
                        JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Zapisywanie pliku",
                                JOptionPane.ERROR_MESSAGE
                        );

                        //Zakończ działanie metody
                        return;
                    }

                    //Jeśli wystąpi wyjątek związany z operacjami I/O
                    catch(IOException ex) {

                        //Wyświetl komunikat z wiadomością błędu
                        JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Zapisywanie pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );

                        //Wyświetl komunikat o nieudanym zapisie ścieżki domyślnego pliku z notatkami
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Doszło do błędu w trakcie zapisu domyślnego pliku notatek. " +
                                        "Przy następnym uruchomieniu konieczne będzie ręczne załadowanie" +
                                        "pliku z notatkami.",
                                "Zapisywanie pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                    //Wyświetl komunikat o udanym zapisie notatek do pliku
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                }

                //Zakończ działanie aplikacji
                System.exit(0);

                //Jeśli zamknięto okno zapisu danych do pliku
            } else if(to_save == JOptionPane.NO_OPTION){

                //Zakończ działanie aplikacji
                System.exit(0);
            }
        }

        //Jeśli listy notatek są równe
        else {

            //Początek kodu z prawdopodobnymi wyjątkami
            try{

                //Stwórz instancję klasy zapisującej do pliku i podaj jej ścieżkę do pliku konfiguracyjnego
                FileWriter settings_writer = new FileWriter(settings_file);

                //Zapisz ścieżkę do domyślnego pliku z notatkami w pliku konfiguracyjnym
                settings_writer.write(default_path);
                settings_writer.close();
            }

            //Jeśli wystąpi wyjątek związany z operacjami I/O
            catch(IOException ex) {

                //Wyświetl komunikat z wiadomością błędu
                JOptionPane.showMessageDialog(
                        main_frame,
                        ex.getMessage(),
                        "Zapisywanie pliku domyślnego",
                        JOptionPane.ERROR_MESSAGE
                );

                //Wyświetl komunikat o nieudanym zapisie domyślnej ścieżki do pliku konfiguracyjnegi
                JOptionPane.showMessageDialog(
                        main_frame,
                        "Doszło do błędu w trakcie zapisu domyślnego pliku notatek. " +
                                "Przy następnym uruchomieniu konieczne będzie ręczne załadowanie" +
                                "pliku z notatkami.",
                        "Zapisywanie pliku domyślnego",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            //Zakończ działanie aplikacji
            System.exit(0);
        }
    }

    /**
     * Metoda główna aplikacji. Inicjalizuje wartości domyślne ustawień oraz listy notatek oraz buduje interfejs.
     * @param args Argumenty podawane przy uruchomieniu aplikacji (niestosowane)
     */
    public static void main(String[] args) {

        //Wywołanie metody inicjalizującej wartości domyślne ustawień i list notatek
        loadDefault();

        //Stwórz nowe okno menu głównego
        hm = new HomeMenu();

        //Stwórz nowy pasek menu
        JMenuBar mb = new JMenuBar();


        //Stwórz menu obsługi plików
        JMenu file = new JMenu("Plik");

        //Stwórz pozycje w menu służące do otwierania i zapisu plików
        JMenuItem open = new JMenuItem("Otwórz plik");
        JMenuItem save = new JMenuItem("Zapisz do pliku");

        //Dodaj logikę do przycisku otwierania plików
        open.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));
            int i = fc.showOpenDialog(main_frame);
            if(i==JFileChooser.APPROVE_OPTION){
                fh = new FileHandler(fc.getSelectedFile());
                NoteList fetched_notes = fh.parseDocToNotes();
                if (fetched_notes != null) {
                    if(noteList != null) noteList.setNoteList(fh.parseDocToNotes().getNoteList()); else noteList = new NoteList(fh.parseDocToNotes().getNoteList(), NoteList.FULL);
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

        //Dodaj logikę do przycisku zapisywania plików
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
                    if(readNoteList != null) readNoteList.setNoteList(noteList.getNoteList()); else readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);
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

        //Stwórz pozycję w menu wyboru domyślnego pliku z notatkami
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

        //Stwórz pozycję w menu służącą do wywołania metody odpowiedzialnej za wyjście z aplikacji
        JMenuItem exit = new JMenuItem("Wyjdź");

        //Dodaj logikę do przycisku opuszczenia aplikacji
        exit.addActionListener(e -> {

            //Wywołaj metodę obsługującą wyjście z aplikacji
            handleClose();
        });

        //Dodaj wszystkie pozycje do menu
        file.add(open); file.add(save); file.add(new JSeparator(JSeparator.HORIZONTAL)); file.add(select_default);
        file.add(new JSeparator(JSeparator.HORIZONTAL)); file.add(exit);

        //Stwórz menu opcji notatek
        JMenu notes = new JMenu("Notatki");

        //Stwó©z menu sortowania notatek
        JMenu sort_menu = new JMenu("Sortuj");

        //Stwórz pozycje w menu odpowiedzialne za różne rodzaje sortowania
        JMenuItem sort_by_createdate = new JMenuItem("wg daty utworzenia");
        JMenuItem sort_by_moddate = new JMenuItem("wg daty modyfikacji");
        JMenuItem sort_by_label = new JMenuItem("wg etykiety");
        JMenuItem sort_by_type = new JMenuItem("wg typu");
        JMenuItem sort_by_completion = new JMenuItem("wg stopnia ukończenia");

        //Stwórz pozycję w menu będącą check-boxem deinifującym kolejność sortowania
        JCheckBoxMenuItem sort_descending = new JCheckBoxMenuItem("Sortuj rosnąco", true);

        //Dodaj logikę do pozycji sortowania. Przypisz do każdej pozycji metodę sortującą listę notatek z innym wywoływanym trybem.
        //Następnie przeładuj aplikację wraz z listą notatek.
        sort_by_createdate.addActionListener(e -> { noteList.sortNote(NoteList.BY_CREATE_DATE, sort_descending.getState()); reloadApp(true); });
        sort_by_moddate.addActionListener(e -> { noteList.sortNote(NoteList.BY_MOD_DATE, sort_descending.getState()); reloadApp(true); });
        sort_by_label.addActionListener(e -> { noteList.sortNote(NoteList.BY_LABEL, sort_descending.getState()); reloadApp(true); });
        sort_by_type.addActionListener(e -> { noteList.sortNote(NoteList.BY_TYPE, sort_descending.getState()); reloadApp(true); });
        sort_by_completion.addActionListener(e -> { noteList.sortNote(NoteList.BY_COMPLETION, sort_descending.getState()); reloadApp(true); });

        //Wstaw pozycje do menu
        sort_menu.add(sort_by_createdate);
        sort_menu.add(sort_by_moddate);
        sort_menu.add(sort_by_label);
        sort_menu.add(sort_by_type);
        sort_menu.add(sort_by_completion);
        sort_menu.add(sort_descending);

        //Dodaj pozycję w menu odpowiedzialną za dodanie nowej notatki
        JMenuItem add_note = new JMenuItem("Dodaj notatkę");

        //Dodaj logikę do pozycji dodania nowej notatki
        add_note.addActionListener(e -> {

            //Stwórz nowe okno edycji notatki (konstruktor domyślny = nowa notatka)
            en = new EditNote();

            //Odśwież aplikację
            reloadApp(true);
        });

        //Dodaj pozycję dodania nowej notatki oraz menu sortowania do menu notatek
        notes.add(add_note); notes.add(sort_menu);

        //Stwórz menu zabezpieczeń
        JMenu security = new JMenu("Zabezpieczenia");

        //Stwórz pozycję w menu odpowiedzialną za zmianę / dodanie hasła
        JMenuItem password_change = new JMenuItem("Dodaj / Zmień hasło");

        //Dodaj logikę do pozycji. Wywołaj metodę zmiany hasła.
        password_change.addActionListener(e -> changePassword());

        //Stwórz pozycję w menu odpowiedzialną za kasowanie hasła
        JMenuItem password_remove = new JMenuItem("Usuń hasło");

        //Dodaj logikę do pozycji
        password_remove.addActionListener(e -> {

            //Przechowaj informacje o obecnie otwartym oknie
            String temp_current_window = Main.current_window;

            //Początek kodu z prawdopodobnymi wątkami
            try{

                //Jeśli hasło jest puste
                if(password == null){

                    //Wyświetl komunikat o tym fakcie i zakończ działanie metody
                    JOptionPane.showMessageDialog(main_frame, "Hasło już nie istnieje", "Nie można usunąć hasła", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                //Stwórz kopię obecnej listy notatek
                NoteList temp = new NoteList(noteList.getNoteList(), NoteList.FULL);

                //Jeśli na liście występuje notatka ukryta, zwróć wyjątek bezpieczeństwa
                for(int i = 0; i < temp.getListLength(); i++){
                    if(temp.getNote(i).getHidden()){
                        throw new SecurityException("Dalej istnieją ukryte notatki. Usuń je lub upublicznij przed usunięciem hasła.");
                    }
                }

                //Ustaw hasło na pustą wartość oraz przełącz aplikację w tryb jawny
                password = null;
                hidden_mode = false;

                //Ustaw informację o obecnym oknie na przechowaną wartość
                Main.current_window = temp_current_window;

                //Przeładuj aplikację
                Main.reloadApp(true);

                //Wyświetl komunikat o powodzeniu operacji
                JOptionPane.showMessageDialog(main_frame, "Hasło zostało pomyślnie usunięte", "Kasowanie hasła", JOptionPane.INFORMATION_MESSAGE);

            }

            //Jeśli wystąpi wyjątek bezpieczeństwa
            catch (SecurityException ex){

                //Wyświetl komunikat z wiadomością błędu
                JOptionPane.showMessageDialog(main_frame, ex.getMessage(), "Nie można usunąć hasła", JOptionPane.ERROR_MESSAGE);
            }
        });

        //Dodaj pozycje zmiany i kasowania hasła do menu bezpieczeństwa
        security.add(password_change); security.add(password_remove);


        //Stwórz menu informacji
        JMenu info = new JMenu("Informacje");

        //Stwórz pozycję w menu z informacjami o autorze
        JMenuItem author = new JMenuItem("O autorze");

        //Dodaj logikę do pozycji
        author.addActionListener(e -> {

            //Wyświetl komunikat z informacją o autorze
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    "Stworzone przez: \n\n Michał Mikuła",
                    "O autorze",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        //Stwórz pozycję w menu z informacją o wersji aplikacji
        JMenuItem version = new JMenuItem("O tej wersji");

        //Dodaj logikę do pozycji
        version.addActionListener(e -> {

            //Wyświetl komunikat z informacją o wersji aplikacji
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    "Notepad-Todo \n\n Wersja 1.0.0 \n Copyright Michał Mikuła 2023",
                    "O aplikacji",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        //Stwórz pozycję w menu z linkiem do repozytorium projektu
        JMenuItem github_repo = new JMenuItem("Open Source - Repozytorium");

        //Dodaj logikę do pozycji
        github_repo.addActionListener(e -> {

            //Początek kodu z prawdopodobnymi wyjątkami
            try {

                //Otwórz w przeglądarce systemowej adres repozytorium
                Desktop.getDesktop().browse(new URI("https://github.com/Polaczeq22/notepad-todo"));
            }

            //Jeśli wystąpi wątek związany z operacjami IO lub błędnej składni adresu URI
            catch(IOException | URISyntaxException ex){

                //Wyświetl komunikat z wiadomością wyjątku
                JOptionPane.showMessageDialog(
                        Main.main_frame,
                        ex.getMessage(),
                        "Wewnętrzny błąd aplikacji",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        //Dodaj pozycje informacyjne do menu
        info.add(author); info.add(version); info.add(github_repo);

        //Dodaj wszystkie menu do paska
        mb.add(file); mb.add(notes); mb.add(security); mb.add(info);

        //Ustaw w ramce pasek menu
        main_frame.setJMenuBar(mb);

        //Ustaw w panelu głównym układ, rozmiar oraz widoczność
        rp.setLayout(lt);
        rp.setBounds(0,0,1200,800);
        rp.setVisible(true);

        //Dodaj do panelu głównego menu główne, utwórz mnemoniczną reprezentację w układzie
        rp.add(hm, 0);
        lt.addLayoutComponent(hm, "HomeMenu");

        //Dodaj panel główny do ramki
        main_frame.add(rp);

        //Ustaw domyślną operację wyjścia z aplikacji na brak reakcji (aplikacja sama to kontroluje)
        main_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        //Dodaj logikę do ramki - reakcja na wywołanie sygnału zamknięcia aplikacji
        main_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                //Wywołaj metodę zamykającą aplikację
                handleClose();
            }
        });

        //Ustaw widoczność ramki
        main_frame.setVisible(true);
    }
}