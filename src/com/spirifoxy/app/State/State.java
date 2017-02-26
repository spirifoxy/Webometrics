package com.spirifoxy.app.State;

import com.spirifoxy.app.Site;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by spirifoxy on 26.02.2017.
 */
public interface State {
    ArrayList<String> prepareURLs(Site s);
    Element findResults(Document d);
}
