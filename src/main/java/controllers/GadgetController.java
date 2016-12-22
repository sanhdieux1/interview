package controllers;

import com.google.inject.Singleton;

import handle.GadgetHandler;
import handle.GadgetHandlerImpl;
import models.exception.MException;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

@Singleton
public class GadgetController {

    private GadgetHandler handler;

    public GadgetController() {
        handler = new GadgetHandlerImpl();
    }

    public Result addGadget(@Param("type") String type, @Param("data") String data, Context context) {
        try{
            return handler.addGadget(type, data, context);
        } catch (MException e){
            return handleException(e);
        }
    }

    public Result getGadgets() {
        try{
            return handler.getGadgets();
        } catch (MException e){
            return handleException(e);
        }

    }

    public Result getDataGadget(@Param("id") String id) {
        try{
            return handler.getDataGadget(id);
        } catch (MException e){
            return handleException(e);
        }
    }

    public Result handleException(MException e) {
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
}