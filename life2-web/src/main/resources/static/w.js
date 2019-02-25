var app = angular.module('app', ['ngSanitize']);

app.controller('controller', function($scope, $http) {

    $scope.month = 7;
    $scope.year = 1998;
    $scope.from = 'rich.s';

    $scope.search = function() {

    $scope.emails = [];

        var url = '/artifacts';

        var params = new Object();
        params.owner = "rich";
        if ($scope.year && $scope.month) params['after'] = formatDate(new Date($scope.year, $scope.month, 1));
        if ($scope.year && $scope.month) params['before'] = formatDate(new Date($scope.year, parseInt($scope.month, 10) + 1, 0));
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

    $scope.preify = function(text) {
        if (!text) return "<div>&nbsp;</div>";
        return "<div>&nbsp;</div><div>" + text + "</div>";
    }

    $scope.monthChoices = {0 : 'jan', 1 : 'feb', 2 : 'mar', 3 : 'apr', 4 : 'may', 5 : 'jun', 6 : 'jul', 7 : 'aug', 8 : 'sep', 9 : 'oct', 10 : 'nov', 11 : 'dec'}
    $scope.yearChoices = rangeOfYears();
});

function showFeature() {
  document.getElementById("feature-background").style.display = "block";
}

function hideFeature() {
  document.getElementById("feature-background").style.display = "none";
}

function rangeOfYears() {
    var list = [];
    for (var i = 1998; i <= 2019; i++) {
        list.push(i);
    }
    return list;
}

function formatDate(d) {

    month = '' + (d.getMonth() + 1),
    day = '' + d.getDate(),
    year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}