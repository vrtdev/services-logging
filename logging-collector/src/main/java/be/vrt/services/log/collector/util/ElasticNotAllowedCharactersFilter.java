package be.vrt.services.log.collector.util;

import java.util.Map;

public final class ElasticNotAllowedCharactersFilter {

    private ElasticNotAllowedCharactersFilter() {
        //private constructor for util class
    }

    public static Map<String, Object> filter(Map<String, Object> headers) {
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            if(entry.getKey().contains(".")) {
                headers.put(filter(entry.getKey()), entry.getValue());
                headers.remove(entry.getKey());
            }
        }
        return headers;
    }

    public static String filter(String arg) {
        if(arg == null) return null;
        return arg.replaceAll("\\.", "_");
    }
}
