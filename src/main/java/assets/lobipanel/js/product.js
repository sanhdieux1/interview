var productTable = null;

function createProducTable() {
    productTable = $("#productTable").DataTable(
            {
                "searching" : false,
                "paging" : false,
                "ordering" : false,
                "info" : false,
                "columnDefs" : [ {
                    "targets" : -1,
                    "data" : null,
                    "class" : "action",
                    "defaultContent" : 
                            '<button type="button" class="btn btn-danger delete-action"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Delete</span>'
                            + '</button>'

                } ]
            });
}

function deleteProduct(product, callback) {
    $.ajax({ method : "POST",
    url : "/product/delete",
    dataType : "json",
    data : { "product" : product
    },
    dataContext : "json",
    success : function(data) {
        if (productTable != null && data.type == "success" && data.data >0) {
            callback();
        }
    },
    error : function(data) {
        alert(data.statusText);
    }
    });
}

function addEventAction() {
    $('.action').off();
    $('.action').unbind();
    $('.action').on('click', '.delete-action', function(e) {
        if (productTable != null) {
            var row = productTable.row($(this).parents('tr'));
            var removeRowFunction = function() {
                row.remove().draw();
            }
            deleteProduct(row.data()[0], removeRowFunction);
        }
        
    });
}

$(function() {
    createProducTable();
    addEventAction();

    $('#addProduct').on('click', function() {
        var newProduct = $("#productInput").val();
        $.ajax({ method : "POST",
        url : "/product/insert",
        dataType : "json",
        data : { "product" : newProduct
        },
        dataContext : "json",
        success : function(data) {
            if (productTable != null && data.type == "success" && data.data == true) {
                productTable.row.add([ newProduct, "" ]).draw(false);
                addEventAction();
                $("#productInput").val("");
            }
        },
        error : function(data) {
            alert(data);
        }
        });
    });

});
