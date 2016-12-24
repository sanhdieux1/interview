var app = angular.module('myApp', ['ngMaterial', 'ngMessages'])

app.controller('myCtrl', function($scope, $element,$mdDialog) {
    var originatorEv;

    $scope.openMenu = function($mdOpenMenu, ev) {
        originatorEv = ev;
        $mdOpenMenu(ev);
    };
    
    $scope.openMenu2 = function($mdOpenMenu, ev) {
        originatorEv = ev;
        $mdOpenMenu(ev);
    };
});

app.controller("widgetController",function($scope){
	$http({
		method: 'GET',
		url: '/gadget/gadgets'
	}).then(function successCallback(response) {
		$scope.widgets = response.data;
		}, function errorCallback(response) {
		alert("Something went wrong for widget controller ajax");
	   });
});

app.controller('epicController', function($scope) {
	$scope.releases = ["1.2.0","1.2.01","1.3.0"];
	$scope.products = ["557x Infrastructure", "ANV", "PCC","UTC"];
	$scope.metrics = ["UNEXECUTED", "FAILED", "WIP","BLOCKED","PASSED","PLANNED","UNPLANNED"];
	$scope.selectedProject;
	$scope.selectedRelease;
	$scope.selectedProducts;
	$scope.selectedMetrics;
	$scope.selectedEpics;
	$scope.initFetchProjects = function(){
		$http({
			  method: 'GET',
			  url: '/listproject'
			}).then(function successCallback(response) {
				$scope.projects = response.data;
			  }, function errorCallback(response) {
				  alert("Something went wrong for epic controller ajax");
			  });
	}
	
	$scope.fetchEpics = function(){
		if($scope.selectedProject == null || $scope.selectedProject == undefined || $scope.selectedProject == ""){
			return;
		}
		if($scope.selectedRelease == null || $scope.selectedRelease == undefined || $scope.selectedRelease == ""){
			return;
		}
	}
	
	$scope.fetchEpics = function(){
		$http({
			  method: 'GET',
			  url: '/getEpicLinks',
			  data: {project : $scope.selectedProjects}
			}).then(function successCallback(response) {
				$scope.epics = response.data;
			  }, function errorCallback(response) {
				  alert("Something went wrong for epic controller ajax");
			  });
	}
});