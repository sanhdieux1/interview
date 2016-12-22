package handle;

import ninja.Context;
import ninja.Result;
import service.gadget.EpicService;
import service.gadget.EpicUtility;
import service.gadget.GadgetUtility;
import service.gadget.UserUtility;

public abstract class GadgetHandler extends Handler{
	protected GadgetUtility gadgetService;
	protected EpicService epicService;
	public GadgetHandler() {
		super();
		userService = new UserUtility();
		epicService = new EpicUtility();
	}

	public abstract Result addGadget(String type, String data, Context context);

	public abstract Result getGadgets();

	public abstract Result getDataGadget(String id);

}
