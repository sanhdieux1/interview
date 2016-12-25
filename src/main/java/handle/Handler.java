package handle;

import util.gadget.EpicUtility;
import util.gadget.GadgetUtility;
import util.gadget.StoryUtility;
import util.gadget.UserUtility;

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
