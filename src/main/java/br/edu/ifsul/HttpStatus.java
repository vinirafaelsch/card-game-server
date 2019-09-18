package br.edu.ifsul;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum HttpStatus {

    OK(200, "Ok"),
    CREATED(201, "Created"),
    NOT_MODIFIED(304, "Not Modified"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private Integer id;
    private String message;

    HttpStatus(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    private static final Map<Integer, HttpStatus> lookup =
            Collections.synchronizedMap(new HashMap<>());

    static {
        EnumSet.allOf(HttpStatus.class).forEach(
                tipoAcesso -> lookup.put(
                        tipoAcesso.getId(),
                        tipoAcesso
                )
        );
    }

    public static Map<String, String> getEnum() {
        Map<String, String> returnMap = new HashMap<>();
        for (HttpStatus no : EnumSet.allOf(HttpStatus.class)) {
            returnMap.put(no.name(), no.getMessage());
        }
        return returnMap;
    }

    public static HttpStatus getById(Integer id) {
        return (lookup.containsKey(id)) ? lookup.get(id) : null;
    }

    public static HttpStatus getByMessage(String message) {
        return (lookup.containsValue(message)) ? lookup.get(message) : null;
    }
}
