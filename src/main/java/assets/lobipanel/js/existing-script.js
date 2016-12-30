
    	window.setTimeout(function() {
	    	$(".alert").fadeTo(500, 0).slideUp(500, function(){
	        	$(this).remove(); 
	    	});
		}, 3000);
    	
	
    	function setCookie(cname, cvalue) {
		    document.cookie = cname + "=" + cvalue + ";path=" + window.location.pathname;
		}
		
		function getCookie(cname) {
		    var name = cname + "=";
		    var ca = document.cookie.split(';');
		    for(var i = 0; i < ca.length; i++) {
		        var c = ca[i];
		        while (c.charAt(0) == ' ') {
		            c = c.substring(1);
		        }
		        if (c.indexOf(name) == 0) {
		            return c.substring(name.length, c.length);
		        }
		    }
		    return "";
		}
		
		function deleteCookie(cname) {
			document.cookie = cname + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
		}
   
	
		
        $(function() {
        	<!-- home page -->
            $('.mypanel').lobiPanel({
                reload: false,
                close: false,
                editTitle: false
            });
            <!-- show dashboard -->
            $('.mypanel1').lobiPanel({
                reload: false,
                close: false,
                editTitle: false,
                unpin: false,
                expand: false,
                minimize: false
            });
            <!-- two panels -->
            $('#lobipanel-multiple').find('.panel').lobiPanel({
                reload: false,
                // close: false,
                editTitle: false,
                sortable: true
            });
            
            // new dashboard
            if (getCookie("sonar-div") != "") {
            	document.getElementById("sonar-div").className = getCookie("sonar-div");
            }

            if (getCookie("review-div") != "") {
            	document.getElementById("review-div").className = getCookie("review-div");
            }

            if (getCookie("sonar") == "open") {
            	document.getElementById("sonar-btn").disabled = true;
            }

            if (getCookie("review") == "open") {
            	document.getElementById("review-btn").disabled = true;
            }

            // if panels are closed
            if (getCookie("sonar") == "close" || getCookie("sonar") == "") {
            	$('.panel-info').lobiPanel('close');
            	if(null == document.getElementById("sonar-btn")){
            		return;
            	}
            	document.getElementById("sonar-btn").disabled = false;
            }

            if (getCookie("review") == "close" || getCookie("review") == "") {
            	$('.panel-danger').lobiPanel('close');
            	if(null == document.getElementById("review-btn")){
            		return;
            	}
            	document.getElementById("review-btn").disabled = false;
            }

            // close panels
            $('.panel-info').on('beforeClose.lobiPanel', function() {
            	setCookie("sonar", "close");
            	window.location.reload();
            });

            $('.panel-danger').on('beforeClose.lobiPanel', function() {
            	setCookie("review", "close");
            	window.location.reload();
            });
        });
        
        $(document).ready(function() {
			$("#sonar-config").hide();
			$("#sonar-config-toggle").click(function() {
				$("#sonar-config").toggle();
				var $this = $(this);
				$this.toggleClass("sonar-config-toggle");
				if ($this.hasClass("sonar-config-toggle")) {
					$this.text("Hide Configuration");
				} else {
					$this.text("Show Configuration");
				}
			});
	
			$("#odreview-config").hide();
			$("#odreview-config-toggle").click(function() {
				$("#odreview-config").toggle();
				var $this = $(this);
				$this.toggleClass("odreview-config-toggle");
				if ($this.hasClass("odreview-config-toggle")) {
					$this.text("Hide Configuration");
				} else {
					$this.text("Show Configuration");
				}
			});
			
			$("#epic-config").hide();
			$("#epic-config-toggle").click(function() {
				$("#epic-config").toggle();
				var $this = $(this);
				$this.toggleClass("epic-config-toggle");
				if ($this.hasClass("epic-config-toggle")) {
					$this.text("Hide Configuration");
				} else {
					$this.text("Show Configuration");
				}
			});
			
			$("#us-config").hide();
			$("#us-config-toggle").click(function() {
				$("#us-config").toggle();
				var $this = $(this);
				$this.toggleClass("us-config-toggle");
				if ($this.hasClass("us-config-toggle")) {
					$this.text("Hide Configuration");
				} else {
					$this.text("Show Configuration");
				}
			});
			
			$("#cycle-config").hide();
			$("#cycle-config-toggle").click(function() {
				$("#cycle-config").toggle();
				var $this = $(this);
				$this.toggleClass("cycle-config-toggle");
				if ($this.hasClass("cycle-config-toggle")) {
					$this.text("Hide Configuration");
				} else {
					$this.text("Show Configuration");
				}
			});
			
			$("#assignee-config").hide();
			$("#assignee-config-toggle").click(function() {
				$("#assignee-config").toggle();
				var $this = $(this);
				$this.toggleClass("assignee-config-toggle");
				if ($this.hasClass("assignee-config-toggle")) {
					$this.text("Hide Configuration");
				} else {
					$this.text("Show Configuration");
				}
			});
		});
    
	
		function sonarClick() {
			console.log("add sonar gadget");
			setCookie("sonar", "open");
			window.location.reload();
		};

		function reviewClick() {
			console.log("add review gadget");
			setCookie("review", "open");
			window.location.reload();
		};

		function layouta() {
			setCookie("sonar-div", "col-md-12");
			setCookie("review-div", "col-md-12");
			window.location.reload();
		};

		function layoutaa() {
			setCookie("sonar-div", "col-md-6");
			setCookie("review-div", "col-md-6");
			window.location.reload();
		};

		function layoutba() {
			setCookie("sonar-div", "col-md-4");
			setCookie("review-div", "col-md-8");
			window.location.reload();
		};

		function layoutab() {
			setCookie("sonar-div", "col-md-8");
			setCookie("review-div", "col-md-4");
			window.location.reload();
		};

		function layoutaaa() {
			setCookie("sonar-div", "col-md-4");
			setCookie("review-div", "col-md-4");
			window.location.reload();
		};
	
	
		$("input[name='release']").change(function() {
			$.get('/release/ialist/' + $(this).val(), function(data) {
				$('#ia_list').find('option').remove().end();
				var ias = data.slice(1, -1).split(',');
				for (var i = 0; i < ias.length; i++) {
				 	$('<option value="'+ias[i]+'">'+ias[i]+'</option>').appendTo('#ia_list');
				}
			});
		});
	
	
		$('ul.nav li.dropdown').hover(function() {
		  $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeIn(500);
		}, function() {
		  $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeOut(500);
		});
	