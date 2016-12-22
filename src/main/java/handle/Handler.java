package handle;

import service.gadget.EpicUtility;
import service.gadget.StoryUtility;
import service.gadget.UserUtility;

public abstract class Handler {
	protected UserUtility userService;
	protected EpicUtility epicService;
	protected StoryUtility storyService;
	public Handler() {
		userService = UserUtility.getInstance();
		epicService = EpicUtility.getInstance();
		storyService = StoryUtility.getInstance();
	}
	
}
