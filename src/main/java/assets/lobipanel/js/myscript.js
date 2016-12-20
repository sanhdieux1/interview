var head = "http://localhost:1373/executeSearch"
$(function(){
	$.ajax({
		url : head,
		headers: {
            //The following header does nothing except show that 
            //if custom headers are sent the server must accept them with
            //Access-Control-Allow-Headers. Such headers trigger a preflight request
            'X-Foo-for-demo-only': 'I-do-nothing' 
        },
		data : {
			projectName : "FNMS 557x",
			type : "Epic",
			release : "aaa",
			metric : "aaa"
		},
		contentType : 'application/json',
		success : function(data){
			console.log(data);
		}
	})
});