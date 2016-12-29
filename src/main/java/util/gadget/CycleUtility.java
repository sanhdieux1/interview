package util.gadget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import handle.executors.CycleTestCallable;
import handle.executors.ExecutorManagement;
import manament.log.LoggerWapper;
import models.ExecutionIssueResultWapper;
import models.exception.APIException;
import models.gadget.CycleVsTestExecution;
import models.main.GadgetData;
import models.main.Release;
import util.Constant;
import util.PropertiesUtil;

public class CycleUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(CycleUtility.class);
    private static CycleUtility INSTANCE = new CycleUtility();

    private CycleUtility() {
    }

    public static CycleUtility getInstance() {
        return INSTANCE;
    }

    public List<GadgetData> getDataCycle(CycleVsTestExecution cycleGadget) throws APIException {
        List<GadgetData> returnData = new ArrayList<>();
        Set<String> cycles = cycleGadget.getCycles();
        String project = cycleGadget.getProjectName();
        Release release = cycleGadget.getRelease();
        if(cycleGadget.isSelectAllCycle()){
            cycles = AssigneeUtility.getInstance().getListCycleName(project, release, cycleGadget.getProducts());
        }
        List<CycleTestCallable> tasks = new ArrayList<>();
        if(cycles != null && !cycles.isEmpty()){
            for (String cycle : cycles){
                tasks.add(new CycleTestCallable(cycle, project));
            }
            List<Future<ExecutionIssueResultWapper>> taskResult = ExecutorManagement.getInstance().invokeTask(tasks);
            List<ExecutionIssueResultWapper> results = ExecutorManagement.getInstance().getResult(taskResult);
            for (ExecutionIssueResultWapper wapper : results){
                if(wapper != null && wapper.getExecutionsVO() != null){
                    GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(wapper.getExecutionsVO());
                    gadgetData.setKey(wapper.getIssue());
                    returnData.add(gadgetData);
                }
            }
        } else{
            logger.fastDebug("No Test Cycle in gadget %s", cycleGadget.getId());
        }
        return returnData;
    }
}
