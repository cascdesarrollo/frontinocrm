angular.module('pedidosService', [])
        .factory('factoryPedidosService', function ($http) {
            return {
                listadoArticulos: function (token, familia, filtro, pagina) {
                    return $http.get
                            (FRONTINOCLI + "pedidos/articulospedido?"
                                    + "token=" + token + "&familia=" + familia
                                    + "&filtro=" + filtro
                                    + "&pagina=" + pagina);
                },
                listadoFamilias: function (token) {
                    return $http.get
                            (FRONTINOCLI + "pedidos/familias?"
                                    + "token=" + token);
                },
                guardar: function (token, objeto) {
                    return $http({
                        method: 'PUT',
                        url: FRONTINOCLI + 'pedidos/guardar?token='+token,
                        data:objeto
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
