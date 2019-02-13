var app = angular.module('app', ['ngSanitize']);

app.controller('controller', function($scope, $http) {

    $scope.after = '2006-06-01';
    $scope.before = '2006-09-30';
    $scope.from = 'rich.s';

    $scope.search = function() {

    $scope.emails = [];

        var url = '/artifacts';

        var params = new Object();
        params.owner = "rich";
        if ($scope.after) params['after'] = $scope.after;
        if ($scope.before) params['before'] = $scope.before;
        if ($scope.from) params['from'] = [ $scope.from ];
        if ($scope.to) params['to'] = [ $scope.to ];
        if ($scope.who) params['who'] = [ $scope.who ];

        $http.post(url, JSON.stringify(params))
            .then(function(response) {
                $scope.artifacts = response.data;
            });

/*
        var firstParam = true;
        Object.keys(params).forEach( function(key) {
            url += firstParam ? '?' : '&';
            url += key + '=' + params[key];
            firstParam = false;
        })
        *

        $http.get(url)
            .then(function(response) {
                $scope.artifacts = response.data;
                $scope.body = "<div><b>hello</b></div>";
            });
            */
    }

    $scope.read = function(trove, key) {

        showFeature();

        $http.get('/artifact/rich.s/' + trove + '/' + key)
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

    $scope.bodyify = function(text) {
        if (!text) return null;
        return "<div><br/>" + text.replace(/(?:\r\n|\r|\n)/g, "<br/>") + "</div>";
    }
});

function showFeature() {
  document.getElementById("feature-background").style.display = "block";
}

function hideFeature() {
  document.getElementById("feature-background").style.display = "none";
}