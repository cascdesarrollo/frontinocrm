angular.module('loginServices', [])
        .factory('factoryLoginService', function ($http) {
            return {
                validar: function (username, password) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'session/login',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        dataType: "json",
                        data: $.param({
                            'username': username,
                            'password': password
                        })
                    });
                },
                salir: function (token) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'session/logout',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        dataType: "json",
                        data: $.param({
                            'valida': token
                        })
                    });
                },
                activarusuario: function (username, password, codigo) {
                    return $http({
                        method: 'POST',
                        url: FRONTINOCLI + 'session/activar',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        dataType: "json",
                        data: $.param({
                            'emailactiva': username,
                            'password': password,
                            'codigo': codigo
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
