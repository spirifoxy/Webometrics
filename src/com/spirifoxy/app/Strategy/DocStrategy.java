package com.spirifoxy.app.Strategy;

import com.spirifoxy.app.Site;
import javafx.collections.ObservableList;

/**
 * Created by spirifoxy on 26.02.2017.
 */
public interface DocStrategy {
    ObservableList<Site> load();
    void save(ObservableList<Site> sites);
}
