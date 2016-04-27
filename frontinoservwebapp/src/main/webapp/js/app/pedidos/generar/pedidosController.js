angular.module('frontinoCli.pedidos', ['ngAnimate', 'ui.bootstrap', 'pedidosService'])

        .controller('PedidosCtrl', function ($scope, $cookies, $window, $location, $uibModal,
                factoryPedidosService, translationService) {

            $scope.pageClass = 'page-in';
            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();

            $scope.iniciarTotales = function () {
                var result = {};
                result.lineas = 0;
                result.exento = 0;
                result.gravado = 0;
                result.impuesto = 0;
                result.total = 0;
                return result;
            };

            var precargado = JSON.parse($window.sessionStorage.getItem('addPedido'));
            if (precargado) {
                $scope.totales = precargado.totales || {};
                $scope.objetosList = precargado.listado || [];
                $scope.objetosSeleccionados = precargado.seleccionados || [];
            } else {
                $scope.objetosList = [];
                $scope.objetosSeleccionados = [];
                $scope.totales = $scope.iniciarTotales();
            }
            $scope.consultarListado = function () {
                factoryPedidosService.listadoArticulos(
                        $cookies.get('csrftoken')
                        )
                        .success(function (data) {
                            $scope.objetosList = data;
                            $scope.objetosSeleccionados = [];
                            $scope.totales = $scope.iniciarTotales();
                        }).error(function (dataError) {
                    console.log(dataError);
                    $window.sessionStorage.removeItem('segPedido');
                });
            };
            if ($scope.objetosList.length <= 0) {
                console.log('consulta listado');
                $scope.consultarListado();
            }
            $scope.regresar = function () {
                $location.path('#/dashboard/', false);
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();


            $scope.seleccion = function (cantidad, linea) {
                if (linea.can_ddv + (cantidad) <= 0) {
                    linea.can_ddv = 0;
                } else {
                    linea.can_ddv = linea.can_ddv + (cantidad);
                }
                $scope.orden(linea);
            };


            $scope.orden = function (linea) {
                if (linea.can_ddv > 0) {
                    if ($scope.objetosSeleccionados.indexOf(linea) < 0) {
                        $scope.objetosSeleccionados.push(linea);
                    }
                    $scope.calcular(linea);
                } else {
                    $scope.remover(linea);
                }
                $scope.totalizar();
            };

            $scope.remover = function (linea) {
                var posicion = $scope.objetosSeleccionados.indexOf(linea);
                if (posicion >= 0) {
                    linea.can_ddv = 0;
                    $scope.objetosSeleccionados.splice(posicion, 1);
                    $scope.calcular(linea);
                }
                $scope.totalizar();
            };

            $scope.calcular = function (linea) {
                linea.sbt_ddv = linea.can_ddv * linea.pre_ddv;
                linea.bd1_ddv = linea.pd1_ddv > 0 ? linea.sbt_ddv * (linea.pd1_ddv / 100) : 0;
                linea.bd2_ddv = linea.pd2_ddv > 0 ? linea.sbt_ddv * (linea.pd2_ddv / 100) : 0;
                linea.bd3_ddv = linea.pd3_ddv > 0 ? linea.sbt_ddv * (linea.pd3_ddv / 100) : 0;
                linea.dto_ddv = (linea.bd1_ddv + linea.bd2_ddv + linea.bd3_ddv);
                var net = linea.sbt_ddv - linea.dto_ddv;
                linea.bi1_ddv = linea.pi1_ddv > 0 ? net * (linea.pi1_ddv / 100) : 0;
                linea.bi2_ddv = linea.pi2_ddv > 0 ? net * (linea.pi2_ddv / 100) : 0;
                linea.bi3_ddv = linea.pi3_ddv > 0 ? net * (linea.pi3_ddv / 100) : 0;
                linea.iva_ddv = (linea.bi1_ddv + linea.bi2_ddv + linea.bi3_ddv);
                linea.tto_ddv = net + linea.iva_ddv;
            };

            $scope.totalizar = function () {
                $scope.totales.lineas = $scope.objetosSeleccionados.length;
                $scope.totales.exento = 0;
                $scope.totales.gravado = 0;
                $scope.totales.impuesto = 0;
                $scope.totales.total = 0;
                for (var i = 0; i < $scope.objetosSeleccionados.length; i++) {
                    var linea = $scope.objetosSeleccionados[i];
                    $scope.totales.impuesto += linea.iva_ddv;
                    if ($scope.totales.impuesto > 0) {
                        $scope.totales.gravado += (linea.sbt_ddv - linea.dto_ddv);
                    } else {
                        $scope.totales.exento += (linea.sbt_ddv - linea.dto_ddv);
                    }
                }
                $scope.totales.total = $scope.totales.exento + $scope.totales.gravado + $scope.totales.impuesto;
                $scope.almancenaObjetos();
            };
            $scope.almancenaObjetos = function () {
                $scope.eliminarObjetos();
                var datos = {};
                datos.filtro = {};
                datos.totales = $scope.totales;
                datos.listado = $scope.objetosList;
                datos.seleccionados = $scope.objetosSeleccionados;
                $window.sessionStorage.setItem('addPedido', JSON.stringify(datos));
            };

            $scope.eliminarObjetos = function () {
                $window.sessionStorage.removeItem('addPedido');
            };

            $scope.limpiarDetalle = function () {
                $scope.totales = $scope.iniciarTotales();
                $scope.objetosList = [];
                $scope.objetosSeleccionados = [];
                $scope.consultarListado();
            };

            $scope.itemPreview = {};

            $scope.asignarItem = function (item) {
                $scope.itemPreview = item;
            };

            $scope.items = ['item1', 'item2', 'item3'];

            $scope.animationsEnabled = true;

            $scope.open = function (item) {
                var modalInstance = $uibModal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'myModalContent.html',
                    controller: 'ModalInstanceCtrl',
                    size: 'lg',
                    resolve: {
                        item: function () {
                            return item;
                        }
                    }
                });


            };

            $scope.toggleAnimation = function () {
                $scope.animationsEnabled = !$scope.animationsEnabled;
            };


        });

angular.module('frontinoCli.pedidos').controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, item) {
    $scope.item = item;
    $scope.ok = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});


        