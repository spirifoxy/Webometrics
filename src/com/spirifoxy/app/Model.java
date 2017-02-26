package com.spirifoxy.app;

import com.spirifoxy.app.State.GoogleState;
import com.spirifoxy.app.State.State;
import com.spirifoxy.app.Strategy.DocStrategy;
import com.spirifoxy.app.Strategy.SerDocStrategy;
import javafx.collections.ObservableList;
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

    private State state;
    private DocStrategy docStrat;

    public Model() {
        state = new GoogleState();

        File here = new File(".");
        String dataName = "data.ser";
        try {
            dataPath = here.getCanonicalPath() + "/" + dataName;
        } catch (IOException e) {
            Main.showErrorBox(e);
        }

        docStrat = new SerDocStrategy(getDataPath());
        siteData = docStrat.load();
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

    public State getState() {
        return state;
    }

    public DocStrategy getDocStrat() {
        return docStrat;
    }

    public void setState(State s) {
        state = s;
    }

    public void addSite(Site s) {
        siteData.add(s);
    }

    public void deleteSite(ObservableList<Site> s) {
        s.forEach(siteData::remove);
    }

    public String processURL(String url) throws Exception {
        Document d = Jsoup.connect(url).get();
        Element content = state.findResults(d);

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
