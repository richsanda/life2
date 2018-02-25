$(pageBehavior);

function pageBehavior () {

    actionsBehavior($(this));

    $('#before').val(nowify());
}

function refreshEntries(showStory) {

    var url = "/soundtrack";

    $.ajax({
        url: url,
        dataType: "json"
    }).success(function (data) {
        showPane(buildAllPane("all", true), buildEntries(data, updateEntryPosition, createAddEntryDiv, showStory));
    });
}

function showPane(paneDiv) {
    $('#pane').html(paneDiv);
}

function go() {

    var after = $('#after').val();
    var before = $('#before').val();
    var names = $('#names').val();

    var url = "/person/emails?after=" + after + "&before=" + before + "&names=" + names;

    $.ajax({
        url: url,
        dataType: "json"
    }).success(function (data) {
        showPane(buildEmails(data));
    });
}

function regionClick (e, behaviorMap) {

    var $$ = $(e.target);
    while ($$.length > 0) {
        for (var key in behaviorMap) {
            if ($$.hasClass(key)) {
                behaviorMap[key]($$);
                return
            }
        }
        $$ = $$.parent();
    }
}

function actionsClick(e) {

    var actions = {
        'go' : function ($$) {
            go($$);
        },
        'toggle-story' : function ($$) {
            var entryDiv = $$.closest('div.entry');
            var storyDiv = entryDiv.find('div.story');
            storyDiv.toggle();
            pushState();
        },
        'no-op' : function ($$) {}
    };

    regionClick(e, actions);
}

function actionsBehavior(root) {

    $(root).on('click touchstart', actionsClick);

    return root
}

function readEntry($$) {

    var entryDiv = $$.closest('div.entry');
    var id = entryDiv.attr('id');

    if (!id) {

        entryDiv.replaceWith(createAddEntryDiv());
        return
    }

    var url = "/entry/" + id;

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json"
    }).success(function (data) {
        entryDiv.replaceWith(showEntry(data, true));
    });
}

function buildEmails(emails) {

    var entriesDiv = $("<div class='entries'></div>");

    var i = 1;
    $.each(emails, function() {
        var entryDiv = showEmail(this, i++);
        entriesDiv.append(entryDiv);
    });

    return entriesDiv;
}

function showIntroPane() {

    $('#pane').hide();
    $('#years').hide();
    $('#intro').show();
    $('#soundtrack').empty();
}

function buildAllPane(title, showStories) {

    var soundtrackPaneDiv = $(
        "<div class='soundtrack-pane'>" +
        "<div class='soundtrack-title'>" + title + "</div>" +
        "</div>"
    );

    soundtrackPaneDiv.append(buildStoryButtonDiv(showStories));

    return soundtrackPaneDiv;
}

function buildStoryButtonDiv(showStories) {
    if (showStories) {
        return $("<div class='tab hide-stories'>hide stories</div>");
    } else {
        return $("<div class='tab show-stories'>show stories</div>");
    }
}

function showEmail(entry, count) {

    var timelineDiv = showTimeline();

    var releaseCircleDiv = showCircle(entry.releasePosition, 5, "hi");
    var soundtrackCircleDiv = showCircle(entry.soundtrackPosition, Math.max(5, Math.sqrt(entry.score) * 1.5), "hi");
    var timespanDiv = $("<div class='timespan' title='" + "hi" + "'></div>");
    timespanDiv.css({
        'left': entry.releasePosition + '%',
        'right': 100 - entry.soundtrackPosition + '%'
    });

    timelineDiv.append(releaseCircleDiv);
    timelineDiv.append(soundtrackCircleDiv);
    timelineDiv.append(timespanDiv);

    var titleDiv = $("<div class='title toggle-story'/>");

    var fromDiv = $(
        "<div class='year-ordinal'>" +
        "<div class='count'>" + count + ". </div>" +
        "<div class='year title-button'>" + prettyDateify(entry.sent) + "</div>" +
        "<div class='year title-button' id='" + entry.from + "'>" + entry.from + " -> " + entry.to + "</div>" +
        "</div>"
    );

    var subjectDiv = $(
        "<div class='title-info'>" +
        "<span class='title'>" + entry.subject + "</span>" +
        "</div>"
    );

    titleDiv.css("background-color", calculateColor(entry));

    titleDiv.append(fromDiv);
    titleDiv.append(subjectDiv);
    titleDiv.append(timelineDiv);

    var entryDiv = $("<div class='entry' id='" + entry.key + "'/>");
    entryDiv.append(titleDiv);

    var storyDiv = $("<div class='story'>" + storyify(entry.body) + "</div>");
    entryDiv.append(storyDiv);
    storyDiv.append("<hr/>");

    $(storyDiv).hide();

    return entryDiv;
}

function showTimeline() {

    return $(
        "<div class='timeline-container'>" +
        "<div class='timeline-segment'>62</div>" +
        "<div class='timeline-segment'>67</div>" +
        "<div class='timeline-segment'>72</div>" +
        "<div class='timeline-segment'>77</div>" +
        "<div class='timeline-segment'>82</div>" +
        "<div class='timeline-segment'>87</div>" +
        "<div class='timeline-segment soundtrack-segment'>92</div>" +
        "<div class='timeline-segment soundtrack-segment'>97</div>" +
        "<div class='timeline-segment soundtrack-segment'>02</div>" +
        "<div class='timeline-segment soundtrack-segment'>07</div>" +
        "<div class='timeline-segment-last soundtrack-segment'>12</div>" +
        "</div>"
    );
}

