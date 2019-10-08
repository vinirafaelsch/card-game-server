package br.edu.ifsul;

import java.util.*;

@SuppressWarnings("unused")
public enum Card {

    NARUTO(1, "Naruto", 90, 100, 90),
    SASUKE(2, "Sasuke", 88, 85, 88),
    SAKURA(3, "Sakura", 65, 70, 60),
    BORUTO(4, "Boruto", 55, 80, 65),
    SARADA(5, "Sarada", 54, 70, 70),
    MITSUKI(6, "Mitsuki", 55, 75, 75),
    KAKASHI(7, "Kakashi", 78, 65, 80),
    ITACHI(8, "Itachi", 80, 60, 85),
    PAIN(9, "Pain", 80, 80, 85),
    JIRAYA(10, "Jiraya", 75, 74, 75),
    SAI(11, "Sai", 65, 72, 70),
    OROCHIMARU(12, "Orochimaru", 78, 74, 79),
    OBITO(13, "Obito", 85, 82, 82),
    MADARA(14, "Madara", 89, 88, 92),
    NEJI(15, "Neji", 70, 70, 78),
    SHIKAMARU(16, "Shikamaru", 74, 68, 60),
    ICHIRAKU(17, "Ichiraku", 100, 100, 100),
    KISAME(18, "Kisame", 75, 80, 80),
    SHIKADAI(19, "Shikadai", 53, 55, 55),
    SHISUI(20, "Shisui", 79, 65, 84);

    private Integer id;
    private String name;
    private Integer strength;
    private Integer stamina;
    private Integer defense;

    Card(Integer id, String name, Integer strength, Integer stamina, Integer defense) {
        this.id = id;
        this.name = name;
        this.stamina = stamina;
        this.defense = defense;
        this.strength = strength;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStamina() {
        return stamina;
    }

    public Integer getDefense() {
        return defense;
    }

    public Integer getStrength() {
        return strength;
    }

    private static final Map<Integer, Card> lookup = Collections.synchronizedMap(new HashMap<>());

    static {
        EnumSet.allOf(Card.class).forEach(
                card -> lookup.put(card.getId(), card)
        );
    }

    public static Map<Integer, String> getEnum() {
        Map<Integer, String> returnMap = new HashMap<>();
        for (Card card : EnumSet.allOf(Card.class)) {
            returnMap.put(card.getId(), card.getName());
        }
        return returnMap;
    }

    public static List<Card> getEnumList() {
        List<Card> cards = new ArrayList<>();
        for (Card card : EnumSet.allOf(Card.class)) {
            cards.add(card);
        }
        return cards;
    }

    public static Card getRandomCard() {
        Random rand = new Random();
        List<Card> cards = getEnumList();
        return cards.get(rand.nextInt(cards.size()));
    }

    public static Card getById(Integer id) {
        return (lookup.containsKey(id)) ? lookup.get(id) : null;
    }

    public static Card getByName(String name) {
        return (lookup.containsValue(name)) ? lookup.get(name) : null;
    }
}

