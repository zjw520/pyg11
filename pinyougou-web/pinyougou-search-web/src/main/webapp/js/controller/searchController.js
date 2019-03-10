/** 定义搜索控制器 */
app.controller("searchController", function ($scope, baseService) {

    $scope.searchParam = {keywords: ""}

    $scope.search = function () {
        baseService.sendPost("/search", $scope.searchParam).then(function (response) {
            $scope.resultMap = response.data;
        })
    }


});
