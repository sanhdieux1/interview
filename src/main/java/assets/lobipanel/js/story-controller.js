$("#usProject").change(function() {
  callAjaxOnUsProjectAndRelease();
});

$("#usRelease").change(function() {
  callAjaxOnUsProjectAndRelease();
});

$("#usProduct").change(function() {
  callAjaxOnUsProjectAndRelease();
});

$("#us-add-epic-btn").click(function() {
  var options = $("#usEpicAvailable option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#usEpic").append(options);
  $("#usEpicAvailable option:selected").remove();
  reloadUSList();
});

$("#us-add-all-epic-btn").click(function() {
  addAllEpic();
});

$("#us-remove-epic-btn").click(function() {
  var options = $("#usEpic option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#usEpicAvailable").append(options);
  $("#usEpic option:selected").remove();
  reloadUSList();
});

$("#us-remove-all-epic-btn").click(function() {
  removeAllEpic();
});

$("#us-update-btn").click(
  function() {
    $(this).prop("disabled", true);
    var jsonString = createJsonStringObjectFromUsInputField();
    callAjaxToUpdateUsGadget(jsonString);
  });


$("#usEpicSelectAll").change(function() {
  $("#us-epic-available-div").fadeOut();
  $("#us-epic-available-div").fadeIn();
});

$("#usCheckAllEpic").click(function() {
  if ($(this).prop("checked")) {
    $("#us-epic-container").fadeOut();
    addAllEpic();
  } else {
    $("#us-epic-container").fadeIn();
    callAjaxOnUsProjectAndRelease();
  }
});

$("#usCheckAllStory").click(function() {
  if ($(this).prop("checked")) {
    $("#us-container").fadeOut();
  } else {
    $("#us-container").fadeIn();
    if (!$("#usCheckAllEpic").prop("checked")) {
      callAjaxOnUsProjectAndRelease();
    }
  }
});

function createJsonStringObjectFromUsInputField() {
  var options;
  var values;
  var object = {};
  var jsonString;
  if (null == $("#dashboardId").val()) {
    alert("No valid dashboard id provided.");
    $("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#usProject").val() == null) {
    alert("No project selected");
    $("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#usRelease").val() == null) {
    alert("No release selected");
    $("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#usProduct").val() == null) {
    alert("No product selected");
    $("us-update-btn").prop("disabled", false);
    return;
  } else if ($("#usEpic option").length == 0 && !$("#usCheckAllEpic").prop("checked")) {
    alert('No epic links selected.');
    $("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#usMultiSelect").val() == null && !$("#usCheckAllStory").prop("checked")) {
    alert("No user story selected for the fetched epic links ");
    $("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#usMetricMultiSelect").val() == null) {
	$("#us-update-btn").prop("disabled", false);
    alert("No test metric selected");
    return;
  }

  options = $("#usEpic option");
  values = $.map(options, function(option) {
    return option.value;
  });

  object['id'] = TEST_US_ID;
  object['dashboardId'] = $("#dashboardId").val();
  object['projectName'] = $("#usProject").val();
  object['release'] = $("#usRelease").val();
  object['products'] = [$("#usProduct").val()];
  object['metrics'] = $("#usMetricMultiSelect").val();

  if ($("#usCheckAllEpic").prop("checked")) {
    object["selectAllEpic"] = true;
  } else {
    object['epic'] = values;
  }

  if ($("#usCheckAllStory").prop("checked")) {
    object["selectAllStory"] = true;
  } else {
    object['stories'] = $("#usMultiSelect").val();
  }
  jsonString = JSON.stringify(object);

  return jsonString;
}

