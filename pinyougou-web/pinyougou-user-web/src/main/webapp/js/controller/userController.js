/** 定义控制器层 */
app.controller('userController', function ($scope, baseService) {

    $scope.user = {}
    $scope.save = function () {
        if ($scope.user.password != $scope.password) {
            alert("密码不一致,请重新输入!");
            return;
        }
        baseService.sendPost("/user/save?smsCode="+$scope.smsCode, $scope.user).then(function (response) {
            if (response.data) {
                alert("注册成功!");
                $scope.user = {};
                $scope.password = "";
                $scope.smsCode = "";
            } else {
                alert("注册失败!");
            }
        })
    }

    $scope.sendCode = function () {
        if ($scope.user.phone) {
            baseService.sendGet("/user/sendCode?phone=" + $scope.user.phone).then(function (response) {
                alert(response.data ? "发送成功!" : "发送失败!");
            });
        } else {
            alert("请输入手机号码")
        }
    }

    $scope.showName = function(){
        baseService.sendGet("/user/showName").then(function (response) {

            $scope.redirectUrl = window.encodeURIComponent(location.href);
            $scope.loginName = response.data.loginName;
        })
    };


    $scope.findUser = function () {
        baseService.sendGet("/user/findUser").then(function (response) {
            $scope.userData=response.data;
        })
    }

    $scope.findAll = function () {
        baseService.sendGet("/address/findAll").then(function (response) {
            $scope.adressList = response.data.addressList;
            $scope.pList = response.data.provinceList;

        })

    }
    $scope.show = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));

    }

    $scope.aliasArra=["家里","父母家","公司"]
    $scope.choseAlias = function (num) {
        $scope.entity.alias =$scope.aliasArra[num];

    }
    $scope.findprovinceByPid = function (id,list) {
        baseService.sendGet("/total/address","id="+id).then(function (response) {
            $scope[list]=response.data
        })
    }


    $scope.$watch("entity.province_id",function (newVal,oldVal) {

        if(newVal){
            $scope.findprovinceByPid(newVal,'citiesList')

        }else{
            $scope.citiesList=[];

        }

    })
    $scope.$watch("entity.city_id",function (newVal,oldVal) {
        if(newVal){
            $scope.findprovinceByPid(newVal,'townList')

        }else{
            $scope.townList=[];

        }

    })

  $scope.updataDefaluStatus = function (entity) {
        baseService.sendPost("/address/addressDefaultStatus",entity).then(function (response) {
            if (response.data){
                $scope.findAll();

            }

        })

  }

    $scope.saveOrUpdare = function (entity) {
        var url ="/saveAddress";
        if(entity.id){
            url="/updateAddress";
        }
        baseService.sendPost("/address"+url,entity).then(function (reponse) {
            if(reponse.data){

                alert("保存成功")
                $scope.findAll();

            }else{
                alert("保存失败")
            }
        })
    }
    $scope.deleteAdressById = function (id) {
        baseService.sendGet("/address/delete","id="+id).then(function (response) {
            if (response.data) {
                alert("删除成功");
                $scope.findAll();

            }else{
                alert("删除失败");
            }
        })


    }



});