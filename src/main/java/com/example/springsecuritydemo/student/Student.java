package com.example.springsecuritydemo.student;

public class Student {

    private Integer strudentId;
    private String studentName;

    public Student(int strudentId, String studentName) {
        this.strudentId = strudentId;
        this.studentName = studentName;
    }

    public Integer getStrudentId() {
        return strudentId;
    }

    public void setStrudentId(Integer strudentId) {
        this.strudentId = strudentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
