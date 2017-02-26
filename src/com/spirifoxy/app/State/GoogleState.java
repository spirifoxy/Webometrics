package com.spirifoxy.app.State;

import com.spirifoxy.app.Site;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by spirifoxy on 26.02.2017.
 */
public class GoogleState implements State {
    @Override
    public ArrayList<String> prepareURLs(Site s) {
        final String G_PAGES_BEGIN = "http://www.google.ru/search?hl=ru&q=site:";
        final String G_PAGES_END = "&newwindow=1&filter=0";

        final String G_LINKS_BEGIN = "http://www.google.ru/search?hl=ru&q=link:";
        final String G_LINKS_END = "&newwindow=1&filter=0";

        final String G_DOC_BEGIN = "http://www.google.ru/search?hl=ru&q=site:";
        final String G_DOC_END = "+filetype:pdf+OR+filetype:ppt+OR+filetype:doc+OR+filetype:ps+OR+filetype:docx+OR+" +
                "filetype:pptx+OR+filetype:odp+OR+filetype:odt&newwindow=1&filter=0";

        final String G_SCHOLAR = "http://scholar.google.ru/scholar?as_vis=1&q=site:";

        String url = s.getAddress();
        ArrayList<String> urls = new ArrayList<>();

        urls.addAll(Arrays.asList(
                G_PAGES_BEGIN + url + G_PAGES_END,
                G_LINKS_BEGIN + url + G_LINKS_END,
                G_DOC_BEGIN + url + G_DOC_END,
                G_SCHOLAR + url
        ));
        return urls;
    }

    @Override
    public Element findResults(Document d) {
        return d.baseUri().substring(8, 15).equals("scholar")
                ? d.getElementById("gs_ab_md")
                : d.getElementById("resultStats1");
    }
}
