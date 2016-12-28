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

$("#cycleCheckAll").click(function() {
	if ($(this).prop("checked")) {
		$("#cycle-container").fadeOut();
	} else {
		$("#cycle-container").fadeIn();
	}
});

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
			if (gadgetList[i]["selectAllCycle"] == true ) {
				$("#cycleCheckAll").prop("checked", true);
				$("#cycle-container").fadeOut();
			} else {
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
