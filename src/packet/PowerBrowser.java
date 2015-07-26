package packet;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.controlsfx.control.NotificationPane;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalTime;


/**
 * Twitter: @d0tplist
 * Created by Alejandro Covarrubias on 7/5/15.
 */
public class PowerBrowser extends Tab {

    private WebEngine engine;
    private NotificationPane progressNotification;
    private SplitPane splitPane = new SplitPane();
    private ListView<Historia> historial = new ListView<>();
    private AnchorPane anchorPaneHistoria = new AnchorPane();
    Scene scene;
    ListView<Downloader> downloads;
    String last;

    public PowerBrowser(NotificationPane notificationPane, TextField urlTextField, Scene scene,ListView<Downloader> downloads) {
        this.scene = scene;
        this.downloads = downloads;
        setText("Loading");
        ProgressBar progressBar = new ProgressBar(1);
        progressNotification = new NotificationPane();
        setContent(progressNotification);
        WebView view = new WebView();
        engine = view.getEngine();
        engine.getLoadWorker().runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                notificationPane.show();
            } else {
                notificationPane.hide();
            }
        });
        engine.load("http://www.google.com/");
        progressNotification.setContent(splitPane);
        splitPane.getItems().add(view);


        engine.setOnStatusChanged(event -> {
            if (engine.getLocation().startsWith("http")) {
                urlTextField.setText(engine.getLocation());
            }
            if (engine.getTitle() != null) {
                if (engine.getTitle().length() < 20) {
                    setText(engine.getTitle());
                } else {
                    setText(engine.getTitle().substring(0, 20) + "...");
                }
            }

        });

        engine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (downloadableFiles(engine.getLocation())) {
                downloadFile(engine.getLocation());

            }

            if (historial.isVisible()) {
                historial.getItems().clear();
                for (WebHistory.Entry in : engine.getHistory().getEntries()) {
                    historial.getItems().add(new Historia(in.getUrl(), in.getTitle(), LocalTime.now()));
                }
            }
        });

        engine.setOnError(event -> System.out.println(event.getMessage()));

        engine.setOnAlert(event -> System.out.println(event.getData()));

        engine.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Old value: " + oldValue + " new Value: " + newValue);
            if (newValue != null) {
                if (!last.startsWith("https://www.google.com")) {
                    engine.load("https://www.google.com.mx/#q=" + last);
                    urlTextField.clear();
                }
            }
        });


        notificationPane.showFromTopProperty().bind(new SimpleBooleanProperty(false));
        notificationPane.textProperty().bind(engine.getLoadWorker().messageProperty());

        progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());
        progressBar.setPrefWidth(300);

        ToolBar toolBarHistoria = new ToolBar();
        anchorPaneHistoria.getChildren().add(toolBarHistoria);
        anchorPaneHistoria.getChildren().add(historial);

        AnchorPane.setTopAnchor(toolBarHistoria, 0.0);
        AnchorPane.setRightAnchor(toolBarHistoria, 0.0);
        AnchorPane.setLeftAnchor(toolBarHistoria, 0.0);

        AnchorPane.setTopAnchor(historial, 40.0);
        AnchorPane.setRightAnchor(historial, 0.0);
        AnchorPane.setLeftAnchor(historial, 0.0);
        AnchorPane.setBottomAnchor(historial, 0.0);

        Button botonExitHistoria = new Button("Close");
        toolBarHistoria.getItems().add(botonExitHistoria);
        botonExitHistoria.setOnAction(event -> splitPane.getItems().remove(anchorPaneHistoria));

        
    }

    public WebEngine getEngine() {

        return engine;
    }


    public void goRight() {
        engine.executeScript("history.forward()");
    }

    public void goLeft() {
        engine.executeScript("history.back()");
    }

    public void go(String url) {
        last = url;
        if (!url.startsWith("http://") || !url.startsWith("https://")) {
            engine.load("http://" + url+"/");
        } else {
            engine.load(url);
        }
    }

    public void showHTML() {
        try {
            if (splitPane.getItems().size() == 1) {
                URL yahoo = new URL(engine.getLocation());
                URLConnection yc = yahoo.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        yc.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine).append("\n");
                }
                in.close();


                AnchorPane tempanchor = new AnchorPane();
                ToolBar tempToolBar = new ToolBar();
                Button botonSave = new Button("Save");
                Button botonExit = new Button("Close");
                tempanchor.getChildren().add(tempToolBar);
                TextArea editor = new TextArea();
                editor.setText(a.toString());
                tempanchor.getChildren().add(editor);

                AnchorPane.setTopAnchor(tempToolBar, 0.0);
                AnchorPane.setRightAnchor(tempToolBar, 0.0);
                AnchorPane.setLeftAnchor(tempToolBar, 0.0);

                AnchorPane.setTopAnchor(editor, 40.0);
                AnchorPane.setRightAnchor(editor, 0.0);
                AnchorPane.setLeftAnchor(editor, 0.0);
                AnchorPane.setBottomAnchor(editor, 0.0);

                tempToolBar.getItems().add(botonExit);
                tempToolBar.getItems().add(botonSave);

                botonExit.setOnAction(event -> splitPane.getItems().remove(tempanchor));
                botonSave.setOnAction(event -> {
                    FileChooser chooser = new FileChooser();
                    File archivo = chooser.showSaveDialog(scene.getWindow());
                    try {
                        FileWriter writer = new FileWriter(archivo);
                        writer.write(a.toString());
                        writer.close();
                        progressNotification.setText("Done!");
                        progressNotification.show();
                    } catch (IOException ex) {
                        progressNotification.setText(ex.getMessage());
                        progressNotification.show();
                    }

                });
                splitPane.getItems().add(tempanchor);
            } else {
                if (splitPane.getItems().size() > 1) {
                    splitPane.getItems().remove(1);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showHistory() {
        if (splitPane.getItems().size() > 1) {
            splitPane.getItems().remove(1);
        } else {

            historial.getItems().clear();

            splitPane.getItems().add(anchorPaneHistoria);

            for (WebHistory.Entry in : engine.getHistory().getEntries()) {
                historial.getItems().add(new Historia(in.getUrl(), in.getTitle(), LocalTime.now()));
            }

            historial.setOnMouseClicked(event -> {
                if (historial.getSelectionModel().getSelectedIndex() != -1) {
                    engine.load(historial.getSelectionModel().getSelectedItem().getUrl());
                }
            });
        }

    }

    public boolean downloadableFiles(String location) {
        if (location.endsWith(".exe")) {
            return true;
        } else if (location.endsWith(".dmg")) {
            return true;
        } else if (location.endsWith(".pdf")) {
            return true;
        } else if (location.endsWith(".png")) {
            return true;
        } else if (location.endsWith(".jpg")) {
            return true;
        } else if (location.endsWith(".txt")) {
            return true;
        } else if (location.endsWith(".xls")) {
            return true;
        } else if (location.endsWith(".doc")) {
            return true;
        } else if (location.endsWith(".mp3")) {
            return true;
        } else if (location.endsWith(".mp4")) {
            return true;
        } else if (location.endsWith(".zip")) {
            return true;
        } else if (location.endsWith(".rar")) {
            return true;
        } else if (location.endsWith(".7z")) {
            return true;
        } else if (location.endsWith(".png")) {
            return true;
        } else if (location.endsWith(".sql")) {
            return true;
        } else if (location.endsWith(".jar")) {
            return true;
        } else if (location.endsWith(".deb")) {
            return true;
        } else if (location.endsWith(".tar")) {
            return true;
        } else if (location.endsWith(".tar.gz")) {
            return true;
        }
        return false;
    }

    public void downloadFile(String location) {
        downloads.getItems().add(new Downloader(location, downloads));
    }


}
