package com.spirifoxy.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    private ObservableList<Site> siteData;
    private String dataPath;

    public void showErrorBox(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Во время исполнения программы возникла ошибка");
        alert.setContentText(e.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public Main() {
        siteData = FXCollections.observableArrayList();

        File here = new File(".");
        String dataName = "data.in";

        try {
            dataPath = here.getCanonicalPath() + "/" + dataName;

            InputStream in = new FileInputStream(dataPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] params = line.split(":");
                siteData.add(new Site(params[0], params[1]));
            }
            if (siteData.size() == 0) {
                siteData.add(new Site("altstu", "www.altstu.ru"));
            }
            br.close();
            in.close();
        } catch (Exception e) {
            this.showErrorBox(e);
        }
    }

    public ObservableList<Site> getSiteData() {
        return siteData;
    }

    public String getDataPath() {
        return dataPath;
    }
    
    public void addSite(Site s) {
        siteData.add(s);
    }

    public void deleteSite(ObservableList<Site> s) {
        s.forEach(siteData::remove);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainView.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Webometrics");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        MainViewController controller = loader.getController();
        controller.setMain(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
