package controllers;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.ProductUtility;

@Singleton
//@FilterWith(AdminSecureFilter.class)
public class ProductController {
//    final static LoggerWapper logger = LoggerWapper.getLogger(ProductController.class);
    final static Logger logger = Logger.getLogger(ProductController.class);
    public Result addProduct(@Param("product") String product){
        ProductUtility.getInstance().insert(product);
        return Results.html().redirect("/product");
    }
    
    public Result productPage(){
        Set<String> products = ProductUtility.getInstance().getAll();
        logger.info(products.toString());
        return Results.html().render("isProductPage" , true).render("products",products);
    }
}
