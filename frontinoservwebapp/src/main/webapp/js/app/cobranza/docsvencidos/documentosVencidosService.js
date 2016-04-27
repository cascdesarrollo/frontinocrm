angular.module('docsVencidosService', [])
        .factory('factoryDocsVencidosService', function ($http) {
            return {
                listadoDocsVencidos: function (token) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'documentos/consultar',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        dataType: "json",
                        data: $.param({
                            'token': token,
                            'fecIni': 0,
                            'fecFin': 0,
                            'ideTdo':0,
                            'vencidos':true
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
