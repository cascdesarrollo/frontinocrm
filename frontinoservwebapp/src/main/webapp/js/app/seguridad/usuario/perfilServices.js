angular.module('perfilServices', [])
        .factory('factoryPerfilService', function ($http) {
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
                cambiarPass: function (token, objeto) {
                    console.log(objeto.password);
                    console.log(objeto.repassword);
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'session/cambiopass',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        dataType: "json",
                        data: $.param({
                            'token': token,
                            'pold': objeto.old_password,
                            'pnew': objeto.password,
                            'prenew': objeto.repassword
                        })
                    });
                }
            }
            ;
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
