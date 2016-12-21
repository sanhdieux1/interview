package util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.exception.MException;

public class JSONUtil {
    private static JSONUtil INSTANCE = new JSONUtil();
    private static ObjectMapper mapper = new ObjectMapper();
    private JSONUtil(){
        
    }
    public static JSONUtil getInstance(){
        return INSTANCE;
    }
    public <T> List<T> convertJSONtoListObject(String json, Class<T> t) {
        List<T> listObject;
        try {
            listObject = mapper.readValue(json,
                    mapper.getTypeFactory().constructCollectionType(List.class, t));
        } catch (IOException e) {
            throw new MException("cannot parse result");
        }
        return listObject;
    }
    public <T> T convertJSONtoObject(String json, Class<T> type) {
        if(json == null){
            return null;
        }
        T result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(json, type);
        } catch (IOException e) {
            throw new MException("cannot parse result");
        }
        return result;
    }
}
