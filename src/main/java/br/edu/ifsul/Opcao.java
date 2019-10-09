package br.edu.ifsul;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Opcao {


    STRENGTH(1, "Strength"),
    STAMINA(2, "Stamina"),
    DEFENSE(3, "Defense");

    private Integer id;
    private String name;

    Opcao(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, Opcao> lookup =
            Collections.synchronizedMap(new HashMap<>());

    static {
        EnumSet.allOf(Opcao.class).forEach(opcao -> lookup.put(opcao.getId(), opcao));
    }

    public static Map<String, String> getEnum() {
        Map<String, String> returnMap = new HashMap<>();
        for (Opcao no : EnumSet.allOf(Opcao.class)) {
            returnMap.put(no.name(), no.getName());
        }
        return returnMap;
    }

    public static Opcao getById(Integer id) {
        return (lookup.containsKey(id)) ? lookup.get(id) : null;
    }

    public static Opcao getByMessage(String message) {
        return (lookup.containsValue(message)) ? lookup.get(message) : null;
    }
}
