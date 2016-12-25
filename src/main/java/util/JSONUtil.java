package util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import manament.log.LoggerWapper;
import models.exception.APIException;
import util.gadget.GadgetUtility;

public class JSONUtil {
    private static JSONUtil INSTANCE = new JSONUtil();
    private static ObjectMapper mapper = new ObjectMapper();
    final static LoggerWapper logger = LoggerWapper.getLogger(JSONUtil.class);
    private JSONUtil(){
        
    }
    public static JSONUtil getInstance(){
        return INSTANCE;
    }
    public <T> List<T> convertJSONtoListObject(String json, Class<T> t) throws APIException {
        if(json == null){
            return null;
        }
        List<T> listObject;
        try {
            listObject = mapper.readValue(json,
                    mapper.getTypeFactory().constructCollectionType(List.class, t));
        } catch (IOException e) {
            logger.fasttrace("cannot parse json: %s", e, json);
            throw new APIException("cannot parse json" , e);
        }
        return listObject;
    }
    public <T> T convertJSONtoObject(String json, Class<T> type) throws APIException {
        if(json == null){
            return null;
        }
        T result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(json, type);
        } catch (IOException e) {
            logger.fasttrace("cannot parse json: %s", e, json);
            throw new APIException("cannot parse json", e);
        }
        return result;
    }
}
