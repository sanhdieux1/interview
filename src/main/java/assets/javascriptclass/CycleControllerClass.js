// A cycle controller class, contains html Id and element
function CycleController(dashboardId, container, htmlElements) {
  GadgetController.call(this, dashboardId, container, htmlElements);

  if(htmlElements != null && htmlElements["projectId"] != null){
    this.htmlSelectCycleId  = htmlElements.selectCycleId;

    this.htmlCheckSelectAllCycleId = htmlElements.checkSelectAllCycleId;
    
    this.htmlTableId = htmlElements.tableId;

    this.htmlContainerCycleId = htmlElements.containerCycleId;

    this.htmlLoaderCycleContainerId = htmlElements.loaderCycleContainerId;
  }

  CycleController.prototype = {
    callAjaxOnProjectReleaseProductChanged: function() {
      if ($(htmlProjectId).val() == null || $(htmlReleaseId).val() == null || $(htmlProductId).val() == null) {
        return;
      }

      if (!$(this.htmlCheckSelectAllCycleId).prop("checked")) {
        $.ajax({
          url: GET_CYCLE_URI,
          data: {
            project: $(htmlProjectId).val(),
            release: $(htmlReleaseId).val(),
            products: JSON.stringify([$(htmlProductId).val()])
          },

          beforeSend: function() {
            CycleController.prototype.hideCycleSelect();
          },
          success: function(data) {
            if (debugAjaxResponse(data)) {
              return;
            }
            data.sort();
            appendToSelect(true, data, htmlSelectCycleId);
          }
        }).done(function(data) {
          CycleController.prototype.showCycleSelect();
        });
      }
    },
    getGadgetList: function(){
      $.ajax({
            url : GET_GADGETS_URI,
          data : {
            "dashboardId" : this.dashboardId
          },
          success: function(gadgetList) {
            CycleController.prototype.drawCycleGadget(gadgetList);
          },
          error: function(response) {
            alert("Error while drawing cycle table");
            CycleController.prototype.showCycleTable();
          },
          beforeSend: function() {
            CycleController.prototype.hideCycleTable();
          }
        }).done(function(gadgetList) {
          if (debugAjaxResponse(gadgetList)) {
            return;
          }
          console.log(gadgetList);
        });
    },
    callAjaxOnTable: function(){
      $.ajax({
            url : GET_GADGETS_URI,
          data : {
            "dashboardId" : this.dashboardId
          },
          success: function(gadgetList) {
            CycleController.prototype.drawCycleGadget(gadgetList);
          },
          error: function(response) {
            alert("Error while drawing cycle table");
            CycleController.prototype.showCycleTable();
          },
          beforeSend: function() {
             CycleController.prototype.hideCycleTable();
          }
        }).done(function(gadgetList) {
          if (debugAjaxResponse(gadgetList)) {
            return;
          }
          console.log(gadgetList);
        });
    },
    createJsonStringFromInput: function(){
      var object = {};
      if(null == dashboardId){
        alert("No valid dashboard id provided.");
        return;
      }
      else if ($(htmlProjectId).val() == null) {
        alert("No project selected");
        return;
      } else if ($(htmlReleaseId).val() == null) {
        alert("No release selected");
        return;
      } else if ($(htmlProductId).val() == null) {
        alert("No product selected");
        return;
      } else if ($(htmlSelectCycleId).val() == null && !$(htmlCheckSelectAllCycleId).prop("checked")) {
        alert("No cycle selected");
        return;
      } else if ($(htmlMetricId) == null) {
        alert("No test metric selected");
        return;
      }
      object['id'] = this.id;
      object['dashboardId'] = this.dashboardId;
      object['projectName'] = $(htmlProjectId).val();
      object['release'] = $(htmlReleaseId).val();
      object['products'] = [$(htmlProductId).val()];
      object['metrics'] = $(htmlMetricId).val();

      if ($(htmlCheckSelectAllCycleId).prop("checked")) {
        object['selectAllCycle'] = true;
      } else {
        object['cycle'] = $(htmlSelectCycleId).val();
      }
      return JSON.stringify(object);
    },
    updateGadget: function(jsonString){
      if (jsonString != null && jsonString != "") {
        $.ajax({
          url: SAVE_GADGET_URI,
          method: 'POST',
          data: {
            type: CYCLE_TYPE,
            data: jsonString
          },
          beforeSend: function() {
            $(htmlLoaderTableId).fadeIn();
          },
          success: function(data) {
            if (debugAjaxResponse(data)) {
              return;
            }
            alert("Gadget updated succesfully");
          }
        }).done(function() {
          $(htmlLoaderTableId).fadeOut();
        });
      }
    },
    drawGadget: function(gadgetList) {
      for (var i = 0; i < gadgetList.length; i++) {
        if (gadgetList[i]["type"] == CYCLE_TYPE && gadgetList[i]["dashboardId"] == this.dashboardId) {
          this.id = gadgetList[i]["id"];
          if (gadgetList[i]["projectName"] != "" && gadgetList[i]["projectName"] != null) {
            $(htmlProjectId).val(gadgetList[i]["projectName"]);
          }

          if (gadgetList[i]["products"] != null) {
            $(htmlProductId).val(gadgetList[i]["products"]);
          }


          if (gadgetList[i]["release"] != "" && gadgetList[i]["release"] != null) {
            $(htmlReleaseId).val(gadgetList[i]["release"]);
          }

          if (gadgetList[i]["metrics"] != "" && gadgetList[i]["metrics"] != null) {
            $(htmlMetricId).val(gadgetList[i]["metrics"]);
          }
          if (gadgetList[i]["selectAllCycle"] == true) {
            $(htmlCheckSelectAllCycleId).prop("checked", true);
            $(htmlContainerCycleId).fadeOut();
          } else if (gadgetList[i]["cycle"] != null) {
            $(htmlCheckSelectAllCycleId).prop("checked", false);
            CycleController.prototype.showCycleSelect();
            $(htmlLoaderCycleContainerId).fadeIn();
            appendToSelect(true, gadgetList[i]["cycle"], htmlSelectCycleId);
            $(htmlSelectCycleId).val(gadgetList[i]["cycle"]);
          }
          console.log(gadgetList[i]["id"]);
          CycleController.prototype.drawTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
          break;
        }
      }
    },
    drawTable: function(gadgetId, metricArray) {
      var columnList = getColumnArray(metricArray, false);
      if (dataTable != null) {
        console.log("Redrawing table: " + dataTable);
        dataTable.ajax.reload();
      } else {
        CycleController.prototype.hideCycleTable();
        dataTable = $(htmlTableId).DataTable({
          "fnDrawCallback": function(oSettings) {
            CycleController.prototype.showCycleTable();
          },
          paging: false,
          "ajax": {
            url: GET_DATA_URI,
            data: {
              "id": gadgetId
            },
            dataSrc: function(responseJson) {
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
          "columns": [{
            "data": "key.key"
          },
          {
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
          }, {
            "data": "planned",
            "render": function(data, displayOrType, rowData, setting){
              return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }, {
            "data": "unplanned",
            "render": function(data, displayOrType, rowData, setting){
              return createIssueLinks(data, displayOrType, rowData, setting);
            }
          }]
        });

        dataTable.columns(columnList).visible(false);
      }
    },
    hideCycleSelect: function () {
      $(this.htmlSelectCycleId).fadeOut();
      $(this.htmlLoaderCycleContainerId).fadeIn();
    },

    showCycleSelect: function () {
      $(this.htmlSelectCycleId).fadeIn();
      $(this.htmlLoaderCycleContainerId).fadeOut();
    },

    showCycleTable: function () {
     $(this.htmlContainerTableId).fadeIn();
      $(this.htmlLoaderTableId).fadeOut();
    },

    hideCycleTable: function () {
      $(this.htmlContainerTableId).fadeOut();
      $(this.htmlLoaderTableId).fadeIn();
    }
  }
}


inheritPrototype(CycleController, GadgetController);