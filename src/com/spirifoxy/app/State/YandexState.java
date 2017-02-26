package com.spirifoxy.app.State;

import com.spirifoxy.app.Site;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by spirifoxy on 26.02.2017.
 */
public class YandexState implements State{
    @Override
    public ArrayList<String> prepareURLs(Site s) {
        final String YA_PAGES = "http://yandex.ru/yandsearch?text=site:";
        final String YA_PAGES_END = "&lr=197";

        final String YA_LINKS_BEGIN = "http://yandex.ru/yandsearch?text=link=\"";
        final String YA_LINKS_END = "*//*\"&noreask=1";

        final String YA_DOC_BEGIN = "http://yandex.ru/yandsearch?text=site:";
        final String YA_DOC_END = "+%26+%28mime:pdf+%7C+mime:ppt+%7C+mime:doc+%7C+mime:docx+%7C+mime:pptx+" +
                "%7C+mime:odt+%7C+mime:odp%29";

        String url = s.getAddress();
        ArrayList<String> urls = new ArrayList<>();

        urls.addAll(Arrays.asList(
                YA_PAGES + url + YA_PAGES_END,
                YA_LINKS_BEGIN + url + YA_LINKS_END,
                YA_DOC_BEGIN + url + YA_DOC_END
        ));
        return urls;
    }

    @Override
    public Element findResults(Document d) {
        return d.select("div[class=serp-adv__found]").first();
    }
}
