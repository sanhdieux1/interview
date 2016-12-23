package handle;

import service.gadget.EpicUtility;
import service.gadget.GadgetUtility;
import service.gadget.StoryUtility;
import service.gadget.UserUtility;

public abstract class Handler {
	protected UserUtility userService;
	protected EpicUtility epicService;
	protected StoryUtility storyService;
	protected GadgetUtility gadgetService;
	public Handler() {
		userService = UserUtility.getInstance();
		epicService = EpicUtility.getInstance();
		storyService = StoryUtility.getInstance();
		gadgetService = GadgetUtility.getInstance();
	}
	
}
