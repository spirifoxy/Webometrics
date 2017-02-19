package com.spirifoxy.app.view;

import com.spirifoxy.app.Main;
import com.spirifoxy.app.model.Site;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by spirifoxy on 14.02.2017.
 */
public class SiteOverviewController {
    @FXML
    private ChoiceBox<Site> siteList;
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

    private Main main;

    private ArrayList<Label> resultLabels;

    enum Options {YANDEX, GOOGLE}
    private Options currentOption;

    private String user;
    private String key;

    public SiteOverviewController() {
        currentOption = Options.GOOGLE;

        //TODO change this please
        user = "spirifoxy";
        key = "03.364476428:da836085f2894e4585e1d1499c23ab86";
    }

    @FXML
    private void initialize() {
        btnData.setDisable(true);
        btnSettings.setDisable(true);
        rbtnYandex.setDisable(true);

        resultLabels = new ArrayList<>();
        resultLabels.addAll(Arrays.asList(lSize, lVisibility, lRichFiles, lScholar));
    }

    public void setMain(Main main) {
        this.main = main;

        siteList.setItems(main.getSiteData());
        if (siteList != null) {
            siteList.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void chooseOption() {
        currentOption = rbtnYandex.isSelected() ? Options.YANDEX : Options.GOOGLE;
    }

    @FXML
    private void launch() {
        final String YA_PAGES = "http://yandex.ru/yandsearch?text=site:";
        final String YA_PAGES_KEY = "http://xmlsearch.yandex.ru/xmlsearch?site=";

        final String YA_LINKS_BEGIN = "http://yandex.ru/yandsearch?text=link=\"";
        final String YA_LINKS_BEGIN_KEY = "http://xmlsearch.yandex.ru/xmlsearch?text=\"http://";
        final String YA_LINKS_END = "/*\"&noreask=1";

        final String YA_DOC_BEGIN = "http://yandex.ru/yandsearch?text=site:";
        final String YA_DOC_BEGIN_KEY = "http://xmlsearch.yandex.ru/xmlsearch?text=site:";
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

        String url = siteList.getSelectionModel().getSelectedItem().getAddress();

        ArrayList<Integer> r = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();

        for (Label l : resultLabels) {
            l.setText("");
        }
        lWRank.setText("");

        switch (currentOption) {
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
                        YA_PAGES + url,
                        YA_LINKS_BEGIN + url + YA_LINKS_END,
                        YA_DOC_BEGIN + url + YA_DOC_END
                        /*
                        YA_PAGES_KEY + url + "&user=" + user + "&key=" + key,
                        YA_LINKS_BEGIN_KEY + url + YA_LINKS_END + "&user=" + user + "&key=" + key,
                        YA_DOC_BEGIN_KEY + url + YA_DOC_END + "&user=" + user + "&key=" + key
                        */
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
                        result = process(urls.get(i));
                        r.add(Integer.parseInt(result));
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Во время исполнения программы возникла ошибка");
                            alert.setContentText(e.getMessage());
                            alert.showAndWait();
                        });
                    } finally {
                        fResult = result;
                    }

                    final int fI = i;
                    Platform.runLater(() -> resultLabels.get(fI).setText(fResult));
                    updateProgress(i + 1, urls.size());

                    WR += coef[i] * r.get(i);
                }

                final int fWR = WR;
                Platform.runLater(()-> lWRank.setText(Integer.toString(fWR)));

                return null;
            }
        };

        pbResults.progressProperty().bind(request.progressProperty());
        new Thread(request).start();
    }

    private String process(String url) throws Exception {
        Document e = Jsoup.connect(url).get();
        Element content;

        switch (currentOption) {
            case GOOGLE:
                if (url.substring(7, 14).equals("scholar"))
                    content = e.getElementById("gs_ab_md");
                else
                    content = e.getElementById("resultStats");
                break;
            case YANDEX:
                content = e.select("found[priority*=phrase]").first();
                break;
            default:
                content = null;
        }

        if (content == null)
            throw new IOException("Ошибка подключения");

        String text = content.text();
        String[] searchList = {
                "Нашлось", "Нашлась", "Нашёлся", "Нашлись",
                "ответ",
                "тыс.",
                "Результатов: примерно ",
                "Результатов: ",
                "Найдено",
                "документов",
                "ов",
                "а"
        };
        for (int i = 0; i < searchList.length; ++i) {
            text = text.replace(searchList[i], "");
        }
        return text.replaceAll("\\(.+\\)", "").replaceAll("[  ]", "");
    }
}
