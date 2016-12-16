package handle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import models.MException;
import ninja.Results;

public class ExceptionHandler implements InvocationHandler {
    private MHandler handler;

    public ExceptionHandler(MHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try{
            return method.invoke(handler, args);
        } catch (InvocationTargetException e){
            Throwable t = e.getTargetException();
            if(t instanceof MException){
                MException mException = (MException) t;
                return Results.json().render(mException);
            }
            throw t;
        }
    }

}
