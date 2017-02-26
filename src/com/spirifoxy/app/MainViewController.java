package com.spirifoxy.app;

import com.spirifoxy.app.State.GoogleState;
import com.spirifoxy.app.State.YandexState;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by spirifoxy on 14.02.2017.
 */
public class MainViewController {
    @FXML
    private ChoiceBox<Site> cbSites;
    @FXML
    private Button btnData;
    @FXML
    private Button btnSettings;
    @FXML
    private RadioButton rbtnYandex;
    @FXML
    private ProgressBar pbResults;
    @FXML
    private Label lSize;
    @FXML
    private Label lVisibility;
    @FXML
    private Label lRichFiles;
    @FXML
    private Label lScholar;
    @FXML
    private Label lWRank;


    @FXML
    private AnchorPane navList;
    @FXML
    private TableView<Site> tvSites;
    @FXML
    private TableColumn<Site, String> colName;
    @FXML
    private TableColumn<Site, String> colAddress;

    @FXML
    private TextField tfSiteName;
    @FXML
    private TextField tfSiteAddress;

    private ArrayList<Label> resultLabels;

    private Model model;

    @FXML
    private void initialize() {
        model = new Model();

        btnSettings.setDisable(true);
        //rbtnYandex.setDisable(true);

        resultLabels = new ArrayList<>(Arrays.asList(lSize, lVisibility, lRichFiles, lScholar));

        cbSites.setItems(model.getSiteData());
        tvSites.setItems(model.getSiteData());
        if (cbSites.getItems().size() != 0) {
            cbSites.getSelectionModel().selectFirst();
        }

        tvSites.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        prepareSlideMenuAnimation();
    }


    private void prepareSlideMenuAnimation() {
        TranslateTransition openNav = new TranslateTransition(new Duration(700), navList);
        openNav.setToX(0);
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), navList);

        btnData.setOnAction((ActionEvent evt) -> {
            if (navList.getTranslateX() != 0) {
                openNav.play();
            } else {
                closeNav.setToX(-(navList.getWidth()));
                closeNav.play();

                model.getDocStrat().save(tvSites.getItems());
            }
        });
    }

    @FXML
    private void addSite() {
        model.addSite(new Site(
                tfSiteName.getText(),
                tfSiteAddress.getText())
        );
        tfSiteName.clear();
        tfSiteAddress.clear();
    }

    @FXML
    private void deleteSite() {
        model.deleteSite(tvSites.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void chooseState(ActionEvent e) {
        if (rbtnYandex.isSelected())
            model.setState(new YandexState());
        else
            model.setState(new GoogleState());
    }

    @FXML
    private void launch() {
        Site site = cbSites.getSelectionModel().getSelectedItem();

        ArrayList<Integer> r = new ArrayList<>();
        ArrayList<String> urls = model.getState().prepareURLs(site);

        for (Label l : resultLabels) {
            l.setText("");
        }
        lWRank.setText("");

        Task request = new Task<Void>() {
            @Override
            public Void call() {
                String result = "";
                int[] coef = {2, 4, 1, 1};
                int WR = 0;

                for (int i = 0; i < urls.size(); i++) {
                    final String fResult;

                    try {
                        result = model.processURL(urls.get(i));
                        r.add(Integer.parseInt(result));
                    } catch (Exception e) {
                        Platform.runLater(() ->
                                Main.showErrorBox(e));

                    } finally {
                        fResult = result;
                    }

                    final int fI = i;
                    Platform.runLater(() -> resultLabels.get(fI).setText(fResult));
                    updateProgress(i + 1, urls.size());

                    WR += coef[i] * r.get(i);
                }

                final long fWR = WR;
                Platform.runLater(() -> lWRank.setText(Long.toString(fWR)));
                return null;
            }
        };
        pbResults.progressProperty().bind(request.progressProperty());
        new Thread(request).start();
    }


}
