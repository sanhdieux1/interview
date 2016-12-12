angular.module('myApp', ['ngMaterial', 'ngMessages'])
.controller('myCtrl', function($scope, $element,$mdDialog) {
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