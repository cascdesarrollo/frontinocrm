angular.module('frontinoCli.consultaDocs', ['ui.bootstrap', 'consultaDocsService'])

        .controller('ConsultaDocCtrl', function ($scope, $cookies, $window, $routeParams, factoryConsulDocsService, translationService) {
            $scope.pageClass = 'page-in';
            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();

            $scope.tipobus = $routeParams.tipobus;
            $scope.tipDoc = 'D';
            ($scope.tipobus === 'S') ? $scope.tipoSaldo = '2' : $scope.tipoSaldo = '1';

            $scope.format='dd/MM/yyyy';
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
            $scope.generarFiltro = function () {
                var filtro = {};
                filtro.token = $cookies.get('csrftoken');
                filtro.tipo = $scope.tipDoc;
                filtro.tipoSaldo = $scope.tipoSaldo;
                if ($scope.fecDesde.tipobus === 'N') {
                    filtro.fecIni = $scope.fecDesde.getTime();
                    filtro.fecFin = $scope.fecHasta.getTime();
                } else {
                    $scope.fecDesde.tipobus = 'N';

                }
                return filtro;
            };

            $scope.objetosList = [];
            $scope.listadoDocumentos = function () {

                factoryConsulDocsService.listadoDocumentos(
                        $scope.generarFiltro()
                        )
                        .success(function (data) {
                            $scope.objetosList = data;
                            console.log($scope.objetosList);
                        }).error(function (dataError) {
                    console.log(dataError);
                    $scope.objetosList = [];
                });
            };
            $scope.listadoDocumentos();
            $scope.hoy=new Date();
            $scope.styleStatus = function (item) {
                if($scope.hoy>valorFecha(item.fve_dvt)){
                    return 'rojo';
                }
                
            };

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();


        })
        ;


function valorFecha(valor) {
    var cadena = valor.split("-");
    var fecha = new Date();
    fecha.setYear(parseInt(cadena[0]));
    fecha.setMonth(parseInt(cadena[1]) - 1);
    fecha.setDate(parseInt(cadena[2]));
    return  fecha;
}