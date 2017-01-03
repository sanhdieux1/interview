/*
 * EpicController 
 *
 * 
 */
function EpicController(dashboardId, container, htmlElements) {
  GadgetController.call(this, dashboardId, container, htmlElements);

  if(htmlElements != null && htmlElements["projectId"]){
    this.htmlSelectEpicId  = htmlElements.selectEpicId;
    this.htmlCheckSelectAllEpicId = htmlElements.checkSelectAllEpicId;
   
    this.htmlTableId = htmlElements.tableId;

    this.htmlContainerEpicId = htmlElements.containerEpicId;

    this.htmlLoaderContainerEpicId = htmlElements.loaderContainerEpicId;
  }

  EpicController.prototype = {
    callAjaxOnProjectReleaseProductChanged: function() {
      if ($(htmlProjectId).val() == null || $(htmlReleaseId).val() == null || $(htmlProductId).val() == null) {
        return;
      }

      if (!$(this.htmlCheckSelectAllEpicId).prop("checked")) {
        $.ajax({
          url: GET_EPIC_URI,
          data: {
            project: $(htmlProjectId).val(),
            release: $(htmlReleaseId).val(),
            products: JSON.stringify([$(htmlProductId).val()])
          },

          beforeSend: function() {
            EpicController.prototype.hideEpicLinks();
          },
          success: function(data) {
            if (debugAjaxResponse(data)) {
              return;
            }
            data.sort();
            appendToSelect(true, data, htmlSelectEpicId);
          }
        }).done(function(data) {
          EpicController.prototype.showEpicLinks();
        });
      }
    },
    callAjaxOnTable: function(){
      $.ajax({
         url: GET_GADGETS_URI,
         data:{
          "dashboardId": this.dashboardId
         },
         beforeSend: function() {
           EpicController.prototype.hideEpicTable();
         },
         error: function() {
           alert("Failed to execute ajax fetching epic table");
           EpicController.prototype.showEpicTable();
         },
         success: function(gadgetList) {
           if (debugAjaxResponse(gadgetList)) {
             return;
           }
           console.log(gadgetList);
           EpicController.prototype.drawEpicGadget(gadgetList);
         }
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
      } else if ($(htmlSelectEpicId).val() == null && !$(htmlCheckSelectAllEpicId).prop("checked")) {
        alert("No epic links selected");
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

      if ($(htmlCheckSelectAllEpicId).prop("checked")) {
        object['selectAll'] = true;
      } else {
        object['epic'] = $(htmlSelectEpicId).val();
      }
      return JSON.stringify(object);
    },
    updateGadget: function(jsonString){
      if (jsonString != null && jsonString != "") {
        $.ajax({
          url: SAVE_GADGET_URI,
          method: 'POST',
          data: {
            type: EPIC_TYPE,
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
        if (gadgetList[i]["type"] == EPIC_TYPE && gadgetList[i]["dashboardId"] == this.dashboardId) {
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
          if (gadgetList[i]["selectAll"] == true) {
            $(htmlCheckSelectAllEpicId).prop("checked", true);
            $(htmlContainerEpicId).fadeOut();
          } else if (gadgetList[i]["epic"] != "" && gadgetList[i]["epic"] != null) {
            $(htmlCheckSelectAllEpicId).prop("checked", false);
            $(htmlContainerEpicId).fadeIn();
            appendToSelect(true, gadgetList[i]["epic"], htmlSelectEpicId);
            $(htmlSelectEpicId).val(gadgetList[i]["epic"]);
          }
          console.log(gadgetList[i]["id"]);
          EpicController.prototy.drawTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
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
        EpicController.prototy.hideEpicTable();
        dataTable = $(htmlTableId).DataTable({
          "fnDrawCallback": function(oSettings) {
            EpicController.prototy.showEpicTable();
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
          }, {
            "data": "key.summary"
          }, {
            "data": "key.priority.name",
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
    showEpicLinks: function() {
      $(this.htmlSelectEpicId).fadeIn();
      $(this.htmlLoaderContainerEpicId).fadeOut();
    },
    hideEpicLinks: function() {
      $(this.htmlSelectEpicId).fadeOut();
      $(this.htmlLoaderContainerEpicId).fadeIn();
    },
    showEpicTable: function() {
      $(this.htmlContainerTableId).fadeIn();
      $(this.htmlLoaderTableId).fadeOut();
    },
    hideEpicTable: function() {
      $(this.htmlContainerTableId).fadeOut();
      $(this.htmlLoaderTableId).fadeIn();
    }
  }
}



inheritPrototype(EpicController, GadgetController);

