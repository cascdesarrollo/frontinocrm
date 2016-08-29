angular.module('pedidosSeguimientoService', [])
        .factory('factoryPedSegService', function ($http) {
            return {
                listadoPedidos: function (filtro) {
                    return $http.get
                            (FRONTINOCLI + "pedidos/listado?"
                                    + "token=" + filtro.token
                                    + "&fecIni=" + filtro.fecIni
                                    + "&fecFin=" + filtro.fecFin
                                    + "&staPed=" + filtro.staPed);

                },
                detallePedidos: function (token, id) {
                    return $http.get
                            (FRONTINOCLI + "pedidos/detalle?"
                                    + "token=" + token
                                    + "&id=" + id);

                },
                status: function (token, ideped, status) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'pedidos/status?token='+token,
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        dataType: "json",
                        data: $.param({
                            'ideped': ideped,
                            'status': status
                        })
                    });
                }
            };
        })
        .factory('translationService', function ($resource) {
            return{
                getTranslation: function ($scope, language) {
                    var languageFilePath = 'translations/translation_' + language + '.json';
                    $resource(languageFilePath).get(function (data) {
                        $scope.translation = data;
                    });
                }};
        });
