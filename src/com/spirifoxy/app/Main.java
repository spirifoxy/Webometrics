package com.spirifoxy.app;

import com.spirifoxy.app.model.Site;
import com.spirifoxy.app.view.SiteOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private ObservableList<Site> siteData = FXCollections.observableArrayList();

    public Main() {
        siteData.add(new Site("altstu", "www.altstu.ru"));
        siteData.add(new Site("nstu", "www.nstu.ru"));
    }

    public ObservableList<Site> getSiteData() {
        return siteData;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/MainOverview.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Webometrics");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        SiteOverviewController controller = loader.getController();
        controller.setMain(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
