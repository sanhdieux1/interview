$("#assigneeProject").change(function() {
  callAjaxOnAssigneeProjectAndRelease();
});

$("#assigneeRelease").change(function() {
  callAjaxOnAssigneeProjectAndRelease();
});

$("#assigneeProduct").change(function() {
  callAjaxOnAssigneeProjectAndRelease();
});

$("#assignee-add-cycle-btn").click(function() {
  var options = $("#assigneeCycleAvailable option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycle").append(options);
  $("#assigneeCycleAvailable option:selected").remove();
});

$("#assignee-remove-cycle-btn").click(function() {
  var options = $("#assigneeCycle option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycleAvailable").append(options);
  $("#assigneeCycle option:selected").remove();
});

$("#assignee-add-all-cycle-btn").click(function() {
  var options = $("#assigneeCycleAvailable option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycle").find("option").remove().end();
  $("#assigneeCycle").append(options);
  $("#assigneeCycleAvailable option").remove();
});

$("#assignee-remove-all-cycle-btn").click(function() {
  var options = $("#assigneeCycle option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycleAvailable").find("option").remove().end();
  $("#assigneeCycleAvailable").append(options);
  $("#assigneeCycle option").remove();
});

$("#assignee-update-btn").click(
  function() {
    var object = {};
    var options;
    var values;
    var jsonString;
    if ($("#assigneeProject").val() == "" || $("#assigneeProject").val() == null || $("#assigneeRelease").val() == "" || $("#assigneeRelease").val() == null) {
      $("#warning-message").val(
        "Please select project or release for this gadget.");
      $("dashboard-alert").fadeIn();
      return;
    } else if ($("#assigneeProduct").val() == null) {
      alert("No product selected");
      return;
    } else if ($("#assigneeCycle option").length == 0 && !$("#assigneeCheckAllCycle").prop("checked")) {
      alert("No cycle selected");
      return;
    } else if ($("#assigneeMultiSelect").val() == null && !$("#assigneeCheckAll").prop("checked")) {
      alert("No assignee selected");
      return;
    } else if ($("#assigneeMetricMultiSelect").val() == null) {
      alert("No test metric selected");
      return;
    }
    options = $("#assigneeCycle option");
    values = $.map(options, function(option) {
      return option.value;
    });

    object['projectName'] = $("#assigneeProject").val();
    object['release'] = $("#assigneeRelease").val();
    object['products'] = [$("#assigneeProduct").val()];
    object['metrics'] = $("#assigneeMetricMultiSelect").val();

    if ($("#assigneeCheckAllCycle").prop("checked")) {
      object['selectAllCycle'] = true;
    } else {
      object['cycles'] = values;
    }
    if ($("#assigneeCheckAll").prop("checked")) {
      object['selectAllAssignee'] = true;
    } else {
      object['assignee'] = $("#assigneeMultiSelect").val();
    }

    jsonString = JSON.stringify(object);
    $.ajax({
      url: '/gadget/addGadget',
      method: 'POST',
      data: {
        type: 'ASSIGNEE_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        hideAssigneeTable();
      },
      error: function(res) {
        alert("Error while updating object using Ajax");
        console.log(res);
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        alert("Gadget updated succesfully");
      }
    }).done(function(returnMessage) {

      console.log(jsonString);
      showAssigneeTable();
    });
  });

$("#assignee-draw-table-btn").click(function() {
  callAjaxOnAssigneeTable();
});

$("#assigneeCheckAll").click(function() {
  if ($(this).prop("checked")) {
    $("#assignee-container").fadeOut();
  } else {
    $("#assignee-container").fadeIn();
  }
});

$("#assigneeCheckAllCycle").click(function() {
  if ($(this).prop("checked")) {
    $("#assignee-cycle-available-div").fadeOut();
  } else {
    $("#assignee-cycle-available-div").fadeIn();
  }
});

function drawAssigneeGadget(gadgetList) {

  for (var i = 0; i < gadgetList.length; i++) {
    if (gadgetList[i]["type"] == "ASSIGNEE_TEST_EXECUTION") {
      console.log("At gadget List");
      if (gadgetList[i]["projectName"] != "" && gadgetList[i]["projectName"] != null) {
        $("#assigneeProject").val(gadgetList[i]["projectName"]);
      }

      if (gadgetList[i]["release"] != "" && gadgetList[i]["release"] != null) {
        $("#assigneeRelease").val(gadgetList[i]["release"]);
      }

      if (gadgetList[i]["products"] != "" && gadgetList[i]["products"] != null) {
        $("#assigneeProduct").val(gadgetList[i]["products"]);
      }

      if (gadgetList[i]["assignee"] != "" && gadgetList[i]["assignee"] != null) {
        appendToSelect(true, gadgetList[i]["assignee"],
          "#assigneeMultiSelect");
        $("#assigneeMultiSelect").val(gadgetList[i]["assignee"]);
      }

      if (gadgetList[i]["metrics"] != "" && gadgetList[i]["metrics"] != null) {
        $("#assigneeMetricMultiSelect").val(gadgetList[i]["metrics"]);
      }
      if (gadgetList[i]["cycles"] != "" && gadgetList[i]["cycles"] != null) {
        appendToSelect(true, gadgetList[i]["cycles"], "#assigneeCycle");
        $("#assigneeCycle").val(gadgetList[i]["cycles"]);
      }
      console.log("prepare to draw table");
      drawAssigneeTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
      break;
    }
  }
}

function drawAssigneeTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, true);
  $("#assignee-table-container").html("");

  var jsonObjectForAssigneeTable;
  $
    .ajax({
      url: "/gadget/getData?",
      method: "GET",
      data: {
        id: gadgetId
      },
      beforeSend: function() {
        hideAssigneeTable();
      },
      error: function(response) {
        alert("Failed to draw table");
        console.log(response);
        showAssigneeTable();
      }
    })
    .done(
      function(responseData) {
        var index = 0;
        if (debugAjaxResponse(responseData)) {
          return;
        }
        console.log("Get individual data ok");

        jsonObjectForAssigneeTable = responseData;
        $
          .each(
            jsonObjectForAssigneeTable["data"],
            function(cycleKey, assigneeArray) {
              console
                .log(assigneeArray["issueData"].length);
              if (assigneeArray["issueData"].length != 0) {
                var customTableId = "assignee-table-" + index;
                var assigneeTableDataSet = [];
                var assigneeIndividualTable;

                appendTemplateTable(
                  customTableId,
                  cycleKey,
                  "#assignee-table-container");
                $("#" + customTableId).append(
                  templateHeaderFooter1);

                console
                  .log("Pass each function");

                for (var i = 0; i < assigneeArray["issueData"].length; i++) {
                  var anAssigneeDataSet = [];
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["key"]["key"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["unexecuted"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["failed"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["wip"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["blocked"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["passed"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["planned"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["unplanned"]);
                  assigneeTableDataSet
                    .push(anAssigneeDataSet);
                }

                assigneeIndividualTable = $(
                    "#" + customTableId)
                  .DataTable({
                    paging: false,
                    data: assigneeTableDataSet,
                    columns: [{
                      title: "Assignee"
                    }, {
                      title: "UNEXECUTED"
                    }, {
                      title: "FAILED"
                    }, {
                      title: "WIP"
                    }, {
                      title: "BLOCKED"
                    }, {
                      title: "PASSED"
                    }]
                  });
                assigneeIndividualTable
                  .columns(columnList)
                  .visible(false);
                index++;
              }
            });
        showAssigneeTable();

      });
}

function callAjaxOnAssigneeTable() {
  $.ajax({
    url: '/gadget/gadgets',
    success: function(gadgetList) {
      if (debugAjaxResponse(gadgetList)) {
        return;
      }
      drawAssigneeGadget(gadgetList);
    },
    error: function(response) {
      alert("Error while drawing assignee table");
    },
    beforeSend: function() {
      hideAssigneeTable();
    }
  }).done(function(gadgetList) {
    console.log(gadgetList);
  });
}

function callAjaxOnAssigneeProjectAndRelease() {
  if ($("#assigneeRelease").val() == null) {
    return;
  } else if ($("#assigneeProject").val() == null) {
    return;
  } else if ($("#assigneeProduct").val() == null) {
    return;
  }

  if (!$("#assigneeCheckAllCycle").prop("checked")) {
    $.ajax({
      url: "/listcycle?",
      data: {
        project: $("#assigneeProject").val(),
        release: $("#assigneeRelease").val(),
        products: JSON.stringify([$("#assigneeProduct").val()])
      },

      beforeSend: function() {
        hideAssigneeCycle();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        data.sort();
        appendToSelect(true, data, "#assigneeCycleAvailable");
        $("#assigneeCycle").find("option").remove().end();
      },
      error: function(data) {
        alert("Error : Cant fetch cycle");
        console.log(data);
      }
    }).always(function() {
      showAssigneeCycle();
    });
  }
  if (!$("#assigneeCheckAll").prop("checked")) {

    $.ajax({
      url: "/getassignee?",
      data: {
        project: $("#assigneeProject").val(),
        release: $("#assigneeRelease").val()
      },

      beforeSend: function() {
        hideAssignee();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        var tempAssigneeList = [];
        $.each(data, function(key, map) {
          tempAssigneeList.push(map["assignee"]);
        });
        tempAssigneeList.sort();
        appendToSelect(true, tempAssigneeList, "#assigneeMultiSelect");
      },
      error: function(data) {
        alert("Error fetching assignee list");
        console.log(data);
      }
    }).always(function() {
      showAssignee();
    });
  }
}

function addAllCycle() {
  var options = $("#assigneeCycleAvailable option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycle").append(options);
  $("#assigneeCycleAvailable option").remove();
}

function removeAllCycle() {
  var options = $("#assigneeCycle option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycleAvailable").append(options);
  $("#assigneeCycle option").remove();
}

function hideAssigneeCycle() {
  $('#assignee-cycle-available-div').fadeOut();
  $("#assignee-cycle-loader").fadeIn();
}

function showAssigneeCycle() {
  $('#assignee-cycle-available-div').fadeIn();
  $("#assignee-cycle-loader").fadeOut();
}

function hideAssignee() {
  $('#assigneeMultiSelect').fadeOut();
  $("#assignee-loader").fadeIn();
}

function showAssignee() {
  $('#assigneeMultiSelect').fadeIn();
  $("#assignee-loader").fadeOut();
}

function hideAssigneeTable() {
  $('#assignee-table-container').fadeOut();
  $("#assignee-table-loader").fadeIn();
}

function showAssigneeTable() {
  $('#assignee-table-container').fadeIn();
  $("#assignee-table-loader").fadeOut();
}
