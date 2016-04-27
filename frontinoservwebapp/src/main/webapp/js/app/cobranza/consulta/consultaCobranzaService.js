angular.module('consultaCobranzaService', [])
        .factory('factoryConsulCobService', function ($http) {
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

                }
            };
        })
        .factory('translationService', function ($resource) {
            return{
                getTranslation: function ($scope, language) {
                    var languageFilePath = 'translations/translation_' + language + '.json';
                    //  console.log(languageFilePath);
                    $resource(languageFilePath).get(function (data) {
                        $scope.translation = data;
                    });
                }}
        });
