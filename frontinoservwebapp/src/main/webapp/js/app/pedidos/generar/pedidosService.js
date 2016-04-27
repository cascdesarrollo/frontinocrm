angular.module('pedidosService', [])
        .factory('factoryPedidosService', function ($http) {
            return {
                listadoArticulos: function (token) {
                    return $http.get
                            (FRONTINOCLI + "pedidos/articulospedido?"
                                    + "token=" + token);
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
