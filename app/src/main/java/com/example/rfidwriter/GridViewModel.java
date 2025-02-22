package com.example.rfidwriter;

public class GridViewModel {
    // string course_name for storing course_name
    // and imgid for storing image id.
    private String course_name;
    private int imgid;

    public GridViewModel(String course_name, int imgid) {
        this.course_name = course_name;
        this.imgid = imgid;
    }

    public GridViewModel(String course_name) {
        this.course_name = course_name;
        //this.imgid = imgid;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }
}
