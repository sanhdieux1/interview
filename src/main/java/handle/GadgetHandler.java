package handle;

import ninja.Context;
import ninja.Result;
import service.EpicService;
import service.EpicServiceImpl;
import service.GadgetServiceImpl;
import service.UserServiceImpl;

public abstract class GadgetHandler extends Handler{
	protected GadgetServiceImpl gadgetService;
	protected EpicService epicService;
	public GadgetHandler() {
		super();
		userService = new UserServiceImpl();
		epicService = new EpicServiceImpl();
	}

	public abstract Result addGadget(String type, String data, Context context);

	public abstract Result getGadgets();

	public abstract Result getDataGadget(String id);

}
