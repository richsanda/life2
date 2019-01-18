var app = angular.module('app', []);

app.controller('controller', function($scope, $http) {

    $scope.after = '2006-06-01';
    $scope.before = '2006-09-30';
    $scope.from = 'rich.s';

    $scope.search = function() {

    $scope.emails = [];

        var url = 'http://localhost:11000/emails';

        var params = new Object();
        if ($scope.after) params['after'] = $scope.after;
        if ($scope.before) params['before'] = $scope.before;
        if ($scope.from) params['from'] = $scope.from;
        if ($scope.to) params['to'] = $scope.to;
        if ($scope.who) params['who'] = $scope.who;

        var firstParam = true;
        Object.keys(params).forEach( function(key) {
            url += firstParam ? '?' : '&';
            url += key + '=' + params[key];
            firstParam = false;
        })

        $http.get(url)
            .then(function(response) {
                $scope.artifacts = response.data;
            });
    }

    $scope.read = function(trove, key) {

        showFeature();

        $http.get('http://localhost:11000/email/' + trove + '/' + key)
            .then(function(response) {
                $scope.feature = response.data;
            });
    }

    $scope.hideFeature = function() {
        hideFeature();
    }

    $scope.prettyDateify = function(text) {

        if (!text) return null;
        var d = new Date(text);
        var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
        var year = d.getFullYear();
        var month = d.getMonth();
        var date = d.getDate();
        return months[month] + " " + date + ", " + year;
    }
});

function showFeature() {
  document.getElementById("feature-background").style.display = "block";
}

function hideFeature() {
  document.getElementById("feature-background").style.display = "none";
}