var globalEpicTable = null;
var globalUsTable = null;

$(document).ready(function() {
	if ($('#epicProject').length != 0) {
		$.get("/listproject", function(data) {
			appendToSelect(false, data, "#epicProject");
			appendToSelect(false, data, "#usProject");
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
	if ($("#epicRelease").val() == null || $("#epicRelease").val() == "") {
		return;
	}
	$.ajax({
		url : "/getEpicLinks?",
		data : {
			project : $(this).val(),
			release : $("#epicRelease").val()
		},

		beforeSend : function() {
			hideEpicLinks();
		}
	}).done(function(data) {
		appendToSelect(true, data, "#epicMultiSelect");
		showEpicLinks();
	});
});

$("#epicRelease").change(function() {
	if ($("#epicProject").val() == null || $("#epicProject").val() == "") {
		return;
	}
	$.ajax({
		url : "/getEpicLinks?",
		data : {
			project : $("#epicProject").val(),
			release : $(this).val()
		},

		beforeSend : function() {
			hideEpicLinks();
		}
	}).done(function(data) {
		appendToSelect(true, data, "#epicMultiSelect");
		showEpicLinks();
	});
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
				$("dashboard-alert").show();
				alert("Missing configuration details");
			} else {

				var object = {};
				var issue = [];
				object['cycleName'] = "";
				object['projectName'] = $("#epicProject").val();
				object['release'] = $("#epicRelease").val();
				object['metrics'] = $("#epicMetricMultiSelect").val();
				$.each($("#epicMultiSelect").val(), function(key, value) {
					issue.push({
						"key" : value,
						self : "",
						parrent : ""
					});
				});
				object['epic'] = issue;
				var jsonString = JSON.stringify(object);
				$.ajax({
					url : '/gadget/addGadget',
					method : 'POST',
					data : {
						type : 'EPIC_US_TEST_EXECUTION',
						data : jsonString
					},
					beforeSend : function() {
						$('#epic-table-loader').show();
					}
				}).done(function(data1) {
					$('#epic-table-loader').hide();
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
	$("#usEpic").append(options);
	$("#usEpicAvailable option:selected").remove();
	reloadAssigneeList();
});

$("#us-remove-epic-btn").click(function() {
	var options = $("#usEpic option:selected").clone();
	$("#usEpicAvailable").append(options);
	$("#usEpic option:selected").remove();
	reloadAssigneeList();
});

function reloadAssigneeList() {
	if ($("#usEpic option") == null) {
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
			$("#usMultiSelect").hide();
			$("#us-us-loader").show();
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

				$("#usMultiSelect").show();
				$("#us-us-loader").hide();
			});
}

$("#us-update-btn").click(
		function() {
			if ($("#usProject").val() == "" || $("#usProject").val() == null
					|| $("#usRelease").val() == ""
					|| $("#usRelease").val() == null) {
				$("#warning-message").val(
						"Please select project name for this gadget.");
				$("dashboard-alert").show();
				return;
			}
			if ($("#usEpic option") == null) {
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
			object['epic'] = convertEpicLinkToApiIssue(values);
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
				}
			}).done(function(returnMessage) {
				console.log(jsonString);
				showUsTable();
			});
		});

$("#us-draw-table-btn").click(function(){
	callAjaxOnUsTable();
});

function callAjaxOnUsTable() {
	$.ajax({
		url : '/gadget/gadgets',
		beforeSend : function() {
			hideUsTable();
		}
	}).done(function(gadgetList) {
		console.log(gadgetList);
		drawUsGadget(gadgetList);
		showUsTable();
	});

}

$("#usEpicSelectAll").change(function() {
	$("#us-epic-available-div").hide();
	$("#us-epic-available-div").show();
});

function drawUsGadget(gadgetList) {
	for (var i = 0; i < gadgetList.length; i++) {
		if (gadgetList[i]["type"] == "STORY_TEST_EXECUTION") {
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
			if (globalUsTable != null) {
				globalUsTable.api().ajax.reload();
			} else {
				globalUsTable = $('#us-table').dataTable(
						{
							"fnDrawCallback" : function(oSettings) {
								$('#us-table-loader').hide();
								$("#us-table-container").show();
							},
							"lengthMenu" : [ [ 10, 25, 50, -1 ],
									[ 10, 25, 50, "All" ] ],
							"processing" : true,
							"ajax" : {
								url : "/gadget/getData",
								data : {
									"id" : gadgetList[i]["id"]
								},
								beforeSend : function() {
									$('#us-table-loader').show();
									$("#us-table-container").hide();
								}
							},
							"columns" : [ {
								"data" : "title"
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
			}
			break;
		}
	}
}

function callAjaxOnEpicTable() {
	$.ajax({
		url : '/gadget/gadgets',
		beforeSend : function() {
			hideEpicTable();
		}
	}).done(function(gadgetList) {
		console.log(gadgetList);
		drawEpicGadget(gadgetList);
		showEpicTable();
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
				$("#epicMultiSelect").find("option").remove().end();
				$.each(gadgetList[i]["epic"], function(key, value) {
					$(
							'<option value="' + value["key"] + '" selected>'
									+ value["key"] + '</option>').appendTo(
							"#epicMultiSelect");
				});

				$("#epicMultiSelect").val(gadgetList[i]["epic"]);
			}
			if (globalEpicTable != null) {
				console.log(globalEpicTable);
				globalEpicTable.api().ajax.reload();
			} else {
				$.ajax({
					url : "/gadget/getData",
					data : {
						"id" : gadgetList[i]["id"]
					},

					beforeSend : function() {
						hideEpicTable();
					}
				}).done(function(gadgetData) {
					if(gadgetData == null || gadgetData == ""){
						alert("There is no available gadget");
						return;
					}
					globalEpicTable = $('#epic-table').dataTable(
							{
								"fnDrawCallback" : function(oSettings) {
									showEpicTable();
								},
								"lengthMenu" : [ [ 10, 25, 50, -1 ],
										[ 10, 25, 50, "All" ] ],
								"processing" : true,
								"ajax":{
									url : "/gadget/getData",
									data : {
										"id" : gadgetList[i]["id"]
									},
									dataSrc: function(responseJson){
										var tempObject = new Object();
										tempObject.data = [];
										var tempArray = [];
										$.each(responseJson["data"], function(k1, v1){
											$.each(v1, function(k2,v2){
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
					showEpicTable();
				});
			}
			break;
		}
	}
}

function assignUsSetting(gadgetList) {
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

function convertEpicLinkToApiIssue(epicList){
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
	$('#epic-table-container').hide();
	$('#epic-table-loader').show();
}

function hideEpicLinks() {
	$('#epicMultiSelect').fadeOut();
	$('#epic-link-loader').fadeIn();
}

function cleanSelect(targetId) {
	$(targetId).find('option').remove().end();
}

function showUsEpic() {
	$('#us-epic-loader').hide();
	$("#us-epic-available-div").show();
}

function hideUsEpic() {
	$("#us-epic-loader").show();
	$("#us-epic-available-div").hide();
}

function showUsStory() {
	$("#us-us-loader").hide();
	$("#usMultiSelect").show();
}

function hideUsStory() {
	$("#us-epic-loader").hide();
	$("#us-epic-available-div").show();
}

function showUsTable() {
	$('#us-table-loader').hide();
	$("#us-table").show();
}

function hideUsTable() {
	$('#us-table-loader').show();
	$("#us-table").hide();
}
