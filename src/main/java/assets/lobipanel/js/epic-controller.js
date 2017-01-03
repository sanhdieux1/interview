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
$("#epic-add-gadget").click(function() {
	$(this).prop("disabled",true);
	var jsonString = createJsonStringObjectFromEpicInput();
	callAjaxToUpdateGadget(jsonString);
	callAjaxOnEpicTable();
	
});

// When epic gadget "select all" is clicked
$("#epicCheckAll").click(function() {
	if ($(this).prop("checked")) {
		$("#epic-link-container").fadeOut();
	} else {
		$("#epic-link-container").fadeIn();
		callAjaxOnEpicProjectAndRelease();
	}
});
// Send ajax to get a list of epic gadgets
function callAjaxOnEpicTable() {
	$.ajax({
		url : GET_GADGETS_URI,
		data : {
			dashboardId : $("#dashboardId").val()
		},
		beforeSend : function() {
			hideEpicTable();
		},
		error : function(xhr, textStatus, error) {
			debugError(xhr, textStatus, error);
			$("#epic-add-gadget").prop("disabled",false);
			showEpicTable();
		},
		success : function(gadgetList) {
			if (debugAjaxResponse(gadgetList)) {
				return;
			}
			console.log(gadgetList);
			drawEpicGadget(gadgetList);
		}
	});
}

//Send ajax once project or release input changed on gui
function callAjaxOnEpicProjectAndRelease() {
	if ($("#epicProject").val() == null || $("#epicRelease").val() == null
			|| $("#epicProduct").val() == null) {
		return;
	}

	if (!$("#epicCheckAll").prop("checked")) {
		$.ajax({
			url : "/getEpicLinks?",
			data : {
				project : $("#epicProject").val(),
				release : $("#epicRelease").val(),
				products : JSON.stringify([ $("#epicProduct").val() ])
			},

			beforeSend : function() {
				hideEpicLinks();
			},
			success : function(data) {
				if (debugAjaxResponse(data)) {
					return;
				}
				data.sort();
				appendToSelect(true, data, "#epicMultiSelect");
			}
		}).always(function(data) {
			showEpicLinks();
		});
	}
}

function createJsonStringObjectFromEpicInput() {
	var object = {};
	if (null == $("#dashboardId").val()) {
		alert("No valid dashboard id provided.");
		return;
	} else if ($("#epicProject").val() == null) {
		alert("No project selected");
		return;
	} else if ($("#epicRelease").val() == null) {
		alert("No release selected");
		return;
	} else if ($("#epicProduct").val() == null) {
		alert("No release selected");
		return;
	} else if ($("#epicMultiSelect").val() == null
			&& !$("#epicCheckAll").prop("checked")) {
		alert("No epic links selected");
		return;
	} else if ($("#epicMetricMultiSelect") == null) {
		alert("No test metric selected");
		return;
	}
	object['id'] = TEST_EPIC_ID;
	console.log("TEST_EPIC_ID: " + TEST_EPIC_ID);
	object['dashboardId'] = $("#dashboardId").val();
	object['projectName'] = $("#epicProject").val();
	object['release'] = $("#epicRelease").val();
	object['products'] = [ $("#epicProduct").val() ];
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
			url : SAVE_GADGET_URI,
			method : 'POST',
			data : {
				type : 'EPIC_US_TEST_EXECUTION',
				data : jsonString
			},
			success : function(data) {
				if (debugAjaxResponse(data)) {
					return;
				}
				alert("Gadget updated succesfully");
			},
			error: function(xhr, textStatus, error){
				 debugError(xhr, textStatus, error);
				$("#epic-add-gadget").prop("disabled",false);
			}
		});
	}
}

// Draw epic gadget
function drawEpicGadget(gadgetList) {
	for (var i = 0; i < gadgetList.length; i++) {
		if (gadgetList[i]["type"] == EPIC_TYPE) {
			TEST_EPIC_ID = gadgetList[i]["id"];
			console.log(gadgetList[i]["id"]);
			drawEpicTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
			break;
		}
	}
}

function drawEpicTable(gadgetId, metricArray) {
	var columnList = getColumnArray(metricArray, false);
	resetTableColumns(GLOBAL_EPIC_TABLE, false);
	if (GLOBAL_EPIC_TABLE != null) {
		console.log(GLOBAL_EPIC_TABLE);
		hideEpicTable();
		GLOBAL_EPIC_TABLE.ajax.reload(function() {
			showEpicTable();
		});
		GLOBAL_EPIC_TABLE.columns(columnList).visible(false);
	} else {
		hideEpicTable();
		GLOBAL_EPIC_TABLE = $('#epic-table').DataTable(
				{
					"fnDrawCallback" : function(oSettings) {
						$("#epic-add-gadget").prop("disabled", false);
						showEpicTable();
					},
					bAutoWidth : false,
					"ajax" : {
						url : "/gadget/getData",
						data : {
							"id" : gadgetId
						},
						dataSrc : function(responseJson) {
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
					"columns" : [
							{
								"data" : "key.key"
							},
							{
								"data" : "key.summary"
							},
							{
								"data" : "key.priority.name",
							},
							{
								"data" : "unexecuted",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							},
							{
								"data" : "failed",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							},
							{
								"data" : "wip",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							},
							{
								"data" : "blocked",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							},
							{
								"data" : "passed",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							},
							{
								"data" : "planned",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							},
							{
								"data" : "unplanned",
								"render" : function(data, displayOrType,
										rowData, setting) {
									return createIssueLinks(data,
											displayOrType, rowData, setting);
								}
							} ]
				});
		GLOBAL_EPIC_TABLE.columns(columnList).visible(false);
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
