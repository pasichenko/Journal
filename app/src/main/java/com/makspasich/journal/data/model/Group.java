package com.makspasich.journal.data.model;

public class Group {
    public String id_group;
    public String number_group;
    public Student starosta;

    public Group() {
    }

    public Group(String id_group, String number_group, Student starosta) {
        this.id_group = id_group;
        this.number_group = number_group;
        this.starosta = starosta;
    }
}
