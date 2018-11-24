function InitGoogleCustomSearchEngine(){
    var cx = '012945477697512725722:4jk5v58a-jg';
    var gcse = document.createElement('script');
    gcse.type = 'text/javascript';
    gcse.async = false;
    gcse.src = 'https://cse.google.com/cse.js?cx=' + cx;
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(gcse, s);
}

function compile(compileAndRun){
	$.ajax({
		url : 'compile',
		data : {
			autocomplete : "false"
		},
		success : function(response){
			if(response == "ok")
				swal("Compiled", "Successful compilation!", "success").then(function(){
					if(compileAndRun.includes("true"))
						execute();
				});
			else if(response.includes("Main method not found!")) { 
				swal("Error!", "Main method not found!", "error");
			} else {	
				var div = document.createElement("div");
				var area = document.createElement("textarea");
				area.style.width = "100%";
				area.style.fontSize = "14px";
				area.rows = 10;
				area.disabled = true;
				area.value = "";
				$.each(JSON.parse(response), function(idx, obj) {
					area.value = area.value + obj + "\n";
				});
				console.log(area.value)
				div.append(area);
				InitGoogleCustomSearchEngine();
				var google = document.createElement("div");
				google.classList.add("gcse-search");
				div.append(google);
				swal("Error","","error",{
					content : div,
				});
			}
		},
		error : function(){
			alert("compilation error");
		},
		type : 'GET'
	})
}

/*
 * <div class="progress">
                <div class="progress-bar progress-bar-danger progress-bar-striped" role="progressbar" 
                aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 50%" id="swalProgress">
                  <span class="sr-only">40% Complete (success)</span>
                </div>
              </div>
 */


function execute(){
	
	var div = document.createElement("div");
		div.className = "progress";
	var div1 = document.createElement("div");
		div1.className = "progress-bar progress-bar-danger progress-bar-striped";
		div1.setAttribute("role", "progressbar");
		div1.setAttribute("aria-valuemin", "0");
		div1.setAttribute("aria-valuemax", "100");
		div1.setAttribute("id", "swalProgress");
	div.appendChild(div1);
	
	$.ajax({
		url : 'execute',
		success : function(response){ 
			
			if(response.includes("compile")) {
				swal("Compile?", "You have to compile first!", "info", {
					buttons : {
						"No" : "No, thanks!",
						"Yes" : "Yes, compile and run!"
					}
				}).then((value) => {
					if(value === "Yes"){
						compile("true");
					}
				})
			} else {
				started(60);
				swal("Wait for it!", "You will be redirect to the virtual machine!\nIt takes one minute.","info", {
					content : div,
					buttons : false,
					timer : 60000,
					closeOnClickOutside : false
				}).then(function(){
					$.ajax({
						url : 'manageVM',
						success : function(response) {
							console.log(response);
							window.open("https://" + response + ":8081");
						},
						error : function(){
							alert("manage virtual machine error");
						},
						type : 'GET'
					})
				});
			}
		},
		error : function(){
			alert("execution error");
		},
		type : 'GET'
	})
}

function started(duration) {
    var TotalSeconds = duration;
    var start = Date.now();
    var intervalSetted = null;

    function timer() {
        var diff = duration - (((Date.now() - start) / 1000) | 0);
        var seconds = (diff % 60) | 0;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        		
        $('#swalProgress').css({
            width: seconds * (100/TotalSeconds) + "%"
        });

        if (diff <= 0) {
            clearInterval(intervalSetted);
        }
    }

    timer();
    intervalSetted = setInterval(timer, 1000);
}