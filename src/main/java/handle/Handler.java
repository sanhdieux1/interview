package handle;

import service.gadget.EpicUtility;
import service.gadget.UserUtility;

public abstract class Handler {
	protected UserUtility userService;
	protected EpicUtility epicService;
	public Handler() {
		userService = UserUtility.getInstance();
		epicService = EpicUtility.getInstance();
	}
	
}
