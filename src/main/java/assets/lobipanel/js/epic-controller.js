/*
 * This script deals with epic controller, setting up jquery on change, click, etc...
 */

// When epic project input is changed
$("#epicProject").change(function() {
  callAjaxOnEpicProjectAndRelease();
});
// When epic release input is changed
$("#epicRelease").change(function() {
  callAjaxOnEpicProjectAndRelease();
});

$("#epicProduct").change(function() {
  callAjaxOnEpicProjectAndRelease();
});

// when Update button for epic gadget is clicked
$("#epic-add-gadget").click(
  function() {
    var jsonString = createJsonStringObjectFromEpicInput();
    callAjaxToUpdateGadget(jsonString);
  });
// When Epic gadget Draw table button is clicked
$("#epic-get-data").click(function() {
	if(TEST_EPIC_ID != null){
		drawEpicTable(TEST_EPIC_ID, $("#epicMetricMultiSelect").val());
	}
	else{
		callAjaxOnEpicTable();
	}
});
// When epic gadget "select all" is clicked
$("#epicCheckAll").click(function() {
  if ($(this).prop("checked")) {
    $("#epic-link-container").fadeOut();
  } else {
    $("#epic-link-container").fadeIn();
  }
});
// Send ajax to get a list of epic gadgets
function callAjaxOnEpicTable() {
  $.ajax({
    url: GET_GADGETS_URI,
    data:{
    	dashboardId: $("#dashboardId").val()
    },
    beforeSend: function() {
      hideEpicTable();
    },
    error: function() {
      alert("Failed to execute ajax fetching epic table");
      showEpicTable();
    },
    success: function(gadgetList) {
      if (debugAjaxResponse(gadgetList)) {
        return;
      }
      console.log(gadgetList);
      drawEpicGadget(gadgetList);
    }
  });
}

function createJsonStringObjectFromEpicInput() {
  var object = {};
  if(null == $("#dashboardId").val()){
	  alert("No valid dashboard id provided.");
	  return;
  }
  else if ($("#epicProject").val() == null) {
    alert("No project selected");
    return;
  } else if ($("#epicRelease").val() == null) {
    alert("No release selected");
    return;
  } else if ($("#epicProduct").val() == null) {
    alert("No release selected");
    return;
  } else if ($("#epicMultiSelect").val() == null && !$("#epicCheckAll").prop("checked")) {
    alert("No epic links selected");
    return;
  } else if ($("#epicMetricMultiSelect") == null) {
    alert("No test metric selected");
    return;
  }
  object['id'] = TEST_EPIC_ID;
  object['dashboardId'] = $("#dashboardId").val();
  object['projectName'] = $("#epicProject").val();
  object['release'] = $("#epicRelease").val();
  object['products'] = [$("#epicProduct").val()];
  object['metrics'] = $("#epicMetricMultiSelect").val();

  if ($("#epicCheckAll").prop("checked")) {
    object['selectAll'] = true;
    object['epic'] = null;
  } else {
    object['epic'] = $("#epicMultiSelect").val();
  }
  return JSON.stringify(object);
}

function callAjaxToUpdateGadget(jsonString) {
  if (jsonString != null && jsonString != "") {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: 'EPIC_US_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        $('#epic-table-loader').fadeIn();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        alert("Gadget updated succesfully");
      }
    }).done(function() {
      $('#epic-table-loader').fadeOut();
    });
  }
}

// Send ajax once project or release input changed on gui
function callAjaxOnEpicProjectAndRelease() {
  if ($("#epicProject").val() == null || $("#epicRelease").val() == null || $("#epicProduct").val() == null) {
    return;
  }

  if (!$("#epicCheckAll").prop("checked")) {
    $.ajax({
      url: "/getEpicLinks?",
      data: {
        project: $("#epicProject").val(),
        release: $("#epicRelease").val(),
        products: JSON.stringify([$("#epicProduct").val()])
      },

      beforeSend: function() {
        hideEpicLinks();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        data.sort();
        appendToSelect(true, data, "#epicMultiSelect");
      }
    }).done(function(data) {
      showEpicLinks();
    });
  }
}
// Draw epic gadget
function drawEpicGadget(gadgetList) {
  for (var i = 0; i < gadgetList.length; i++) {
    if (gadgetList[i]["type"] == "EPIC_US_TEST_EXECUTION") {

      if (gadgetList[i]["projectName"] != "" && gadgetList[i]["projectName"] != null) {
        $("#epicProject").val(gadgetList[i]["projectName"]);
      }

      if (gadgetList[i]["products"] != null) {
        $("#epicProduct").val(gadgetList[i]["products"]);
      }


      if (gadgetList[i]["release"] != "" && gadgetList[i]["release"] != null) {
        $("#epicRelease").val(gadgetList[i]["release"]);
      }

      if (gadgetList[i]["metrics"] != "" && gadgetList[i]["metrics"] != null) {
        $("#epicMetricMultiSelect").val(gadgetList[i]["metrics"]);
      }
      if (gadgetList[i]["selectAll"] == true) {
        $("epicCheckAll").prop("checked,true");
        hideEpicLinks();
      } else if (gadgetList[i]["epic"] != "" && gadgetList[i]["epic"] != null) {
        appendToSelect(true, gadgetList[i]["epic"], "#epicMultiSelect");
        $("#epicMultiSelect").val(gadgetList[i]["epic"]);
      }
      console.log(gadgetList[i]["id"]);
      drawEpicTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
      break;
    }
  }
}

function drawEpicTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, false);
  if (GLOBAL_EPIC_TABLE != null) {
    console.log(GLOBAL_EPIC_TABLE);
    GLOBAL_EPIC_TABLE.ajax.reload();
  } else {
    $.ajax({
      url: "/gadget/getData",
      data: {
        "id": gadgetId
      },

      beforeSend: function() {
        hideEpicTable();
      },
      error: function() {
        alert("Failed to draw Epic table");
      },
      success: function(gadgetData) {

        if (gadgetData == null || gadgetData == "") {
          alert("There is no available gadget");
          return;
        }
        GLOBAL_EPIC_TABLE = $('#epic-table').DataTable({
          "fnDrawCallback": function(oSettings) {
            showEpicTable();
          },
          paging: false,
          "ajax": {
            url: "/gadget/getData",
            data: {
              "id": gadgetId
            },
            dataSrc: function(responseJson) {
              var tempArray = [];
              if (debugAjaxResponse(responseJson)) {
                return;
              }

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
            "data": "key.summary"
          }, {
            "data": "key.priority.name",
            },
          {
            "data": "unexecuted",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "failed",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "wip",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "blocked",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "passed",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "planned",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "unplanned",
            "render": function(data, displayOrType, rowData, setting){
            	return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }]
        });
        GLOBAL_EPIC_TABLE.columns(columnList).visible(false);
      }
    }).done(function() {
      showEpicTable();
    });
  }
}

function showEpicLinks() {
  $('#epicMultiSelect').fadeIn();
  $('#epic-link-loader').fadeOut();
}

function showEpicTable() {
  $('#epic-table-container').fadeIn();
  $('#epic-table-loader').fadeOut();
}

function hideEpicTable() {
  $('#epic-table-container').fadeOut();
  $('#epic-table-loader').fadeIn();
}

function hideEpicLinks() {
  $('#epicMultiSelect').fadeOut();
  $('#epic-link-loader').fadeIn();
}
