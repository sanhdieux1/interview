// An abstract gadget controller class

function GadgetController(dashboardId, container, htmlElements) {
  this.dashboardId = dashboardId;
  this.id = null;
  this.htmlContainerId = container;
  this.dataTable = null;
  if(htmlElements != null && htmlElements["projectId"] != null){

    this.htmlProjectId = htmlElements.projectId;
    this.htmlReleaseId = htmlElements.releaseId;
    this.htmlProductId = htmlElements.productId;
    this.htmlMetricId = htmlElements.metricId;
    this.htmlBtnUpdateGadgetId = htmlElements.btnUpdateGadgetId;
    this.htmlBtnAddGadgetId = htmlElements.btnAddGadgetId;
    this.htmlContainerTableId = htmlElements.containerTableId;
    this.htmlLoaderTableId = htmlElements.loaderTableId;
  }
  
  GadgetController.prototype = {
    constructor: GadgetController,
    updateGadget: function() {

    },
    deleteGadget: function() {

    },
    drawGadget: function() {

    },
    drawTable: function() {

    },
    callAjaxOnProjectReleaseProductChanged: function() {

    }
  }
}

function inheritPrototype(childObject, parentObject) {
  var copyOfParent = Object.create(parentObject.prototype);
  copyOfParent.constructor = childObject;
  childObject.prototype = copyOfParent;
}

