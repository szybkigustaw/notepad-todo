import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Date;
import java.util.Objects;

/**
 * Klasa odpowiedzialna za wykonywanie operacji wejścia/wyjścia na plikach oraz przetwarzająca dane w nich zawarte. Odpowiada za odczyt notatek z pliku oraz ich zapis do pliku.
 *
 * @author Michał Mikuła
 * @version 1.0
 */
public class FileHandler {
    private String file_path;
    private File xml_file;
    private Document parsed_file;

    /**
     * Metoda zwracająca ścieżkę do pliku, na którym wykonywane będą operacje I/O.
     * @return Ciąg znaków reprezentujący ścieżkę do pliku.
     */
    public String getFile_path(){ return this.file_path; }

    /**
     * Metoda nadpisująca obecną ścieżkę do pliku nową, zdefiniowaną przez użytkownika.
     * @param file_path Ciąg znaków - nowa ścieżka do pliku.
     */
    public void setFile_path(String file_path){ this.file_path = file_path; }

    public File getXml_file(){ return this.xml_file; }
    public void setXml_file(File xml_file){ this.xml_file = xml_file; }

    public Document getParsed_file(){ return this.parsed_file; }
    public void parseXml(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.parsed_file = db.parse(getXml_file());
        } catch (Exception e){
            System.out.println("Essa");
            System.out.println(e.getMessage());
        }
    }

     public NoteList parseDocToNotes(){
        NoteList notes = new NoteList();
            Element root = getParsed_file().getDocumentElement();
            Element password = (Element) root.getElementsByTagName("Password").item(0);
            String password_grabbed = password.getTextContent();
            Main.password = password_grabbed.stripTrailing().stripLeading();
            NodeList notes_got = root.getElementsByTagName("Note");

            for(int i = 0; i < notes_got.getLength(); i++){
                Element note_props = (Element) notes_got.item(i);
                if(Objects.equals(note_props.getElementsByTagName("Type").item(0).getTextContent(), "NOTE")) {
                    String note_label = note_props.getElementsByTagName("Label").item(0).getTextContent();
                    String note_text = note_props.getElementsByTagName("Text").item(0).getTextContent();
                    Date note_create_date = new Date(Long.parseLong(note_props.getElementsByTagName("CreateDate").item(0).getTextContent()));
                    Date note_modified_date = new Date(Long.parseLong(note_props.getElementsByTagName("ModDate").item(0).getTextContent()));
                    boolean note_hidden = !note_props.getElementsByTagName("Hidden").item(0).getTextContent().equals("false");

                    Note note = new Note(note_label, note_text, note_hidden);
                    note.setCreate_date(note_create_date);
                    note.setMod_date(note_modified_date);

                    notes.addNote(note);

                } else if(Objects.equals(note_props.getElementsByTagName("Type").item(0).getTextContent(), "TODO-NOTE")){
                    String note_label = note_props.getElementsByTagName("Label").item(0).getTextContent();
                    String note_text = note_props.getElementsByTagName("Text").item(0).getTextContent();
                    Date note_create_date = new Date(Long.parseLong(note_props.getElementsByTagName("CreateDate").item(0).getTextContent()));
                    Date note_modified_date = new Date(Long.parseLong(note_props.getElementsByTagName("ModDate").item(0).getTextContent()));
                    boolean note_hidden = !note_props.getElementsByTagName("Hidden").item(0).getTextContent().equals("false");

                    ToDoNote todo_note;

                    Element todo_list = (Element) note_props.getElementsByTagName("ToDoList").item(0);
                    String[] todos_text = new String[todo_list.getElementsByTagName("ToDo").getLength()];
                    boolean[] todos_checked = new boolean[todo_list.getElementsByTagName("ToDo").getLength()];
                    if(note_props.getElementsByTagName("ToDoList").getLength() > 0) {
                        for (int j = 0; j < todo_list.getElementsByTagName("ToDo").getLength(); j++) {
                            Element todo = (Element) todo_list.getElementsByTagName("ToDo").item(j);
                            todos_text[j] = todo.getElementsByTagName("Text").item(0).getTextContent();
                            todos_checked[j] = Objects.equals(todo.getElementsByTagName("Checked").item(0).getTextContent(), "true");
                        }
                    }
                    todo_note = new ToDoNote(note_label, note_text, todos_text, todos_checked, note_hidden);
                    todo_note.setCreate_date(note_create_date);
                    todo_note.setMod_date(note_modified_date);

                    notes.addNote(todo_note);
                }
            }
        return notes;
     }

     public void parseToFile(NoteList notes){
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml_doc = db.newDocument();

            Element root = xml_doc.createElement("NoteList");
            xml_doc.appendChild(root);

            Element password = xml_doc.createElement("Password");
            password.appendChild(xml_doc.createTextNode(Main.password));
            root.appendChild(password);

            for(int i = 0; i < notes.getListLength(); i++){
                Element note = xml_doc.createElement("Note");
                root.appendChild(note);

                Element type = xml_doc.createElement("Type");
                type.appendChild(xml_doc.createTextNode(notes.getNote(i).getType() == Note.NOTE ? "NOTE" : "TODO-NOTE"));
                note.appendChild(type);

                Element label = xml_doc.createElement("Label");
                label.appendChild(xml_doc.createTextNode(notes.getNote(i).getLabel()));
                note.appendChild(label);

                Element text = xml_doc.createElement("Text");
                text.appendChild(xml_doc.createTextNode(notes.getNote(i).getText()));
                note.appendChild(text);

                Element create_date = xml_doc.createElement("CreateDate");
                create_date.appendChild(xml_doc.createTextNode(Long.toString(notes.getNote(i).getCreate_date().getTime())));
                note.appendChild(create_date);

                Element mod_date = xml_doc.createElement("ModDate");
                mod_date.appendChild(xml_doc.createTextNode(Long.toString(notes.getNote(i).getMod_date().getTime())));
                note.appendChild(mod_date);

                Element hidden = xml_doc.createElement("Hidden");
                hidden.appendChild(xml_doc.createTextNode(notes.getNote(i).getHidden() ? "true" : "false"));
                note.appendChild(hidden);

                if(notes.getNote(i).getType() == Note.TODO_NOTE){
                    Element todo_list = xml_doc.createElement("ToDoList");
                    note.appendChild(todo_list);

                    for(int j = 0; j < ((ToDoNote)notes.getNote(i)).getTodo().length; j++){
                        Element todo = xml_doc.createElement("ToDo");
                        todo_list.appendChild(todo);

                        Element todo_text = xml_doc.createElement("Text");
                        todo_text.appendChild(xml_doc.createTextNode(((ToDoNote)notes.getNote(i)).getTodo(j)));
                        todo.appendChild(todo_text);

                        Element todo_checked = xml_doc.createElement("Checked");
                        todo_checked.appendChild(xml_doc.createTextNode(((ToDoNote) notes.getNote(i)).getChecked(j) ? "true" : "false"));
                        todo.appendChild(todo_checked);
                    }
                }

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(xml_doc);
                StreamResult result = new StreamResult(new File(this.file_path));
                transformer.transform(source, result);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
     }

     FileHandler(String file_path){
        this.setFile_path(file_path);
        this.setXml_file(new File(this.getFile_path()));

        if(getXml_file().exists() && getXml_file().canRead()){
            this.parseXml();
        } else if (!getXml_file().exists()){
            System.out.println("Brak pliku");
        } else if (!getXml_file().canRead()){
            System.out.println("Nie można otworzyć pliku!");
        }
     }

}
