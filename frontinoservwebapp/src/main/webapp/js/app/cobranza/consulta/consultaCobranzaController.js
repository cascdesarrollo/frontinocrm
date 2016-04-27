angular.module('frontinoCli.consultaCobranza', ['ui.bootstrap', 'consultaCobranzaService'])

        .controller('ConsultaCobCtrl', function ($scope, $cookies, $window, $timeout, factoryConsulCobService) {
            $scope.pageClass = 'page-in';
            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();
            $scope.valoresFechas = function () {
                var hoy = new Date();
                var day = hoy.getDate();
                var month = hoy.getMonth() - 1;
                var year = hoy.getFullYear();
                //Fecha
                $scope.fecDesde = new Date(year, month, day, 0, 0, 0, 0);
                $scope.fecHasta = hoy;
                $scope.opendesde = false;
                $scope.openhasta = false;
            };
            $scope.valoresFechas();
            $scope.openDesde = function () {
                $scope.opendesde = true;
            };

            $scope.openHasta = function () {
                $scope.openhasta = true;
            };


            $scope.staPed = "1";
            var precargado = JSON.parse($window.localStorage.getItem('segPedido'));
            if (precargado) {
                $scope.fecDesde = new Date(precargado.fecIni);
                $scope.fecHasta = new Date(precargado.fecFin);
                $scope.staPed = precargado.staPed;
                $scope.objetosList = precargado.pedidosList || [];
            } else {
                $scope.objetosList = [];
            }
            $scope.listadoPedidos = function () {
                var filtro = {};
                filtro.token = $cookies.get('csrftoken');
                filtro.fecIni = $scope.fecDesde.getTime();
                filtro.fecFin = $scope.fecHasta.getTime();
                filtro.staPed = $scope.staPed;
                factoryPedSegService.listadoPedidos(
                        filtro
                        )
                        .success(function (data) {
                            $scope.objetosList = data;
                            filtro.pedidosList = $scope.objetosList;
                            $window.localStorage.setItem('segPedido', JSON.stringify(filtro));
                        }).error(function (dataError) {
                    $window.localStorage.removeItem('segPedido');
                });

            };
            if ($scope.objetosList.length <= 0) {
                $scope.listadoPedidos();
            }

        })
        .controller('DetallePedSegCtrl', function ($scope, $cookies, $window, $location, $routeParams, factoryPedSegService) {
            $scope.pageClass = 'page-back';
            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();
            $scope.idePed = $routeParams.idePed;
            $scope.detalleList = [];
            factoryPedSegService.detallePedidos(
                    $cookies.get('csrftoken'), $scope.idePed
                    )
                    .success(function (data) {
                        $scope.detalleList = data;
                    }).error(function (data) {
            });


            $scope.regresar = function () {
                //$location.path('/sample/' + $scope.checkinId, false);
                $location.path('/seguimiento/', false);
            };

        });


        