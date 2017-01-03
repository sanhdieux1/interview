/*
 * Abstract Controller class
 * reorganizing the greenhopper gadget into OOP architecture 
 * (UNUSED)
 */

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