var globalHtmlResources;
var app = angular.module('myApp', [ 'ngMaterial', 'ngMessages' ])

app.controller('myCtrl', function($scope, $element, $mdDialog) {
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

app.controller("widgetController", function($scope) {
	$scope.initController = function() {
		$http({
			method : 'GET',
			url : '/gadget/gadgets'
		}).then(function successCallback(response) {
			$scope.widgets = response.data;
			// Fetch html resources from server, html resources is a global
			// value and will only be fetched once
			// var tobeAppend;
			// foreach widget in $scope.widgets
			// if(globalHtmlResources == null || globalHtmlResources ==
			// undefined)
			// Ajax fetch resource
			//			
			// $http({
			// method: 'GET',
			// url: '/gadget/gadgets'
			// }).then(function successCallback(response){
			// }, function errorCallback(response){
			// });
			//			
			// end if
			//			
			// var plainResource = globalHtmlResources["widget.type"];
			// var hiddenInput = $(tobeAppend).find(".to-be-name");
			// hiddenInput.val(widget.id);
			// tobeAppend += plainResource;
			// end foreach
			// tobeAppend.append("#widget-container");
			// *Note: maybe ng-include or ng-bind-html can be used to append
			// html resources instead
		}, function errorCallback(response) {
			alert("Something went wrong for widget controller ajax");
		});
	}

});

app.controller('epicController', function($scope) {
	// For {release, products, metrics} will be hardcode for testing purpose.
	// Next we will try to use freemarker to render these hardcode resources to
	// javascript instead.
	// if we cant use freemarker, move on to making another Server API to get
	// these resources.
	// Technically we can render freemarker value to html elements, then use
	// javascript to get these elements value
	// but let's find a more direct way: freemarker variable -> javascript
	$scope.releases = [ "1.2.0", "1.2.01", "1.3.0" ]; // 
	$scope.products = [ "557x Infrastructure", "ANV", "PCC", "UTC" ];
	$scope.metrics = [ "UNEXECUTED", "FAILED", "WIP", "BLOCKED", "PASSED",
			"PLANNED", "UNPLANNED" ];
	$scope.selectedProject;
	$scope.selectedRelease;
	$scope.selectedProducts;
	$scope.selectedMetrics;
	$scope.selectedEpics;
	$scope.initFetchProjects = function() {
		// Only fetch project list once
		// if $scope.$parent.projects == null
		// Ajax fetch projects
		// endif
	}

	$scope.fetchEpics = function() {
		if ($scope.selectedProject == null
				|| $scope.selectedProject == undefined
				|| $scope.selectedProject == "") {
			return;
		}
		if ($scope.selectedRelease == null
				|| $scope.selectedRelease == undefined
				|| $scope.selectedRelease == "") {
			return;
		}
	}

	$scope.fetchEpics = function() {
		$http({
			method : 'GET',
			url : '/getEpicLinks',
			data : {
				project : $scope.selectedProjects
			}
		}).then(function successCallback(response) {
			$scope.epics = response.data;
		}, function errorCallback(response) {
			alert("Something went wrong for epic controller ajax");
		});
	}
});