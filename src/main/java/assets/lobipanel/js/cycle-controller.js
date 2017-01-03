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
  $(this).prop("disabled", true);
  var jsonString = createJsonStringObjectFromCycleInput();
  callAjaxToUpdateCycle(jsonString);
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
  if(null == $("#dashboardId").val()){
	  alert("No valid dashboard id provided.");
	  return;
  }
  else if ($("#cycleProject").val() == null || $("#cycleRelease").val() == null) {
	  alert("No project or cycle selected");
    return;
  } else if ($("#cycleProduct").val() == null) {
    alert("No Products selected");
  } else if ($("#cycleMultiSelect").val() == null && !$("#cycleCheckAll").prop("checked")) {
    alert("No cycle selected");
    return;
  } else if ($("#cycleMetricMultiSelect").val() == null) {
    alert("No metric selected for this widget");
    return;
  }
  
  object['id'] = TEST_CYCLE_ID;
  object['dashboardId'] = $("#dashboardId").val();
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
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: CYCLE_TYPE,
        data: jsonString
      },
      beforeSend: function() {
        hideCycleTable();
      },
      error: function(res) {
    	$(this).prop("disabled", true);
        alert("Error while updating object using Ajax");
        $("#cycle-update-btn").prop("disabled",false);
        showCycleTable();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        alert("Gadget updated succesfully");
      }
    }).done(function() {
      console.log(jsonString);
    });
  }
}

function callAjaxOnCycleTable() {
  $.ajax({
		  url : GET_GADGETS_URI,
		data : {
			dashboardId : $("#dashboardId").val()
		},
    success: function(gadgetList) {
      drawCycleGadget(gadgetList);
    },
    error: function(response) {
      alert("Error while drawing cycle table");
      $(this).prop("disabled", true);
      showCycleTable();
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
		if (gadgetList[i]["type"] == CYCLE_TYPE) {
			TEST_CYCLE_ID = gadgetList[i]["id"];
			console.log("Prepare to draw table");
			drawCycleTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
			break;
		}
	}
}

function drawCycleTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, true);
  resetTableColumns(GLOBAL_CYCLE_TABLE, true);
  if (GLOBAL_CYCLE_TABLE != null) {
    console.log(GLOBAL_CYCLE_TABLE);
    hideCycleTable();
    GLOBAL_CYCLE_TABLE.ajax.reload(function(){
    	showCycleTable();
    });
    GLOBAL_CYCLE_TABLE.columns(columnList).visible(false);
  } else {
	  hideCycleTable();
      GLOBAL_CYCLE_TABLE = $('#cycle-table').DataTable({
        "fnDrawCallback": function(oSettings) {
          $("#cycle-update-btn").prop("disabled", false);
          showCycleTable();
        },
        bAutoWidth: false,
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
        }]

      });
      GLOBAL_CYCLE_TABLE.columns(columnList).visible(false);
      
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
    }).done(function() {
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
