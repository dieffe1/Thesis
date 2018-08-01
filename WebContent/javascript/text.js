$(document).ready(function() { 
	
	var sp = document.location.href.split("&");
	if(sp[1] != undefined)
		var mode = sp[1].split("\=")[1];

	editor = CodeMirror.fromTextArea($('#fileCode')[0], {
		tabSize : 4,
		lineNumbers : true,
		matchBrackets : true,
		mode : "text/x-java",
		extraKeys : {
			"Ctrl-Space" : "autocomplete"
		},
		autoCloseBrackets: true,
	});
	
	if(mode == "read")
		editor.setOption("readOnly", true);
	else
		$('#lock').hide();
	
	editor2 = CodeMirror.fromTextArea($('#textarea2')[0], {
		tabSize : 4,
		lineNumbers : true,
		mode : "text/x-java",
		readOnly: 'nocursor'
	});

	$.ajax({
		url : 'readText',
		success: function(response){
			var string = response.substring(0,4);
			if(string == "lock"){
				editor.setOption("readOnly", true);
				window.setInterval(function(){
					$.ajax({
						url : 'readText',
						success: function(responseText){
							if(responseText == "removed") {
								swal("Warning", "File was deleted!", "warning").then(() => {
									document.location.href = "page?action=homepage";								
								})
							}
							else
								editor.setValue(responseText.substring(4));
						},
						type : 'GET'
					});
				}, 1000);	

			}
			else {
				window.setInterval(function(){
					$.ajax({
						url : 'saveText',
						data : {
							text : editor.getValue()
						},
						type : 'POST'
					});
				}, 1000);
			}
		},
		type : 'GET'
	});
	
});

window.onload = function() {
	$('#a_drop').show();
	var user = $('#user').html();
	load(user);
	window.setInterval(load, 5000, $('#user').html());
	
}
function createCheckfile() {
	$.ajax({
		url: 'createCheckfile',
		success: function(response){
			if(response == "yes") {
				var name = null;
				swal("Please enter description:", {
					content: "input",
				})
				.then((value) => {
					name = value;
					if (name != null) {
						$.ajax({
							url: 'createCheckfile',
							data : {
								description : name
							},
							success: function(){
								swal("Created", "Checkpoint created successfully!", "success");
							},
							error : function(){ 
								alert("error");
							},
							type : 'POST'
						});
					}
				})
			}
			else if(response == "no") {
				swal("Error", "Impossible to create checkpoint, file in edit!","error");
			}
		},
		error : function(){ 
			alert("error");
		},
		type : 'GET'
	});


}

var fileConsulted;

function consult() {
	$.ajax({
		url: 'restoreCheckfile',
		success: function(response){
			var div = document.createElement("div");
				div.className = "text-center";
			$.each(JSON.parse(response), function(idx, obj) {
				var button = document.createElement("button");
				button.className = "btn btn-warning";
				
				button.onclick = function(){
					$('#contenuto').show();
					editor2.setValue(obj.code);
					$('#mainarea').attr("style","float: left; width: 45%; margin-left:2%;");
					fileConsulted = obj.id;
					$("div#contenuto h2").html(obj.name);
				};

				var span = document.createElement("span");
					span.className = "info-box-icon bg-yellow";
				var iconFolder = document.createElement("i");
					iconFolder.className = "fa fa-file icon_folder";
				var br = document.createElement("br");
				var folderName = document.createElement("p");
					folderName.className = "names";
					folderName.innerHTML = obj.name;
				iconFolder.appendChild(br);
				iconFolder.appendChild(folderName);
				span.appendChild(iconFolder);
				button.appendChild(span);
				div.appendChild(button);
				$('#ripristina').show();
			})
			swal("Choose","Please select which file version do you want to consult","info",{
				content:div,
			});
		},
		error : function(){ 
			alert("error");
		},
		type : 'GET'
	});
}

