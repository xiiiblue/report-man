package com.bluexiii.reportman.domain;

/**
 * Created by bluexiii on 17/10/2017.
 */
public class Task {
    private String taskName;
    private String taskStatus;
    private String connTag;
    private String sql;
    private int sheetId;
    private int offsetX;
    private int offsetY;
    private String cellStyle;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getConnTag() {
        return connTag;
    }

    public void setConnTag(String connTag) {
        this.connTag = connTag;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getSheetId() {
        return sheetId;
    }

    public void setSheetId(int sheetId) {
        this.sheetId = sheetId;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public String getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(String cellStyle) {
        this.cellStyle = cellStyle;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", connTag='" + connTag + '\'' +
                ", sql='" + sql + '\'' +
                ", sheetId=" + sheetId +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", cellStyle='" + cellStyle + '\'' +
                '}';
    }
}
