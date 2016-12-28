// When epic project input change
$("#epicProject").change(function() {
	callAjaxOnEpicProjectAndRelease();
});
// When epic release input change
$("#epicRelease").change(function() {
	callAjaxOnEpicProjectAndRelease();
});
//when Update button for epic gadget is clicked
$("#epic-add-gadget").click(
		function() {
			if ($("#epicProject").val() == ""
					|| $("#epicProject").val() == null
					|| $("#epicRelease").val() == ""
					|| $("#epicRelease").val() == null
					|| $("#epicMultiSelect") == null) {
				$("#warning-message").val(
						"Please select project name for this gadget.");
				$("dashboard-alert").fadeIn();
				alert("Missing configuration details");
			} else {

				var object = {};
				object['cycleName'] = "";
				object['projectName'] = $("#epicProject").val();
				object['release'] = $("#epicRelease").val();
				object['metrics'] = $("#epicMetricMultiSelect").val();
				if ($("#epicCheckAll").prop("checked")) {
					object['selectAll'] = true;
				} else {
					object['epic'] = $("#epicMultiSelect").val();
				}
				var jsonString = JSON.stringify(object);
				$.ajax({
					url : '/gadget/addGadget',
					method : 'POST',
					data : {
						type : 'EPIC_US_TEST_EXECUTION',
						data : jsonString
					},
					beforeSend : function() {
						$('#epic-table-loader').fadeIn();
					}
				}).done(function(data1) {
					$('#epic-table-loader').fadeOut();
				});
			}
		});
// When Epic gadget Draw table button is clicked
$("#epic-get-data").click(function() {
	callAjaxOnEpicTable();
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
		url : '/gadget/gadgets',
		beforeSend : function() {
			hideEpicTable();
		},
		error : function() {
			alert("Failed to execute ajax fetching epic table");
			showEpicTable();
		}
	}).done(function(gadgetList) {
		console.log(gadgetList);
		drawEpicGadget(gadgetList);
	});
}
// Send ajax once project or release input changed on gui
function callAjaxOnEpicProjectAndRelease() {
	if ($("#epicProject").val() == null || $("#epicProject").val() == ""
			|| $("#epicRelease").val() == null || $("#epicRelease").val() == "") {
		return;
	}
	if (!$("#epicCheckAll").prop("checked")) {
		$.ajax({
			url : "/getEpicLinks?",
			data : {
				project : $("#epicProject").val(),
				release : $("#epicRelease").val()
			},

			beforeSend : function() {

				hideEpicLinks();

			}
		}).done(function(data) {
			appendToSelect(true, data, "#epicMultiSelect");
			showEpicLinks();
		});
	}
}
// Draw epic gadget
function drawEpicGadget(gadgetList) {
	for (var i = 0; i < gadgetList.length; i++) {
		if (gadgetList[i]["type"] == "EPIC_US_TEST_EXECUTION") {

			if (gadgetList[i]["projectName"] != ""
					&& gadgetList[i]["projectName"] != null) {
				$("#epicProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["release"] != ""
					&& gadgetList[i]["release"] != null) {
				$("#epicRelease").val(gadgetList[i]["release"]);
			}

			if (gadgetList[i]["metrics"] != ""
					&& gadgetList[i]["metrics"] != null) {
				$("#epicMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}

			if (gadgetList[i]["epic"] != "" && gadgetList[i]["epic"] != null) {
				appendToSelect(true, gadgetList[i]["epic"], "#epicMultiSelect");
				$("#epicMultiSelect").val(gadgetList[i]["epic"]);
			}
			drawEpicTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
			break;
		}
	}
}

function drawEpicTable(gadgetId, metricArray) {
	var columnList = getColumnArray(metricArray, false);
	if (globalEpicTable != null) {
		console.log(globalEpicTable);
		globalEpicTable.api().ajax.reload();
	} else {
		$.ajax({
			url : "/gadget/getData",
			data : {
				"id" : gadgetId
			},

			beforeSend : function() {
				hideEpicTable();
			},
			error : function() {
				alert("Failed to draw Epic table");
			}
		}).done(function(gadgetData) {
			if (gadgetData == null || gadgetData == "") {
				alert("There is no available gadget");
				return;
			}
			globalEpicTable = $('#epic-table').DataTable({
				"fnDrawCallback" : function(oSettings) {
					showEpicTable();
				},
				paging : false,
				"ajax" : {
					url : "/gadget/getData",
					data : {
						"id" : gadgetId
					},
					dataSrc : function(responseJson) {
						var tempArray = [];

						$.each(responseJson["data"], function(k1, v1) {
							$.each(v1, function(k2, v2) {
								tempArray.push(v2);
							});

						});
						console.log(tempArray);
						return tempArray;
					}
				},
				"columns" : [ {
					"data" : "key.key"
				}, {
					"data" : "unexecuted"
				}, {
					"data" : "failed"
				}, {
					"data" : "wip"
				}, {
					"data" : "blocked"
				}, {
					"data" : "passed"
				}, {
					"data" : "planned"
				}, {
					"data" : "unplanned"
				} ]
			});

			globalEpicTable.columns(columnList).visible(false);
			showEpicTable()
		});
	}
}

function showEpicLinks() {
	$('#epicMultiSelect').fadeIn();
	$('#epic-link-loader').fadeOut();
}

function convertEpicLinkToApiIssue(epicList) {
	var issue = [];

	$.each(epicList, function(key, value) {
		issue.push({
			"key" : value,
			self : "",
			parrent : ""
		});
	});
	return issue;
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