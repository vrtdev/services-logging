package be.vrt.services.log.collector.util;

import java.util.HashMap;
import java.util.Map;

public final class ElasticNotAllowedCharactersFilter {

    private ElasticNotAllowedCharactersFilter() {
        //private constructor for util class
    }

    public static Map<String, Object> filter(Map<String, Object> headers) {
        Map<String, Object> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            newMap.put(filter(entry.getKey()), entry.getValue());
        }
        return newMap;
    }

    public static String filter(String arg) {
        if (arg == null) return null;
        return arg.replaceAll("\\.", "_");
    }
}
