import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.Objects;

/**
 * Wykonuje operacje wejścia/wyjścia na plikach oraz przetwarza dane w nich zawarte. Odpowiada za odczyt notatek z pliku oraz ich zapis do pliku.
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class FileHandler {
    /**
     * Określa, czy plik istnieje
     */
    private boolean file_exists;
    /**
     * Określa, czy plik jest możliwy do odczytu
     */
    private boolean file_can_read;
    /**
     * Określa, czy plik jest możliwy do zapisu
     */
    private boolean file_can_write;
    /**
     * Ścieżka dostępu do pliku
     */
    private String file_path;
    /**
     * Reprezentacja pliku zawierającego dane o notatkach.
     */
    private File xml_file;
    /**
     * Dokument XML odczytany z pliku.
     */
    private Document parsed_file;

    /**
     * Zwraca ścieżkę do pliku, na którym wykonywane będą operacje I/O.
     * @return Ścieżka do pliku.
     */
    public String getFile_path(){ return this.file_path; }

    /**
     * Nadpisuje obecną ścieżkę do pliku nową, zdefiniowaną przez użytkownika.
     * @param file_path Nowa ścieżka do pliku.
     */
    public void setFile_path(String file_path){ this.file_path = file_path; }

    /**
     * Zwraca reprezentację pliku przechowywaną w tym obiekcie
     * @return Reprezentacja pliku zawierającego dane o notatkach.
     */
    public File getXml_file(){ return this.xml_file; }

    /**
     * Nadpisuje przechowywany w obiekcie plik nowym. Dodatkowo przeprowadza od nowa kontrolę możliwości odczytu/zapisu.
     * @param xml_file Plik — dokument XML przechowujący dane o notatkach.
     */
    public void setXml_file(File xml_file){
        this.xml_file = xml_file;
        this.file_exists = getXml_file().exists();
        this.file_can_write = getXml_file().canWrite();
        this.file_can_read = getXml_file().canRead();
    }

    /**
     * Zwraca dokument przechowywany w tym obiekcie, zawierający odczytane z pliku dane o notatkach.
     * @return Reprezentacja dokumentu XML z pliku z danymi przechowywana w tym obiekcie.
     */
    public Document getParsed_file(){ return this.parsed_file; }

    /**
     * Zwraca stan możliwości odczytu pliku przechowywanego w tym obiekcie.
     * @return Wartość true, jeśli plik można odczytać.
     */
    public boolean isFileReadable(){ return this.file_can_read; }

    /**
     * Zwraca stan możliwości zapisu pliku przechowywanego w tym obiekcie.
     * @return Wartość true, jeśli plik można zapisać/nadpisać.
     */
    public boolean isFileWritable(){ return this.file_can_write; }

    /**
     * Zwraca stan egzystencji pliku przechowywanego w tym obiekcie.
     * @return Wartość true, jeśli plik istnieje.
     */
    public boolean doesFileExist(){ return this.file_exists; }

    /**
     * Dokonuje translacji danych z pliku przechowywanego w tym obiekcie i zapisuje je jako dokument XML.
     */
    public void parseXml(){

        //Początek kodu z ewentualnymi wyjątkami
        try{

            //Skorzystaj z API umożliwiającego uzyskanie parsera XML (użyj domyślnych ustawień)
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            //Stwórz nowy konwerter dokumentów XML na reprezentacje DOM
            DocumentBuilder db = dbf.newDocumentBuilder();

            //Jeśli plik nie istnieje
            if(!doesFileExist()){

                //Zwróć odpowiedni wyjątek
                throw new FileNotFoundException("Nie ma takiego pliku.");
            }

            //Jeśli plik jest nie do odczytu
            if(!isFileReadable()){

                //Zwróć odpowiedni wyjątek
                throw new AccessDeniedException("Nie można odczytać pliku. Sprawdź uprawnienia dostępu.");
            }

            //Wykorzystując pozyskane narzędzie, dokonaj konwersji pliku XML na reprezentację drzewa DOM
            this.parsed_file = db.parse(getXml_file());

        //Złap wyjątek związany z brakiem dostępu do pliku
        } catch(AccessDeniedException ex){

            //Wyświetl informację o błędzie
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Błąd odczytu pliku",
                    JOptionPane.ERROR_MESSAGE
            );

        //Złap wyjątek związany z nieistnieniem pliku
        } catch(FileNotFoundException ex){

            //Wyświetl informację o błędzie
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Błąd odczytu pliku",
                    JOptionPane.ERROR_MESSAGE
            );
        //Złap wyjątek związany z wadliwą konfiguracją parsera
        //(co jest niemożliwe, bo używamy ustawień domyślnych)
        } catch(ParserConfigurationException | SAXException ex){

            //Wyświetl informację o błędzie
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Wewnętrzny błąd aplikacji",
                    JOptionPane.ERROR_MESSAGE
            );

        //Złap wyjątek związany z obsługą operacji I/O
        } catch(IOException ex){

            //Wyświetl informację o błędzie
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Wewnętrzny błąd aplikacji",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Dokonuje translacji zawartości węzłów znajdujących się w drzewie DOM dokumentu przechowywanego w tym obiekcie na notatki oraz ich zawartość.
     * @return Lista notatek uzyskana z konwersji danych.
     */
     public NoteList parseDocToNotes(){

        //Jeśli żadnego dokumentu nie ma
        if(getParsed_file() == null){

            //Wyświetl informację o błędzie
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    "Błąd parsowania pliku",
                    "Wewnętrzny błąd aplikacji",
                    JOptionPane.ERROR_MESSAGE
            );

            //Zwróć wartość pustą
            return null;
        }

        //Stwórz nową pustą listę notatek
        NoteList notes = new NoteList();

        //Uzyskaj element-korzeń dokumentu
        Element root = getParsed_file().getDocumentElement();

        //Uzyskaj węzeł zawierający dane o haśle
        Element password = (Element) root.getElementsByTagName("Password").item(0);

        //Wyciągnij dane tekstowe z tego elementu. Jeśli jest pusty, przypisz wartość pustą
        String password_grabbed = password == null ? null : password.getTextContent();

        //Uzyskaj listę węzłów zawierającą węzły <Note> - notatki
        NodeList notes_got = root.getElementsByTagName("Note");

            //Dla każdego węzła na liście
            for(int i = 0; i < notes_got.getLength(); i++){

                //Uzyskaj węzeł z listy pod indeksem i
                Element note_props = (Element) notes_got.item(i);

                //Jeśli w węźle typem notatki jest typ NOTE
                if(Objects.equals(note_props.getElementsByTagName("Type").item(0).getTextContent(), "NOTE")) {

                    //Rozpocznij kod z prawdopodobnymi wyjątkami
                    try {
                        //Pobierz z wyłuskanego węzła etykietę notatki
                        String note_label = note_props.getElementsByTagName("Label").item(0).getTextContent();

                        //Pobierz z wyłuskanego węzła treść notatki
                        String note_text = note_props.getElementsByTagName("Text").item(0).getTextContent();

                        //Pobierz z wyłuskanego węzła datę utworzenia notatki w formie liczby Long, którą to przekaż do konstruktora obiektu Date
                        Date note_create_date = new Date(Long.parseLong(note_props.getElementsByTagName("CreateDate").item(0).getTextContent()));

                        //Pobierz z wyłuskanego węzła datę modyfikacji notatki w formie liczby Long, którą to przekaż do konstruktora obiektu Date
                        Date note_modified_date = new Date(Long.parseLong(note_props.getElementsByTagName("ModDate").item(0).getTextContent()));

                        //Pobierz z wyłuskanego węzła stan ukrycia notatki
                        boolean note_hidden = !note_props.getElementsByTagName("Hidden").item(0).getTextContent().equals("false");

                        //Jeśli hasło nie jest zdefiniowane, a mimo to notatka jest ukryta
                        if(password == null && note_hidden){

                            //Prawdopodobnie doszło do manipulacji danymi, zwróć wyjątek bezpieczeństwa
                            throw new SecurityException("Brak informacji o haśle pomimo istnienia notatek ukrytych. \n" +
                                    "Domniemana manipulacja pliku. Odmowa odczytu.");
                        }

                        //Stwórz nową notatkę na podstawie wyłuskanych danych
                        Note note = new Note(note_label, note_text, note_hidden);

                        //Ustaw daty utworzenia oraz modyfikacji
                        note.setCreate_date(note_create_date);
                        note.setMod_date(note_modified_date);

                        //Dodaj notatkę do listy
                        notes.addNote(note);

                    //Jeśli wyłapany zostanie wyjątek bezpieczeństwa
                    } catch (SecurityException ex){

                        //Wyświetl informację o błędzie odczytu pliku i zwróć wartość pustą
                        JOptionPane.showMessageDialog(Main.main_frame, ex.getMessage(), "Błąd odczytu pliku", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }

                    //Jeśli w węźle typem notatki jest typ TODO-NOTE
                } else if(Objects.equals(note_props.getElementsByTagName("Type").item(0).getTextContent(), "TODO-NOTE")){

                    //Rozpocznij egzekucję kodu z możliwymi wyjątkami
                    try {

                        //Pobierz z wyłuskanego węzła etykietę notatki
                        String note_label = note_props.getElementsByTagName("Label").item(0).getTextContent();

                        //Pobierz z wyłuskanego węzła treść notatki
                        String note_text = note_props.getElementsByTagName("Text").item(0).getTextContent();

                        //Pobierz z wyłuskanego węzła datę utworzenia notatki w formie liczby Long, którą to przekaż do konstruktora obiektu Date
                        Date note_create_date = new Date(Long.parseLong(note_props.getElementsByTagName("CreateDate").item(0).getTextContent()));

                        //Pobierz z wyłuskanego węzła datę modyfikacji notatki w formie liczby Long, którą to przekaż do konstruktora obiektu Date
                        Date note_modified_date = new Date(Long.parseLong(note_props.getElementsByTagName("ModDate").item(0).getTextContent()));

                        //Pobierz z wyłuskanego węzła stan ukrycia notatki
                        boolean note_hidden = !note_props.getElementsByTagName("Hidden").item(0).getTextContent().equals("false");

                        //Zadeklaruj zmienną referencyjną do notatki z listą zadań
                        ToDoNote todo_note;

                        //Wyłuskaj węzeł zawierający listę węzłów definiujących zadania
                        Element todo_list = (Element) note_props.getElementsByTagName("ToDoList").item(0);

                        //Zadeklaruj tablicę obiektów String o ilości elementów równej ilości węzłów w liście zadań
                        String[] todos_text = new String[todo_list.getElementsByTagName("ToDo").getLength()];

                        //Zadeklaruj tablicę wartości boolean o ilości elementów równej ilości węzłów w liście zadań
                        boolean[] todos_checked = new boolean[todo_list.getElementsByTagName("ToDo").getLength()];

                        //Jeśli lista ma co najmniej jeden węzeł <ToDo>
                        if (note_props.getElementsByTagName("ToDoList").getLength() > 0) {

                            //Dla każdego węzła z listy
                            for (int j = 0; j < todo_list.getElementsByTagName("ToDo").getLength(); j++) {

                                //Pobierz węzeł znajdujący się na liście pod indeksem j
                                Element todo = (Element) todo_list.getElementsByTagName("ToDo").item(j);


                                //Przypisz do elementu j tablicy obiektów String treść zadania z wcześniej pobranego węzła
                                todos_text[j] = todo.getElementsByTagName("Text").item(0).getTextContent();

                                //Przypisz do elementu j tablicy wartości boolean stan ukończenia zadania z wcześniej pobranego węzła
                                todos_checked[j] = Objects.equals(todo.getElementsByTagName("Checked").item(0).getTextContent(), "true");
                            }
                        }

                        //Jeżeli hasło jest niezdefiniowane, pomimo ukrytej notatki
                        if(password == null && note_hidden){

                            //Istnieje duża szansa manipulacji danymi. Zwróć wyjątek bezpieczeństwa
                            throw new SecurityException("Brak informacji o haśle pomimo istnienia notatek ukrytych. \n" +
                                    "Domniemana manipulacja pliku. Odmowa odczytu.");
                        }

                        //Stwórz nową notatkę z listą zadań, wykorzystując pobrane dane
                        todo_note = new ToDoNote(note_label, note_text, todos_text, todos_checked, note_hidden);

                        //Przypisz do notatki dane o datach
                        todo_note.setCreate_date(note_create_date);
                        todo_note.setMod_date(note_modified_date);

                        //Dodaj notatkę do listy
                        notes.addNote(todo_note);

                    //Jeśli wyłapany zostanie wyjątek bezpieczeństwa
                    } catch (SecurityException ex){

                        //Wyświetl informację o błędzie odczytu pliku i zwróć wartość pustą
                        JOptionPane.showMessageDialog(Main.main_frame, ex.getMessage(), "Błąd odczytu pliku", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                }
            }

            //Przypisz do pola z hasłem wartość hasła
            Main.password = password == null ? Main.password : password_grabbed.stripTrailing().stripLeading();

        //Zwróć nowo utworzoną listę notatek
        return notes;
     }

    /**
     * Buduje reprezentację drzewa DOM, wykorzystując do tego dane z notatek z listy.
     * Następnie przekształca uzyskane drzewo do formy dokumentu XML, który to z kolei umieszczany jest
     * w pliku przechowywanym w tym obiekcie.
     * @param notes Lista notatek, która ma zostać zapisana w pliku.
     * @throws FailedToWriteToFileException Gdy konwersja listy notatek do pliku zakończy się niepowodzeniem.
     */
     public void parseToFile(NoteList notes) throws FailedToWriteToFileException{
        //Rozpocznij egzekucję kodu z możliwością wystąpienia wyjątków
        try {

            //Uzyskaj nową instancję zestawu API do budowy dokumentów
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            //Stwórz nowy, pusty dokument
            Document xml_doc = db.newDocument();

            //Stwórz element-trzon — definiujący listę notatek jako całe ciało
            Element root = xml_doc.createElement("NoteList");

            //Umieść element w drzewie
            xml_doc.appendChild(root);


            //Jeśli hasło jest zdefiniowane
            if(Main.password != null) {

                //Stwórz element przechowujący dane o haśle i przypisz je do jego wartości tekstowej
                Element password = xml_doc.createElement("Password");
                password.appendChild(xml_doc.createTextNode(Main.password));

                //Umieść element w drzewie
                root.appendChild(password);
            }


            //Dla każdej notatki na liście
            for(int i = 0; i < notes.getListLength(); i++) {

                //Stwórz element reprezentujący ciało notatki
                Element note = xml_doc.createElement("Note");

                //Umieść element w drzewie
                root.appendChild(note);


                //Stwórz element przechowujący dane o type notatki
                Element type = xml_doc.createElement("Type");

                //Przypisz do wartości tekstowej elementu tekst w zależności od uzyskanego typu notatki
                type.appendChild(xml_doc.createTextNode(notes.getNote(i).getType() == Note.NOTE ? "NOTE" : "TODO-NOTE"));

                //Umieść element w drzewie
                note.appendChild(type);


                //Stwórz element przechowujący dane o etykiecie notatki i przypisz je do jego wartości tekstowej
                Element label = xml_doc.createElement("Label");
                label.appendChild(xml_doc.createTextNode(notes.getNote(i).getLabel()));

                //Umieść element w drzewie
                note.appendChild(label);


                //Stwórz element przechowujący dane o treści notatki i przypisz je do jego wartości tekstowej
                Element text = xml_doc.createElement("Text");
                text.appendChild(xml_doc.createTextNode(notes.getNote(i).getText()));

                //Umieść element w drzewie
                note.appendChild(text);


                //Stwórz element przechowujący dane o dacie utworzenia notatki
                Element create_date = xml_doc.createElement("CreateDate");

                //Przypisz do elementu wartość liczbową daty (wyrażoną poprzez tzw. uniksowy timestamp)
                create_date.appendChild(xml_doc.createTextNode(Long.toString(notes.getNote(i).getCreate_date().getTime())));

                //Umieść element w drzewie
                note.appendChild(create_date);


                //Stwórz element przechowujący dane o dacie modyfikacji notatki
                Element mod_date = xml_doc.createElement("ModDate");

                //Przypisz do elementu wartość liczbową daty (wyrażoną poprzez tzw. uniksowy timestamp)
                mod_date.appendChild(xml_doc.createTextNode(Long.toString(notes.getNote(i).getMod_date().getTime())));

                //Umieść element w drzewie
                note.appendChild(mod_date);


                //Stwórz element przechowujący dane o stanie ukrycia notatki
                Element hidden = xml_doc.createElement("Hidden");

                //Jeśli hasło jest zdefiniowane oraz na liście występują ukryte notatki
                if (notes.getNote(i).getHidden() && Main.password == null) {

                    //Zwróć wyjątek bezpieczeństwa. Nie można przechować notatek ukrytych bez zabezpieczającego je hasła
                    throw new SecurityException("Nie można zapisać pliku. Dalej istnieją notatki ukryte pomimo braku hasła. Dodaj najpierw hasło i spróbuj ponownie.");
                }

                //Przypisz do elementu wartość boolean w formie ciągu znaków
                hidden.appendChild(xml_doc.createTextNode(notes.getNote(i).getHidden() ? "true" : "false"));

                //Umieść element w drzewie
                note.appendChild(hidden);

                //Jeśli typem notatki jest TODO_NOTE
                if (notes.getNote(i).getType() == Note.TODO_NOTE) {

                    //Stwórz element będący ciałem listy zadań
                    Element todo_list = xml_doc.createElement("ToDoList");

                    //Umieść element w drzewie
                    note.appendChild(todo_list);


                    //Dla każdego zadania z listy zadań w notatce
                    for (int j = 0; j < ((ToDoNote) notes.getNote(i)).getTodo().length; j++) {

                        //Stwórz element będący ciałem zadania
                        Element todo = xml_doc.createElement("ToDo");

                        //Umieść element w drzewie
                        todo_list.appendChild(todo);


                        //Stwórz element zawierający dane o treści zadania i umieść w nim te dane
                        Element todo_text = xml_doc.createElement("Text");
                        todo_text.appendChild(xml_doc.createTextNode(((ToDoNote) notes.getNote(i)).getTodo(j)));

                        //Umieść element w drzewie
                        todo.appendChild(todo_text);


                        //Stwórz element zawierający dane o stanie odhaczenia zadania
                        Element todo_checked = xml_doc.createElement("Checked");

                        //Umieść w formie ciągu znakowego wartość boolean pobraną z notatki
                        todo_checked.appendChild(xml_doc.createTextNode(((ToDoNote) notes.getNote(i)).getChecked(j) ? "true" : "false"));

                        //Umieść element w drzewie
                        todo.appendChild(todo_checked);
                    }
                }

                //Uzyskaj nową instancję zestawu API przekształcającego powstałe drzewo DOM na dokument (wykorzystaj ustawienia domyślne)
                Transformer transformer = TransformerFactory.newInstance().newTransformer();

                //Stwórz źródłową reprezentację drzewa DOM dokumentu i umieść w nim wcześniej powstałe drzewo
                DOMSource source = new DOMSource(xml_doc);

                //Jeśli plik nie istnieje
                if (!doesFileExist()) {

                    //Utwórz tymczasową kopię pliku
                    File temp = getXml_file();

                    //Jeśli udało się utworzyć plik w lokalizacji pliku tymczasowego
                    if (temp.createNewFile()) {

                        //Wyświetl o tym informację na standardowym wyjściu
                        System.out.println("Utworzono plik w podanej lokalizacji");

                        //Ustaw wartość pliku przechowywanego w obiekcie na wartość pliku tymczasowego
                        setXml_file(temp);
                    }

                    //Jeśli to się nie powiedzie
                    else {

                        //Wyrzuć wyjątek operacji I/O
                        throw new IOException("Nie można utworzyć pliku.");
                    }


                    //Jeśli plik jest nie do zapisu
                    if (!isFileWritable()) {

                        //Zwróć odpowiedni wątek
                        throw new AccessDeniedException("Odmowa dostępu do pliku. Plik albo nie istnieje albo ma wadliwe ustawienia dostępu.");
                    }

                    //Stwórz nowy kontener wyjściowy i umieść w nim plik docelowy
                    StreamResult result = new StreamResult(getXml_file());

                    //Dokonaj przekształcenia drzewa DOM ze źródła na dokument XML umieszczony w pliku docelowym
                    transformer.transform(source, result);
                }
            }
        }

        //Jeśli wyłapano wyjątek bezpieczeństwa
        catch (SecurityException ex){

            //Wyświetl informacje o błędzie zapisu pliku
            JOptionPane.showMessageDialog(Main.main_frame, ex.getMessage(), "Błąd zapisu pliku", JOptionPane.ERROR_MESSAGE);

        //Jeśli wyłapano wyjątek dot. braku dostępu do pliku
        } catch(AccessDeniedException ex) {

            //Wyświetl informację o błędzie zapis pliku
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Błąd zapisu pliku",
                    JOptionPane.ERROR_MESSAGE
            );

            //Zwróć wyjątek niepowodzenia zapisu pliku
            throw new FailedToWriteToFileException("Nie udało się zapisać do pliku");
        }

        //Jeśli wystąpi wyjątek związany z operacjami I/O
        catch (IOException ex){

            //Wyświetl komunikat z wiadomością błędu
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Błąd zapisu pliku",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        //Jeśli wyłapano każdy inny wyjątek
        catch (Exception ex){

            //Wyświetl informację o błędzie wewnętrznym
            JOptionPane.showMessageDialog(
                    Main.main_frame,
                    ex.getMessage(),
                    "Wewnętrzny błąd aplikacji",
                    JOptionPane.ERROR_MESSAGE
            );

            //Zwróć wyjątek niepowodzenia zapisu pliku
            throw new FailedToWriteToFileException("Nie udało się zapisać do pliku");
        }
     }

    /**
     * Konstruktor parametryczny. Tworzy reprezentację pliku w podanej ścieżce i poddaje go odczytowi na dokument XML.
     * Obydwa obiekty przechowuje powstały obiekt w swoim ciele.
     * @param file_path Ścieżka dostępu do pliku.
     */
     FileHandler(String file_path){

         //Ustaw ścieżkę do pliku
        this.setFile_path(file_path);

        //Stwórz nową reprezentację pliku w danej ścieżce
        this.setXml_file(new File(this.getFile_path()));

        //Określ odpowiednie stany pliku
        this.file_exists = this.xml_file.exists();
        this.file_can_read = this.xml_file.canRead();
        this.file_can_write = this.xml_file.canWrite();

        //Dokonaj odczytu pliku i konwersji na dokument XML
        this.parseXml();
     }

    /**
     * Konstruktor parametryczny. Przechowuje reprezentację pliku w podanej ścieżce i poddaje go odczytowi na dokument XML.
     * Powstały dokument przechowuje w swoim ciele.
     * @param file Reprezentacja pliku, który ma być obsługiwany.
     */
     FileHandler(File file){

         //Przechowaj podaną w parametrze reprezentację pliku
         this.setXml_file(file);

         //Pobierz ścieżkę dostępu do pliku i ją przechowaj jako pole klasowe
         this.setFile_path(file.getPath());

         //Określ odpowiednie stany pliku
         this.file_exists = this.xml_file.exists();
         this.file_can_read = this.xml_file.canRead();
         this.file_can_write = this.xml_file.canWrite();

         //Dokonaj odczytu pliku i konwersji na dokument XML
         this.parseXml();
     }

    /**
     * Konstruktor parametryczny. Przechowuje reprezentację pliku w podanej ścieżce. Nie dokonuje natychmiastowego odczytu.
     * @param file Reprezentacja pliku, który ma być obsługiwany.
     * @param write Zmienna typu boolean określająca, czy plik będzie odczytywany, czy zapisywany. Jeśli odczytywany — konstruktor dokona tego automatycznie.
     */
    FileHandler(File file, boolean write){

        //Przechowaj podaną w parametrze reprezentację pliku
        this.setXml_file(file);

        //Pobierz ścieżkę dostępu do pliku i ją przechowaj jako pole klasowe
        this.setFile_path(file.getPath());

        //Określ odpowiednie stany pliku
        this.file_exists = this.xml_file.exists();
        this.file_can_read = this.xml_file.canRead();
        this.file_can_write = this.xml_file.canWrite();

        //Jeśli plik ma zostać odczytany
        if(!write){

            //Dokonaj odczytu pliku i jego konwersji na dokument XML, przechowywany w ciele obiektu
            this.parseXml();
        }
    }

}