var UserStoryController = function(dashboardId,  container, htmlElements){
  GadgetController.call(dashboardId, container, htmlElements);

  if(htmlElements != null && htmlElements["projectId"] != null){
    this.htmlSelectEpicId  = htmlElements.selectEpicId;
    this.htmlSelectEpicAvailableId = htmlElements.selectEpicAvailableId;
    this.htmlSelectStoryId = htmlElements.selectStoryId;
    
    this.htmlCheckAllEpicId = htmlElements.checkAllEpicId;
    this.htmlCheckAllStoryId = htmlElements.checkAllStoryId;

    this.htmlBtnAddEpicId = htmlElements.btnAddEpicId;
    this.htmlBtnRemoveEpicId = htmlElements.btnRemoveEpicId;
    this.htmBtnAddAllEpicId = htmlElements.btnAddAllEpicId;
    this.htmlBtnRemoveAllEpicId = htmlElements.btnRemoveAllEpicId;

    this.htmlLoaderContainerEpicId = htmlElements.loaderContainerEpicId;
    this.htmlLoaderStoryId = htmlElements.loaderStoryId;

    this.htmlContainerEpicId = htmlElements.containerEpicId;
    this.htmlContainerEpicAvailableId = htmlElements.containerEpicAvailableId;
    this.htmlContainerStoryId = htmlElements.containerStoryId;
  }

  UserStoryController.prototype = {
    callAjaxOnProjectReleaseProductChanged: function () {
      if ($(htmlProjectId).val() == null || $(htmlReleaseId).val() == null || $(htmlProductId).val() == null) {
        return;
      }

      $.ajax({
        url: GET_EPIC_URI,
        data: {

          project: $(htmlProjectId).val(),
          release: $(htmlReleaseId).val(),
          products: JSON.stringify([$(htmlProductId).val()])
        },

        beforeSend: function() {
          if (!$(htmlCheckAllEpicId).prop("checked")) {
            UserStoryController.prototype.hideUsEpic();
          } 
          if (!$(htmlCheckAllStoryId).prop("checked")) {
            UserStoryController.prototype.hideUsStory();
          }

        },
        error: function(data) {
          alert("Error: Ajax failed.");
          if (!$(htmlCheckAllEpicId).prop("checked")) {
            UserStoryController.prototype.showUsEpic();
          }
          if (!$(htmlCheckAllStoryId).prop("checked")) {
            UserStoryController.prototype.showUsStory();
          }
        },
        success: function(data) {
          if (debugAjaxResponse(data)) {
            return;
          }
          data.sort();
          if (!$(htmlCheckAllEpicId).prop("checked")) {
            appendToSelect(true, data, htmlSelectEpicAvailableId);
            $(htmlSelectEpicId).find('option').remove().end();
            UserStoryController.prototype.showUsEpic();
          } else if ($(htmlCheckAllEpicId).prop("checked") && !$(htmlCheckAllStoryId).prop("checked")) {
            $(htmlSelectEpicAvailableId).find('option').remove().end();
            appendToSelect(true, data, htmlSelectEpicId);
            UserStoryController.prototype.reloadUSList();
          }
        }
      });
    },

    createJsonStringObjectFromInput: function () {
      var options;
      var values;
      var object = {};
      var jsonString;
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
      } else if ($(htmlSelectEpicId+" option").length == 0 && !$(htmlCheckAllEpicId).prop("checked")) {
        alert('No epic links selected.');
        return;
      } else if ($(htmlSelectStoryId).val() == null && !$(htmlCheckAllStoryId).prop("checked")) {
        alert("No user story selected for the fetched epic links ");
        return;
      } else if ($(htmlMetricId).val() == null) {
        alert("No test metric selected");
        return;
      }

      options = $(htmlSelectEpicId + " option");
      values = $.map(options, function(option) {
        return option.value;
      });
      
      object['id'] = this.id;
      object['dashboardId'] = this.dashboardId;
      object['projectName'] = $(htmlProjectId).val();
      object['release'] = $(htmlReleaseId).val();
      object['products'] = [$(htmlProductId).val()];
      object['metrics'] = $(htmlMetricId).val();

      if ($(htmlCheckAllEpicId).prop("checked")) {
        object["selectAllEpic"] = true;
      } else {
        object['epic'] = values;
      }

      if ($(htmlCheckAllStoryId).prop("checked")) {
        object["selectAllStory"] = true;
      } else {
        object['stories'] = $(htmlSelectStoryId).val();
      }
      jsonString = JSON.stringify(object);

      return jsonString;
    },
    updateGadget : function(jsonString){
      if (null != jsonString &&  "" != jsonString) {
          $.ajax({
            url: SAVE_GADGET_URI,
            method: 'POST',
            data: {
              type: US_TYPE,
              data: jsonString
            },
            beforeSend: function() {
              UserStoryController.prototype.hideUsTable();
            },
            error: function(res) {
              alert("Error while updating object using Ajax");
              UserStoryController.prototype.showUsTable();
            },
            success: function(data) {
              if (debugAjaxResponse(data)) {
                return;
              }
              alert("Gadget updated succesfully");
            }
          }).done(function(returnMessage) {
            console.log(jsonString);
            UserStoryController.prototype.showUsTable();
          });
        }
    },
    callAjaxOnTable: function () {
      if(!US_TABLE_LOADING)
      $.ajax({
        url : GET_GADGETS_URI,
        data: {
          "dashboardId": this.dashboardId
        },
        success: function(gadgetList) {
          UserStoryController.prototype.drawGadget(gadgetList);
        },
        error: function(response) {
          alert("Failed to send ajax for User story table");
          UserStoryController.prototype.showUsTable();
        },
        beforeSend: function() {
          US_TABLE_LOADING = true;
          UserStoryController.prototype.hideUsTable();
        }
      }).done(function(gadgetList) {
        if (debugAjaxResponse(gadgetList)) {
          return;
        }
        console.log(gadgetList);
      });
    },
    drawGadget: function (gadgetList) {
      for (var i = 0; i < gadgetList.length; i++) {
        if (gadgetList[i]["type"] == US_TYPE && gadgetList[i]["dashboardId"] == dashboardId) {
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
          if(gadgetList[i]["selectAllStory"] == true){
            $(htmlCheckAllStoryId).prop("checked",true);
            $(htmlContainerStoryId).fadeOut();
          }
          else if (gadgetList[i]["stories"] != null) {
            $(htmlCheckAllStoryId).prop("checked",false);
            $(htmlContainerStoryId).fadeIn();
            $(htmlLoaderStoryId).fadeOut();
            appendToSelect(true, gadgetList[i]["stories"], htmlSelectStoryId);
            $(htmlSelectStoryId).val(gadgetList[i]["stories"]);
          }

          if(gadgetList[i]["selectAllEpic"] == true){
            $(htmlCheckAllEpicId).prop("checked", true);
            $(htmlContainerEpicId).fadeOut();
          }
          else if (gadgetList[i]["epic"] != null) {
            $(htmlCheckAllEpicId).prop("checked", false);
            $(htmlContainerEpicId).fadeIn();
            $(htmlLoaderContainerEpicId).fadeOut();
            appendToSelect(true, gadgetList[i]["epic"], htmlSelectEpicId);
            $(htmlSelectEpicId).val(gadgetList[i]["epic"]);
          }

          if (gadgetList[i]["metrics"] != "" && gadgetList[i]["metrics"] != null) {
            $(htmlMetricId).val(gadgetList[i]["metrics"]);
          }
          console.log("prepare to draw table");
          UserStoryController.prototype.drawTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
          break;
        }
      }
    },
    drawTable: function (gadgetId, metricArray) {
      var columnList = getColumnArray(metricArray, false);
      var jsonObjectForUsTable;

      $(htmlContainerTableId).html("");
      $.ajax({
        url: GET_DATA_URI,
        method: "GET",
        data: {
          "id": gadgetId
        },
        beforeSend: function() {
          UserStoryController.prototype.hideUsTable();
        },
        error: function(response) {
          alert("Error: Failed to send ajax");
          console.log(response);
          UserStoryController.prototype.showUsTable();
        }
      }).done(
        function(responseData) {
          var index = 0;
          if (debugAjaxResponse(responseData)) {
            return;
          }
          jsonObjectForUsTable = responseData;
          console.log(jsonObjectForUsTable["data"]);

          $.each(jsonObjectForUsTable["data"], function(epicKey,
            storyArray) {
            if (storyArray["issueData"].length != 0) {
              var customTableId = "us-table-" + index;
              var usTableDataSet = [];
              var usIndividualTable;
              appendTemplateTable(customTableId, epicKey + ": "+ storyArray["summary"],
                htmlContainerTableId);
              $("#" + customTableId).append(TEMPLATE_HEADER_FOOTER);
              console.log("Pass each function");

              for (var i = 0; i < storyArray['issueData'].length; i++) {
                var aStoryDataSet = [];
                aStoryDataSet.push(storyArray['issueData'][i]["key"]["key"]);
                aStoryDataSet.push(storyArray['issueData'][i]["key"]["summary"]);
                aStoryDataSet.push(storyArray['issueData'][i]["key"]["priority"]["name"]);
                aStoryDataSet.push(storyArray['issueData'][i]["unexecuted"]);
                aStoryDataSet.push(storyArray['issueData'][i]["failed"]);
                aStoryDataSet.push(storyArray['issueData'][i]["wip"]);
                aStoryDataSet.push(storyArray['issueData'][i]["blocked"]);
                aStoryDataSet.push(storyArray['issueData'][i]["passed"]);
                aStoryDataSet.push(storyArray['issueData'][i]["planned"]);
                aStoryDataSet.push(storyArray['issueData'][i]["unplanned"]);
                usTableDataSet.push(aStoryDataSet);
              }

              usIndividualTable = $("#" + customTableId).DataTable({
               "autoWidth": true,
                paging: false,
                data: usTableDataSet,
                columns: [{
                  title: "User Story"
                },{
                  title: "SUMMARY"
                },{
                  title: "PRIORITY"
                }, {
                  title: "UNEXECUTED",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }, {
                  title: "FAILED",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }, {
                  title: "WIP",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }, {
                  title: "BLOCKED",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }, {
                  title: "PASSED",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }, {
                  title: "PLANNED",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }, {
                  title: "UNPLANNED",
                  "render": function(data, displayOrType, rowData, setting){
                    return createIssueLinks(data, displayOrType, rowData, setting);
                  }
                }]
              });
              usIndividualTable.columns(columnList).visible(false);
              index++;
            }
          });
          UserStoryController.prototype.showUsTable();
        });

    },
    reloadUSList: function () {
      if ($(htmlCheckAllStoryId).prop("checked,true")) {
        return;
      } else if ($(htmlSelectEpicId + " option").length == 0) {
        cleanSelect(htmlSelectStoryId);
        return;
      }
      var options = $(htmlSelectEpicId + " option")
      var values = $.map(options, function(option) {
        return option.value;
      });
      var jsonString = JSON.stringify(values);
      $.ajax({
        url: GET_STORY_URI,
        data: {
          epics: jsonString
        },
        beforeSend: function() {
          UserStoryController.prototype.hideUsEpic();
        },
        success: function(){
          if (data == null || data.length == 0) {
            return;
          }

          $(htmlSelectStoryId).find("option").remove().end();
          $.each(data, function(key, list) {
            for (var i = 0; i < list.length; i++) {
              $(
                '<option value="' + list[i] + '">' + list[i] + '</option>').appendTo(
                htmlSelectStoryId);
            }
          });
        },
        error: function(){
          alert("Failed to reload User Story List");
          UserStoryController.prototype.showUsEpic();
        }
      }).done(
        function() {
          UserStoryController.prototype.showUsStory();
        });
    },
    addEpic: function () {
      var options = $(htmlSelectEpicAvailableId + " option:selected").clone();
      if (options.length == 0) {
        return;
      }
      $(htmlSelectEpicId).append(options);
      $(htmlSelectEpicAvailableId +" option:selected").remove();
      UserStoryController.prototype.reloadUSList();
    },

    removeEpic: function () {
      var options = $(htmlSelectEpicId+" option:selected").clone();
      if (options.length == 0) {
        return;
      }
      $(htmlSelectEpicAvailableId).append(options);
      $(htmlSelectEpicId + " option:selected").remove();
      UserStoryController.prototype.reloadUSList();
    },


    addAllEpic: function () {
      var options = $(htmlSelectEpicAvailableId + " option").clone();
      if (options.length == 0) {
        return;
      }
      $(htmlSelectEpicId).append(options);
      $(htmlSelectEpicAvailableId + " option").remove();
      UserStoryController.prototype.reloadUSList();
    },

    removeAllEpic: function () {
      var options = $(htmlSelectEpicId + " option").clone();
      if (options.length == 0) {
        return;
      }
      $(htmlSelectEpicAvailableId).append(options);
      $(htmlSelectEpicId + " option").remove();
      UserStoryController.prototype.reloadUSList();
    },
    showUsEpic : function () {
      $(htmlLoaderContainerEpicId).fadeOut();
      $(htmlContainerEpicAvailableId).fadeIn();
    },

    hideUsEpic : function () {
      $(htmlLoaderContainerEpicId).fadeIn();
      $(htmlContainerEpicAvailableId).fadeOut();
    },

    showUsStory : function () {
      $(htmlLoaderStoryId).fadeOut();
      $(htmlSelectStoryId).fadeIn();
    },

    hideUsStory: function () {
      $(htmlSelectStoryId).fadeOut();
      $(htmlLoaderStoryId).fadeIn();
    },

    showUsTable : function () {
      $(this.htmlContainerTableId).fadeIn();
      $(this.htmlLoaderTableId).fadeOut();
    },

    hideUsTable: function () {
      $(this.htmlContainerTableId).fadeOut();
      $(this.htmlLoaderTableId).fadeIn();
    }
  }
}

inheritPrototype(UserStoryController, GadgetController);