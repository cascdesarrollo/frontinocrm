angular.module('frontinoCli.seguimiento', ['ui.bootstrap', 'pedidosSeguimientoService'])

        .controller('PedSegCtrl', function ($scope, $cookies, $window, $location, factoryPedSegService, translationService) {
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

            $scope.processConsultar = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Consultando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Cargando Pedidos'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');

            $scope.processConfirmar = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Procesando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Confirmando Pedido'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');

            $scope.processAnular = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Procesando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Anulando Registro de Pedido'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');


            $scope.staPed = "0";
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
                $scope.processConsultar.modal('show');
                factoryPedSegService.listadoPedidos(
                        filtro
                        )
                        .success(function (data) {
                            $scope.processConsultar.modal('hide');
                            $scope.objetosList = data;
                            filtro.pedidosList = $scope.objetosList;
                            $window.localStorage.setItem('segPedido', JSON.stringify(filtro));
                        }).error(function (dataError) {
                    $scope.processConsultar.modal('hide');
                    $window.localStorage.removeItem('segPedido');
                });
            };
            if ($scope.objetosList.length <= 0) {
                $scope.listadoPedidos();
            }
            $scope.regresar = function () {
                $location.path('#/dashboard/', false);
            };

            $scope.anularPedido = function (item) {
                bootbox.confirm("Esta operación es irreversible, \n Desea Anular Pedido?", function (result) {
                    if (result) {
                        $scope.processAnular.modal('show');
                        factoryPedSegService.status($cookies.get('csrftoken'),
                                item.ide_ped, '2')
                                .success(function (data) {
                                    $scope.processAnular.modal('hide');
                                    bootbox.alert("Pedido se Anulado!", function () {
                                        $scope.listadoPedidos();
                                    });
                                }).error(function (dataError) {
                            $scope.processAnular.modal('hide');
                            if (dataError) {
                                $scope.muestraMensajeError = dataError.error;
                                $scope.mensajeError = dataError.des_error;
                            } else {
                                $scope.muestraMensajeError = true;
                                $scope.mensajeError = "Error Consultando BackEnd";
                            }
                        });

                    }
                });
            };

            $scope.confirmarPedido = function (item) {
                bootbox.confirm("Desea Confirmar su Pedido?", function (result) {
                    if (result) {
                        $scope.processConfirmar.modal('show');
                        factoryPedSegService.status($cookies.get('csrftoken'),
                                item.ide_ped, '1')
                                .success(function (data) {
                                    $scope.processConfirmar.modal('hide');
                                    bootbox.alert("Pedido ha Sido Confirmado!", function () {
                                        $scope.listadoPedidos();
                                    });
                                }).error(function (dataError) {
                            $scope.processConfirmar.modal('hide');
                            if (dataError) {
                                $scope.muestraMensajeError = dataError.error;
                                $scope.mensajeError = dataError.des_error;
                            } else {
                                $scope.muestraMensajeError = true;
                                $scope.mensajeError = "Error Consultando BackEnd";
                            }
                        });
                    }
                });
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();

        })
        .controller('DetallePedSegCtrl', function ($scope, $cookies, $window, $location, $routeParams, factoryPedSegService, translationService) {

            $scope.pageClass = 'page-back';
            $scope.muestraMensajeError = false;
            $scope.mensajeError = '';

            $scope.processConsultar = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Consultando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Cargando Detalle de Pedido ' + $routeParams.idePed
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');

            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();
            $scope.idePed = $routeParams.idePed;
            $scope.detalleList = [];
            $scope.processConsultar.modal('show');
            factoryPedSegService.detallePedidos(
                    $cookies.get('csrftoken'), $scope.idePed
                    )
                    .success(function (data) {
                        $scope.processConsultar.modal('hide');
                        $scope.detalleList = data;
                    }).error(function (dataError) {
                $scope.processConsultar.modal('hide');
                if (dataError) {
                    $scope.muestraMensajeError = dataError.error;
                    $scope.mensajeError = dataError.des_error;
                } else {
                    $scope.muestraMensajeError = true;
                    $scope.mensajeError = "Error Consultando BackEnd";
                }
            });





            $scope.regresar = function () {
                $location.path('/seguimiento/', false);
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();

        });



        