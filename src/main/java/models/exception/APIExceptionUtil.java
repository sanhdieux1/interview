package models.exception;

import ninja.Result;
import ninja.Results;

public class APIExceptionUtil {
    public static Result convert(APIException e) {
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
}
