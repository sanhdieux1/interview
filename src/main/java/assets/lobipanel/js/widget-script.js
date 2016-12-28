var globalEpicTable = null;
var globalUsTable = null;
var templateHeaderFooter = "<thead><tr><th>User Story</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></thead><tfoot><tr><th>User Story</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></tfoot>";
var templateHeaderFooter1 = "<thead><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></thead><tfoot><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></tfoot>";
var globalCycleTable = null;
var globalAssigneeTable = null;
$(document).ready(function() {
	if ($('#epicProject').length != 0) {
		$.get("/listproject", function(data) {
			appendToSelect(false, data, "#epicProject");
			appendToSelect(false, data, "#usProject");
			appendToSelect(false, data, "#cycleProject");
			appendToSelect(false, data, "#assigneeProject");
		});
	}
});

$(function() {
	$.ajaxSetup({
		error : function(jqXHR, exception) {
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

$("#epicProject").change(function() {
	callAjaxOnEpicProjectAndRelease();
});

$("#epicRelease").change(function() {
	callAjaxOnEpicProjectAndRelease();
});

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

$("#epic-get-data").click(function() {
	callAjaxOnEpicTable();
});

$("#usProject").change(function() {
	if ($("#usRelease").val() == null || $("#usRelease").val() == "") {
		return;
	}
	callAjaxOnUsProjectAndRelease();

});

$("#usRelease").change(function() {
	if ($("#usProject").val() == null || $("#usProject").val() == "") {
		return;
	}
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
	var options = $("#usEpicAvailable option").clone();
	if (options.length == 0) {
		return;
	}
	$("#usEpic").append(options);
	$("#usEpicAvailable option").remove();
	reloadUSList();
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
	var options = $("#usEpic option").clone();
	if (options.length == 0) {
		return;
	}
	$("#usEpicAvailable").append(options);
	$("#usEpic option").remove();
	reloadUSList();
});

function reloadUSList() {
	if ($("#usEpic option").length == 0) {
		return;
	}
	var options = $("#usEpic option")
	var values = $.map(options, function(option) {
		return option.value;
	});
	var jsonString = JSON.stringify(values);
	$.ajax({
		url : "/gadget/getStoryInEpic",
		data : {
			epics : jsonString
		},
		beforeSend : function() {
			$("#usMultiSelect").fadeOut();
			$("#us-us-loader").fadeIn();
		}
	}).done(
			function(data) {
				if (data == null || data.length == 0) {
					return;
				}

				$('#usMultiSelect').find("option").remove().end();
				$.each(data, function(key, list) {
					for (var i = 0; i < list.length; i++) {
						$(
								'<option value="' + list[i] + '">' + list[i]
										+ '</option>').appendTo(
								'#usMultiSelect');
					}
				})

				$("#usMultiSelect").fadeIn();
				$("#us-us-loader").fadeOut();
			});
}

$("#us-update-btn").click(
		function() {
			if ($("#usProject").val() == "" || $("#usProject").val() == null
					|| $("#usRelease").val() == ""
					|| $("#usRelease").val() == null) {
				$("#warning-message").val(
						"Please select project name for this gadget.");
				$("dashboard-alert").fadeIn();
				return;
			}
			if ($("#usEpic option").length == 0) {
				return;
			}
			var options = $("#usEpic option");
			var values = $.map(options, function(option) {
				return option.value;
			});

			var object = {};
			object['projectName'] = $("#usProject").val();
			object['release'] = $("#usRelease").val();
			object['metrics'] = $("#usMetricMultiSelect").val();
			object['epic'] = values;
			object['stories'] = $("#usMultiSelect").val();
			var jsonString = JSON.stringify(object);
			$.ajax({
				url : '/gadget/addGadget',
				method : 'POST',
				data : {
					type : 'STORY_TEST_EXECUTION',
					data : jsonString
				},
				beforeSend : function() {
					hideUsTable();
				},
				error : function(res) {
					alert("Error while updating object using Ajax");
				},
				success : function(data) {
					alert("Gadget updated succesfully");
				}
			}).done(function(returnMessage) {
				console.log(jsonString);
				showUsTable();
			});
		});

$("#us-draw-table-btn").click(function() {
	callAjaxOnUsTable();
});

function callAjaxOnUsTable() {
	$.ajax({
		url : '/gadget/gadgets',
		success : function(gadgetList) {
			drawUsGadget(gadgetList);
		},
		error : function(response) {
			alert("Error while drawing user story table");
		},
		beforeSend : function() {
			hideUsTable();
		}
	}).done(function(gadgetList) {
		console.log(gadgetList);
	});

}

$("#usEpicSelectAll").change(function() {
	$("#us-epic-available-div").fadeOut();
	$("#us-epic-available-div").fadeIn();
});

function drawUsGadget(gadgetList) {

	for (var i = 0; i < gadgetList.length; i++) {
		if (gadgetList[i]["type"] == "STORY_TEST_EXECUTION") {
			console.log("At gadget List");
			if (gadgetList[i]["projectName"] != ""
					&& gadgetList[i]["projectName"] != null) {
				$("#usProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["release"] != ""
					&& gadgetList[i]["release"] != null) {
				$("#usRelease").val(gadgetList[i]["release"]);
			}

			if (gadgetList[i]["stories"] != ""
					&& gadgetList[i]["stories"] != null) {
				appendToSelect(true, gadgetList[i]["stories"], "#usMultiSelect");
				$("#usMultiSelect").val(gadgetList[i]["stories"]);
			}

			if (gadgetList[i]["metrics"] != ""
					&& gadgetList[i]["metrics"] != null) {
				$("#usMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}
			if (gadgetList[i]["epic"] != "" && gadgetList[i]["epic"] != null) {
				appendToSelect(true, gadgetList[i]["epic"], "#usEpic");
				$("#usEpic").val(gadgetList[i]["epic"]);
			}
			console.log("prepare to draw table");
			drawUsTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
			break;
		}
	}
}

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

function drawUsTable(gadgetId, metricArray) {
	var columnList = getColumnArray(metricArray, false);
	var jsonObjectForUsTable;

	$("#us-table-container").html("");
	$.ajax({
		url : "/gadget/getData?",
		method : "GET",
		data : {
			id : gadgetId
		},
		beforeSend : function() {
			hideUsTable();
		},
		error : function(response) {
			alert("Failed to draw table");
			console.log(response);
			showUsTable();
		}
	}).done(function(responseData) {
		var index = 0;

		console.log("Get individual data ok");
		jsonObjectForUsTable = responseData;
		if (jsonObjectForUsTable["data"] == null) {
			alert("There is no gadget in database.")
			return;
		}
		$.each(jsonObjectForUsTable["data"], function(epicKey, storyArray) {
			var customTableId = "us-table-" + index;
			var usTableDataSet = [];
			var usIndividualTable;
			appendTemplateTable(customTableId, epicKey, "#us-table-container");
			$("#" + customTableId).append(templateHeaderFooter);
			console.log("Pass each function");

			for (var i = 0; i < storyArray.length; i++) {
				var aStoryDataSet = [];
				aStoryDataSet.push(storyArray[i]["key"]["key"]);
				aStoryDataSet.push(storyArray[i]["unexecuted"]);
				aStoryDataSet.push(storyArray[i]["failed"]);
				aStoryDataSet.push(storyArray[i]["wip"]);
				aStoryDataSet.push(storyArray[i]["blocked"]);
				aStoryDataSet.push(storyArray[i]["passed"]);
				aStoryDataSet.push(storyArray[i]["planned"]);
				aStoryDataSet.push(storyArray[i]["unplanned"]);
				usTableDataSet.push(aStoryDataSet);
			}

			usIndividualTable = $("#" + customTableId).DataTable({
				paging : false,
				data : usTableDataSet,
				columns : [ {
					title : "User Story"
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
				}, {
					title : "PLANNED"
				}, {
					title : "UNPLANNED"
				} ]
			});
			usIndividualTable.columns(columnList).visible(false);
			index++;
		});

		showUsTable();
	});

}

// Section: Cycle Gadget

$("#cycleProject").change(function() {
	if ($("#cycleRelease").val() == null || $("#cycleRelease").val() == "") {
		return;
	}
	callAjaxOnCycleProjectAndRelease();

});

$("#cycleRelease").change(function() {
	if ($("#cycleProject").val() == null || $("#cycleProject").val() == "") {
		return;
	}
	callAjaxOnCycleProjectAndRelease();
});

$("#cycle-update-btn")
		.click(
				function() {
					var object = {};

					if ($("#cycleProject").val() == ""
							|| $("#cycleProject").val() == null
							|| $("#cycleRelease").val() == ""
							|| $("#cycleRelease").val() == null) {
						$("#warning-message")
								.val(
										"Please select project name and release for this gadget.");
						$("dashboard-alert").fadeIn();
						return;
					}

					if ($("#cycleMultiSelect").val() == null) {
						alert("No cycle selected");
						return;
					}
					if ($("#cycleMetricMultiSelect").val() == null) {
						alert("No metric selected for this widget");
						return;
					}

					object['projectName'] = $("#cycleProject").val();
					object['release'] = $("#cycleRelease").val();
					object['metrics'] = $("#cycleMetricMultiSelect").val();
					if ($("#cycleCheckAll").prop("checked")) {
						object['selectAll'] = true;
					} else {
						object['cycles'] = $("#cycleMultiSelect").val();
					}
					var jsonString = JSON.stringify(object);
					$.ajax({
						url : '/gadget/addGadget',
						method : 'POST',
						data : {
							type : 'TEST_CYCLE_TEST_EXECUTION',
							data : jsonString
						},
						beforeSend : function() {
							hideCycleTable();
						},
						error : function(res) {
							alert("Error while updating object using Ajax");
						},
						success : function(data) {
							alert("Gadget updated succesfully");
						}
					}).always(function() {
						console.log(jsonString);
						showCycleTable();
					});
				});

$("#cycle-draw-table-btn").click(function() {
	callAjaxOnCycleTable();
});

// Section: Assignee
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

// Section: Function
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
function callAjaxOnCycleTable() {
	$.ajax({
		url : '/gadget/gadgets',
		success : function(gadgetList) {
			drawCycleGadget(gadgetList);
		},
		error : function(response) {
			alert("Error while drawing cycle table");
		},
		beforeSend : function() {
			hideCycleTable();
		}
	}).done(function(gadgetList) {
		console.log(gadgetList);
	});

}

function drawCycleGadget(gadgetList) {
	for (var i = 0; i < gadgetList.length; i++) {
		if (gadgetList[i]["type"] == "TEST_CYCLE_TEST_EXECUTION") {
			console.log("At gadget List");
			if (gadgetList[i]["projectName"] != ""
					&& gadgetList[i]["projectName"] != null) {
				$("#cycleProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["release"] != ""
					&& gadgetList[i]["release"] != null) {
				$("#cycleRelease").val(gadgetList[i]["release"]);
			}
			if(gadgetList[i]["selectAll"] == true){
				$("#cycleCheckAll").prop("checked",true);
				$("#cycle-container").fadeOut();
			}
			else{
				if (gadgetList[i]["cycles"] != ""
					&& gadgetList[i]["cycles"] != null) {
				appendToSelect(true, gadgetList[i]["cycles"],
						"#cycleMultiSelect");
				$("#cycleMultiSelect").val(gadgetList[i]["cycles"]);
			}
			}
			

			if (gadgetList[i]["metrics"] != ""
					&& gadgetList[i]["metrics"] != null) {
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
		globalCycleTable.api().ajax.reload();
	} else {
		$.ajax({
			url : "/gadget/getData",
			data : {
				"id" : gadgetId
			},

			beforeSend : function() {
				hideCycleTable();
			}
		}).done(function(gadgetData) {
			if (gadgetData == null || gadgetData == "") {
				alert("There is no available gadget");
				return;
			}
			globalCycleTable = $('#cycle-table').DataTable({
				"fnDrawCallback" : function(oSettings) {
					showCycleTable();
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
				} ]
			});
			globalCycleTable.columns(columnList).visible(false);
			showCycleTable();
		});
	}
}

function callAjaxOnCycleProjectAndRelease() {
	if (!$("#epicCheckAll").prop("checked")) {
		$.ajax({
			url : "/listcycle?",
			data : {
				project : $("#cycleProject").val(),
				release : $("#cycleRelease").val()
			},

			beforeSend : function() {
				hideCycleSelect();
			},
			success : function(data) {
				if (data == null) {
					console.log(data);
					alert("Data fetched from cycle select is null");
					return;
				}
				appendToSelect(true, data, "#cycleMultiSelect");
			},
			error : function(data) {
				alert("Error fetching cycle");
				console.log(data);
			}
		}).always(function() {
			showCycleSelect();
		});
	}
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

function appendTemplateTable(id, title, container) {
	$('<h4>' + title + '</h4>').appendTo(container);
	$('<table id="' + id + '"></table>').appendTo(container);
	$('<br><hr>').appendTo(container);
}

function callAjaxOnUsProjectAndRelease() {
	$.ajax({
		url : "/getEpicLinks?",
		data : {
			project : $("#usProject").val(),
			release : $("#usRelease").val()
		},

		beforeSend : function() {
			hideUsEpic();
		},
		error : function(data) {
			alert("Error fetching epic links");
		}
	}).done(function(data) {
		appendToSelect(true, data, "#usEpicAvailable");
		$('#usEpic').find('option').remove().end();
		showUsEpic();
	});
}

function appendToSelect(cleanOption, list, targetId) {
	if (cleanOption == true) {
		cleanSelect(targetId);
	}
	for (var z = 0; z < list.length; z++) {
		$('<option value="' + list[z] + '" selected>' + list[z] + '</option>')
				.appendTo(targetId);
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

function cleanSelect(targetId) {
	$(targetId).find('option').remove().end();
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

$("#usCheckAllEpic").click(function() {
	if ($(this).prop("checked")) {
		$("#us-epic-available-div").fadeOut();
	} else {
		$("#us-epic-available-div").fadeIn();
	}
});

$("#epicCheckAll").click(function() {
	if ($(this).prop("checked")) {
		$("#epic-link-container").fadeOut();
	} else {
		$("#epic-link-container").fadeIn();
	}
});

$("#usCheckAllStory").click(function() {
	if ($(this).prop("checked")) {
		$("#us-container").fadeOut();
	} else {
		$("#us-container").fadeIn();
	}
});

$("#cycleCheckAll").click(function() {
	if ($(this).prop("checked")) {
		$("#cycle-container").fadeOut();
	} else {
		$("#cycle-container").fadeIn();
	}
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

function callAjaxOnEpicProjectAndRelease(){
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

function getColumnArray(metricArray, isCycleOrAssignee) {
	var columnList = [];

	if (metricArray == null) {
		alert("There is no metric to show.");
		return;
	}
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
	if (!isCycleOrAssignee) {
		if ($.inArray('PLANNED', metricArray) == -1) {
			columnList.push(6);
		}
		if ($.inArray('UNPLANNED', metricArray) == -1) {
			columnList.push(7);
		}
	}
	return columnList;
}
