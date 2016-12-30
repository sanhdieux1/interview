var productTable = null;

function createProducTable() {
    productTable = $("#productTable").DataTable({
        "searching" : false,
        "paging":   false,
        "ordering": false,
        "info":     false,
        "columnDefs": [ {
            "targets": -1,
            "data": null,
            "style" : "text-align: center",
            "defaultContent": /*'<td style="text-align: center">'*/
                 '<button type="button" class="btn btn-danger">'
                +'<span class="glyphicon glyphicon-trash" aria-hidden="true"> Delete</span>'
                + '</button>'
                + '<button type="button" class="btn btn-info">'
                +      '<span class="glyphicon glyphicon-edit" aria-hidden="true"> Edit</span>'
                + '</button>'
                /*+ '</td>'*/
        } ]
    });
}


$(function() {

    createProducTable();

    $('#addProduct').on('click', function() {
        $.ajax({
            method : "POST",
            url : "/product/insert",
            dataType: "json",
            data: {
                "product" : $("#productInput").val()
            },
            success : function (data){
                if(productTable!=null){
                    productTable.row.add([ 
                        counter + '.1', 
                        counter + '.2']).draw(false);
                }
            },
            error: function (data){

            }
        });


    });
});
