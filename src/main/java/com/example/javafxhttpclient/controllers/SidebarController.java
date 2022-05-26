package com.example.javafxhttpclient.controllers;

import com.example.javafxhttpclient.core.enums.SavedTreeItemType;
import com.example.javafxhttpclient.core.modals.AddTreeItemModalWindow;
import com.example.javafxhttpclient.core.modals.CheckModalWindow;
import com.example.javafxhttpclient.core.modals.RenameTreeItemModalWindow;
import com.example.javafxhttpclient.entities.RequestEntity;
import com.example.javafxhttpclient.core.misc.treeItems.FolderTreeItem;
import com.example.javafxhttpclient.core.misc.treeItems.SavedRequestTreeCellImpl;
import com.example.javafxhttpclient.core.misc.treeItems.SavedRequestTreeItemAbstract;
import com.example.javafxhttpclient.core.utils.Util;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class SidebarController implements Initializable {
    public static SidebarController instance;

    private final TreeItem<String> invisibleRootComponent;

    public List<RequestEntity> rootTreeItems;

    @FXML
    public AnchorPane rootParent;

    @FXML
    public TreeView<String> rootTreeView;

    @FXML
    public TextField filterTextField;

    public SidebarController() {
        invisibleRootComponent = new TreeItem<>(null);

        // temp
        RequestEntity root1 = new RequestEntity(1, SavedTreeItemType.FOLDER, "Simple api request");
        RequestEntity model11 = new RequestEntity(2, SavedTreeItemType.REQUEST, "Json test 1");
        RequestEntity model12 = new RequestEntity(3, SavedTreeItemType.REQUEST, "Json test 2");
        root1.setChildren(model11, model12);

        RequestEntity root2 = new RequestEntity(4, SavedTreeItemType.FOLDER, "Some folder");
        RequestEntity root3 = new RequestEntity(5, SavedTreeItemType.REQUEST, "testing");

        List<RequestEntity> data = new ArrayList<>();
        data.add(root1);
        data.add(root2);
        data.add(root3);

        rootTreeItems = data;
        invisibleRootComponent.getChildren().addAll(data.stream().map(RequestEntity::getFxmlComponent).toList());

        if (instance == null) {
            instance = this;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootTreeView.setCellFactory(p -> new SavedRequestTreeCellImpl(this));
        rootTreeView.setRoot(invisibleRootComponent);
        rootTreeView.setShowRoot(false);
        debounceFilter();
    }

    // |=====================================================
    // | METHODS
    // |=====================================================

    public void onCreateButtonClick(ActionEvent event) {
        // lose focus
        rootParent.requestFocus();
        this.create(event);
    }

    public void create(ActionEvent event) {
        // memoize here
        final boolean isTreeViewRootParentFocused = rootParent.isFocused();

        try {
            AddTreeItemModalWindow addTreeItemModalWindow = new AddTreeItemModalWindow();
            addTreeItemModalWindow.show(() -> {
                String newName = addTreeItemModalWindow.getDisplayName();
                SavedTreeItemType newType = addTreeItemModalWindow.getType();

                if (newType != null && Util.isStringValid(newName)) {
                    int randomId = Util.randInt(1000);
                    RequestEntity newRootItem = new RequestEntity(randomId, newType, newName);
                    addTreeItemToSpecificLevel(newRootItem, isTreeViewRootParentFocused);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            event.consume();
        }
    }

    public void rename(ActionEvent event) {
        var selectedTreeItem = rootTreeView.getSelectionModel().getSelectedItem();

        try {
            RenameTreeItemModalWindow renameTreeItemModalWindow = new RenameTreeItemModalWindow();
            renameTreeItemModalWindow.setOldName(selectedTreeItem.getValue());

            renameTreeItemModalWindow.show(() -> {
                String newName = renameTreeItemModalWindow.getNewName();

                if (Util.isStringValid(newName)) {
                    renameTreeItemToSpecificLevel(newName);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            event.consume();
        }
    }

    public void delete(ActionEvent event) {
        CheckModalWindow checkModalWindow = new CheckModalWindow();

        try {
            checkModalWindow.show(() -> {
                if (checkModalWindow.isAnswerYes()) {
                    // delete
                    deleteTreeItemToSpecificLevel();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            event.consume();
        }
    }

    private void addTreeItemToSpecificLevel(RequestEntity item, boolean isTreeViewRootParentFocused) {
        System.out.println(isTreeViewRootParentFocused);

        if (isTreeViewRootParentFocused) {
            // fxml
            invisibleRootComponent.getChildren().addAll(item.getFxmlComponent());

            // add data
            rootTreeItems.add(item);

            return;
        }

        SavedRequestTreeItemAbstract selectedTreeItem = (SavedRequestTreeItemAbstract) rootTreeView
                .getSelectionModel()
                .getSelectedItem();

        if (selectedTreeItem.getClass().equals(FolderTreeItem.class)) {
            if (item.getType() == SavedTreeItemType.FOLDER) {
                Util.showAlertModal(Alert.AlertType.ERROR, "Allowed only request, cannot nest folders !");
                return;
            }

            // fxml ( allowed here only if selected is folder and item type is request )
            selectedTreeItem.getChildren().add(item.getFxmlComponent());

            // add data to children of already existing item in root
            for (RequestEntity parent : rootTreeItems) {
                if (parent.getId() == selectedTreeItem.getId()) {
                    parent.getChildren().add(item);
                    break;
                }
            }
        } else {
            // fxml
            invisibleRootComponent.getChildren().addAll(item.getFxmlComponent());

            // add data
            rootTreeItems.add(item);
        }
    }

    private void renameTreeItemToSpecificLevel(String newName) {
        SavedRequestTreeItemAbstract selectedTreeItem = (SavedRequestTreeItemAbstract) rootTreeView.getSelectionModel().getSelectedItem();
        int id = selectedTreeItem.getId();

        // set fxml
        selectedTreeItem.setValue(newName);

        // update data
        for (RequestEntity parent : rootTreeItems) {
            if (parent.getId() == id) {
                var foundIndex = rootTreeItems.indexOf(parent);
                var foundElement = rootTreeItems.get(foundIndex);
                foundElement.setName(newName);
                rootTreeItems.set(foundIndex, foundElement);
                break;
            }

            for (RequestEntity child : parent.getChildren()) {
                if (child.getId() == id) {
                    var foundParentIndex = rootTreeItems.indexOf(parent);
                    var foundParentElement = rootTreeItems.get(foundParentIndex);
                    foundParentElement.setChildName(id, newName);
                    rootTreeItems.set(foundParentIndex, foundParentElement);
                    break;
                }
            }
        }
    }

    private void deleteTreeItemToSpecificLevel() {
        SavedRequestTreeItemAbstract selectedTreeItem = (SavedRequestTreeItemAbstract) rootTreeView.getSelectionModel().getSelectedItem();
        int id = selectedTreeItem.getId();

        // fxml
        selectedTreeItem.getParent().getChildren().remove(selectedTreeItem);

        // delete data
        for (RequestEntity parent : rootTreeItems) {
            if (parent.getId() == id) {
                rootTreeItems.remove(parent);
                break;
            }

            for (RequestEntity child : parent.getChildren()) {
                if (child.getId() == id) {
                    var foundParentIndex = rootTreeItems.indexOf(parent);
                    var foundParentElement = rootTreeItems.get(foundParentIndex);
                    foundParentElement.removeChild(child);
                    rootTreeItems.set(foundParentIndex, foundParentElement);
                    break;
                }
            }
        }
    }

    // |=====================================================
    // | OTHER
    // |=====================================================

    private void debounceFilter() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        filterTextField.textProperty().addListener((observable, oldValue, filterText) -> {
            pause.setOnFinished(event -> {
                if (filterText.isEmpty()) {
                    invisibleRootComponent.getChildren().clear();
                    invisibleRootComponent.getChildren().addAll(rootTreeItems.stream().map(RequestEntity::getFxmlComponent).toList());
                    return;
                }

                List<RequestEntity> filteredItems = new ArrayList<>();

                // filter ui here
                for (var parent : rootTreeItems) {
                    int childMatched = 0;
                    RequestEntity tempParent = parent.cloneEmpty();
                    tempParent.getFxmlComponent().setExpanded(true);

                    // first check children
                    for (var child : parent.getChildren()) {
                        if (child.getName().contains(filterText)) {
                            childMatched++;
                            tempParent.getChildren().add(child);
                            tempParent.setChildren(child);
                        }
                    }

                    if (tempParent.getName().contains(filterText) || childMatched != 0) {
                        filteredItems.add(tempParent);
                    }
                }

                invisibleRootComponent.getChildren().clear();
                invisibleRootComponent.getChildren().addAll(filteredItems.stream().map(RequestEntity::getFxmlComponent).toList());
            });
            pause.playFromStart();
        });
    }
}
