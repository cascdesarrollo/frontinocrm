angular.module('frontinoCli', ['ngRoute', 'ngResource', 'ngCookies',
    'ngAnimate',
    'generalServices',
    'frontinoCli.seguimiento',
    'frontinoCli.consultaCobranza',
    'frontinoCli.pedidos',
    'frontinoCli.consultaDocs',
    'frontinoCli.perfil'
])
        .config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/', {
                    templateUrl: 'dashboard.html',
                    controller: 'DashBoardCtrl'
                });
                $routeProvider.when('/construccion', {
                    templateUrl: 'pages/construccion.html'
                });
                $routeProvider.when('/otra', {
                    templateUrl: 'otra.html'
                });
                $routeProvider.when('/seguimiento', {
                    templateUrl: 'pages/pedidos/seguimiento/seguimiento.html',
                    controller: 'PedSegCtrl',
                    reloadOnSearch: false
                });
                $routeProvider.when('/seguimiento/:idePed', {
                    templateUrl: 'pages/pedidos/seguimiento/detallePedido.html',
                    controller: 'DetallePedSegCtrl'
                });
                $routeProvider.when('/vencidos/:tipobus', {
                    templateUrl: 'pages/documentos/consulta/documentos.html',
                    controller: 'ConsultaDocCtrl'
                });
                $routeProvider.when('/documentos/:tipobus', {
                    templateUrl: 'pages/documentos/consulta/documentos.html',
                    controller: 'ConsultaDocCtrl'
                });
                $routeProvider.when('/nuevoPedido', {
                    templateUrl: 'pages/pedidos/generar/pedido.html'
                    , controller: 'PedidosCtrl'
                });
                $routeProvider.when('/perfil', {
                    templateUrl: 'pages/usuario/perfil.html'
                    , controller: 'PerfilCtrl'
                });
                
                $routeProvider.when('/cambiopassword', {
                    templateUrl: 'pages/usuario/cambiopassword.html'
                    , controller: 'CambioPasswordCtrl'
                });
                $routeProvider.otherwise({redirectTo: '/'});
            }])
        .controller('MainCtrl', function ($scope, $cookies, $window,
                factoryGeneralService, translationService
                ) {

            $scope.pageClass = 'page-back';

            $scope.validaSesion = function () {
                if (!$cookies.get('csrftoken')) {
                    alert('no es');
                    $cookies.remove('csrftoken');
                    $window.location.href = "index.html";
                }
            };
            $scope.validaSesion();

            $scope.cerrarSesion = function () {
                factoryGeneralService.salir(
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
            var process = $(
                    '<div class="modal fade" id="process" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  data-keyboard="false" >'


                    + '<div class="modal-dialog" >'
                    + '<div class="modal-content">'
                    + '<div class="modal-header">'
                    + '<h4 class="modal-title" id="myModalLabel">Procesando</h4>'
                    + '</div>'
                    + '<div class="modal-body">'
                    + 'Cargando Datos de Usuario'
                    + '<div class="progress">'
                    + '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
                    + '<span class="sr-only">10% Complete</span>'
                    + '</div>'
                    + '</div>'

                    + '</div>'
                    + '</div>'
                    + '</div>'
                    + '</div>');

            process.modal('show');

            $scope.dataSes = {};
            factoryGeneralService.datos($cookies.get('csrftoken'))
                    .success(function (data) {
                        if (data) {
                            process.modal('hide');
                            $scope.dataSes = data;
                            console.log(data);
                        } else {
                            process.modal('hide');
                            $scope.cerrarSesion();
                        }
                    }).error(function (data) {
                process.modal('hide');
                $scope.cerrarSesion();
            });

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();


        })
        .controller('DashBoardCtrl', function ($scope, $cookies, $window,
                factoryGeneralService, translationService) {
            $scope.pageClass = 'page-back';

            $scope.dataDashBoard = {};
            factoryGeneralService.infoTablero($cookies.get('csrftoken'))
                    .success(function (data) {
                        if (data) {
                            $scope.dataDashBoard = data;
                            console.log('info dash');
                            console.log(data);
                        }
                    }).error(function (data) {

            });

            $scope.translate = function () {
                translationService.getTranslation($scope, $scope.selectedLanguage);
            };
            $scope.selectedLanguage = IDIOMA;
            $scope.translate();

        });
var IDIOMA = 'es';
//var FRONTINOCLI = 'http://54.214.255.80:9090/frontinoservbackend/webresources/';
var FRONTINOCLI = 'http://localhost:8080/frontinoservbackend/webresources/';
        