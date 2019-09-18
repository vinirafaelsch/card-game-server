package br.edu.ifsul;

public enum Card {

    NARUTO(1, "Naruto");

    Integer ID;
    String name;

    Card(Integer ID, String name) {
        this.ID = ID;
        this.name = name;
    }
}

