package controllers;

import com.google.inject.Singleton;

import handle.GadgetHandler;
import handle.GadgetHandlerImpl;
import ninja.Context;
import ninja.Result;
import ninja.params.Param;

@Singleton
public class GadgetController {
	
	
	private GadgetHandler handler;
	public GadgetController() {
		handler = new GadgetHandlerImpl();
	}

	public Result addGadget(@Param("type") String type, @Param("data") String data, Context context){
		return handler.addGadget(type, data, context);
	}
	
	public Result getGadgets(){
		return handler.getGadgets();
		
	}
	
	public Result getDataGadget(@Param("id") String id){
		return handler.getDataGadget(id);
	}
}
