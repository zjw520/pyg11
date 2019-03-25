/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function ($scope, $controller, $location, $timeout, baseService) {

    /** 指定继承cartController */
    $controller("baseController", {$scope: $scope});

    $scope.findSeckillGoods = function () {
        baseService.sendGet("/seckill/findSeckillGoods").then(function (response) {
            $scope.seckillGoodsList = response.data;
        })
    }

    $scope.findOne = function () {
        var id = $location.search().id;
        baseService.sendGet("/seckill/findOne?id=" + id).then(function (response) {
            $scope.entity = response.data;
            $scope.downcount($scope.entity.endTime);
        })
    }

    $scope.downcount = function (endTime) {
        /**  计算出相差的毫秒数 */
        var milliSeconds = new Date(endTime).getTime()
            - new Date().getTime();
        /** 计算出相差的秒数 */
        var seconds = Math.floor(milliSeconds / 1000);
        /** 判断秒是否大于零 */
        if (seconds > 0) {
            /** 计算出分钟 */
            var minutes = Math.floor(seconds / 60);
            /** 计算出小时 */
            var hours = Math.floor(minutes / 60);
            /** 计算出天数 */
            var days = Math.floor(hours / 24);
            /** 定义resArr封装最后显示的时间 */
            var resArr = new Array();
            if (days > 0) {
                resArr.push(calc(days + "天 "));
            }
            if (hours > 0) {
                resArr.push((calc(hours - days * 24) + ":"));
            }
            if (minutes > 0) {
                resArr.push(calc((minutes - hours * 60) + ":"));
            }
            if (seconds > 0) {
                resArr.push(calc((seconds - minutes * 60)));
            }
            $scope.timeStr = resArr.join("");

            /** 开启延迟定时器 */
            $timeout(function () {
                $scope.downcount(endTime);
            }, 1000);
        } else {
            $scope.timeStr = "秒杀结束！";
        }
    }

    var calc = function (num) {
        return num > 9 ? num : "0" + num;
    }


    $scope.submitOrder = function () {
        if ($scope.loginName) {
            baseService.sendGet("/order/submitOrder?id=" + $scope.entity.id).then(function (response) {
                if (response.data) {
                    location.href = "/order/pay.html"
                } else {
                    alert("下单失败");
                }
            })
        } else {
            location.href = "http://sso.pinyougou.com?service="
                + $scope.redirectUrl;

        }
    }


});