function findString() {
	var name = null;
	var type = null;
	swal("Please enter string to find:", {
		content:"input"
	})
	.then((value) => {
		if(value != null) {
			name = value;

			swal("Info","Where do you want to search the string?","info", {
				buttons:{
					checkpoint:"Checkpoint",
					project:"Project"
				}
			})
			.then((value)=> {
				type= value;
				if (name != null && name != "") {
					$.ajax({
						url: 'findString',
						data : {
							text : name,
							type : type
						},
						success: function(response){
							if(response == "empty"){
								swal("Warning","String not found!","warning");
							}
							else {
								var div = document.createElement("div");
								div.className = "text-center";
								$.each(JSON.parse(response), function(idx, obj) {
									var button = document.createElement("button");
									button.className = "btn btn-warning";

									button.onclick = function(){
										$('#mainarea').attr("style","float: left; width: 45%; margin-left:2%;");
										$('#contenuto').show();
										editor2.setValue(obj.code);
										$("div#contenuto h2").html(obj.name);
									};

									var span = document.createElement("span");
										span.className = "info-box-icon bg-yellow";
									var iconFolder = document.createElement("i");
										iconFolder.className = "fa fa-file icon_folder";
									var br = document.createElement("br");
									var folderName = document.createElement("p");
										folderName.className = "names";
										folderName.innerHTML = obj.name;
									iconFolder.appendChild(br);
									iconFolder.appendChild(folderName);
									span.appendChild(iconFolder);
									button.appendChild(span);
									div.appendChild(button);
									$('#ripristina').hide();
								})
								swal("Found!", "Searched string: " + name, "success",{
//									title: "String: " + name,
									content: div,
								});
							}
						},
						error : function(){ 
							alert("error");
						},
						type : 'GET'
					});
				}
			})
		}
	})
}

function ripristina() {
	var resp;
	$.ajax({
		url: 'restoreCheckfile',
		data : {
			fileConsulted : fileConsulted
		},
		success : function(response) {
			if(response != "not") {
				var mode = true;
				swal({
					text: "Salvare stato attuale del file? ", 
					buttons: {
						catch: "YES!",
						defeat:  "NO!"
					},
				})
				.then((value) => {
					switch (value) {
						case "catch":
							mode = true;
							break;
						case "defeat":
							mode = false;
							break;
						default:
							return;
					}
					if (mode == true) {
						var name = null;
						swal("Please enter description:", {
							content: "input",
						})
						.then((value) => {
							name = value;
							if (name != null) {
								$.ajax({
									url: 'createCheckfile',
									data : {
										description : name
									},
									success: function(){
										swal("Created", "Checkpoint created successfully!", "success");
									},
									error : function(){ 
										alert("error");
									},
									type : 'POST'
								});
							}
							$.ajax({
								url: 'restoreCheckfile',
								data : {
									fileConsulted : fileConsulted
								},
								success: function(response){
									swal("Success","Checkpoint restored successfully!","success");
									editor.setValue(response);
									nascondi();
								},
								type:'POST'
							});
						})
					}
					else {
						$.ajax({
							url: 'restoreCheckfile',
							data : {
								fileConsulted : fileConsulted
							},
							success : function(response) {
								editor.setValue(response);
								nascondi();
							},
							type:'POST'
						});
					}
				});

			}
			else {
				swal("Error", "Impossible to restore checkpoint, file in edit!","error");
			}
		},
		type:'POST'
	});

}
function nascondi() {
	$('#mainarea').removeAttr("style", null);
	$('#contenuto').hide();
}

function closeFile() {
	document.location.href="page?action=homepage";
}

function removeFile() {
	$.ajax({
		url : 'removeFile',
		success: function(response){
			if(response == "yes") {
				swal("Removed", "File removed successfully!", "success").then(() => {
					document.location.href = "page?action=homepage";								
				})
			}
			else if(response == "no") {
				swal("Error", "Impossible to remove file, file in edit!","error");
			}
		},
		type : 'POST'
	});
}

function renameFile() {
	swal("Please enter new name:", {
		content: "input",
	})
	.then((value) => {
		if (value != null && value != "") {
			$.ajax({
				url: 'renameFile',
				data : {
					name :  value
				},
				success: function(response){
					if(response == "exist")
						swal("Error", "There is already a file with the same name", "error").then(()=>{
							renameFile();
						});
					else {
						swal("Renamed","Successful renamed!", "success").then(() => {
							$('h2').html(value);								
						})
					}
				},
				error : function(){ 
					alert("error");
				},
				type : 'POST'
			});
		}
	})
}
