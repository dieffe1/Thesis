function createProject(type) {
	
	var name = null;
	swal("Please insert project name:", {
		title: "New project",
		content: "input",
		})
		.then((value) => {
			name = value;
			if (name != null && name != "") {
				$.ajax({
					url: 'createProject',
					data : {
						projectName : name,
						type : type
					},
					success : function(response){ 
						if(response == "exist"){
							swal("Error", "There is already a project with the same name!", "error").
								then(() => { createProject(type);})
						} else {
							swal("Created", "Project created successfully!", "success").then(() => {
								document.location.href = "page?action=homepage";								
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

var formdata = null;

function readMultipleFiles(evt) {
	var files = evt.target.files;
	formdata = new FormData();
	if (files) {
		for (var i=0, f; f=files[i]; i++) {
			formdata.append("files", f, f.name);
			console.log(f.webkitRelativePath);
		}
	} else {
		alert("Failed to load files");
	}
}

function upload(name) {
	if (formdata == null) {
		swal("Error!","You have to select a directory before!","error");
	} else {
		var xhttp = new XMLHttpRequest();
		xhttp.open("POST", "upload");
		xhttp.send(formdata);
		xhttp.onreadystatechange = function() {
			if(xhttp.readyState == 4 && xhttp.status == 200){
				if(xhttp.response == "error"){
					swal("Error!", "You can upload only source files!", "error");
				} else {
					tryAgain(xhttp.response, name);
				}
			}
		};
	}
}

function uploadRequest(){
	var inputFile = document.createElement("input");
	var name = null;
	inputFile.type = "file";
	inputFile.classList = "btn btn-block bg-red text-center";
	inputFile.setAttribute("webkitdirectory", true);
	inputFile.id = "inputFile";
	inputFile.addEventListener('change', readMultipleFiles, false);
	swal("Please insert project name:", {
		title: "Upload Project",
		content: "input",
		}).then((value) => {
			name = value;
			if (name != null) {
				swal({
					button: "Upload",
					title: "Select a folder",
					content: inputFile,	
					
				}).then((value1) => {
					if(value1){
						upload(name);
					}
				});
			}
		})
}


function tryAgain(id, name){
	$.ajax({
		url : "renameProject",
		data : {
			id : id,
			name : name
		},
		success : function(response){
			if(response == "exist")
				swal("Error", "There is already a project with the same name", "error").then(()=>{
					swal("Please insert project name:", {
						title: "Upload Project",
						content: "input",
					}).then((value) => {
						tryAgain(id, value);
					});
				});
			else {
				swal("Uploaded", "Your projects has been uploaded successfully!", "success").then(() => {
					document.location.href = "page?action=homepage";								
				})
			}
		},
		type : "POST"
	});		
	
}