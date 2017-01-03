package controllers;

import java.util.Set;

import com.google.inject.Singleton;

import filter.AdminSecureFilter;
import manament.log.LoggerWapper;
import models.ResultCode;
import models.exception.ResultsUtil;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.ProductUtility;

@Singleton
public class ProductController {
    final static LoggerWapper logger = LoggerWapper.getLogger(ProductController.class);
    @FilterWith(AdminSecureFilter.class)
    public Result addProduct(@Param("product") String product){
        return Results.json().render("type", "success").render("data",ProductUtility.getInstance().insert(product));
    }
    @FilterWith(AdminSecureFilter.class)
    public Result deleteProduct(@Param("product") String product){
        long result = ProductUtility.getInstance().delete(product);
        return Results.json().render("type", "success").render("data",result);
    }
    @FilterWith(AdminSecureFilter.class)
    public Result productPage(){
        Set<String> products = ProductUtility.getInstance().getAll();
        return Results.html().render("isProductPage" , true).render("products",products);
    }
    
    public Result getAll(){
        Set<String> products = ProductUtility.getInstance().getAll();
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, products);
    }
}
