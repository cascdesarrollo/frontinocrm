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

            $scope.processConsultar = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Consultado</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Cargando Articulos'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');

            $scope.processGuardar = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Procesando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Almacenenado Pedido'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');

            $scope.reasignarSeleccionados = function () {
                for (var j = 0; j < $scope.objetosList.length; j++) {
                    for (var k = 0; k < $scope.objetosSeleccionados.length; k++) {

                        if ($scope.objetosList[j].ide_pro.ide_pro === $scope.objetosSeleccionados[k].ide_pro.ide_pro
                                &&
                                $scope.objetosList[j].ide_und.ide_und === $scope.objetosSeleccionados[k].ide_und.ide_und
                                ) {
                            //Eliminamos la linea en el principal
                            $scope.objetosList.splice(j, 1);
                            //agregamis la linea calculada en esa posicion
                            $scope.objetosList.splice(j, 0, $scope.objetosSeleccionados[k]);

                            /*$scope.objetosList[j].can_ddv = $scope.objetosSeleccionados[k].can_ddv;
                             $scope.objetosSeleccionados[k] = $scope.objetosList[j];*/
                        }

                    }
                }
            };

            var precargado = JSON.parse($window.sessionStorage.getItem('addPedido'));
            if (precargado) {
                $scope.busqueda = precargado.busqueda || {};
                $scope.objetosList = precargado.listado || [];
                $scope.objetosSeleccionados = precargado.seleccionados || [];
                $scope.totales = precargado.totales || {};
                $scope.reasignarSeleccionados();
            } else {
                $scope.objetosList = [];
                $scope.objetosSeleccionados = [];
                $scope.busqueda = {};
                $scope.busqueda.familia = {};
                $scope.busqueda.subFamilia = {};
                $scope.busqueda.filtro = '';
                $scope.busqueda.pagina = 1;
                $scope.totales = $scope.iniciarTotales();
            }
            $scope.totales = $scope.iniciarTotales();

            $scope.consultarListado = function () {
                $scope.processConsultar.modal('show');
                var familia = 0;
                if ($scope.busqueda.subFamilia && $scope.busqueda.subFamilia.ide_fam > 0) {
                    familia = $scope.busqueda.subFamilia.ide_fam;
                } else if ($scope.busqueda.familia && $scope.busqueda.familia.ide_fam > 0) {
                    familia = $scope.busqueda.familia.ide_fam;
                }
                factoryPedidosService.listadoArticulos($cookies.get('csrftoken'), familia,
                        $scope.busqueda.filtro, $scope.busqueda.pagina)
                        .success(function (data) {
                            $scope.almancenaObjetos(); //antes de busqueda para mantener filtros
                            $scope.objetosList = JSON.parse(data.registros);
                            $scope.reasignarSeleccionados();
                            $scope.busqueda.pagina = data.pagina;
                            $scope.busqueda.paginas = data.paginas;
                            $scope.totalizar();
                            $scope.processConsultar.modal('hide');
                        }).error(function (dataError) {
                    $scope.processConsultar.modal('hide');
                    console.log(dataError);
                    $window.sessionStorage.removeItem('segPedido');
                });

            };
            $scope.ir = function (indice) {
                $scope.busqueda.pagina = indice;
                $scope.consultarListado();
            };


            //Familias
            $scope.familiasList = [];
            $scope.subFamiliasList = [];
            factoryPedidosService.listadoFamilias(
                    $cookies.get('csrftoken')
                    ).success(function (data) {
                $scope.familiasList = data;
            }).error(function (dataError) {
                console.log(dataError);
            });

            $scope.filtroSubFamilia = function () {
                $scope.subFamiliasList = [];
                for (var i = 0; i < $scope.familiasList.length; i++) {
                    if ($scope.familiasList[i].pad_fam) {
                        if ($scope.familiasList[i].pad_fam.ide_fam === $scope.busqueda.familia.ide_fam) {
                            $scope.subFamiliasList.push($scope.familiasList[i]);
                        }
                    }
                }
            };

            $scope.getPaginas = function () {
                return new Array($scope.busqueda.paginas);
            };

            $scope.clasePagina = function (indice) {
                if ((indice + 1) === $scope.busqueda.pagina) {
                    return 'active';
                } else {
                    return '';
                }
            };

            $scope.claseInicio = function () {
                if ($scope.busqueda.pagina === 1) {
                    return 'disabled';
                } else {
                    return '';
                }
            };

            $scope.claseFin = function () {
                if ($scope.busqueda.pagina === $scope.busqueda.paginas) {
                    return 'disabled';
                } else {
                    return '';
                }
            };


            $scope.listadoArticulos = function () {
                $scope.consultarListado();
            };

            if ($scope.objetosList.length <= 0) {
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
                datos.busqueda = $scope.busqueda;
                datos.seleccionados = $scope.objetosSeleccionados;
                $window.sessionStorage.setItem('addPedido', JSON.stringify(datos));
            };

            $scope.eliminarObjetos = function () {
                $window.sessionStorage.removeItem('addPedido');
            };

            $scope.limpiarDetalle = function (consulta) {
                $scope.totales = $scope.iniciarTotales();
                $scope.objetosList = [];
                $scope.objetosSeleccionados = [];
                if (consulta) {
                    $scope.consultarListado();
                }
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

            $scope.save = function () {
                $scope.mensajeError = null;
                $scope.muestraMensajeError = false;

                bootbox.confirm("Desea Registrar Pedido?", function (result) {
                    if (result) {
                        $scope.processGuardar.modal('show');
                        factoryPedidosService.guardar($cookies.get('csrftoken'),
                                $scope.objetosSeleccionados)
                                .success(function (data) {
                                    $scope.processGuardar.modal('hide');
                                    $scope.limpiarDetalle(false);
                                    $scope.eliminarObjetos();
                                    bootbox.alert("Peido Registrado Exitosamente! "
                                            + "<br> "
                                            + "Debe confirmar pedido para finalizar proceso", function () {
                                                $window.location.href = "principal.html";
                                            });
                                }).error(function (dataError) {
                            $scope.processGuardar.modal('hide');
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


angular.module('frontinoCli.pedidos').controller('ModalSaveCtrl', function ($scope, $uibModalInstance, item) {
    $scope.item = item;
    $scope.ok = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});


        