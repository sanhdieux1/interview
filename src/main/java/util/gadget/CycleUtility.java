package util.gadget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import handle.executors.CycleTestCallable;
import models.ExecutionIssueResultWapper;
import models.GadgetData;
import models.exception.APIException;
import models.gadget.CycleVsTestExecution;
import util.Constant;
import util.PropertiesUtil;

public class CycleUtility {
    private static CycleUtility INSTANCE = new CycleUtility();
    
    private CycleUtility(){
    }
    public static CycleUtility getInstance(){
        return INSTANCE;
    }
    public List<GadgetData> getDataCycle(CycleVsTestExecution cycleGadget) throws APIException {
        List<GadgetData> returnData = new ArrayList<>();
        Set<String> cycles = cycleGadget.getCycles();
        String project = cycleGadget.getProjectName();
        List<CycleTestCallable> tasks = new ArrayList<>();
        for (String cycle : cycles){
            tasks.add(new CycleTestCallable(cycle, project));
        }
        ExecutorService taskExecutor = Executors.newFixedThreadPool(PropertiesUtil.getInt(Constant.CONCURRENT_THREAD));
        try{
            List<Future<ExecutionIssueResultWapper>> results = taskExecutor.invokeAll(tasks);
            for (Future<ExecutionIssueResultWapper> result : results){
                ExecutionIssueResultWapper wapper = result.get();
                if(wapper != null && wapper.getExecutionsVO() != null){
                    GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(wapper.getExecutionsVO());
                    gadgetData.setKey(wapper.getIssue());
                    returnData.add(gadgetData);
                }
            }
        } catch (ExecutionException e){
            if(e.getCause() instanceof APIException){
                throw (APIException) e.getCause();
            }
            throw new APIException("Cannot invoke task", e);
        } catch (InterruptedException e){
            throw new APIException("Cannot invoke task", e);
        } finally{
            taskExecutor.shutdown();
        }
        return returnData;
    }
}
