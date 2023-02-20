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
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

/**
 * Tworzy ramkę dla całej aplikacji oraz umieszcza w niej cztery okna. Kontroluje rozmiar okna aplikacji, przechowuje listę notatek globalną dla całej aplikacji oraz informacje o ustawionym haśle.
 *
 * <p>Przechowuje dwie wersje listy notatek: obecną oraz ostatnią odczytaną z pliku (używanej przy determinowaniu, czy doszło do zmian w liście względem wczytanego pliku)</p>
 * <p>Wykorzystuje układ kartowy, który umożliwia wyświetlanie każdego okna aplikacji na całej powierzchni ramki.</p>
 * <p>Przechowuje również plik z ustawieniami domyślnymi, z których odczytuje ścieżkę do domyślnego pliku z notatkami.</p>
 *
 * @version 1.1.1
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
     * Kolekcja danych przechowująca dane o ustawieniach aplikacji
     *
     * <p>Składa się z par klucz-wartość o następującej wartości: </p>
     * <ul>
     *     <li><b>default_path -</b> ścieżka dostępu do domyślnego pliku z notatkami </li>
     *     <li><b>auto_save -</b> zmienna definiująca, czy aktywna ma być funkcja auto zapisu (wartości: true/false) </li>
     *     <li><b>access_password -</b> hasło dostępu do notatek</li>
     *     <li><b>security_phrase -</b> fraza bezpieczeństwa, używana przy resetowaniu hasła</li>
     *     <li><b>show_system_uname -</b> zmienna definiująca, czy menu główne powinno zwracać się do użytkownika przy użyciu nazwy systemowego konta (wartości: true/false)</li>
     * </ul>
     */
    public static HashMap<String, String> settings;
    /**
     * Kolekcja danych przechowująca dane o ustawieniach aplikacji ostatnio pobrane z pliku (wykorzystywana do kontroli zmian zachodzących w ustawieniach)
     * @see #settings
     */
    public static HashMap<String, String> previous_settings;
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
     * Reprezentuje obecnie wybrany tryb sortowania notatek na liście.
     */
    public static int sort_type = NoteList.BY_LABEL;

    /**
     * Reprezentuje obecnie wybraną kolejność sortowania (wartość <i>true</i> dla kolejności malejącej)
     */
    public static boolean sort_descending = true;

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
     * @param reloadList zmienna definiująca, czy lista notatek również ma zostać przeładowana przed przeładowaniem interfejsu (<i>true</i> jeśli tak)
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
     * <p>Wyświetla okno kontekstowe, w którym prosi użytkownika o podanie nowego hasła. Dokonuje przy tym kilku sprawdzeń.</p>
     * <ul>
     *     <li>Czy hasło w ogóle jest ustawione (jeśli nie, tworzy zupełnie nowe hasło)</li>
     *     <li>Czy użytkownik zna frazę bezpieczeństwa</li>
     *     <li>Czy hasło już nie było wcześniej ustawione</li>
     *     <li>Czy nie doszło do pomyłki przy weryfikacji wpisanego nowego hasła</li>
     * </ul>
     */
    public static void changePassword(){

        //Zapisz obecny stan hasła
        String old_password = settings.get("access_password");

        //Jeśli hasło istnieje
        if(settings.get("access_password") != null) {

            //Początek kodu z prawdopodobnymi wyjątkami
            try {

                //Wyświetl komunikat z prośbą o podanie frazy bezpieczeństwa
                String security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj frazę bezpieczeństwa: ",
                        "Zmiana hasła",
                        JOptionPane.QUESTION_MESSAGE
                );

                //Jeśli operację anulowano — przerwij metodę. W innym przypadku zahaszuj fb
                if(Objects.equals(security_phrase, null)) return;
                security_phrase = hashString(security_phrase);

                //Jeśli wprowadzona przez użytkownika fb nie zgadza się z wartością w ustawieniach
                if(!Objects.equals(settings.get("security_phrase"), security_phrase)){

                    //Wyświetl o tym komunikat
                    JOptionPane.showMessageDialog(Main.main_frame, "Błędna fraza bezpieczeństwa", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Wyświetl komunikat proszący użytkownika o podanie nowego hasła
                String new_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                //Jeśli użytkownik anulował operację — przerwij metodę. W innym przypadku zahaszuj uzyskany wynik
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

                    //Jeśli anulowano operację — przerwij metodę. W innym przypadku zahaszuj uzyskany wynik
                    if(Objects.equals(new_password_retype, null)) return;
                    new_password_retype = hashString(new_password_retype);

                    //Jeśli ponownie wprowadzone hasło nie zgadza się z poprzednio wpisanym
                    if (!Objects.equals(new_password, new_password_retype)) {

                        //Wyświetl komunikat o tym fakcie
                        JOptionPane.showMessageDialog(Main.main_frame, "Hasła są różne", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                    }

                    //Jeśli są równe
                    else {

                        //Ustaw nowe hasło
                       settings.replace("access_password", new_password);

                        //Wyświetl komunikat o powodzeniu operacji
                        JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie zmieniono hasło", "Zmiana hasła", JOptionPane.INFORMATION_MESSAGE);

                        //Przeładuj aplikację
                        Main.reloadApp(true);
                    }
                }
                //Jeśli wystąpi wyjątek o błędnym algorytmie
            } catch (NoSuchAlgorithmException ex) {

                //Wyrzuć wiadomość wyjątku do konsoli
                System.out.println(ex.getMessage());
            }

            //Jeśli nie
        } else {

            //Początek kodu z prawdopodobnymi wyjątkami
            try{

                //Wyświetl komunikat z prośbą o podanie frazy bezpieczeństwa
                String security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj frazę bezpieczeństwa: ",
                        "Zmiana hasła",
                        JOptionPane.QUESTION_MESSAGE
                );

                //Jeśli anulowano operację — przerwij metodę. W innym przypadku zahaszuj frazę bezpieczeństwa.
                if(Objects.equals(security_phrase, null)) return;
                security_phrase = hashString(security_phrase);

                //Jeśli wprowadzona fb nie zgadza się z wartością w ustawieniach aplikacji
                if(!Objects.equals(settings.get("security_phrase"), security_phrase)){

                    //Wyświetl o tym komunikat i przerwij metodę
                    JOptionPane.showMessageDialog(Main.main_frame, "Błędna fraza bezpieczeństwa", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Wyświetl okno, w którym użytkownik ma wpisać swoje hasło
                String new_password = JOptionPane.showInputDialog(Main.main_frame, "Podaj nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                //Jeśli anulowano operację — przerwij metodę. W innym przypadku zahaszuj nowe hasło
                if(Objects.equals(new_password, null)) return;
                new_password = hashString(new_password);

                //Wyświetl okno, w którym użytkownik ma ponownie wpisać swoje hasło
                String new_password_retype = JOptionPane.showInputDialog(Main.main_frame, "Podaj jeszcze raz nowe hasło: ", "Zmiana hasła", JOptionPane.QUESTION_MESSAGE);

                //Jeśli anulowano operację — przerwij metodę. W innym przypadku zahaszuj i to hasło
                if(Objects.equals(new_password_retype, null)) return;
                new_password_retype = hashString(new_password_retype);

                //Jeśli obydwa hasze się nie zgadzają
                if (!Objects.equals(new_password, new_password_retype)) {

                    //Wyświetl komunikat o tym fakcie
                    JOptionPane.showMessageDialog(Main.main_frame, "Hasła są różne", "Zmiana hasła", JOptionPane.ERROR_MESSAGE);

                    //Jeśli są równe
                } else {

                    //Przypisz podaną wartość hasła
                    settings.put("access_password",new_password);

                    //Wyświetl komunikat o powodzeniu akcji
                    JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie dodano hasło", "Zmiana hasła", JOptionPane.INFORMATION_MESSAGE);

                    //Przeładuj aplikację
                    Main.reloadApp(true);
                }
                //Jeśli wystąpi wyjątek o błędnym algorytmie
            } catch (NoSuchAlgorithmException ex){

                    //Przekaż wiadomość z błędu do konsoli
                    System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Wyświetla okno kontekstowe, w którym pozwala użytkownikowi na zmianę obecnej frazy bezpieczeństwa.
     * <p>Sprawdza również, czy:</p>
     * <ul>
     * <li>Fraza bezpieczeństwa jest ustawiona (jeśli nie, pozwala ją ustawić)</li>
     * <li>Czy użytkownik zna obecną frazę</li>
     * <li>Czy użytkownik nie próbuje ustawić tej samej frazy bezpieczeństwa co obecnie</li>
     * <li>Czy użytkownik na pewno wprowadził prawidłowo zamierzoną nową frazę</li>
     * </ul>
     */
    public static void changeSecurityPhrase(){

        //Przechowaj obecną wartość frazy bezpieczeństwa
        String old_sf = settings.get("security_phrase");

        //Jeśli fraza bezpieczeństwa nie jest ustawiona
        if(old_sf != null){

            //Początek kodu z prawdopodobnymi wyjątkami
            try{

                //Wyświetl komunikat z prośbą o podanie frazy bezpieczeństwa
                String security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj frazę bezpieczeństwa: ",
                        "Zmiana frazy bezpieczeństwa",
                        JOptionPane.QUESTION_MESSAGE
                );

                //Jeśli użytkownik nie podał fb — przerwij metodę
                if(Objects.equals(security_phrase, null)) return;

                //Zahaszuj uzyskaną fb
                security_phrase = hashString(security_phrase);

                //Jeśli aktualna fb nie jest taka sama co podana przez użytkownika
                if(!Objects.equals(old_sf, security_phrase)){

                    //Wyświetl o tym komunikat
                    JOptionPane.showMessageDialog(Main.main_frame, "Błędna fraza bezpieczeństwa", "Zmiana frazy bezpieczeństwa", JOptionPane.ERROR_MESSAGE);
                }

                //W innym przypadku
                else {

                 //Wyświetl komunikat z prośbą o podanie nowej frazy bezpieczeństwa
                    String new_security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj nową frazę bezpieczeństwa: ",
                        "Zmiana frazy bezpieczeństwa",
                        JOptionPane.QUESTION_MESSAGE
                    );

                    //Jeśli użytkownik nie podał żadnej wartości — przerwij metodę
                    if(Objects.equals(new_security_phrase, null)) return;

                    //Zahaszuj nową fb
                    new_security_phrase = hashString(new_security_phrase);

                    //Jeśli aktualna i nowa fb są równe
                    if(Objects.equals(new_security_phrase, old_sf)){

                        //Wyświetl o tym komunikat
                        JOptionPane.showMessageDialog(Main.main_frame, "Podano tą samą frazę bezpieczeństwa", "Zmiana frazy bezpieczeństwa", JOptionPane.ERROR_MESSAGE);
                    }

                    //W innym wypadku
                    else{

                        //Wyświetl komunikat z prośbą o podanie ponownie nowe frazy bezpieczeństwa
                        String retype_security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj jeszcze raz nową frazę bezpieczeństwa: ",
                        "Zmiana frazy bezpieczeństwa",
                        JOptionPane.QUESTION_MESSAGE
                    );

                        //jeśli użytkownik nie podał żadnej wartości — przerwij metodę
                        if(Objects.equals(retype_security_phrase, null)) return;

                        //Zahaszuj ponownie wprowadzoną nową fb
                        retype_security_phrase = hashString(retype_security_phrase);

                        //Jeśli obydwie nowe fb nie są sobie równe
                        if(!Objects.equals(new_security_phrase, retype_security_phrase)){

                            //Wyświetl o tym komunikat
                            JOptionPane.showMessageDialog(Main.main_frame, "Frazy bezpieczeństwa nie zgadzają się", "Zmiana frazy bezpieczeństwa", JOptionPane.ERROR_MESSAGE);
                        }

                        //W innym przypadku
                        else{

                            //Zastąp obecnie zapisaną w ustawieniach wartość fb na nowo wprowadzoną
                            settings.replace("security_phrase", new_security_phrase);

                            //Wyświetl komunikat o powodzeniu operacji
                            JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie zmieniono frazę bezpieczeństwa", "Zmiana frazy bezpieczeństwa", JOptionPane.INFORMATION_MESSAGE);
                            JOptionPane.showMessageDialog(Main.main_frame, "Lepiej zapisać ją gdzieś albo mocno zapamiętać. Bez niej nie ma możliwości zmiany hasła ;3");
                        }
                    }
                }
            }

            //Jeśli wystąpi wyjątek o nieprawidłowym algorytmie
            catch (NoSuchAlgorithmException ex){

                //Przekaż wiadomość z błędu do konsoli
                System.out.println(ex.getMessage());
            }
        }

        //W innym przypadku
        else {

            //Początek kodu z ewentualnymi wyjątkami
            try {

                //Wyświetl komunikat z prośbą o podanie nowej fb
                String new_security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj nową frazę bezpieczeństwa: ",
                        "Zmiana frazy bezpieczeństwa",
                        JOptionPane.QUESTION_MESSAGE
                );

                //Jeśli użytkownik nie podał żadnej wartości — przerwij metodę
                if(Objects.equals(new_security_phrase, null)) return;

                //Zahaszuj nową fb
                new_security_phrase = hashString(new_security_phrase);

                //Wyświetl komunikat z prośbą o podanie ponownie nowej fb
                String retype_security_phrase = JOptionPane.showInputDialog(
                        main_frame,
                        "Podaj jeszcze raz nową frazę bezpieczeństwa: ",
                        "Zmiana frazy bezpieczeństwa",
                        JOptionPane.QUESTION_MESSAGE
                );

                //Jeśli użytkownik nie podał żadnej wartości — przerwij metodę
                if(Objects.equals(retype_security_phrase, null)) return;

                //Zahaszuj ponownie uzyskaną nową fb
                retype_security_phrase = hashString(retype_security_phrase);

                //Jeśli nowe fb nie są sobie równe
                if (!Objects.equals(new_security_phrase, retype_security_phrase)) {

                    //Wyświetl o tym komunikat
                    JOptionPane.showMessageDialog(Main.main_frame, "Frazy bezpieczeństwa są różne", "Zmiana frazy bezpieczeństwa", JOptionPane.ERROR_MESSAGE);
                }

                //W innym przypadku
                else {

                    //Umieść nową frazę bezpieczeństwa w ustawieniach
                    settings.put("security_phrase", new_security_phrase);

                    //Wyświetl komunikat o powodzeniu operacji
                    JOptionPane.showMessageDialog(Main.main_frame, "Pomyślnie dodano frazę bezpieczeństwa", "Zmiana frazy bezpieczeństwa", JOptionPane.INFORMATION_MESSAGE);
                    JOptionPane.showMessageDialog(Main.main_frame, "Lepiej zapisać ją gdzieś albo mocno zapamiętać. Bez niej nie ma możliwości zmiany hasła ;3");
                }
            }

            //Jeśli wystąpi wyjątek o nieprawidłowym algorytmie
            catch (NoSuchAlgorithmException ex){

                //Przekaż wiadomość z błędu do konsoli
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Ładuje wartości domyślne ustawień aplikacji. Wywołuje również odczyt pliku z domyślnej ścieżki.
     *
     * <p>Jeśli pliku z ustawieniami nie ma, sam tworzy nowy plik ustawień i uzupełnia go wartościami domyślnymi</p>
     */
    public static void loadDefault(){

        //Zapisz reprezentację pliku z ustawieniami, uzyskanego z domyślnej ścieżki domowej użytkownika systemowego
        //Windows: C://Users/użytkownik/.settings.txt
        //Linux: /home/użytkownik/.settings.txt
        settings_file = new File(String.format("%s%s.settings.txt", System.getProperty("user.home"), System.getProperty("file.separator")));

        //Jeśli plik nie istnieje
        if(!settings_file.exists()){

            //Wyświetl komunikat o tworzeniu nowego pliku
            JOptionPane.showMessageDialog(main_frame, "Poczekaj. Przygotowuję aplikację po raz pierwszy...", "Pierwsze uruchomienie", JOptionPane.PLAIN_MESSAGE);

            //Stwórz puste listy notatek
            noteList = new NoteList(new Note[0], NoteList.FULL);
            readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);

            //Stwórz kolekcję ustawień i uzupełnij ją danymi generycznymi
            settings = new HashMap<>();
            settings.put("default_path","");
            settings.put("auto_save","false");
            settings.put("access_password", null);
            settings.put("security_phrase", null);
            settings.put("show_system_uname","false");

            //Przypisz wartość obecnej kolekcji ustawień do kolekcji ustawień w historii
            previous_settings = new HashMap<>(settings);

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

                    //Jeśli hasło dostępowe jest niezdefiniowane
                    if(settings.get("access_password") == null){

                        //Wyświetl o tym komunikat
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Nie zdefiniowano żadnego hasła dostępowego. Dostęp do widoku ukrytego oraz ukrywania notatek zostanie " +
                                        "udzielony dopiero po zdefiniowaniu hasła.",
                                "Brak zdefiniowanego hasła dostępowego",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                    //Jeśli fraza bezpieczeństwa nie jest zdefiniowana
                    if(settings.get("security_phrase") == null){

                        //Wyświetl o tym komunikat
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Nie zdefiniowano frazy bezpieczeństwa. Koniecznie potrzeba ustawić ją teraz.",
                                "Brak zdefiniowanej frazy bezpieczeństwa",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        //Rozpocznij procedurę tworzenia nowej frazy bezpieczeństwa
                        while(settings.get("security_phrase") == null) changeSecurityPhrase();
                    }
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

            //Zinicjalizuj puste listy notatek
            noteList = new NoteList(new Note[0], NoteList.FULL);
            readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);

            //Stwórz instancję kolekcji ustawień
            settings = new HashMap<>();

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

                    //Umieść wartości generyczne w kolekcji ustawień
                    settings.put("default_path","");
                    settings.put("auto_save","false");
                    settings.put("access_password", null);
                    settings.put("security_phrase", null);
                    settings.put("show_system_uname","false");

                    previous_settings = new HashMap<>(settings);

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

                    //Jeśli hasło dostępowe nie jest zdefiniowane
                    if(settings.get("access_password") == null){

                        //Wyświetl o tym komunikat
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Nie zdefiniowano żadnego hasła dostępowego. Dostęp do widoku ukrytego oraz ukrywania notatek zostanie " +
                                        "udzielony dopiero po zdefiniowaniu hasła.",
                                "Brak zdefiniowanego hasła dostępowego",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                    //Jeśli fraza bezpieczeństwa nie jest ustawiona
                    if(settings.get("security_phrase") == null){

                        //Wyświetl o tym komunikat
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Nie zdefiniowano frazy bezpieczeństwa. Koniecznie potrzeba ustawić ją teraz.",
                                "Brak zdefiniowanej frazy bezpieczeństwa",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        //Rozpocznij procedurę tworzenia nowej frazy bezpieczeństwa
                        while(settings.get("security_phrase") == null) changeSecurityPhrase();
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
                    while (fs.hasNextLine()) {

                        //Odczytaj linię danych z pliku, podziel je na klucz oraz wartość ustawień w miejscu wystąpienia dwuznaku "::"
                        String read_line = fs.nextLine();
                        String[] kv_pair = read_line.split("(::)");

                        //Przypisz wartość ustawienia do klucza odczytanego z linii danych z pliku
                        settings.put(kv_pair[0], kv_pair.length == 2 ? kv_pair[1] : null);
                    }

                    //Przypisz do zrzutu pierwotnego stanu ustawień aktualną ich wartość
                    previous_settings = new HashMap<>(settings);

                    //Jeśli odczytana ścieżka jest pusta
                    if(Objects.equals(settings.get("default_path"), null)){

                        //Wyświetl komunikat o tym fakcie
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Ścieżka dostępu do pliku jest pusta. Konieczne ręczne wczytanie notatek.",
                                "Wczytywanie domyślnego pliku notatek",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        //Wyświetl komunikat o niezdefiniowanym haśle5
                        if(settings.get("access_password") == null){
                            JOptionPane.showMessageDialog(
                                    main_frame,
                                  "Nie zdefiniowano żadnego hasła dostępowego. Dostęp do widoku ukrytego oraz ukrywania notatek zostanie " +
                                            "udzielony dopiero po zdefiniowaniu hasła.",
                                    "Brak zdefiniowanego hasła dostępowego",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }

                        //Wyświetl komunikat o braku frazy bezpieczeństwa oraz rozpocznij jej proces definiowania
                        if(settings.get("security_phrase") == null){
                            JOptionPane.showMessageDialog(
                                    main_frame,
                                    "Nie zdefiniowano frazy bezpieczeństwa. Koniecznie potrzeba ustawić ją teraz.",
                                    "Brak zdefiniowanej frazy bezpieczeństwa",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            while(settings.get("security_phrase") == null) changeSecurityPhrase();
                        }

                        //Zakończ działanie metody
                        return;
                    }

                    //Jeśli pod odczytaną ścieżką nie ma żadnego pliku
                    if(!new File(settings.get("default_path")).exists()){

                        //Wyświetl komunikat o tym fakcie
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Wczytywanie pliku domyślnego zakończyło się powodzeniem. Konieczne jest ręczne wczytanie żądanego pliku.",
                                "Błąd odczytu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );

                        //Wyświetl komunikat z informacją o braku zdefiniowanego hasła
                        if(settings.get("access_password") == null){
                            JOptionPane.showMessageDialog(
                                    main_frame,
                                  "Nie zdefiniowano żadnego hasła dostępowego. Dostęp do widoku ukrytego oraz ukrywania notatek zostanie " +
                                            "udzielony dopiero po zdefiniowaniu hasła.",
                                    "Brak zdefiniowanego hasła dostępowego",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }

                        //Wyświetl komunikat o braku zdefiniowane frazy bezpieczeństwa oraz rozpocznij proces jej definiowania
                        if(settings.get("security_phrase") == null){
                            JOptionPane.showMessageDialog(
                                    main_frame,
                                    "Nie zdefiniowano frazy bezpieczeństwa. Koniecznie potrzeba ustawić ją teraz.",
                                    "Brak zdefiniowanej frazy bezpieczeństwa",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            while(settings.get("security_phrase") == null) changeSecurityPhrase();
                        }

                        //Zakończ działanie metody
                        return;
                    }

                    //Utwórz instancję klasy obsługującej pliki z notatkami
                    fh = new FileHandler(new File(settings.get("default_path")));

                    //Jeśli notatki na tej liście są obecne, przypisz je do buforowej zmiennej. Jeśli nie, utwórz pustą listę
                    NoteList parsed_notes;
                    if(fh.parseDocToNotes() == null) parsed_notes = new NoteList();
                    else parsed_notes = new NoteList(fh.parseDocToNotes().getNoteList(), NoteList.FULL);

                    //Jeśli buforowa lista ma długość większą niż jeden, uzupełnij obydwie listy aplikacji jej danymi
                    noteList = parsed_notes.getListLength() > 0 ? parsed_notes : new NoteList(new Note[0], NoteList.FULL);
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

        //Jeśli okno edycji notatek istnieje
        if(!checkCurrentNoteEdit()) return;

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


                        if(!Objects.equals(previous_settings, settings)){

                            //Początek kodu z ewentualnymi wyjątkami
                            try {
                                //Stwórz instancję klasy zapisującej do pliku z ustawieniami
                                FileWriter settings_writer = new FileWriter(settings_file);

                                //Dla każdej pary klucz-wartość w kolekcji ustawień
                                settings.forEach((key, value) -> {

                                    //Początek kodu z prawdopodobnymi wyjątkami
                                    try {

                                        //Zapisz do pliku ustawień pojedynczą parę danych, oddzieloną znakiem "::"
                                        settings_writer.write(String.format("%s::%s\n", key, value));
                                    }

                                    //Jeśli wystąpi wyjątek związany z operacjami I/O
                                    catch (IOException ex){

                                        //Wyświetl komunikat o błędzie wraz z jego wiadomością
                                        JOptionPane.showMessageDialog(
                                        main_frame,
                                        ex.getMessage(),
                                            "Błąd zapisu pliku domyślnego",
                                        JOptionPane.ERROR_MESSAGE
                                        );
                                    }
                                });

                                //Zamknij plik
                                settings_writer.close();
                            }

                            //Jeśli złapany zostanie wyjątek związany z operacjami I/O
                            catch(IOException ex){

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
                        }
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

                    //Wyświetl komunikat o udanym zapisie notatek do pliku
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
                }

                //Jeśli zmianie uległa domyślna ścieżka pliku z notatkami
                if(!Objects.equals(previous_settings, settings)){

                    //Początek kodu z ewentualnymi wyjątkami
                    try {
                        //Stwórz instancję klasy zapisującej do pliku z ustawieniami
                        FileWriter settings_writer = new FileWriter(settings_file);

                        //Zapisz informacje o domyślnej ścieżce pliku i zamknij plik
                        settings.forEach((key, value) -> {
                            try {
                                settings_writer.write(String.format("%s::%s\n", key, value));
                            } catch (IOException ex){
                                //Wyświetl komunikat o błędzie wraz z jego wiadomością
                                JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Błąd zapisu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                                );
                            }
                        });
                        settings_writer.close();
                    }

                    //Jeśli złapany zostanie wyjątek związany z operacjami I/O
                    catch(IOException ex){

                        //Wyświetl komunikat o błędzie wraz z jego wiadomością
                        JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Błąd zapisu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }

                //Zakończ działanie aplikacji
                System.exit(0);

                //Jeśli zamknięto okno zapisu danych do pliku
            } else if(to_save == JOptionPane.NO_OPTION){

                //Jeśli zmianie uległa domyślna ścieżka pliku z notatkami
                if(!Objects.equals(previous_settings, settings)){

                    //Początek kodu z ewentualnymi wyjątkami
                    try {
                        //Stwórz instancję klasy zapisującej do pliku z ustawieniami
                        FileWriter settings_writer = new FileWriter(settings_file);

                        //Zapisz informacje o domyślnej ścieżce pliku i zamknij plik
                        settings.forEach((key, value) -> {
                            try {
                                settings_writer.write(String.format("%s::%s\n", key, value));
                            } catch (IOException ex){
                                //Wyświetl komunikat o błędzie wraz z jego wiadomością
                                JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Błąd zapisu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                                );
                            }
                        });
                        settings_writer.close();
                    }

                    //Jeśli złapany zostanie wyjątek związany z operacjami I/O
                    catch(IOException ex){

                        //Wyświetl komunikat o błędzie wraz z jego wiadomością
                        JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Błąd zapisu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }

                //Zakończ działanie aplikacji
                System.exit(0);
            }
        }

        //Jeśli listy notatek są równe
        else {

            //Jeśli ustawienia uległy zmianie
            if(!Objects.equals(previous_settings, settings)){

                    //Początek kodu z ewentualnymi wyjątkami
                    try {
                        //Stwórz instancję klasy zapisującej do pliku z ustawieniami
                        FileWriter settings_writer = new FileWriter(settings_file);

                        //Zapisz informacje o domyślnej ścieżce pliku i zamknij plik
                        settings.forEach((key, value) -> {
                            try {
                                settings_writer.write(String.format("%s::%s\n", key, value));
                            } catch (IOException ex){
                                //Wyświetl komunikat o błędzie wraz z jego wiadomością
                                JOptionPane.showMessageDialog(
                                main_frame,
                                ex.getMessage(),
                                "Błąd zapisu pliku domyślnego",
                                JOptionPane.ERROR_MESSAGE
                                );
                            }
                        });
                        settings_writer.close();
                    }

                    //Jeśli złapany zostanie wyjątek związany z operacjami I/O
                    catch(IOException ex){

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
                }

            //Zakończ działanie aplikacji
            System.exit(0);
        }
    }

    /**
     * Sprawdza, czy doszło do zmian w liście plików, które mogłyby zostać utracone. Następnie pyta użytkownika, czy na pewno chce kontynuować operację.
     * @return Wartość <i>true</i> jeśli operacja ma być kontynuowana.
     */
    public static boolean checkSaved(){

        //Jeśli notatki są równe, zwróć wartość true. Jeśli nie:
        if(!NoteList.areNoteListsEqual(noteList, readNoteList)){

            //Wyświetl komunikat z zapytaniem o chęć kontynuowania operacji mimo utraty danych
            int i = JOptionPane.showConfirmDialog(
                    main_frame,
                    "Ta operacja spowoduje utracenie niezapisanych danych. Czy na pewno chcesz kontynuować?",
                    "Możliwa utrata danych",
                    JOptionPane.YES_NO_OPTION
            );
            return i == JOptionPane.YES_OPTION; //Zwróć wartość true, jeśli wybrano opcję "Tak"
        } else return true;
    }

    /**
     * Sprawdza, czy aktualnie edytowana notatka ma niezapisane zmiany. Jeśli ma, obsługuje ich zapis lub usunięcie.
     * @return Wartość <i>true</i> jeśli można kontynuować operacje zależne od tych zmian.
     */
    public static boolean checkCurrentNoteEdit(){
        //Jeśli okno edycji notatek istnieje
            if(en != null){

                //Jeśli auto zapis nie jest aktywny
               if(Objects.equals(settings.get("auto_save"),"false")){
                   if(en.hasNoteChanged()) {
                       //Wyświetl komunikat z zapytaniem o wolę zapisu aktualnie edytowanej notatki
                       int i = JOptionPane.showConfirmDialog(
                               main_frame,
                               "Aktualnie edytowana notatka nie została zapisana. Zapisać ją?",
                               "Edytowana notatka niezapisana",
                               JOptionPane.YES_NO_CANCEL_OPTION,
                               JOptionPane.QUESTION_MESSAGE
                       );

                       //Jeśli wola została potwierdzona, zapisz notatkę
                       if (i == JOptionPane.YES_OPTION) {
                           en.forceSave();
                       }

                       //Jeśli nie wyrażono takiej woli, skasuj notatkę
                       else if (i == JOptionPane.NO_OPTION) {
                           en.forceDelete();
                       }

                       //Jeśli anulowano, przerwij działanie funkcji
                       else {
                           return false;
                       }
                   }
               }

               //Jeśli auto zapis jest aktywny i notatka uległa zmianie,
               // zapisz notatkę
               else{
                   if(en.hasNoteChanged()){
                       en.forceSave();
                   } else en.forceDelete();
               }
               return true;
            }
            return true;
    }

    /**
     * Metoda główna aplikacji. Inicjalizuje wartości domyślne ustawień oraz listy notatek, oraz buduje interfejs.
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
            if(!checkCurrentNoteEdit()) return; //Jeśli okno edycji notatek ma w sobie notatkę z niezapisanymi zmianami
            if(!checkSaved()) return; //Jeśli nie zgadza się użytkownik na utratę danych, przerwij proces



            //Stwórz nową instancję klasy reprezentującej okno wyboru pliku
            JFileChooser fc = new JFileChooser();

            //Załóż filtr rozszerzeń plików na tę instancję. Ma pokazywać wyłącznie pliki XML
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));

            //Wyświetl to okno
            int i = fc.showOpenDialog(main_frame);

            //Jeśli wybrano plik do odczytu
            if(i==JFileChooser.APPROVE_OPTION) {

                //Stwórz nową instancję klasy obsługującej operacje na plikach i przypisz jej wybrany w oknie plik
                fh = new FileHandler(fc.getSelectedFile());

                //Dokonaj konwersji dokumentu XML na listę notatek
                NoteList fetched_notes = fh.parseDocToNotes();

                //Jeśli lista notatek odczytana z pliku jest pusta
                if (fetched_notes != null) {

                    //Jeśli lista notatek nie jest pusta, przypisz do niej wartość listy odczytanej z pliku. Jeśli jest, stwórz nową instancję klasy listy notatek i przypisz jej tę samą wartość
                    if (noteList != null) noteList.setNoteList(fh.parseDocToNotes().getNoteList());
                    else noteList = new NoteList(fh.parseDocToNotes().getNoteList(), NoteList.FULL);

                    //Jeśli lista notatek w historii nie jest pusta, przypisz do niej wartość obecnej listy notatek. Jeśli jest, stwórz nową instancję klasy listy notatek i przypisz jej tę samą wartość
                    if (readNoteList != null) readNoteList.setNoteList(noteList.getNoteList());
                    else readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);


                    //Przeładuj aplikację
                    Main.reloadApp(true);

                    //Wyświetl komunikat o powodzeniu operacji
                    JOptionPane.showMessageDialog(main_frame, "Pomyślnie wczytano notatki z pliku", "Wczytywanie pliku", JOptionPane.INFORMATION_MESSAGE);

                    //Wyświetl komunikat z zapytaniem o wolę zapisu ścieżki do pliku jako ścieżki domyślnej
                    int save_path_to_default = JOptionPane.showConfirmDialog(
                            main_frame,
                            "Czy chcesz zapisać ten plik z notatkami jako plik domyślny?",
                            "Domyślny plik z notatkami",
                            JOptionPane.YES_NO_OPTION
                    );

                    //Jeśli wyrażono taką wolę
                    if (save_path_to_default == JOptionPane.YES_OPTION) {

                        //Zapisz tę ścieżkę
                        settings.put("default_path", fh.getFile_path());

                        //Wyświetl komunikat z informacją o powodzeniu operacji
                        JOptionPane.showMessageDialog(
                                main_frame,
                                "Zapisano",
                                "Zapisywanie domyślnego pliku",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }

                //Wprowadź aksjomat — pobrane notatki nie są puste
                assert fetched_notes != null;

                //Dla każdej notatki z listy pobranych notatek sprawdź, czy nie ma wśród nich chociaż jednej ukrytej notatki,
                //Jeśli jest oraz nie zdefiniowano w ustawieniach hasła, wyświetl komunikat o braku do niej dostępu
                for (Note note : fetched_notes.getNoteList()) {
                    if (note.getHidden() && settings.get("access_password") == null) {
                        int will_change_password = JOptionPane.showConfirmDialog(
                                main_frame,
                                "Pobrana lista notatek zawiera notatki ukryte. Są one niedostępne ze względu na brak " +
                                        "zdefiniowanego hasła. Czy chcesz teraz zdefiniować hasło dostępu?",
                                "Dostęp do notatek ukrytych zablokowany",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (will_change_password == JOptionPane.YES_OPTION) {
                            changePassword();
                        }

                        //Wstrzymaj pętlę
                        break;
                    }
                }

            }
        });

        //Dodaj logikę do przycisku zapisywania plików
        save.addActionListener(e -> {

            //Jeśli lista notatek jest krótsza od 1
            if(noteList.getListLength() < 1){

                //Wyświetl komunikat o braku informacji do zapisywania i przerwij działanie funkcji
                JOptionPane.showMessageDialog(main_frame, "Nie ma nic do zapisania", "Zapisywanie pliku", JOptionPane.ERROR_MESSAGE);
                return;
            }


            //Jeśli okno edycji notatek istnieje
           if(!checkCurrentNoteEdit()) return;

            //Stwórz obiekt reprezentujący okno wyboru pliku
            JFileChooser fc = new JFileChooser();

            //Załóż na ten obiekt filtr rozszerzeń plików na pliki XML
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));

            //Wyświetl to okno
            int i = fc.showSaveDialog(main_frame);

            //Jeśli zatwierdzono plik do zapisu
            if(i == JFileChooser.APPROVE_OPTION){

                //Początek kodu z prawdopodobnymi wyjątkami
                try {

                    //Stwórz nową instancję klasy obsługującej operacje na plikach (w trybie zapisu)
                    fh = new FileHandler(fc.getSelectedFile(), true);

                    //Skonwertuj listę notatek do formy dokumentu XML i zapisz go do pliku
                    fh.parseToFile(noteList);

                    //Jeśli lista notatek w historii nie jest pusta, ustaw jej wartość na wartość listy notatek obecnej.
                    // Jeśli jest, stwórz nową instancję klasy listy notatek i przypisz jej wartość obecnej listy notatek
                    if(readNoteList != null) readNoteList.setNoteList(noteList.getNoteList()); else readNoteList = new NoteList(noteList.getNoteList(), NoteList.FULL);

                }

                //Jeśli wystąpi wyjątek nieudanego zapisu do pliku
                catch(FailedToWriteToFileException ex){

                    //Wyświetl komunikat z wiadomością błędu i przerwij działanie funkcji
                    JOptionPane.showMessageDialog(
                            main_frame,
                            ex.getMessage(),
                            "Zapisywanie pliku",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }


                //Wyświetl komunikat o prawidłowym wykonaniu operacji
                JOptionPane.showMessageDialog(main_frame, "Pomyślnie zapisano notatki do pliku", "Zapisywanie pliku", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //Stwórz pozycję w menu wyboru domyślnego pliku z notatkami
        JMenuItem select_default = new JMenuItem("Wybierz domyślny plik");

        //Dodaj logikę do tej pozycji
        select_default.addActionListener(e -> {

            //Stwórz instancję klasy reprezentującej okno wyboru pliku
            JFileChooser fc = new JFileChooser();

            //Załóż filtr rozszerzeń na tę instancję. Filtr ma pokazywać tylko pliki XML
            fc.setFileFilter(new FileNameExtensionFilter("Pliki XML","xml"));

           //Wyświetl okno wyboru pliku
            int i = fc.showOpenDialog(main_frame);

            //Jeśli wybrano plik
            if(i == JFileChooser.APPROVE_OPTION) {

                //Stwórz nową instancję klasy reprezentującej otwarty plik w oknie
                File new_default = fc.getSelectedFile();

                //Odczytaj ścieżkę dostępu do tego pliku i zapisz ją
                settings.replace("default_path", new_default.getPath());

                //Wyświetl komunikat o powodzeniu operacji
                JOptionPane.showMessageDialog(main_frame,
                        "Zapisano nowy domyślny plik",
                        "Zapisywanie domyślnego pliku",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });


        //Stwórz pozycję w menu typu check-box definiującą wolę wyświetlania systemowej nazwy użytkownika w menu głównym
        JCheckBoxMenuItem show_uname = new JCheckBoxMenuItem("Pokazuj systemową nazwę użytkownika", Objects.equals(settings.get("show_system_uname"), "true"));

        //Dodaj logikę do tej pozycji — zmień stan w ustawieniach na odpowiadający stanowi check-boxa, zaktualizuj menu główne
        //i przeładuj aplikację
        show_uname.addActionListener(e -> {
            settings.replace("show_system_uname", show_uname.isSelected() ? "true" : "false");
            String temp = current_window;
            System.out.println(current_window);
            hm = new HomeMenu();
            current_window = temp;
            reloadApp(false);
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
        file.add(new JSeparator(JSeparator.HORIZONTAL)); file.add(show_uname); file.add(new JSeparator(JSeparator.HORIZONTAL)); file.add(exit);

        //Stwórz menu opcji notatek
        JMenu notes = new JMenu("Notatki");

        //Stwórz menu sortowania notatek
        JMenu sort_menu = new JMenu("Sortuj");

        //Stwórz pozycje w menu odpowiedzialne za różne rodzaje sortowania
        JMenuItem sort_by_createdate = new JMenuItem("wg daty utworzenia");
        JMenuItem sort_by_moddate = new JMenuItem("wg daty modyfikacji");
        JMenuItem sort_by_label = new JMenuItem("wg etykiety");
        JMenuItem sort_by_type = new JMenuItem("wg typu");
        JMenuItem sort_by_completion = new JMenuItem("wg stopnia ukończenia");

        //Stwórz pozycję w menu będącą check-boxem definiującym kolejność sortowania
        JCheckBoxMenuItem sort_desc = new JCheckBoxMenuItem("Sortuj malejąco", true);

        //Dodaj logikę do pozycji sortowania. Przypisz do każdej pozycji metodę sortującą listę notatek z innym wywoływanym trybem.
        //Następnie przeładuj aplikację wraz z listą notatek.
        sort_by_createdate.addActionListener(e -> { sort_type = NoteList.BY_CREATE_DATE; sort_descending = sort_desc.getState(); reloadApp(true); });
        sort_by_moddate.addActionListener(e -> { sort_type = NoteList.BY_MOD_DATE; sort_descending = sort_desc.getState(); reloadApp(true); });
        sort_by_label.addActionListener(e -> { sort_type = NoteList.BY_LABEL; sort_descending = sort_desc.getState(); reloadApp(true); });
        sort_by_type.addActionListener(e -> { sort_type = NoteList.BY_TYPE; sort_descending = sort_desc.getState(); reloadApp(true); });
        sort_by_completion.addActionListener(e -> { sort_type = NoteList.BY_COMPLETION; sort_descending = sort_desc.getState(); reloadApp(true); });
        sort_desc.addActionListener(e -> { sort_descending = sort_desc.getState(); reloadApp(true); });

        //Wstaw pozycje do menu
        sort_menu.add(sort_by_createdate);
        sort_menu.add(sort_by_moddate);
        sort_menu.add(sort_by_label);
        sort_menu.add(sort_by_type);
        sort_menu.add(sort_by_completion);
        sort_menu.add(sort_desc);

        //Dodaj pozycję w menu odpowiedzialną za dodanie nowej notatki
        JMenuItem add_note = new JMenuItem("Dodaj notatkę");

        //Dodaj logikę do pozycji dodania nowej notatki
        add_note.addActionListener(e -> {

            //Jeśli już istnieje otwarte okno edytowanej notatki
           if(!checkCurrentNoteEdit()) return;

            //Stwórz nowe okno edycji notatki (konstruktor domyślny = nowa notatka)
            en = new EditNote();

            //Odśwież aplikację
            reloadApp(false);
        });

        JCheckBoxMenuItem auto_save = new JCheckBoxMenuItem("Auto zapis notatek edytowanych");
        auto_save.setState(Objects.equals(settings.get("auto_save"), "true"));

        auto_save.addActionListener(e -> {
            if(auto_save.getState()) settings.replace("auto_save", "true");
            else settings.replace("auto_save", "false");
        });

        //Dodaj pozycję dodania nowej notatki oraz menu sortowania do menu notatek
        notes.add(add_note); notes.add(sort_menu); notes.add(auto_save);

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

            //Jeśli hasło jest puste
            if(settings.get("access_password") == null){

                //Wyświetl komunikat o tym fakcie i zakończ działanie metody
                JOptionPane.showMessageDialog(main_frame, "Hasło już nie istnieje", "Nie można usunąć hasła", JOptionPane.ERROR_MESSAGE);
                return;
            }


            //Stwórz kopię obecnej listy notatek
            NoteList temp = new NoteList(noteList.getNoteList(), NoteList.FULL);

            //Jeśli na liście występuje notatka ukryta, zwróć wyjątek bezpieczeństwa
            for(int i = 0; i < temp.getListLength(); i++){
                if(temp.getNote(i).getHidden()){
                   int hidden_found = JOptionPane.showConfirmDialog(
                           main_frame,
                           "Znaleziono ukryte notatki. Dostęp do nich zostanie utracony po skasowaniu hasła. Kontynuować?",
                           "Znaleziono ukryte notatki.",
                           JOptionPane.YES_NO_OPTION,
                           JOptionPane.QUESTION_MESSAGE
                   );
                   if(hidden_found == JOptionPane.YES_OPTION){
                       break;
                   } else {
                       return;
                   }
                }
            }

            //Ustaw hasło na pustą wartość oraz przełącz aplikację w tryb jawny
            settings.remove("access_password");
            hidden_mode = false;

            //Ustaw informację o obecnym oknie na przechowaną wartość
            Main.current_window = temp_current_window;

            //Przeładuj aplikację
            Main.reloadApp(true);

            //Wyświetl komunikat o powodzeniu operacji
            JOptionPane.showMessageDialog(main_frame, "Hasło zostało pomyślnie usunięte", "Kasowanie hasła", JOptionPane.INFORMATION_MESSAGE);
        });

        //Dodaj pozycję w menu odpowiedzialną za zmianę frazy bezpieczeństwa
        JMenuItem sf_change = new JMenuItem("Zmień frazę bezpieczeństwa");

        //Dodaj logikę do tej pozycji — rozpocznij proces tworzenia frazy bezpieczeństwa
        sf_change.addActionListener(e -> changeSecurityPhrase());

        //Dodaj pozycje zmiany i kasowania hasła do menu bezpieczeństwa
        security.add(password_change); security.add(password_remove); security.add(new JSeparator(JSeparator.HORIZONTAL)); security.add(sf_change);


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
                    "Notepad-Todo \n\n Wersja 1.1.1 \n Copyright Michał Mikuła 2023",
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

        //Dodaj logikę do ramki — reakcja na wywołanie sygnału zamknięcia aplikacji
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