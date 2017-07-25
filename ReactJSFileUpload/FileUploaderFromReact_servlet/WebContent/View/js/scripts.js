//Defining a Angular module
var myApp = angular.module('myApp', ['ui.bootstrap']);

myApp.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

myApp.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function(file, uploadUrl){
        var fd = new FormData();
        fd.append('file', file);
        return $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        });
    }
}]);

myApp.controller('fileCtrl', ['$scope', 'fileUpload', '$http', function($scope, fileUpload, $http){
    $scope.uploadFile = function(){
        $scope.uploadFileBtnDisabled = true;
        var self=this;
        var file = $scope.myFile;
        console.log('file is ' );
        console.dir(file);
        var uploadUrl = "/FileUploaderFromReact/getFileFromReact";
        fileUpload.uploadFileToUrl(file, uploadUrl) .success(function(){
               $scope.uploadFileBtnDisabled = false;
        })
        .error(function(){
               $scope.uploadFileBtnDisabled = false;
        });
    };    
}]);
