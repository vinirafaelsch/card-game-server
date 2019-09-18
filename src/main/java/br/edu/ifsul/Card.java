package br.edu.ifsul;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Card {

    NARUTO(1, "Naruto");

    Integer id;
    String name;

    Card(Integer id, String name) {
        this.id = id;
        this.name = name;
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

