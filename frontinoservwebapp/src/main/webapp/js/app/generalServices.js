angular.module('generalServices', [])
        .factory('factoryGeneralService', function ($http) {
            return {
                salir: function (token) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'session/logout',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        dataType: "json",
                        data: $.param({
                            'valida': token
                        })
                    });
                },
                datos: function (token) {
                    return $http({
                        method: 'GET',
                        url: FRONTINOCLI + 'session/dataSession?valida='
                                + token
                    });
                },
                infoTablero: function (token) {
                    return $http({
                        method: 'GET',
                        url: FRONTINOCLI + 'dashboard/dashboardData?token='
                                + token
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
