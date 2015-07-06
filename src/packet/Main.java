package packet;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Twitter: @d0tplist
 * Created by Alejandro Covarrubias on 7/5/15.
 */
public class Main extends Application {

    NotificationPane notificationPane = new NotificationPane();
    AnchorPane root = new AnchorPane();
    Scene scene;
    TabPane tabPane = new TabPane();
    ToolBar toolBar = new ToolBar();
    SplitPane splitPane = new SplitPane();
    ListView<Downloader> downloads = new ListView<>();

    private Button botonNewTab = new Button("+");
    private Button botonLeft = new Button();
    private Button botonRight = new Button();
    private Button botonHome = new Button();
    private Button botonRefresh = new Button();
    private Button botonHTML = new Button();
    private Button botonHistory = new Button();
    private Button botonDownload = new Button();


    ProgressBar indicator = new ProgressBar();

    private AutoCompletionBinding<String> autoCompletionBinding;
    private String[] _possibleSuggestions = {"google.com", "youtube.com", "tumblr.com", "apple.com", "hotmail.com"};
    private Set<String> possibleSuggestions = new HashSet<>(Arrays.asList(_possibleSuggestions));
    private TextField urlTextField = new TextField();
    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem menuItemBorrar = new MenuItem("Delete");


    @Override
    public void start(Stage primaryStage) throws Exception {

        scene = new Scene(notificationPane, 800, 700);
        notificationPane.setGraphic(indicator);
        notificationPane.setContent(root);
        root.getChildren().add(toolBar);
        root.getChildren().add(splitPane);

        AnchorPane.setTopAnchor(toolBar, 0.0);
        AnchorPane.setRightAnchor(toolBar, 0.0);
        AnchorPane.setLeftAnchor(toolBar, 0.0);

        AnchorPane.setTopAnchor(splitPane, 40.0);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        splitPane.getItems().add(tabPane);
        HBox.setHgrow(tabPane, Priority.ALWAYS);
        tabPane.setPrefSize(splitPane.getPrefWidth(), splitPane.getPrefHeight());

        toolBar.getItems().add(botonLeft);
        toolBar.getItems().addAll(botonRight);

        botonLeft.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/left.png"))));
        botonRight.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/right.png"))));
        botonHome.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/home.png"))));
        botonHTML.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/html.png"))));
        botonRefresh.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/refresh.png"))));
        botonHistory.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/history.png"))));
        botonDownload.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sources/download.png"))));

        HBox hboxBotones = new HBox();
        hboxBotones.getChildren().addAll(botonLeft, botonRight);

        toolBar.getItems().add(hboxBotones);
        toolBar.getItems().add(botonHome);
        toolBar.getItems().add(urlTextField);
        toolBar.getItems().add(botonRefresh);
        toolBar.getItems().add(botonHTML);
        toolBar.getItems().add(botonHistory);
        toolBar.getItems().add(botonDownload);
        toolBar.getItems().add(botonNewTab);

        primaryStage.setTitle("BrowserFX by @d0tplist");
        urlTextField.setPromptText("Search");
        urlTextField.setPrefWidth(450);
        botonNewTab.setFont(new Font(14));


        contextMenu.getItems().add(menuItemBorrar);
        menuItemBorrar.setOnAction(event -> {
            if (downloads.getSelectionModel().getSelectedIndex() != -1) {
                downloads.getItems().remove(downloads.getSelectionModel().getSelectedItem());
            }
        });


        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(!tabPane.getTabs().isEmpty()) {
                urlTextField.setText(((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).getEngine().getLocation());
            }
        });

        downloads.setContextMenu(contextMenu);

        botonDownload.setOnAction(event -> {
            if (splitPane.getItems().size() > 1) {
                splitPane.getItems().remove(downloads);
            } else {
                splitPane.getItems().add(downloads);
            }
        });

        botonHome.setOnAction(event1 -> {
            if (tabPane.getTabs().isEmpty()) {
                tabPane.getTabs().add(new PowerBrowser(notificationPane, urlTextField, scene, downloads));
            } else {
                ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).go("http://www.google.com//");
            }
        });

        botonLeft.setOnAction(event -> {
            if (!tabPane.getTabs().isEmpty()) {
                ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).goLeft();
            }
        });
        toolBar.setNodeOrientation(NodeOrientation.INHERIT);

        botonRight.setOnAction(event -> {
            if (!tabPane.getTabs().isEmpty()) {
                ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).goRight();
            }
        });

        botonRefresh.setOnAction(event -> {
            if (!tabPane.getTabs().isEmpty()) {
                ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).getEngine().reload();
            }
        });

        botonHTML.setOnAction(event -> {
            if (!tabPane.getTabs().isEmpty()) {
                ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).showHTML();
            }
        });


        botonHistory.setOnAction(event -> {
            if (!tabPane.getTabs().isEmpty()) {
                ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).showHistory();
            }
        });

        botonNewTab.setOnAction(event -> {
            tabPane.getTabs().add(new PowerBrowser(notificationPane, urlTextField, scene, downloads));
            tabPane.getSelectionModel().selectLast();
        });
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        autoCompletionBinding = TextFields.bindAutoCompletion(urlTextField, possibleSuggestions);

        urlTextField.setOnKeyPressed(ke -> {
            switch (ke.getCode()) {
                case ENTER:
                    autoCompletionLearnWord(urlTextField.getText().trim());
                    if (!tabPane.getTabs().isEmpty()) {
                        ((PowerBrowser) tabPane.getSelectionModel().getSelectedItem()).go(urlTextField.getText());
                    } else {
                        PowerBrowser browser = new PowerBrowser(notificationPane, urlTextField, scene, downloads);
                        tabPane.getTabs().add(browser);
                        browser.go(urlTextField.getText());
                    }
                    break;
                default:
                    break;
            }
        });
        HBox.setHgrow(urlTextField, Priority.ALWAYS);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void autoCompletionLearnWord(String newWord) {
        possibleSuggestions.add(newWord);

        // we dispose the old binding and recreate a new binding
        if (autoCompletionBinding != null) {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(urlTextField, possibleSuggestions);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
