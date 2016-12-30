package handle;

import java.util.List;

import models.exception.APIException;
import ninja.Context;
import ninja.Result;

public abstract class GadgetHandler extends Handler {

    public abstract Result insertOrUpdateGadget(String type, String data, Context context) throws APIException;

    public abstract Result getGadgets(String id) throws APIException;

    public abstract Result getDataGadget(String id) throws APIException;

    public abstract Result getStoryInEpic(List<String> epic) throws APIException;

    public abstract Result getProjectList() throws APIException;
}
