/** 定义控制器层 */
app.controller('itemCatController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/itemCat/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }
        /** 发送post请求 */
        baseService.sendPost("/itemCat/" + url, $scope.entity)
            .then(function (response) {
                if (response.data) {
                    /** 重新加载数据 */
                    $scope.getParentId();
                    $scope.findItemCatByParentId($scope.parentId);
                } else {
                    alert("操作失败！");
                }
            });
    };

    $scope.getParentId = function(){
        $scope.parentId=0;
        if($scope.grade==2){
            $scope.parentId=$scope.itemcat1.id;
        }else if($scope.grade==3){
            $scope.parentId=$scope.itemcat2.id;
        }
    }

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/itemCat/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.getParentId();
                        $scope.findItemCatByParentId($scope.parentId);
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };

    $scope.findItemCatByParentId = function (parentId) {
        baseService.sendGet("/itemCat/findItemCatByParentId?parentId=" + parentId).then(function (response) {
            $scope.dataList = response.data;

        })
    }

    $scope.selectList = function (entity, grade) {
        $scope.grade = grade;
        if ($scope.grade == 1) {
            $scope.itemcat1 = null;
            $scope.itemcat2 = null;
        }
        if ($scope.grade == 2) {
            $scope.itemcat1 = entity;
            $scope.itemcat2 = null;
        }
        if ($scope.grade == 3) {
            $scope.itemcat2 = entity;
        }
        $scope.findItemCatByParentId(entity.id)
    }

    $scope.grade = 1;

    $scope.findAllTypeTemplateList = function () {
        baseService.sendGet("/typeTemplate/findTypeTemplateList").then(function (response) {
            $scope.typeTemplateList = {data: response.data}
        })
    }

    $scope.saveOrUpdateWithGrade = function (grade) {
        if ($scope.grade == 1) {
            $scope.entity.parentId = 0;
        }
        else if ($scope.grade == 2) {
            $scope.entity.parentId = $scope.itemcat1.id
        }
        else if ($scope.grade == 3) {
            $scope.entity.parentId = $scope.itemcat2.id
        }
        $scope.saveOrUpdate();
    }


});