package handle;

import models.exception.MException;
import ninja.Context;
import ninja.Result;

public abstract class GadgetHandler extends Handler{

	public abstract Result addGadget(String type, String data, Context context) throws MException;

	public abstract Result getGadgets() throws MException;

	public abstract Result getDataGadget(String id) throws MException;

}
