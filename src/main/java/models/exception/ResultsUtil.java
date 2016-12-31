package models.exception;

import models.ResultCode;
import ninja.Result;
import ninja.Results;

public class ResultsUtil {
    public static Result convertException(APIException e) {
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
    
    public static Result convertToResult(ResultCode type, Object data){
        return Results.json().render("type", type).render("data", data);
    }
}