function callAjaxToUpdateUsGadget(jsonString) {
  if (null != jsonString && "" != jsonString) {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: 'STORY_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        hideUsTable();
      },
      error: function(xhr, textStatus, error) {
    	  debugError(xhr, textStatus, error);
        $("#us-update-btn").prop("disabled", false);
        showUsTable();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        else{
        	alert("Gadget updated succesfully");
        	TEST_CYCLE_ID = data["data"];
        	drawUsTable(TEST_CYCLE_ID, $("#usMetricMultiSelect").val());
        }
        
      }
    }).always(function(returnMessage) {
      console.log(jsonString);
    });
  }
}

function reloadUSList() {
  if ($("#usCheckAllStory").prop("checked,true")) {
    return;
  } else if ($("#usEpic option").length == 0) {
    cleanSelect("#usMultiSelect");
    return;
  }
  var options = $("#usEpic option")
  var values = $.map(options, function(option) {
    return option.value;
  });
  var jsonString = JSON.stringify(values);
  $.ajax({
    url: "/gadget/getStoryInEpic",
    data: {
      epics: jsonString
    },
    error: function(xhr, textStatus, error) {
    	debugError(xhr, textStatus, error);
    },
    beforeSend: function() {
      $("#usMultiSelect").fadeOut();
      $("#us-us-loader").fadeIn();
    },
    success: function(data) {
      if (debugAjaxResponse(data)) {
        return;
      }

      $('#usMultiSelect').find("option").remove().end();
      $.each(data, function(key, list) {
        for (var i = 0; i < list.length; i++) {
          $(
            '<option value="' + list[i] + '">' + list[i] + '</option>').appendTo(
            '#usMultiSelect');
        }
      })
    }
  }).always(
    function(data) {
      $("#usMultiSelect").fadeIn();
      $("#us-us-loader").fadeOut();
    });
}

function drawUsTable(gadgetId, metricArray) {
	console.log("DRAW US TABLE: "+ gadgetId);
  var columnList = getColumnArray(metricArray, false);
  var jsonObjectForUsTable;
  if (GLOBAL_US_TABLES_AJAX.loading == true && GLOBAL_US_TABLES_AJAX.ajax != null) {
    GLOBAL_US_TABLES_AJAX.ajax.abort();
  }

  GLOBAL_US_TABLES_AJAX.ajax = $.ajax({
    url: "/gadget/getData?",
    method: "GET",
    data: {
      "id": gadgetId
    },
    beforeSend: function() {
      GLOBAL_US_TABLES_AJAX.loading = true;
      hideUsTable();
    },
    error: function(xhr, textStatus, error) {
    	debugError(xhr, textStatus, error);
    },
    success: function(responseData) {
    	if (debugAjaxResponse(responseData)) {
            return;
          }
      $("#us-table-container").html("");
      var index = 0;
      
      jsonObjectForUsTable = responseData;
      console.log(jsonObjectForUsTable["data"]);

      $.each(jsonObjectForUsTable["data"], function(epicKey,
        storyArray) {
        if (storyArray["issueData"].length != 0) {
          var customTableId = "us-table-" + index;
          var usTableDataSet = [];
          var usIndividualTable;
          appendTemplateTable(customTableId, epicKey + ": " + storyArray["summary"],
            "#us-table-container");
          $("#" + customTableId).append(TEMPLATE_HEADER_FOOTER);
          console.log("Pass each function");

          for (var i = 0; i < storyArray['issueData'].length; i++) {
            var aStoryDataSet = [];
            aStoryDataSet.push(storyArray['issueData'][i]["key"]["key"]);
            aStoryDataSet.push(storyArray['issueData'][i]["key"]["summary"]);
            aStoryDataSet.push(storyArray['issueData'][i]["key"]["priority"]["name"]);
            aStoryDataSet.push(storyArray['issueData'][i]["unexecuted"]);
            aStoryDataSet.push(storyArray['issueData'][i]["failed"]);
            aStoryDataSet.push(storyArray['issueData'][i]["wip"]);
            aStoryDataSet.push(storyArray['issueData'][i]["blocked"]);
            aStoryDataSet.push(storyArray['issueData'][i]["passed"]);
            aStoryDataSet.push(storyArray['issueData'][i]["planned"]);
            aStoryDataSet.push(storyArray['issueData'][i]["unplanned"]);
            usTableDataSet.push(aStoryDataSet);
          }

          usIndividualTable = $("#" + customTableId).DataTable({
            bAutoWidth: false,
            data: usTableDataSet,
            columns: [{
              title: "User Story"
            }, {
              title: "SUMMARY"
            }, {
              title: "PRIORITY"
            }, {
              title: "UNEXECUTED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "FAILED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "WIP",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "BLOCKED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "PASSED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "PLANNED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "UNPLANNED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }]
          });
          usIndividualTable.columns(columnList).visible(false);
          index++;
        }
        GLOBAL_US_TABLES_AJAX.loading = false;
      });
    }
  }).always(
    function(responseData) {
      $("#us-update-btn").prop("disabled", false);
      showUsTable();
    });

}

