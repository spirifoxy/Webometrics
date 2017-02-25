package com.spirifoxy.app;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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

   /* private String user;
    private String key;*/

    /* public MainViewController() {
         currentOption = Options.GOOGLE;
     }*/
    @FXML
    private void initialize() {
        model = new Model();

        btnSettings.setDisable(true);
        rbtnYandex.setDisable(true);

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

                model.serializeSites(tvSites.getItems());
                //writeTableToFile();
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
    private void chooseOption() { model.setCurrentOption(rbtnYandex.isSelected()); }

    @FXML
    private void launch() {
        final String YA_PAGES = "http://yandex.ru/yandsearch?text=site:";

        final String YA_PAGES_END = "&lr=197";

        final String YA_LINKS_BEGIN = "http://yandex.ru/yandsearch?text=link=\"";
        final String YA_LINKS_END = "*//*\"&noreask=1";

        final String YA_DOC_BEGIN = "http://yandex.ru/yandsearch?text=site:";
        final String YA_DOC_END = "+%26+%28mime:pdf+%7C+mime:ppt+%7C+mime:doc+%7C+mime:docx+%7C+mime:pptx+" +
                "%7C+mime:odt+%7C+mime:odp%29";

        final String G_PAGES_BEGIN = "http://www.google.ru/search?hl=ru&q=site:";
        final String G_PAGES_END = "&newwindow=1&filter=0";

        final String G_LINKS_BEGIN = "http://www.google.ru/search?hl=ru&q=link:";
        final String G_LINKS_END = "&newwindow=1&filter=0";

        final String G_DOC_BEGIN = "http://www.google.ru/search?hl=ru&q=site:";
        final String G_DOC_END = "+filetype:pdf+OR+filetype:ppt+OR+filetype:doc+OR+filetype:ps+OR+filetype:docx+OR+" +
                "filetype:pptx+OR+filetype:odp+OR+filetype:odt&newwindow=1&filter=0";

        final String G_SCHOLAR = "http://scholar.google.ru/scholar?as_vis=1&q=site:";

        String url = cbSites.getSelectionModel().getSelectedItem().getAddress();

        ArrayList<Integer> r = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();

        for (Label l : resultLabels) {
            l.setText("");
        }
        lWRank.setText("");

        switch (model.getCurrentOption()) {
            case GOOGLE:
                urls.addAll(Arrays.asList(
                        G_PAGES_BEGIN + url + G_PAGES_END,
                        G_LINKS_BEGIN + url + G_LINKS_END,
                        G_DOC_BEGIN + url + G_DOC_END,
                        G_SCHOLAR + url
                ));
                break;
            case YANDEX:
                urls.addAll(Arrays.asList(
                        YA_PAGES + url + YA_PAGES_END,
                        YA_LINKS_BEGIN + url + YA_LINKS_END,
                        YA_DOC_BEGIN + url + YA_DOC_END
                ));
                break;
            default:
                break;
        }

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
                                model.showErrorBox(e));

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
