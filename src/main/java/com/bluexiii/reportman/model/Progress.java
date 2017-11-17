package com.bluexiii.reportman.model;

/**
 * Created by bluexiii on 07/11/2017.
 */
public class Progress {
    private int percent;
    private String message;

    public Progress() {
    }

    public Progress(int percent, String message) {
        this.percent = percent;
        this.message = message;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Progress{" +
                "percent=" + percent +
                ", message='" + message + '\'' +
                '}';
    }
}
