var app = angular.module('app', ['ngSanitize']);

app.controller('controller', function($scope, $http) {

    $scope.artifactLinkClass = 'artifact-link';

    $scope.search = function() {
        $scope.search($scope.year, $scope.month);
    }

    $scope.search = function(year, month) {

        $scope.emails = [];

        var url = '/artifacts';

        var params = new Object();
        params.owner = "rich";
        params['after'] = formatDate(new Date(year, month, 1));
        params['before'] = formatDate(new Date(year, parseInt(month, 10) + 1, 0));
        if ($scope.from) params['from'] = $scope.from.split(/[ ,]+/);
        if ($scope.to) params['to'] = $scope.to.split(/[ ,]+/);

        $http.post(url, JSON.stringify(params))
            .then(function(response) {
                $scope.artifacts = response.data;
            });
    }

    $scope.counts = function() {

        var url = '/artifact/counts';

        var params = new Object();
        params.owner = "rich";
        if ($scope.year && $scope.month) params['after'] = formatDate(new Date(1998, 7, 1));
        if ($scope.year && $scope.month) params['before'] = formatDate(new Date());
        if ($scope.from) params['from'] = $scope.from.split(/[ ,]+/);
        if ($scope.to) params['to'] = $scope.to.split(/[ ,]+/);

        $http.post(url, JSON.stringify(params))
            .then(function(response) {
                var answer = rangeOfYearMonthsWithCounts(1998, 2019, response.data);
                $scope.monthBoxes = answer[0];
                $scope.maxBoxCount = answer[1];
            });
    }

    $scope.enter = function(event) {
      if (event.which === 13)
        $scope.counts();
    }

    $scope.read = function(index, trove, key) {

        var previous = angular.element( document.querySelector('div.artifact-link-selected') );
        previous.toggleClass('artifact-link-selected')
        var current = angular.element( document.querySelector('#artifact' + index) );
        current.toggleClass('artifact-link-selected');

        showFeature();

        $http.get('/artifact/rich.s/' + trove + '/' + key)
            .then(function(response) {
                $scope.feature = response.data;
            });
    }

    $scope.hideFeature = function() {
        $scope.feature = ''
        hideFeature();
    }

    $scope.rangeOfMonths = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];

    $scope.prettyDateify = function(text) {

        if (!text) return null;
        var d = new Date(text);
        var year = d.getFullYear();
        var month = d.getMonth();
        var date = d.getDate();
        return $scope.rangeOfMonths[month] + " " + date + ", " + year;
    }

    $scope.prettyMonthify = function(year, month) {

        return $scope.rangeOfMonths[month] + " " + year;
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

    $scope.year = $scope.yearChoices[0];
    $scope.month = 7;
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

function rangeOfYearMonthsWithCounts(start, end, results) {

    var countsByKey = new Object();
    var max = 1;

    for (var i = 0; i < results.length; i++) {
        r = results[i];
        countsByKey[[r.year, r.month - 1]] = r.count
        max = Math.max(max, r.count)
    }

    result = [];
    for (var y = start; y <= end; y++) {
        var year = new Object();
        year.year = y;
        year.header = true;
        result.push(year);
        for (m = 0; m < 12; m++) {
            var month = new Object();
            month.year = y
            month.month = m
            month.count = countsByKey[[y, m]];
            if (!month.count) month.count = 0
            result.push(month);
        }
    }

    return [result, max];
}

function formatDate(d) {

    month = '' + (d.getMonth() + 1),
    day = '' + d.getDate(),
    year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}