package com.example.javafxhttpclient.core.treeItems.fragments;

import com.example.javafxhttpclient.controllers.SidebarController;
import com.example.javafxhttpclient.core.utils.Util;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.converter.DefaultStringConverter;

public class SavedRequestTreeCellImpl extends TreeCell<String> {
    SidebarController sidebarController;

    public SavedRequestTreeCellImpl(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            SavedRequestTreeItemAbstract treeItem = (SavedRequestTreeItemAbstract) getTreeItem();
            setText(getItem() == null ? "" : getItem());
            setGraphic(treeItem.getGraphic());

            // set context menu item actions here
            if (treeItem.createItem != null) treeItem.createItem.setOnAction(sidebarController::create);
            if (treeItem.renameItem != null) treeItem.renameItem.setOnAction(sidebarController::rename);
            if (treeItem.deleteItem != null) treeItem.deleteItem.setOnAction(sidebarController::delete);

            setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 1 && treeItem.getChildren().size() > 0) {
                    treeItem.setExpanded(!treeItem.isExpanded());
                }

                if (e.getButton().equals(MouseButton.SECONDARY)) {
                    treeItem.getMenu().show(getScene().getWindow(), e.getScreenX(), e.getScreenY());
                }

                e.consume();
            });
        }
    }
}

//    private void renameAction(SavedRequestTreeItemAbstract treeItem) {
//        String beforeText = getItem();
//        TextField tempTextField = new TextField();
//
//        // remove label
//        setText(null);
//        tempTextField.setText(beforeText);
//
//        // on enter
//        tempTextField.setOnAction(ex -> {
//            setText(Util.isStringValid(tempTextField.getText()) ? tempTextField.getText() : beforeText);
//            setGraphic(treeItem.getGraphic());
//        });
//
//        // out of focus
//        tempTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                setText(Util.isStringValid(tempTextField.getText()) ? tempTextField.getText() : beforeText);
//                setGraphic(treeItem.getGraphic());
//            }
//        });
//
//        setGraphic(tempTextField);
//        tempTextField.requestFocus();
//        tempTextField.end();
//
//        System.out.println("double click");
//    }