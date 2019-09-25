package br.edu.ifsul;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Card {

    NARUTO (1, "Naruto", 90, 100, 90),
    SASUKE (2, "Sasuke", 88, 85, 90),
    SAKURA (3, "Sakura", 65, 70, 60),
    BORUTO (4, "Boruto", 50, 80, 65),
    SARADA (5, "Sarada", 50, 70, 70),
    MITSUKI (6, "Mitsuki", 55, 75, 75),

    ;


    private Integer id;
    private String name;
    private Integer streght;
    private Integer stamina;
    private Integer defense;


    Card(Integer id, String name, Integer streght, Integer stamina, Integer defense) {
        this.id = id;
        this.name = name;
        this.streght = streght;
        this.stamina = stamina;
        this.defense = defense;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    private static final Map<Integer, Card> lookup =
            Collections.synchronizedMap(new HashMap<>());

    static {
        EnumSet.allOf(Card.class).forEach(
                card -> lookup.put(
                        card.getId(),
                        card
                )
        );
    }

    public static Map<String, String> getEnum() {
        Map<String, String> returnMap = new HashMap<>();
        for (Card no : EnumSet.allOf(Card.class)) {
            returnMap.put(no.name(), no.getName());
        }
        return returnMap;
    }

    public static Card getById(Integer id) {
        return (lookup.containsKey(id)) ? lookup.get(id) : null;
    }

    public static Card getByMessage(String message) {
        return (lookup.containsValue(message)) ? lookup.get(message) : null;
    }
}

