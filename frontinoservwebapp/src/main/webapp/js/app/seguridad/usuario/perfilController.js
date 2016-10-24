angular.module('frontinoCli.perfil', ['ui.bootstrap', 'perfilServices'])

        .controller('PerfilCtrl', function ($scope, $cookies, $window, $location,
                factoryPerfilService, translationService) {
            $scope.pageClass = 'page-in';
            $scope.pageClass = 'page-back';

            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();

            $scope.cerrarSesion = function () {
                factoryPerfilService.salir(
                        $cookies.get('csrftoken')
                        )
                        .success(function (data) {
                        }).error(function (data) {
                });
                $cookies.remove('csrftoken');
                $window.localStorage.removeItem('segPedido');
                $window.sessionStorage.removeItem('addPedido');
                $window.location.href = "index.html";
            };

            $scope.dataSes = {};
            factoryPerfilService.datos($cookies.get('csrftoken'))
                    .success(function (data) {
                        if (data) {
                            $scope.dataSes = data;
                            console.log(data);
                        } else {
                            $scope.cerrarSesion();
                        }
                    }).error(function (data) {
                $scope.cerrarSesion();
            });


            $scope.regresar = function () {
                $location.path('#/dashboard/', false);
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();


        })
        .controller('CambioPasswordCtrl', function ($scope, $cookies, $window, $location,
                factoryPerfilService, translationService) {
            //Sesion
            $scope.cerrarSesion = function () {
                factoryPerfilService.salir();
                $cookies.remove('csrftoken');
                $window.location.href = "index.html";
            };
            $scope.dataSes = {};
            $scope.objeto = {};
            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                } else {
                    factoryPerfilService.datos($cookies.get('csrftoken'))
                            .success(function (data) {
                                $scope.dataSes = data;
                            }).error(function (data) {
                        $scope.cerrarSesion();
                    });
                }
            };
            $scope.validaSesion();
            //

            $scope.cambiar = function () {
                $scope.muestraMensajeError = false;
                $scope.mensajeError = '';
                factoryPerfilService.cambiarPass($cookies.get('csrftoken'), $scope.objeto)
                        .success(function (data) {
                            bootbox.alert("Password modificado exitosamente!");
                            $scope.regresar();
                        }).error(function (data) {
                    if (data.error) {
                        $scope.muestraMensajeError = data.error;
                        $scope.mensajeError = data.des_error;
                    }
                });
            };

            $scope.regresar = function () {
                $location.path('/', false);
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();


        })
        ;


        