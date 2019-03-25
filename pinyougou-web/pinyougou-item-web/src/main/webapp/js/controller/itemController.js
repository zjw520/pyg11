/** 商品详细页（控制器）*/
app.controller('itemController', function ($scope,$controller) {

    $controller("baseController",{$scope:$scope})

    /** 定义购买数量操作方法 */
    $scope.addNum = function (x) {

        $scope.num = parseInt($scope.num);
        $scope.num += x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    /** 记录用户选择的规格选项 */
    $scope.specItems = {};
    /** 定义用户选择规格选项的方法 */
    $scope.selectSpec = function (name, value) {
        $scope.specItems[name] = value;
        $scope.searchSku();
    };
    /** 判断某个规格选项是否被选中 */
    $scope.isSelected = function (name, value) {
        return $scope.specItems[name] == value;
    };

    $scope.loadSku= function(){
        $scope.sku = itemList[0];
        $scope.specItems = JSON.parse($scope.sku.spec);
    }
    
    $scope.searchSku = function () {
        for (var i = 0; i < itemList.length; i++){
            /** 判断规格选项是不是当前用户选中的 */
            if(itemList[i].spec == JSON.stringify($scope.specItems)){
                $scope.sku = itemList[i];
                return;
            }
        }
    }

    $scope.addToCart = function(){
        alert('skuid:'+$scope.sku.id);
    }

});
