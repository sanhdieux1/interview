package handle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import manament.log.LoggerWapper;
import models.exception.APIException;
import ninja.Result;
import ninja.Results;

public class EpicHandlerImpl extends EpicHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicHandlerImpl.class);
    

    @Override
    public Result getEpicLinks(String project, String release, List<String> products) throws APIException {
        HashSet<String> productsSet = null;
        if(products != null && !products.isEmpty()){
            productsSet = new HashSet<String>(products);
        }
        Set<String> result = epicService.getEpicLinks(project, release, productsSet).stream().map(e -> e.getKey()).collect(Collectors.toSet());
        return Results.json().render(result);
    }
    
}
