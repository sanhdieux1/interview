package controllers;

import java.util.Set;

import com.google.inject.Singleton;

import manament.log.LoggerWapper;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.ProductUtility;

@Singleton
//@FilterWith(AdminSecureFilter.class)
public class ProductController {
    final static LoggerWapper logger = LoggerWapper.getLogger(ProductController.class);
    
    public Result addProduct(@Param("product") String product){
        return Results.json().render("type", "success").render("data",ProductUtility.getInstance().insert(product));
    }
    
    public Result deleteProduct(@Param("product") String product){
        long result = ProductUtility.getInstance().delete(product);
        return Results.json().render("type", "success").render("data",result);
    }
    
    public Result productPage(){
        Set<String> products = ProductUtility.getInstance().getAll();
        return Results.html().render("isProductPage" , true).render("products",products);
    }
    
    
}
