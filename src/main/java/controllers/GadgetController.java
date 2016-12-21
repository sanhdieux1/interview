package controllers;

import com.google.inject.Singleton;

import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import service.GadgetServiceImpl;
import util.JSONUtil;

@Singleton
public class GadgetController {
	private GadgetServiceImpl gadgetService;
	public GadgetController() {
		gadgetService = new GadgetServiceImpl();
	}

	public Result addGadget(@Param("type") String type, @Param("data") String data){
		Gadget gadget = null;
		Type gadgetType = Gadget.Type.valueOf(type);
		
		if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadgetType)){
			gadget = JSONUtil.getInstance().convertJSONtoObject(data, EpicVsTestExecution.class);
		}else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadgetType)){
			
		}else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadgetType)){
			
		}
		if(gadget!=null){
			gadgetService.insert(gadget);
		}
		return Results.json().render("message", "successful");
		
	}
}
