function InitGoogleCustomSearchEngine(){
    var cx = '012945477697512725722:4jk5v58a-jg';
    var gcse = document.createElement('script');
    gcse.type = 'text/javascript';
    gcse.async = false;
    gcse.src = 'https://cse.google.com/cse.js?cx=' + cx;
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(gcse, s);
}

function compile(){
	$.ajax({
		url : 'compile',
		success : function(response){
			if(response == "ok")
				swal("Compiled", "Successful compilation!", "success");
			else {	
				var div = document.createElement("div");
				var area = document.createElement("textarea");
				area.style.width = "100%";
				area.style.fontSize = "12px";
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

function execute(){
	$.ajax({
		url : 'execute',
		success : function(){
			swal("Executed", "Successful!", "success");
		},
		error : function(){
			alert("execution error");
		},
		type : 'GET'
	})
}