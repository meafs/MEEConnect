package com.fhb.meeconnect.DataElements;

import java.util.ArrayList;

/**
 * Created by Faisal Haque Bappy on 30-Aug-19.
 */
public class Category {
    private String catagoryName, coverImageURL;
    private ArrayList<Student> students;

    public Category(String catagoryName, String coverImageURL, ArrayList<Student> students) {
        this.catagoryName = catagoryName;
        this.coverImageURL = coverImageURL;
        this.students = students;
    }

    public String getCatagoryName() {
        return catagoryName;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    

    public ArrayList<Student> getStudents() {
        return students;
    }
}