function showCircle(p, d, t) {

    var circleDiv = $("<div class='soundtrack-circle' title='" + t + "'></div>");

    circleDiv.css({
        "left": "" + p + "%",
        'height': d + 'px',
        'width': d + 'px',
        'margin-left': '-' + d / 2 + 'px',
        'margin-top': '-' + d / 2 + 'px'
    });

    return circleDiv;
}

function showTag(tag) {

    var titleDiv = $("<div class='title'><div class='tag-title title-button' id='" + tag.tag + "'>" + tag.tag + " " + appearanceify(tag.appearances) + "</div> " + valuify(tag.fullTag) + "</div>");
    var storyDiv = $("<div class='story'>" + storyify(tag.story) + "</div>");
    var tagDiv = $("<div class='tag' id='" + tag.key + "'/>");
    tagDiv.append(titleDiv);
    tagDiv.append(storyDiv);
    tagDiv.append("<hr/>");

    return tagDiv;
}

function buildTagLink(tag) {
    var tagLink = $(nameify(null, "#", tag.tag));
    tagLink.text(tagLink.text() + " " + appearanceify(tag.appearances));
    return tagLink;
}

function buildPerson(person) {

    var titleDiv = $("<div class='title'><div class='person-title title-button' id='" + person.tag + "'> " + person.tag + " " + appearanceify(person.appearances) + "</div> " + valuify(person.name) + "</div>");
    var storyDiv = $("<div class='story'>" + storyify(person.story) + "</div>");
    var personDiv = $("<div class='person' id='" + person.key + "'/>");
    personDiv.append(titleDiv);
    personDiv.append(storyDiv);
    storyDiv.append("<hr/>");

    return personDiv;
}

function buildPersonLink(person) {
    var personLink = $(nameify(null, "@", person.tag));
    personLink.append(" " + appearanceify(person.appearances));
    return personLink;
}

function readNameTag(id) {

    var url = "/entries?personTags=" + id;

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json"
    }).success(function (data) {
        showPane(buildPeoplePane(id), buildEntries(data, true));
    });
}

function storyify(text) {

    if (!text) return "";

    // text = text.replace(/(@|#)([0-9a-z\-\./]*)(\{(.*?)\})?/g, nameify);
    text = text.replace(/(?:\r\n|\r|\n)/g, "<br/>");

    return text;
}

function nameify(match, p1, p2, p3, p4, offset, text) {

    var divClass = p1 == '@' ? 'name-tag' : 'hash-tag';
    var linkText = p4;
    var tagText = p2;
    var tagSepIndex = p2.indexOf('/');
    var tagHasSep = tagSepIndex > 0;

    if (tagHasSep) {
        var tagType = p2.substring(0, tagSepIndex);
        if (tagType == 'music') {
            divClass = tagType;
        } else {
            divClass += ' ' + tagType;
        }
        tagText = p2.substring(tagSepIndex + 1);
    } else {
        divClass += ' general';
    }
    tagText = tagText.replace(/\./g, ' ');
    if (!linkText) linkText = tagText;

    return "<div class='" + divClass + "' id='" + p2 + "'>" + linkText + "</div>";
}

function valuify(text) {
    return null == text ? "" : text.replace(/\'/g, "&apos;");
}

function numerify(text) {
    return null == text ? "" : text;
}

function textareaify(text) {
    return null == text ? "" : text;
}

function parenthesize(text) {
    return null == text ? "" : " (" + text + ") ";
}

function appearanceify(count) {
    return (!count || count <= 1) ? "" : parenthesize(count);
}

function releaseify(text) {

    if (!text) return null;
    var d = new Date(text);
    var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
    var year = d.getFullYear();
    var month = d.getMonth();
    var day = d.getDay();
    return (day == 1 && month == 1) ? year : months[month - 1] + ", " + year;
}

function nowify() {
    var d = new Date();
    //var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
    var year = d.getFullYear();
    var month = d.getMonth() + 1;
    var day = d.getDate();
    return year + '-' + (month < 10 ? "0" : "") + month + '-' + (day < 10 ? "0" : "") + day;
}

function dateify(d) {

    //var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
    var year = d.getFullYear();
    var month = d.getMonth() + 1;
    var day = d.getDate();
    return year + '-' + (month < 10 ? "0" : "") + month + '-' + (day < 10 ? "0" : "") + day;
}

function prettyDateify(text) {

     if (!text) return null;
     var d = new Date(text);
     var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
     var year = d.getFullYear();
     var month = d.getMonth();
     var date = d.getDate();
     return months[month] + " " + date + ", " + year;
}

function calculateColor(entry) {

    var r;
    var y;
    var b;
    var g;

    $.each(entry.rankings, function() {
            if (this.type == "FAVORITE") {
                r = Math.round(255 / 100 * this.score);
            } else if (this.type == "REPRESENTATIVE") {
                y = Math.round(255 / 100 * this.score);
            } else if (this.type = "SHARED") {
                b = Math.round(255 / 100 * this.score);
            }
            g = Math.round((b + y + y) / 3);
        }
    );

    return "rgba(" + r + ", " + g + ", " + b + ", " + entry.score / 600 + ")";
}

$(document).ajaxComplete(function(ev, jqXHR, settings) {
    // settings.url is the ajax url... could come in handy...
    pushState();
});

window.onpopstate = function (event) {
    var currentState = history.state;
    if (currentState != null) {
        document.body.innerHTML = currentState.innerhtml;
    }
};

function pushState(url) {

    url = (!url) ? window.location.href : url;
    var state = { url: url, innerhtml: document.body.innerHTML };
    window.history.pushState(state, url, url);
}

var getUrlParameter = function getUrlParameter(sParam) {

    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&&&'), // HACK to include &, obv can't use multiple params now
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

function idOrValue($$) {
    var id = $$.attr('id');
    if (typeof id == typeof undefined || !id) id = $$.text();
    return id;
}