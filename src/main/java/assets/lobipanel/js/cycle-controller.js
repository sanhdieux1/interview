$("#cycleProject").change(function() {
  callAjaxOnCycleProjectAndRelease();

});

$("#cycleRelease").change(function() {
  callAjaxOnCycleProjectAndRelease();
});

$("#cycleProduct").change(function() {
  callAjaxOnCycleProjectAndRelease();
});

$("#cycle-update-btn").click(function() {
  var jsonString = createJsonStringObjectFromCycleInput();
  callAjaxToUpdateCycle(jsonString);
});

$("#cycle-draw-table-btn").click(function() {
  callAjaxOnCycleTable();
});

$("#cycleCheckAll").click(function() {
  if ($(this).prop("checked")) {
    $("#cycle-container").fadeOut();
  } else {
    $("#cycle-container").fadeIn();
  }
});

function createJsonStringObjectFromCycleInput() {

  var object = {};

  if ($("#cycleProject").val() == "" || $("#cycleProject").val() == null || $("#cycleRelease").val() == "" || $("#cycleRelease").val() == null) {
    $("#warning-message").val(
      "Please select project name and release for this gadget.");
    $("dashboard-alert").fadeIn();
    return;
  } else if ($("#cycleProject").val() == null) {
    alert("No Products selected");
  } else if ($("#cycleMultiSelect").val() == null && !$("#cycleCheckAll").prop("checked")) {
    alert("No cycle selected");
    return;
  } else if ($("#cycleMetricMultiSelect").val() == null) {
    alert("No metric selected for this widget");
    return;
  }

  object['projectName'] = $("#cycleProject").val();
  object['release'] = $("#cycleRelease").val();
  object['products'] = [$("#cycleProduct").val()];
  object['metrics'] = $("#cycleMetricMultiSelect").val();
  if ($("#cycleCheckAll").prop("checked")) {
    object['selectAllCycle'] = true;
  } else {
    object['cycles'] = $("#cycleMultiSelect").val();
  }
  var jsonString = JSON.stringify(object);
  return jsonString
}

function callAjaxToUpdateCycle(jsonString) {
  if (jsonString != null && jsonString != "") {
    $.ajax({
      url: '/gadget/addGadget',
      method: 'POST',
      data: {
        type: 'TEST_CYCLE_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        hideCycleTable();
      },
      error: function(res) {
        alert("Error while updating object using Ajax");
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        alert("Gadget updated succesfully");
      }
    }).always(function() {
      console.log(jsonString);
      showCycleTable();
    });
  }
}

function callAjaxOnCycleTable() {
  $.ajax({
    url: '/gadget/gadgets',
    success: function(gadgetList) {
      drawCycleGadget(gadgetList);
    },
    error: function(response) {
      alert("Error while drawing cycle table");
    },
    beforeSend: function() {
      hideCycleTable();
    }
  }).done(function(gadgetList) {
    if (debugAjaxResponse(gadgetList)) {
      return;
    }
    console.log(gadgetList);
  });

}

function drawCycleGadget(gadgetList) {
  for (var i = 0; i < gadgetList.length; i++) {
    if (gadgetList[i]["type"] == "TEST_CYCLE_TEST_EXECUTION") {
      console.log("At gadget List");
      if (gadgetList[i]["projectName"] != "" && gadgetList[i]["projectName"] != null) {
        $("#cycleProject").val(gadgetList[i]["projectName"]);
      }

      if (gadgetList[i]["products"] != "" && gadgetList[i]["products"] != null) {
        $("#cycleProduct").val(gadgetList[i]["products"]);
      }

      if (gadgetList[i]["release"] != "" && gadgetList[i]["release"] != null) {
        $("#cycleRelease").val(gadgetList[i]["release"]);
      }
      if (gadgetList[i]["selectAllCycle"] == true) {
        $("#cycleCheckAll").prop("checked", true);
        $("#cycle-container").fadeOut();
      } else {
        if (gadgetList[i]["cycles"] != "" && gadgetList[i]["cycles"] != null) {
          appendToSelect(true, gadgetList[i]["cycles"],
            "#cycleMultiSelect");
          $("#cycleMultiSelect").val(gadgetList[i]["cycles"]);
        }
      }

      if (gadgetList[i]["metrics"] != "" && gadgetList[i]["metrics"] != null) {
        $("#cycleMetricMultiSelect").val(gadgetList[i]["metrics"]);
      }
      console.log("prepare to draw table");
      drawCycleTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
      break;
    }
  }
}

function drawCycleTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, true);
  if (globalCycleTable != null) {
    console.log(globalCycleTable);
    globalCycleTable.ajax.reload();
  } else {
    $.ajax({
      url: "/gadget/getData",
      data: {
        "id": gadgetId
      },

      beforeSend: function() {
        hideCycleTable();
      }
    }).done(function(gadgetData) {
      if (debugAjaxResponse(gadgetData)) {
        return;
      }
      globalCycleTable = $('#cycle-table').DataTable({
        "fnDrawCallback": function(oSettings) {
          showCycleTable();
        },
        paging: false,
        "ajax": {
          url: "/gadget/getData",
          data: {
            "id": gadgetId
          },
          dataSrc: function(responseJson) {
            if (debugAjaxResponse(responseJson)) {
              return;
            }
            var tempArray = [];
            $.each(responseJson["data"], function(k1, v1) {
              $.each(v1["issueData"], function(k2, v2) {
                tempArray.push(v2);
              });
            });
            console.log(tempArray);
            return tempArray;
          }
        },
        "columns": [{
          "data": "key.key"
        }, {
          "data": "unexecuted"
        }, {
          "data": "failed"
        }, {
          "data": "wip"
        }, {
          "data": "blocked"
        }, {
          "data": "passed"
        }]

      });
      globalCycleTable.columns(columnList).visible(false);
      showCycleTable();
    });
  }
}

function callAjaxOnCycleProjectAndRelease() {
  if ($("#cycleRelease").val() == null || $("#cycleProduct").val() == null || $("#cycleRelease").val() == null) {
    return;
  }
  if (!$("#cycleCheckAll").prop("checked")) {
    $.ajax({
      url: "/listcycle?",
      data: {
        project: $("#cycleProject").val(),
        release: $("#cycleRelease").val(),
        products: JSON.stringify([$("#cycleProduct").val()])
      },

      beforeSend: function() {
        hideCycleSelect();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        data.sort();
        appendToSelect(true, data, "#cycleMultiSelect");
      },
      error: function(data) {
        alert("Error: Failed to get cycle list");
        console.log(data);
      }
    }).always(function() {
      showCycleSelect();
    });
  }
}

function hideCycleSelect() {
  $('#cycleMultiSelect').fadeOut();
  $("#cycle-loader").fadeIn();
}

function showCycleSelect() {
  $('#cycleMultiSelect').fadeIn();
  $("#cycle-loader").fadeOut();
}

function showCycleTable() {
  $('#cycle-table-container').fadeIn();
  $("#cycle-table-loader").fadeOut();
}

function hideCycleTable() {
  $('#cycle-table-container').fadeOut();
  $("#cycle-table-loader").fadeIn();
}
