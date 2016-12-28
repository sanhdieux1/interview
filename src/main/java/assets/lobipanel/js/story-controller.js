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

$("#usEpicSelectAll").change(function() {
	$("#us-epic-available-div").fadeOut();
	$("#us-epic-available-div").fadeIn();
});

$("#usCheckAllEpic").click(function() {
	if ($(this).prop("checked")) {
		$("#us-epic-available-div").fadeOut();
	} else {
		$("#us-epic-available-div").fadeIn();
	}
});

$("#usCheckAllStory").click(function() {
	if ($(this).prop("checked")) {
		$("#us-container").fadeOut();
	} else {
		$("#us-container").fadeIn();
	}
});

function reloadUSList() {
	if ($("#usEpic option").length == 0 && $("#usCheckAllStory").prop("checked")) {
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

function callAjaxOnUsProjectAndRelease() {
	if(!$("#usCheckAllEpic").prop("checked")){
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