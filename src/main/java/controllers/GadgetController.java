package controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import models.UserVO;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import service.GadgetServiceImpl;
import service.UserServiceImpl;
import util.JSONUtil;

@Singleton
public class GadgetController {
	private GadgetServiceImpl gadgetService;
	private UserServiceImpl userServiceImpl;
	public GadgetController() {
		gadgetService = new GadgetServiceImpl();
		userServiceImpl = new UserServiceImpl();
	}

	public Result addGadget(@Param("type") String type, @Param("data") String data, Context context){
		Gadget gadget = null;
		Type gadgetType = Gadget.Type.valueOf(type);
		
		String username = (String) context.getAttribute("username");
		String friendlyname = (String) context.getAttribute("alias"); 
		UserVO userVO = userServiceImpl.find(username,friendlyname);
		
		if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadgetType)){
			EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(data, EpicVsTestExecution.class);
			epicGadget.setUser(userVO.getUsername());
			gadget = epicGadget;
			
		}else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadgetType)){
			
		}else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadgetType)){
			
		}else {
			return Results.json().render("message", "error");
		}
		if(gadget!=null){
			gadgetService.insert(gadget);
		}
		
		return Results.json().render("message", "successful");
		
	}
	
	public Result getGadgets(){
		List<Gadget> gadgets = gadgetService.getAll();
		return Results.json().render(gadgets);
	}
}
