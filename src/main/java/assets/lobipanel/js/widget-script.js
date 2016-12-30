var globalEpicTable = null;
var globalUsTable = [];
var templateHeaderFooter = "<thead><tr><th>User Story</th><th>SUMMARY</th><th>PRIORITY</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></thead><tfoot><tr><th>User Story</th><th>SUMMARY</th><th>PRIORITY</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></tfoot>";
var templateHeaderFooter1 = "<thead><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></thead><tfoot><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></tfoot>";
var globalCycleTable = null;
var globalAssigneeTable = [];

// On document ready, append projects list to 4 widget project field
$(document).ready(function() {
  if ($('#epicProject').length != 0) {
    $.get("/listproject", function(data) {
      if (debugAjaxResponse(data)) {
        return;
      }

      data.sort();
      appendToSelect(false, data, "#epicProject");
      $("#epicProject").val("FNMS 557x");
      appendToSelect(false, data, "#usProject");
      $("#usProject").val("FNMS 557x");
      appendToSelect(false, data, "#cycleProject");
      $("#cycleProject").val("FNMS 557x");
      appendToSelect(false, data, "#assigneeProject");
      $("#assigneeProject").val("FNMS 557x");
    });
  }
});
// Setting up ajax error function
$(function() {
  $.ajaxSetup({
    error: function(jqXHR, exception) {
      if (jqXHR.status === 0) {
        alert('Connection lost.');
      } else if (jqXHR.status == 404) {
        alert('Requested page not found. [404]');
      } else if (jqXHR.status == 500) {
        alert('Internal Server Error [500].');
      } else if (exception === 'parsererror') {
        alert('Requested JSON parse failed.');
      } else if (exception === 'timeout') {
        alert('Time out error.');
      } else if (exception === 'abort') {
        alert('Ajax request aborted.');
      } else {
        alert('Uncaught Error.\n' + jqXHR.responseText);
      }
    }
  });
});

// Create new table for each epic or cycle in Story table and Assignee table
/*
 * id: string: gadget id to create a custom table id
 * title: string: epic link or cycle name on top of table
 * container: string: id of the <div> to append this table to 
 */
function appendTemplateTable(id, title, container) {
  $('<h4>' + title + '</h4>').appendTo(container);
  $('<table id="' + id + '" class="display"></table>').appendTo(container);
  $('<br><hr>').appendTo(container);
}

// Append list to a target "<select>" container
/*
 * cleanOption: boolean : clean up previous <option> in <select>
 * list: array: data to append
 * targetId: string: <select> id.
 */
function appendToSelect(cleanOption, list, targetId) {
  if (cleanOption == true) {
    cleanSelect(targetId);
  }
  for (var z = 0; z < list.length; z++) {
    $('<option value="' + list[z] + '" selected>' + list[z] + '</option>')
      .appendTo(targetId);
  }
}

//clean up previous <option> in <select>
/*
 * targetId: string: <select> id.
 */
function cleanSelect(targetId) {
  $(targetId).find('option').remove().end();
}


/*
 * Get column array of table to hide from view 
 * metricArray: list: a gadget's metric list
 * isCycleOrAssignee: boolean: column number of Cycle-Assignee and UserStory-Epic table is different.
 * 								put a condition here to check
 */
function getColumnArray(metricArray, isCycleOrAssignee) {
  var columnList = [];

  if (metricArray == null) {
    alert("There is no metric to show.");
    return;
  }
  if (!isCycleOrAssignee) {
    if ($.inArray('SUMMARY', metricArray) == -1) {
      columnList.push(1);
    }
    if ($.inArray('PRIORITY', metricArray) == -1) {
      columnList.push(2);
    }
    if ($.inArray('UNEXECUTED', metricArray) == -1) {
      columnList.push(3);
    }
    if ($.inArray('FAILED', metricArray) == -1) {
      columnList.push(4);
    }
    if ($.inArray('WIP', metricArray) == -1) {
      columnList.push(5);
    }
    if ($.inArray('BLOCKED', metricArray) == -1) {
      columnList.push(6);
    }
    if ($.inArray('PASSED', metricArray) == -1) {
      columnList.push(7);
    }

    if ($.inArray('PLANNED', metricArray) == -1) {
      columnList.push(8);
    }
    if ($.inArray('UNPLANNED', metricArray) == -1) {
      columnList.push(9);
    }
  } else {
    if ($.inArray('UNEXECUTED', metricArray) == -1) {
      columnList.push(1);
    }
    if ($.inArray('FAILED', metricArray) == -1) {
      columnList.push(2);
    }
    if ($.inArray('WIP', metricArray) == -1) {
      columnList.push(3);
    }
    if ($.inArray('BLOCKED', metricArray) == -1) {
      columnList.push(4);
    }
    if ($.inArray('PASSED', metricArray) == -1) {
      columnList.push(5);
    }
  }


  return columnList;
}

function sortSelection(selectId) {
  $(selectId).html($(selectId + " option").sort(function(a, b) {
    return a.text == b.text ? 0 : a.text < b.text ? -1 : 1
  }))
}

function debugAjaxResponse(data) {
  if (data == null) {
    alert("Error: Server returned null response");
    return true;
  } else if (data["type"] == "error") {
    alert("Error: " + data["data"]);
    return true;
  }
  console.log(data);

  return false;
}

window.onerror = function(msg, url, linenumber) {
    alert('Error message: '+msg+'\nURL: '+url+'\nLine Number: '+linenumber);
    return true;
}
