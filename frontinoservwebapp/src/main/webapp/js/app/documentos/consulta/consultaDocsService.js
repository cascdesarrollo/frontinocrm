angular.module('consultaDocsService', [])
        .factory('factoryConsulDocsService', function ($http) {
            return {
                listadoDocumentos: function (filtro) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'documentos/consultar',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        dataType: "json",
                        data: $.param(filtro)
                    });
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
