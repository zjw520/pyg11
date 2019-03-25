/** 定义首页控制器层 */
app.controller("indexController", function ($scope, baseService,$controller) {

    $controller('baseController',{$scope:$scope})

    $scope.findContentByCategoryId = function (id) {
        baseService.sendGet("/content/findContentByCategoryId?id=" + id).then(function (response) {
            $scope.dataList = response.data;
        })
    }

    $scope.search = function () {
        var keyword = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keyword;
    }


});