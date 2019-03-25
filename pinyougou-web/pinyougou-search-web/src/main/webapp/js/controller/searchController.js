/** 定义搜索控制器 */
app.controller("searchController", function ($scope, $sce, $location,baseService,$controller) {

    $controller('baseController',{$scope:$scope})

    $scope.searchParam = {keywords: "", category: "", brand: "", price: "", spec: {}, page: 1, rows: 20,sortField:'',sort:''}

    $scope.search = function () {
        baseService.sendPost("/search", $scope.searchParam).then(function (response) {
            $scope.resultMap = response.data;
            $scope.keywords = $scope.searchParam.keywords;
            $scope.initPageNum();
        })
    }

    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    }

    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = value;
            $scope.search();
        } else {
            $scope.searchParam.spec[key] = value;
            $scope.search();
        }
    }

    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = "";
            $scope.search();
        } else {
            delete $scope.searchParam.spec[key];
            $scope.search();
        }
    }
    $scope.initPageNum = function () {
        $scope.pageNums = [];
        var totalPages = $scope.resultMap.totalPages;

        $scope.firstDot = true;
        $scope.lastDot = true;
        /** 开始页码 */
        var firstPage = 1;
        /** 结束页码 */
        var lastPage = totalPages;
        /** 如果总页数大于5，显示部分页码 */
        if (totalPages > 5) {
            // 如果当前页码处于前面位置
            if ($scope.searchParam.page <= 3) {
                lastPage = 5; // 生成前5页页码
                $scope.firstDot = false
            }
            // 如果当前页码处于后面位置
            else if ($scope.searchParam.page >= totalPages - 3) {
                firstPage = totalPages - 4;  // 生成后5页页码
                $scope.lastDot = false
            } else { // 当前页码处于中间位置
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }else{
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        /** 循环产生页码 */
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageNums.push(i);
        }
    }

    $scope.pageSearch = function (page) {
        page = parseInt(page);
        if (page >= 1 && page <= $scope.resultMap.totalPages && page != $scope.searchParam.page) {
            $scope.searchParam.page = page;
            $scope.jumpPage = page;
            $scope.search();
        }
    }

    $scope.sortSearch = function(field,sort){
        $scope.searchParam.sortField = key;
        $scope.searchParam.sortValue = sort;
        $scope.search();
    }

    $scope.getkeywords = function(){
        $scope.searchParam.keywords = $location.search().keywords;
        $scope.search();
    }

});