function callAjaxOnUsProjectAndRelease() {
  if ($("#usProject").val() == null || $("#usRelease").val() == null || $("#usProduct").val() == null) {
    return;
  }

  $.ajax({
    url: "/getEpicLinks?",
    data: {

      project: $("#usProject").val(),
      release: $("#usRelease").val(),
      products: JSON.stringify([$("#usProduct").val()])
    },

    beforeSend: function() {
      if (!$("#usCheckAllEpic").prop("checked")) {
        hideUsEpic();
      } else if (!$("#usCheckAllStory").prop("checked")) {
        hideUsStory();
      }

    },
    error: function(xhr, textStatus, error) {
    	debugError(xhr, textStatus, error);
    	if (!$("#usCheckAllEpic").prop("checked")) {
            showUsEpic();
          } else if (!$("#usCheckAllStory").prop("checked")) {
            showUsStory();
          }
    },
    success: function(data) {
      if (debugAjaxResponse(data)) {
        return;
      }
      data.sort();
      if (!$("#usCheckAllEpic").prop("checked")) {
        appendToSelect(true, data, "#usEpicAvailable");
        $('#usEpic').find('option').remove().end();
        showUsEpic();
      } else if ($("#usCheckAllEpic").prop("checked") && !$("#usCheckAllStory").prop("checked")) {
        $('#usEpicAvailable').find('option').remove().end();
        appendToSelect(true, data, "#usEpic");
        reloadUSList();
      }
    }
  });
}

function addEpic() {
  var options = $("#usEpicAvailable option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#usEpic").append(options);
  $("#usEpicAvailable option:selected").remove();
  reloadUSList();
}

function removeEpic() {
  var options = $("#usEpic option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#usEpicAvailable").append(options);
  $("#usEpic option:selected").remove();
  reloadUSList();
}


function addAllEpic() {
  var options = $("#usEpicAvailable option").clone();
  if (options.length == 0) {
    return;
  }
  $("#usEpic").append(options);
  $("#usEpicAvailable option").remove();
  reloadUSList();
}

function removeAllEpic() {
  var options = $("#usEpic option").clone();
  if (options.length == 0) {
    return;
  }
  $("#usEpicAvailable").append(options);
  $("#usEpic option").remove();
  reloadUSList();
}

function showUsEpic() {
  $('#us-epic-loader').fadeOut();
  $("#us-epic-available-div").fadeIn();
}

function hideUsEpic() {
  $("#us-epic-loader").fadeIn();
  $("#us-epic-available-div").fadeOut();
}

function showUsStory() {
  $("#us-us-loader").fadeOut();
  $("#usMultiSelect").fadeIn();
}

function hideUsStory() {
  $("#us-epic-loader").fadeOut();
  $("#us-epic-available-div").fadeIn();
}

function showUsTable() {
  $('#us-table-loader').fadeOut();
  $("#us-table-container").fadeIn();
}

function hideUsTable() {
  $('#us-table-loader').fadeIn();
  $("#us-table-container").fadeOut();
}
