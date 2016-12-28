$("#assigneeProject").change(function() {
	callAjaxOnAssigneeProjectAndRelease();
});

$("#assigneeRelease").change(function() {
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
			if ($("#assigneeProject").val() == ""
					|| $("#assigneeProject").val() == null
					|| $("#assigneeRelease").val() == ""
					|| $("#assigneeRelease").val() == null) {
				$("#warning-message").val(
						"Please select project name for this gadget.");
				$("dashboard-alert").fadeIn();
				return;
			}
			if ($("#assigneeCycle option").length == 0) {
				return;
			}
			options = $("#assigneeCycle option");
			values = $.map(options, function(option) {
				return option.value;
			});

			object['projectName'] = $("#assigneeProject").val();
			object['release'] = $("#assigneeRelease").val();
			object['metrics'] = $("#assigneeMetricMultiSelect").val();
			object['cycles'] = values;
			object['assignee'] = $("#assigneeMultiSelect").val();
			var jsonString = JSON.stringify(object);
			$.ajax({
				url : '/gadget/addGadget',
				method : 'POST',
				data : {
					type : 'ASSIGNEE_TEST_EXECUTION',
					data : jsonString
				},
				beforeSend : function() {
					hideAssigneeTable();
				},
				error : function(res) {
					alert("Error while updating object using Ajax");
				},
				success : function(data) {
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
			if (gadgetList[i]["projectName"] != ""
					&& gadgetList[i]["projectName"] != null) {
				$("#assigneeProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["release"] != ""
					&& gadgetList[i]["release"] != null) {
				$("#assigneeRelease").val(gadgetList[i]["release"]);
			}

			if (gadgetList[i]["assignee"] != ""
					&& gadgetList[i]["assignee"] != null) {
				appendToSelect(true, gadgetList[i]["assignee"],
						"#assigneeMultiSelect");
				$("#assigneeMultiSelect").val(gadgetList[i]["assignee"]);
			}

			if (gadgetList[i]["metrics"] != ""
					&& gadgetList[i]["metrics"] != null) {
				$("#assigneeMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}
			if (gadgetList[i]["cycles"] != ""
					&& gadgetList[i]["cycles"] != null) {
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
	$.ajax({
		url : "/gadget/getData?",
		method : "GET",
		data : {
			id : gadgetId
		},
		beforeSend : function() {
			hideAssigneeTable();
		},
		error : function(response) {
			alert("Failed to draw table");
			console.log(response);
			showAssigneeTable();
		}
	}).done(
			function(responseData) {
				var index = 0;

				console.log("Get individual data ok");
				jsonObjectForAssigneeTable = responseData;

				if (jsonObjectForAssigneeTable["data"] == null) {
					alert("There is no gadget in database.")
					return;
				}
				$.each(jsonObjectForAssigneeTable["data"], function(cycleKey,
						assigneeArray) {
					var customTableId = "assignee-table-" + index;
					var assigneeTableDataSet = [];
					var assigneeIndividualTable;

					appendTemplateTable(customTableId, cycleKey,
							"#assignee-table-container");
					$("#" + customTableId).append(templateHeaderFooter1);

					console.log("Pass each function");

					for (var i = 0; i < assigneeArray.length; i++) {
						var anAssigneeDataSet = [];
						anAssigneeDataSet.push(assigneeArray[i]["key"]["key"]);
						anAssigneeDataSet.push(assigneeArray[i]["unexecuted"]);
						anAssigneeDataSet.push(assigneeArray[i]["failed"]);
						anAssigneeDataSet.push(assigneeArray[i]["wip"]);
						anAssigneeDataSet.push(assigneeArray[i]["blocked"]);
						anAssigneeDataSet.push(assigneeArray[i]["passed"]);
						anAssigneeDataSet.push(assigneeArray[i]["planned"]);
						anAssigneeDataSet.push(assigneeArray[i]["unplanned"]);
						assigneeTableDataSet.push(anAssigneeDataSet);
					}

					assigneeIndividualTable = $("#" + customTableId).DataTable(
							{
								paging : false,
								data : assigneeTableDataSet,
								columns : [ {
									title : "Assignee"
								}, {
									title : "UNEXECUTED"
								}, {
									title : "FAILED"
								}, {
									title : "WIP"
								}, {
									title : "BLOCKED"
								}, {
									title : "PASSED"
								} ]
							});
					assigneeIndividualTable.columns(columnList).visible(false);
					index++;
				});
				showAssigneeTable();
			});
}

function callAjaxOnAssigneeTable() {
	$.ajax({
		url : '/gadget/gadgets',
		success : function(gadgetList) {
			drawAssigneeGadget(gadgetList);
		},
		error : function(response) {
			alert("Error while drawing assignee table");
		},
		beforeSend : function() {
			hideAssigneeTable();
		}
	}).done(function(gadgetList) {
		console.log(gadgetList);
	});
}

function callAjaxOnAssigneeProjectAndRelease() {
	if ($("#assigneeRelease").val() == null
			|| $("#assigneeRelease").val() == "") {
		return;
	}

	if ($("#assigneeProject").val() == null
			|| $("#assigneeProject").val() == "") {
		return;
	}

	$.ajax({
		url : "/listcycle?",
		data : {
			project : $("#assigneeProject").val(),
			release : $("#assigneeRelease").val()
		},

		beforeSend : function() {
			hideAssigneeCycle();
		},
		success : function(data) {
			if (data == null) {
				console.log(data);
				alert("Data fetched to cycle list is null");
				return;
			}
			appendToSelect(true, data, "#assigneeCycleAvailable");
			$("#assigneeCycle").find("option").remove().end();
		},
		error : function(data) {
			alert("Error fetching cycle");
			console.log(data);
		}
	}).always(function() {
		showAssigneeCycle();
	});

	$.ajax({
		url : "/getassignee?",
		data : {
			project : $("#assigneeProject").val(),
			release : $("#assigneeRelease").val()
		},

		beforeSend : function() {
			hideAssignee();
		},
		success : function(data) {
			var tempAssigneeList = [];

			if (data == null) {
				console.log(data);
				alert("Data fetched to assignee list is null");
				return;
			}
			$.each(data, function(key, map) {
				tempAssigneeList.push(map["assignee"]);
			});
			appendToSelect(true, tempAssigneeList, "#assigneeMultiSelect");
		},
		error : function(data) {
			alert("Error fetching assignee list");
			console.log(data);
		}
	}).always(function() {
		showAssignee();
	});
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