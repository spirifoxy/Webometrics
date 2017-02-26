package com.spirifoxy.app.Strategy;

import com.spirifoxy.app.Main;
import com.spirifoxy.app.Site;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

/**
 * Created by spirifoxy on 26.02.2017.
 *
 */
public class SerDocStrategy implements DocStrategy {
    private String dataPath;

    public SerDocStrategy(String path) {
        dataPath = path;
    }

    @Override
    public ObservableList<Site> load() {
        ObservableList<Site> siteData = FXCollections.observableArrayList();

        try (ObjectInputStream ois
                     = new ObjectInputStream(new FileInputStream(dataPath))) {
            while (true) {
                try {
                    siteData.add((Site) ois.readObject());
                } catch (EOFException e) {
                    ois.close();
                    break;
                } catch (Exception e) {
                    Main.showErrorBox(e);
                }
            }
        } catch (IOException e) {
            Main.showErrorBox(e);
        }

        return siteData;
    }

    @Override
    public void save(ObservableList<Site> sites) {
        try (ObjectOutputStream oos
                     = new ObjectOutputStream(new FileOutputStream(dataPath))) {

            for (Site s : sites) {
                oos.writeObject(s);
            }

        } catch (Exception e) {
            Main.showErrorBox(e);
        }
    }
}
