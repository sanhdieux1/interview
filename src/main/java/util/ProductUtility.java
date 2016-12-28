package util;

import service.DatabaseUtility;

public class ProductUtility extends DatabaseUtility{
    private static ProductUtility INSTANCE = new ProductUtility();

    private ProductUtility() {
    }
    public static ProductUtility getInstance() {
        return INSTANCE;
    }
//    public boolean insert(String product){
//        
//    }
}
