var AssigneeController = function(dashboardId,  container, htmlElements){
  GadgetController.call(dashboardId, container, htmlElements);

  if(htmlElements != null && htmlElements["projectId"] != null){
    this.htmlSelectCycleId  = htmlElements.selectCycleId;
    this.htmlSelectAvailableCycleId = htmlElements.selectAvailableCycleId;
    this.htmlSelectAssigneeId = htmlElements.selectAssigneeId;
    
    this.htmlCheckAllCycleId = htmlElements.checkAllCycleId;
    this.htmlCheckAllAssigneeId = htmlElements.checkAllAssigneeId;

    this.htmlBtnAddId = htmlElements.btnAddId;
    this.htmlBtnRemoveId = htmlElements.btnRemoveId;
    this.htmlBtnAddAllId = htmlElements.btnAddAllId;
    this.htmlBtnRemoveAllId = htmlElements.btnRemoveAllId;

    this.htmlLoaderCycleContainerId = htmlElements.loaderCycleContainerId;
    this.htmlLoaderAssigneeId = htmlElements.loaderAssigneeId;

    this.htmlContainerCycleId = htmlElements.containerCycleId;
    this.htmlContainerCycleAvailableId = htmlElements.containerCycleAvailableId;
    this.htmlContainerAssigneeId = htmlElements.containerAssigneeId;
  }

  AssigneeController.prototype = {
  	callAjaxOnProjectReleaseProductChanged: function(){

  		if ($(htmlReleaseId).val() == null) {
  		  return;
  		} else if ($(htmlProjectId).val() == null) {
  		  return;
  		} else if ($(htmlProductId).val() == null) {
  		  return;
  		}

  		if (!$(htmlCheckAllCycleId).prop("checked")) {
  		  $.ajax({
  		    url: GET_CYCLE_URI,
  		    data: {
  		      project: $(htmlProjectId).val(),
  		      release: $(htmlReleaseId).val(),
  		      products: JSON.stringify([$(htmlProductId).val()])
  		    },

  		    beforeSend: function() {
  		      AssigneeController.prototype.hideAssigneeCycle();
  		    },
  		    success: function(data) {
  		      if (debugAjaxResponse(data)) {
  		        return;
  		      }
  		      data.sort();
  		      appendToSelect(true, data, htmlSelectAvailableCycleId);
  		      $(htmlSelectCycleId).find("option").remove().end();
  		    },
  		    error: function(data) {
  		      alert("Error : Cant fetch cycle");
  		      console.log(data);
  		    }
  		  }).done(function() {
  		    AssigneeController.prototype.showAssigneeCycle();
  		  });
  		}

  		if (!$(htmlCheckAllAssigneeId).prop("checked")) {

  		  $.ajax({
  		    url: GET_ASSIGNEE_URI,
  		    data: {
  		      project: $(htmlProjectId).val(),
  		      release: $(htmlReleaseId).val()
  		    },

  		    beforeSend: function() {
  		      AssigneeController.prototype.hideAssignee();
  		    },
  		    success: function(data) {
  		      if (debugAjaxResponse(data)) {
  		        return;
  		      }
  		      var tempAssigneeList = [];
  		      $.each(data, function(key, map) {
  		        tempAssigneeList.push(map["assignee"]);
  		      });
  		      tempAssigneeList.sort();
  		      appendToSelect(true, tempAssigneeList, htmlSelectAssigneeId);
  		    },
  		    error: function(data) {
  		      alert("Error fetching assignee list");
  		      console.log(data);
  		    }
  		  }).done(function() {
  		    AssigneeController.prototype.showAssignee();
  		  });
  		}
  	},
  	createJsonStringFromInput: function(){
  		  var object = {};
  		  var options;
  		  var values;
  		  var jsonString;
  		  if(null == this.dashboardId){
  			  alert("No valid dashboard id provided.");
  			  return;
  		  }
  		  else if ($(htmlProjectId).val() == null ||$(htmlReleaseId).val() == null) {
  		    $("#warning-message").val(
  		      "Please select project, release, product for this gadget.");
  		    $("dashboard-alert").fadeIn();
  		    return;
  		  } else if ($(htmlProductId).val() == null) {
  		    alert("No product selected");
  		    return;
  		  } else if ($(htmlSelectCycleId + " option").length == 0 && !$(htmlCheckAllCycleId).prop("checked")) {
  		    alert("No cycle selected");
  		    return;
  		  } else if ($(htmlSelectAssigneeId).val() == null && !$(htmlAssigneeChec).prop("checked")) {
  		    alert("No assignee selected");
  		    return;
  		  } else if ($(htmlMetricId).val() == null) {
  		    alert("No test metric selected");
  		    return;
  		  }
  		  
  		  options = $(htmlSelectCycleId+ " option");
  		  values = $.map(options, function(option) {
  		    return option.value;
  		  });
  		  
  		  object['id'] = this.id;
  		  object['dashboardId'] = this.dashboardId;
  		  object['projectName'] = $(htmlProjectId).val();
  		  object['release'] = $(htmlReleaseId).val();
  		  object['products'] = [$(htmlProductId).val()];
  		  object['metrics'] = $(htmlMetricId).val();

  		  if ($(htmlCheckAllCycleId).prop("checked")) {
  		    object['selectAllCycle'] = true;
  		  } else {
  		    object['cycles'] = values;
  		  }
  		  if ($(htmlCheckAllAssigneeId).prop("checked")) {
  		    object['selectAllAssignee'] = true;
  		  } else {
  		    object['assignee'] = $(htmlSelectAssigneeId).val();
  		  }

  		  jsonString = JSON.stringify(object);
  		  
  	},
  	updateGadget: function(jsonString){
  		$.ajax({
  		  url: SAVE_GADGET_URI,
  		  method: 'POST',
  		  data: {
  		    type: ASSIGNEE_TYPE,
  		    data: jsonString
  		  },
  		  beforeSend: function() {
  		    AssigneeController.prototype.hideAssigneeTable();
  		  },
  		  error: function(res) {
  		    alert("Error while updating object using Ajax");
  		    console.log(res);
  		  },
  		  success: function(data) {
  		    if (debugAjaxResponse(data)) {
  		      return;
  		    }
  		    alert("Gadget updated succesfully");
  		  }
  		}).done(function(returnMessage) {

  		  console.log(jsonString);
  		  AssigneeController.prototype.showAssigneeTable();
  		});	
  	},
  	callAjaxOnTable : function(){
  		$.ajax({
  			  url : GET_GADGETS_URI,
  				data: {
  					"dashboardId": this.dashboardId
  				},
  		    success: function(gadgetList) {
  		      if (debugAjaxResponse(gadgetList)) {
  		        return;
  		      }
  		      AssigneeController.prototype.drawAssigneeGadget(gadgetList);
  		    },
  		    error: function(response) {
  		      alert("Error while drawing assignee table");
  		      AssigneeController.prototype.showAssigneeTable();
  		    },
  		    beforeSend: function() {
  		      AssigneeController.prototype.hideAssigneeTable();
  		    }
  		  }).done(function(gadgetList) {
  		    console.log(gadgetList);
  		  });
  	},
  	drawGadget: function (gadgetList) {

  	  for (var i = 0; i < gadgetList.length; i++) {
  	    if (gadgetList[i]["type"] == ASSIGNEE_TYPE && gadgetList[i]["dashboardId"] == this.dashboardId) {
  	       this.id = gadgetList[i]["id"];
  	      console.log("At gadget List");
  	      if (gadgetList[i]["projectName"] != "" && gadgetList[i]["projectName"] != null) {
  	        $(htmlProjectId).val(gadgetList[i]["projectName"]);
  	      }

  	      if (gadgetList[i]["release"] != "" && gadgetList[i]["release"] != null) {
  	        $(htmlReleaseId).val(gadgetList[i]["release"]);
  	      }

  	      if (gadgetList[i]["products"] != "" && gadgetList[i]["products"] != null) {
  	        $(htmlProductId).val(gadgetList[i]["products"]);
  	      }

  	      if (gadgetList[i]["assignee"] != "" && gadgetList[i]["assignee"] != null) {
  	        appendToSelect(true, gadgetList[i]["assignee"],
  	          htmlSelectAssigneeId);
  	        $(htmlSelectAssigneeId).val(gadgetList[i]["assignee"]);
  	      }

  	      if (gadgetList[i]["metrics"] != "" && gadgetList[i]["metrics"] != null) {
  	        $(htmlMetricId).val(gadgetList[i]["metrics"]);
  	      }
  	      if(gadgetList[i]["selectAllTestCycle"] == true){
  	      	$(htmlCheckAllCycleId).prop("checked", true);
  	      	$(htmlContainerCycleId).fadeOut();
  	      }
  	      else if (gadgetList[i]["cycles"] != "" && gadgetList[i]["cycles"] != null) {
  	      	$(htmlCheckAllCycleId).prop("checked", false);
  	      	$(htmlContainerCycleId).fadeIn();
  	      	$(htmlLoaderCycleContainerId).fadeOut();
  	        appendToSelect(true, gadgetList[i]["cycles"], htmlSelectCycleId);
  	        $(htmlSelectCycleId).val(gadgetList[i]["cycles"]);
  	      }
  	      console.log("prepare to draw table");
  	      drawAssigneeTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
  	      break;
  	    }
  	  }
  	},
  	drawTable: function (gadgetId, metricArray) {
  	  var columnList = getColumnArray(metricArray, true);
  	  $(htmlContainerTableId).html("");

  	  var jsonObjectForAssigneeTable;
  	  $
  	    .ajax({
  	      url: GET_DATA_URI,
  	      method: "GET",
  	      data: {
  	        "id": gadgetId
  	      },
  	      beforeSend: function() {
  	        AssigneeController.prototype.hideAssigneeTable();
  	      },
  	      error: function(response) {
  	        alert("Failed to draw table");
  	        console.log(response);
  	        AssigneeController.prototype.showAssigneeTable();
  	      }
  	    })
  	    .done(
  	      function(responseData) {
  	        var index = 0;
  	        if (debugAjaxResponse(responseData)) {
  	          return;
  	        }
  	        console.log("Get individual data ok");

  	        jsonObjectForAssigneeTable = responseData;
  	        $
  	          .each(
  	            jsonObjectForAssigneeTable["data"],
  	            function(cycleKey, assigneeArray) {
  	              console
  	                .log(assigneeArray["issueData"].length);
  	              if (assigneeArray["issueData"].length != 0) {
  	                var customTableId = "assignee-table-" + index;
  	                var assigneeTableDataSet = [];
  	                var assigneeIndividualTable;

  	                appendTemplateTable(
  	                  customTableId,
  	                  cycleKey,
  	                  htmlContainerTableId);
  	                $("#" + customTableId).append(
  	                  TEMPLATE_HEADER_FOOTER_1);

  	                console
  	                  .log("Pass each function");

  	                for (var i = 0; i < assigneeArray["issueData"].length; i++) {
  	                  var anAssigneeDataSet = [];
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["key"]["key"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["unexecuted"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["failed"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["wip"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["blocked"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["passed"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["planned"]);
  	                  anAssigneeDataSet
  	                    .push(assigneeArray["issueData"][i]["unplanned"]);
  	                  assigneeTableDataSet
  	                    .push(anAssigneeDataSet);
  	                }

  	                assigneeIndividualTable = $(
  	                    "#" + customTableId)
  	                  .DataTable({
  	                    paging: false,
  	                    data: assigneeTableDataSet,
  	                    columns: [{
  	                      title: "Assignee"
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
  	                    }]
  	                  });
  	                assigneeIndividualTable
  	                  .columns(columnList)
  	                  .visible(false);
  	                index++;
  	              }
  	            });
  	        AssigneeController.prototype.showAssigneeTable();

  	      });
  	},
  	addAllCycle: function () {
  	  var options = $(htmlSelectAvailableCycleId + " option").clone();
  	  if (options.length == 0) {
  	    return;
  	  }
  	  $(htmlSelectCycleId).append(options);
  	  $(htmlSelectAvailableCycleId +" option").remove();
  	},

  	removeAllCycle: function () {
  	  var options = $(htmlSelectCycleId + " option").clone();
  	  if (options.length == 0) {
  	    return;
  	  }
  	  $(htmlSelectAvailableCycleId).append(options);
  	  $(htmlSelectCycleId + " option").remove();
  	},

  	hideAssigneeCycle: function () {
  	  $(htmlContainerCycleAvailableId).fadeOut();
  	  $(htmlLoaderCycleContainerId).fadeIn();
  	},

  	showAssigneeCycle: function () {
  	  $(htmlContainerCycleAvailableId).fadeIn();
  	  $(htmlLoaderCycleContainerId).fadeOut();
  	},

  	hideAssignee: function () {
  	  $(htmlSelectAssigneeId).fadeOut();
  	  $(htmlLoaderAssigneeId).fadeIn();
  	},

  	showAssignee :function () {
  	  $(htmlSelectAssigneeId).fadeIn();
  	  $(htmlLoaderAssigneeId).fadeOut();
  	},

  	hideAssigneeTable: function () {
  	  $(htmlContainerTableId).fadeOut();
  	  $(htmlLoaderTableId).fadeIn();
  	},

  	showAssigneeTable: function () {
  	  $(htmlContainerTableId).fadeIn();
  	  $(htmlLoaderTableId).fadeOut();
  	}
  }
  
}


inheritPrototype(AssigneeController, GadgetController);