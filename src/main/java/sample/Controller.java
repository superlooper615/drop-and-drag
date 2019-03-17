package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final DataFormat PERSON_LIST = new DataFormat("cis3368/personList");
    public ListView<Person> sourceList;
    public ListView<Person> targetList;

    /**
     * This is fired when the drag event starts over the sourceList
     * @param mouseEvent
     */
    public void dragProspectDetected(MouseEvent mouseEvent) {
        System.out.println("Drag event detected");
        int selected = sourceList.getSelectionModel().getSelectedIndices().size();
        System.out.println(String.format("%d selected",selected));
        if(selected > 0){
            Dragboard dragboard = sourceList.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ArrayList<Person> selectedItems = new ArrayList<>(sourceList.getSelectionModel().getSelectedItems());
            ClipboardContent content = new ClipboardContent();
            content.put(PERSON_LIST,selectedItems);
            dragboard.setContent(content);
            mouseEvent.consume();
        } else {
            System.out.println("nothing selected");
            mouseEvent.consume();
        }
    }

    /**
     * This method is called when the mouse is over a node during when there is an
     * active drag event.
     * @param dragEvent
     */
    public void dragOverCustomer(DragEvent dragEvent) {
        System.out.println("Drag over detected");
        Dragboard dragboard = dragEvent.getDragboard();
        if(dragboard.hasContent(PERSON_LIST)){
            dragEvent.acceptTransferModes(TransferMode.MOVE);

        }
        dragEvent.consume();
    }

    /**
     * This method is called when the mouse is released over the target
     * @param dragEvent
     */
    public void dragDroppedOnCustomer(DragEvent dragEvent) {
        boolean dragCompleted = false;
        Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasContent(PERSON_LIST)) {
            ArrayList<Person> people = (ArrayList<Person>) dragboard.getContent(PERSON_LIST);

            // now update the status of each person
            people.forEach(person -> person.setStatus(Person.Status.Customer));

            targetList.getItems().addAll(people);
            dragCompleted = true;
        }
        dragEvent.setDropCompleted(dragCompleted);
        dragEvent.consume();
    }

    /**
     * This mouse is called after the dragEvent has been mar
     * @param dragEvent
     */
    public void dragDoneOnProspect(DragEvent dragEvent) {
        System.out.println("Drag done detected");
        TransferMode tm = dragEvent.getAcceptedTransferMode();
        if(tm == TransferMode.MOVE) {
            removeSelectedItems();
        }
        dragEvent.consume();
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialized");
        sourceList.getItems().addAll(buildInitialData());
        sourceList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private ObservableList<Person> buildInitialData(){
        ObservableList<Person> list = FXCollections.observableArrayList();
        String[] names = {"John","Manuel","Mary","Ola","Quentin","Gandlalf"};
        int id = 1;
        for(String name:names) {
            list.add(new Person(id++,name));
        }
        return list;
    }

    private void removeSelectedItems() {
        List<Person> selectedFolk = new ArrayList<>();
        for(Person person : sourceList.getSelectionModel().getSelectedItems()){
            selectedFolk.add(person);
        }
        sourceList.getSelectionModel().clearSelection();

        sourceList.getItems().removeAll(selectedFolk);
    }
}
