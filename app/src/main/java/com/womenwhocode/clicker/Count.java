package com.womenwhocode.clicker;

import java.util.ArrayList;

/**
 * Created by Kat on 7/30/14.
 */
public class Count {
    private static final String TAG = "Count";
    public int count;

    private ArrayList<ClickerGroup> clickers = new ArrayList<ClickerGroup>();

    public void addClicker(ClickerGroup clickerGroup) {
        this.clickers.add(clickerGroup);
    }

    public void addClicksFromClickers(int seconds) {
        for(ClickerGroup clicker : clickers) {
            count += clicker.getClicksForSecondsPassed(seconds);
        }
    }
}
