package conf;

import controllers.ApplicationController;
import controllers.ConfigurationController;
import controllers.DashboardController;
import controllers.LoginLogoutController;
import controllers.QueryDataController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {
    	router.GET().route("/executeSearch").with(QueryDataController.class, "getAssigneeTable");
    	router.GET().route("/listcycle").with(QueryDataController.class,"getListCycleName");
    	router.GET().route("/listproject").with(QueryDataController.class,"getProjectList");
    	
        router.GET().route("/login").with(LoginLogoutController.class, "login");
        router.POST().route("/login").with(LoginLogoutController.class, "loginPost");
        router.GET().route("/logout").with(LoginLogoutController.class, "logout");

        router.GET().route("/release").with(ConfigurationController.class, "release");
        router.GET().route("/release/ialist/{name}").with(ConfigurationController.class, "releaseURL");
        router.POST().route("/release").with(ConfigurationController.class, "releasePost");
        router.POST().route("/release/update").with(ConfigurationController.class, "releaseUpdate");
        router.POST().route("/release/delete").with(ConfigurationController.class, "releaseDelete");
        
        router.GET().route("/metric").with(ConfigurationController.class, "metric");
        router.POST().route("/metric").with(ConfigurationController.class, "metricPost");
        router.POST().route("/metric/update").with(ConfigurationController.class, "metricUpdate");
        router.POST().route("/metric/delete").with(ConfigurationController.class, "metricDelete");
        
        router.GET().route("/dashboard").with(DashboardController.class, "dashboard");
        router.GET().route("/dashboard/page={id}").with(DashboardController.class, "dashboard");
        router.GET().route("/dashboard/new").with(DashboardController.class, "new_dashboard");
        router.POST().route("/dashboard/new").with(DashboardController.class, "new_dashboard_post");
        router.GET().route("/dashboard/find").with(DashboardController.class, "find_dashboard");
        router.POST().route("/dashboard/find").with(DashboardController.class, "find_dashboard_post");
        router.GET().route("/dashboard/{id}/update").with(DashboardController.class, "update_dashboard");
        router.POST().route("/dashboard/{id}/update").with(DashboardController.class, "update_dashboard_post");
        router.GET().route("/dashboard/{id}/delete").with(DashboardController.class, "delete_dashboard");
        router.GET().route("/dashboard/{id}/clone").with(DashboardController.class, "clone_dashboard");
        router.GET().route("/dashboard/{id}").with(DashboardController.class, "show_dashboard");

        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
        
        router.GET().route("/page={id}").with(ApplicationController.class, "index");
        router.GET().route("/.*").with(ApplicationController.class, "index");
        
    }

}