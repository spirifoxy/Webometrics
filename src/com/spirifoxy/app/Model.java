package com.spirifoxy.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;

/**
 * Created by spirifoxy on 23.02.2017.
 */
public class Model {
    private ObservableList<Site> siteData;
    private String dataPath;
    enum Options {YANDEX, GOOGLE}
    private Options currentOption;

    public Model() {
        siteData = FXCollections.observableArrayList();
        currentOption = Options.GOOGLE;

        File here = new File(".");
        String dataName = "data.ser";
        try {
            dataPath = here.getCanonicalPath() + "/" + dataName;
        } catch (IOException e) {
            showErrorBox(e);
        }

        try (ObjectInputStream ois
                     = new ObjectInputStream(new FileInputStream(getDataPath()))) {
            while (true) {
                try {
                    siteData.add((Site) ois.readObject());
                } catch (EOFException e) {
                    ois.close();
                    break;
                } catch (Exception e) {
                    showErrorBox(e);
                }
            }
        } catch (IOException e) {
            showErrorBox(e);
        }
        if (siteData.size() == 0) {
            siteData.add(new Site("altstu", "www.altstu.ru"));
        }
    }

    public ObservableList<Site> getSiteData() {
        return siteData;
    }

    public String getDataPath() {
        return dataPath;
    }

    public Options getCurrentOption() {
        return currentOption;
    }

    public void setCurrentOption(Boolean isYandexSelected) {
        currentOption = isYandexSelected ? Options.YANDEX : Options.GOOGLE;
    }

    public void addSite(Site s) {
        siteData.add(s);
    }

    public void deleteSite(ObservableList<Site> s) {
        s.forEach(siteData::remove);
    }

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

    private void writeDataToFile() {

    }

    public void serializeSites(ObservableList<Site> sites) {
        try (ObjectOutputStream oos
                     = new ObjectOutputStream(new FileOutputStream(getDataPath()))) {

            for (Site s : sites) {
                oos.writeObject(s);
            }
            System.out.println("Done");

        } catch (Exception e) {
            showErrorBox(e);
        }
    }

    public String processURL(String url) throws Exception {
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
                content = e.select("div[class=serp-adv__found]").first();
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
