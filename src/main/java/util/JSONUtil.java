package util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import manament.log.LoggerWapper;
import models.ResultCode;
import models.exception.APIException;
import models.gadget.CycleVsTestExecution;
import ninja.Result;
import ninja.Results;

public class JSONUtil {
    private static JSONUtil INSTANCE = new JSONUtil();
    private static ObjectMapper mapper = new ObjectMapper();
    final static LoggerWapper logger = LoggerWapper.getLogger(JSONUtil.class);

    private JSONUtil() {

    }

    public static JSONUtil getInstance() {
        return INSTANCE;
    }

    public <T> List<T> convertJSONtoListObject(String json, Class<T> t) throws APIException {
        if(json == null){
            return null;
        }
        List<T> listObject;
        try{
            listObject = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, t));
        } catch (IOException e){
            logger.fastDebug("cannot parse json: %s", e, json);
            throw new APIException("cannot parse json", e);
        }
        return listObject;
    }

    public <T> T convertJSONtoObject(String json, Class<T> type) throws APIException {
        if(json == null){
            return null;
        }

        T result = null;
        try{
            JsonNode tree = mapper.readTree(json);
            JsonNode errorMessages = tree.get("errorMessages");
            JsonNode errors = tree.get("error");
            if(errorMessages != null){
                StringBuilder message = new StringBuilder();
                if(errorMessages.isArray()){
                    boolean first = true;
                    for (JsonNode error : errorMessages){
                        if(!first){
                            message.append(", ");
                        }
                        message.append(error.asText());
                        first = false;
                    }
                } else{
                    message.append(errorMessages.asText());
                }
                throw new APIException(message.toString());
            }

            if(errors != null){
                StringBuilder message = new StringBuilder();
                if(errors.isArray()){
                    boolean first = true;
                    for (JsonNode error : errors){
                        if(!first){
                            message.append(", ");
                        }
                        message.append(error.asText());
                        first = false;
                    }
                } else{
                    message.append(errors.asText());
                }
                throw new APIException(message.toString());
            }

            result = mapper.readValue(json, type);
        } catch (IOException e){
            logger.fastDebug("cannot parse json: %s", e, json);
            throw new APIException("cannot parse json", e);
        }
        return result;
    }
    
 
}
