package com.spirifoxy.app.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Класс-модель для сайта
 *
 * Created by spirifoxy on 14.02.2017.
 */
public class Site {
    private StringProperty name;
    private StringProperty address;

    public Site() {
        this(null, null);
    }

    public Site(String name, String address) {
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
    }

    public String toString() {
        return getName() + ": " + getAddress();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }
}
