angular.module('frontinoCli.docsVencidos', ['ui.bootstrap', 'docsVencidosService'])

        .controller('DocsVencidosCtrl', function ($scope, $cookies, $window, $timeout, factoryDocsVencidosService, translationService) {
            $scope.pageClass = 'page-in';
            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();

            $scope.tipDoc = 'D';
            $scope.tipVencidos = true;
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


            $scope.objetosList = [];

            $scope.listadoDocsVencidos = function () {
                factoryDocsVencidosService.listadoDocsVencidos(
                        $cookies.get('csrftoken')
                        )
                        .success(function (data) {
                            $scope.objetosList = data;
                            console.log($scope.objetosList);
                        }).error(function (dataError) {
                    console.log(dataError);
                    $scope.objetosList = [];
                });

            };

            $scope.listadoDocsVencidos();

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();

        });



        