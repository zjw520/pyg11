/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/goods/findByPage", page,
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

        $scope.goods.goodsDesc.introduction = editor.html()
        var url = "save";
        if ($scope.goods.id) {
            url = "update";
        }
        /** 发送post请求 */
        baseService.sendPost("/goods/" + url, $scope.goods)
            .then(function (response) {
                if (response.data) {
                    /** 重新加载数据 */
                    alert("保存商品成功")
                    $scope.goods = {}
                    editor.html('');
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.reload();
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };

    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response
        ) {
            if (response.data.status == 200) {
                $scope.picEntity.url = response.data.url;

            } else {
                alert("图片上传失败")
            }
        })
    }

    $scope.goods = {goodsDesc: {itemImages: [], specificationItems: []}}

    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity)

        var test = document.getElementById('file');
        test.value = '';
    }

    $scope.removePic = function (idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx, 1);
    }

    $scope.findItemCatByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemCatByParentId?parentId=" + parentId).then(function (response) {
            $scope[name] = response.data;

        })
    }

    $scope.$watch("goods.category1Id", function (newVal, oldVal) {
        if (newVal) {
            $scope.findItemCatByParentId(newVal, 'itemCatList2')
        } else {
            $scope.itemCatList2 = [];
        }
    })

    $scope.$watch("goods.category2Id", function (newVal, oldVal) {
        if (newVal) {
            $scope.findItemCatByParentId(newVal, 'itemCatList3')
        } else {
            $scope.itemCatList3 = [];
        }
    })

    $scope.$watch("goods.category3Id", function (newVal, oldVal) {
        if (newVal) {
            for (var i = 0; i < $scope.itemCatList3.length; i++) {
                var itemCat = $scope.itemCatList3[i]
                if (itemCat.id == newVal) {
                    $scope.goods.typeTemplateId = itemCat.typeId
                    return;
                }
            }
        }
    })

    $scope.$watch("goods.typeTemplateId", function (newVal, oldVal) {
        if (newVal) {
            baseService.sendGet("/typeTemplate/findOne?id=" + newVal).then(function (response) {
                $scope.brandIds = JSON.parse(response.data.brandIds);
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse(response.data.customAttributeItems);
            })

            baseService.sendGet("/typeTemplate/findSpecByTemplateId?id=" + newVal).then(function (response) {
                $scope.specList = response.data;
            })
        }
    })

    $scope.updateSpecAttr = function (e, specName, optionName) {
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc.specificationItems, 'attributeName', specName);
        if (obj) {
            if (e.target.checked) {
                obj.attributeValue.push(optionName)
            } else {
                var idx = obj.attributeValue.indexOf(optionName);
                obj.attributeValue.splice(idx, 1);
                if (obj.attributeValue.length == 0) {
                    $scope.goods.goodsDesc.specificationItems.splice(
                        $scope.goods.goodsDesc.specificationItems.indexOf(obj), 1)
                }
            }
        } else {
            $scope.goods.goodsDesc.specificationItems.push(
                {"attributeName": specName, "attributeValue": [optionName]}
            )
        }
    }

    $scope.searchJsonByKey = function (jsonArr, key, value) {
        for (var i = 0; i < jsonArr.length; i++) {
            if (jsonArr[i][key] == value) {
                return jsonArr[i]
            }
        }
    }


    $scope.updateMarketable = function (status) {
        if ($scope.ids.length > 0){
            baseService.sendGet("/goods/updateMarketable", "ids=" +
                $scope.ids + "&status=" + status)
                .then(function(response){
                    if (response.data){
                        alert("操作成功！");
                        /** 重新加载数据 */
                        $scope.reload();
                        /** 清空ids数组 */
                        $scope.ids = [];
                    }else{
                        alert("操作失败！");
                    }
                });
        }else{
            alert("请选择要操作的商品！");
        }

    }


    //spec:网络 optionName:移动3G
    $scope.createItems = function () {
        $scope.goods.items = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}]
        var specItems = $scope.goods.goodsDesc.specificationItems;
        for (i = 0; i < specItems.length; i++) {
            var obj = specItems[i]
            $scope.goods.items = $scope.swapItems($scope.goods.items, obj.attributeValue, obj.attributeName);
        }


    }

    $scope.swapItems = function (items, attributeValue, attributeName) {
        var newItems = new Array();
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            for (var j = 0; j < attributeValue.length; j++) {
                var newItem = JSON.parse(JSON.stringify(item));
                newItem.spec[attributeName] = attributeValue[j]
                newItems.push(newItem);
            }
        }
        return newItems;
    }

    $scope.status = ['未审核', '已审核', '审核未通过', '关闭']


})
;