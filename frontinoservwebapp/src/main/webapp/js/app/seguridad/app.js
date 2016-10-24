angular.module('app', ['ngRoute', 'ngResource', 'ngCookies', 'loginServices'])
        .controller('LoginCtrl', function ($scope, $window, $location, factoryLoginService, translationService, $cookies) {
            if ($cookies.get('csrftoken')) {
                $window.location.href = 'principal.html';
            }
            $scope.activacion = false;
            $scope.empresasList = {};
            $scope.empresa = null;
            $scope.consultando = false;
            $scope.auth_token = null;
            $scope.mensajeError = null;
            $scope.muestraMensajeError = false;
            $scope.validateUser = function () {
                $scope.process.modal('show');
                $scope.consultando = true;
                $scope.mensajeError = null;
                $scope.muestraMensajeError = false;
                factoryLoginService.validar($scope.email, $scope.password)
                        .success(function (data) {
                            $scope.process.modal('hide');
                            $scope.auth_token = data.auth_token;
                            if (data.empresas.length > 1) {
                                $scope.empresasList = data.empresas;
                            } else {
                                $scope.empresa = data.empresas[0];
                                $cookies.put('csrftoken', $scope.email + '/' + $scope.empresa.ideEmp +
                                        'e' + $scope.empresa.ideTer +
                                        'e' + $scope.auth_token);
                                $window.location.href = "principal.html";
                            }
                            $scope.consultando = false;
                        }).error(function (data) {
                    $scope.process.modal('hide');
                    $scope.consultando = false;
                    if (data) {
                        $scope.muestraMensajeError = data.error;
                        $scope.mensajeError = data.des_error;
                    } else {
                        $scope.muestraMensajeError = true;
                        $scope.mensajeError = "Error Consultando BackEnd";
                    }
                });
            };


            $scope.process = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Procesando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Verificando Credenciales'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');



            $scope.ingresar = function () {
                $scope.muestraMensajeError = false;
                $scope.mensajeError = null;
                if ($scope.empresa !== null) {
                    $scope.autenticado = true;
                    $cookies.put('csrftoken', $scope.email + '/' + $scope.empresa.ideEmp +
                            'e' + $scope.empresa.ideTer +
                            'e' + $scope.auth_token);
                    $window.location.href = 'principal.html';
                } else {
                    $scope.muestraMensajeError = true;
                    $scope.mensajeError = "Seleccione Empresa";
                }
            };

            $scope.cancelar = function () {
                factoryLoginService.salir($scope.email + '/934fe9123e' + $scope.auth_token)
                        .success(function (data) {

                        }).error(function (data) {

                });
                $scope.empresasList = {};
                $scope.empresa = null;
                $scope.consultando = false;
                $scope.auth_token = null;
                $scope.mensajeError = null;
                $scope.muestraMensajeError = false;

            };

            $scope.activaRegistro = function () {
                $scope.activacion = true;
                $scope.iniciaValoresRegistro();
            };

            $scope.desActivaRegistro = function () {
                $scope.activacion = false;
            };

            $scope.iniciaValoresRegistro = function () {
                $scope.emailActiva = null;
                $scope.passwordActiva = null;
                $scope.passwordActiva2 = null;
                $scope.codigoActiva = null;
                $scope.password = null;
                $scope.acepto = false;
            };

            $scope.activateUser = function () {
                $scope.mensajeError = null;
                $scope.muestraMensajeError = false;
                if (!$scope.acepto) {
                    $scope.mensajeError = 'Debe Aceptar Terminos y Condiciones';
                    $scope.muestraMensajeError = true;
                    return;
                }
                if ($scope.passwordActiva !== $scope.passwordActiva2) {
                    $scope.mensajeError = 'Contraseñas Ingresadas No Coinciden';
                    $scope.muestraMensajeError = true;
                    return;
                }
                $scope.process.modal('show');
                $scope.consultando = true;
                factoryLoginService.activarusuario($scope.emailActiva, $scope.passwordActiva, $scope.codigoActiva)
                        .success(function (data) {
                            $scope.process.modal('hide');
                            bootbox.alert("Su cuenta ha sido activada!");
                            $scope.activacion = false;
                            $scope.email = $scope.emailActiva;
                            $scope.iniciaValoresRegistro();
                            $scope.auth_token = data.auth_token;
                            if (data.empresas.length > 1) {
                                $scope.empresasList = data.empresas;
                            } else {
                                $scope.empresa = data.empresas[0];
                                $cookies.put('csrftoken', $scope.email + '/' + $scope.empresa.ideEmp +
                                        'e' + $scope.empresa.ideTer +
                                        'e' + $scope.auth_token);
                                $window.location.href = "principal.html";
                            }
                            $scope.consultando = false;

                        }).error(function (data) {
                    $scope.process.modal('hide');
                    if (data) {
                        $scope.muestraMensajeError = data.error;
                        $scope.mensajeError = data.des_error;
                    } else {
                        $scope.muestraMensajeError = true;
                        $scope.mensajeError = "Error Consultando BackEnd";
                    }
                });
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();

        });
var IDIOMA = 'es';
//var FRONTINOCLI = 'http://54.214.255.80:9090/frontinoservbackend/webresources/';
var FRONTINOCLI = 'http://localhost:8080/frontinoservbackend/webresources/';