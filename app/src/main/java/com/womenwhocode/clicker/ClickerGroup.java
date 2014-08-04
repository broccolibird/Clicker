package com.womenwhocode.clicker;

import android.util.Log;

/**
 * Generates clicks.
 * Created by Kat on 7/31/14.
 */
public class ClickerGroup {
    private static final String TAG = "ClickerGroup";

    private String name;
    private String description;

    private int numberOfClickers;

    private double baseRate; // clicks per second
    private double additionalRate = 0;

    private double multiplier = 1;

    public ClickerGroup(String name, String description, double baseRate) {
        this.name = name;
        this.description = description;
        this.baseRate = baseRate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void increaseNumberOfClickers(int increaseBy) {
        numberOfClickers = numberOfClickers + increaseBy;
    }

    public double getNumberOfClickers() {
        return numberOfClickers;
    }

    public void addAdditionalRate(double rate) {
        additionalRate = additionalRate + rate;
    }

    public double getAdditionalRate() {
        return additionalRate;
    }

    public void increaseMultiplier(double multiplyBy) {
        multiplier = multiplier * multiplyBy;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public double getClicks() {
        return numberOfClickers * (baseRate + additionalRate) * multiplier;
    }

    public double getClicksForSecondsPassed(int seconds) {
        return seconds * getClicks();
    }
}
