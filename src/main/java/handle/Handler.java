package handle;

import service.EpicService;
import service.EpicServiceImpl;
import service.UserServiceImpl;

public abstract class Handler {
	protected UserServiceImpl userService;
	protected EpicService epicService;
	public Handler() {
		userService = new UserServiceImpl();
		epicService = new EpicServiceImpl();
	}
	
}
