$(document).ready(() => {
	$('#empty').attr("onclick", "createProject(\"empty\")");
	$('#hello').attr("onclick", "createProject(\"hello\")");
});

function firstCheck(size1, size2) {
	if(size1===0 && size2===0){
		$('.content').hide();
		var section = $('<section>').addClass("content text-center");
		var h2 = $('<h2>').addClass("site-heading");
		var span = $('<span>').addClass("site-heading-lower").html("<b>Let's start!</b><br>Create your first project!");
		var button = $('<button>').addClass("btn btn-lg btn-success").html("Create your project");
		button.click(function(){
			swal("Create", "What project do you want to create?", "info", {
				buttons: {
					upload : "Uploaded",
					hello : "Hello",
					empty : "Empty",
				}
			}).then((value)=>{
				switch(value){
				case "empty":
					createProject("empty");
					break;
				case "hello":
					createProject("hello");
					break;
				case "upload":
					uploadRequest();
					break;
				}
			});
		});
		h2.append(span);
		section.append(h2);
		section.append(button);
		$('#explorer').append(section);
		
	}
	location.hash = $('#user').html();
	$('#chat_drop').hide();
	$('#option_drop').hide();
	$('#projectsTreeButton').hide();
	window.setInterval(load, 5000, $('#user').html());
};

function addPackage(projectname) {
	var name = null;
	swal("Please enter package name:", {
		content: "input",
	})
	.then((value) => {
		name = value;
		if (name != null && name != "") {
			$.ajax({
				url: 'addPackage',
				data : {
					name : name
				},
				success: function(response){
					if(response == "exist")
						swal("Error", "There is already a package with the same name", "error").then(()=>{
							addPackage(projectname);
						});
					else {
						swal("Created", "Package created successfully!", "success").then(()=>{
							var buttonFolder = $("<button></button>").addClass("btn btn-warning overflow-ellipsis");
							buttonFolder.attr("onclick", "showContent(\"" + value + "\","+"true)");
							var spanFolderBG = $("<span></span>").addClass("info-box-icon bg-yellow");
							var iconFolder = $("<i></i>").addClass("fa fa-folder icon_folder");
							var br = $("<br>");
							var folderName = $("<p></p>").text(name);
							folderName.attr("class", "names");
							iconFolder.append(br);
							iconFolder.append(folderName);
							spanFolderBG.append(iconFolder);
							buttonFolder.append(spanFolderBG);

							$("#contentDiv").append(buttonFolder);
						});
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

function removePackage() {
	$.ajax({
		url: 'removePackage',
		data:{
			hash : location.hash
		},
		success: function(response){

			if(response == "online"){
				swal("Cannot delete package", "Other users are working on it", "error");
			} else {
				swal("Removed", "Package removed successfully!", "success").then(() => {
					$('#returnButton').click();
				})
			}
		},
		type: 'POST'
	});
}

function addFile() {
	var name = null;
	var hash = location.hash.split("/");

	swal("Please enter file name:", {
		content: "input",
	}).then((value) => {
		name = value;
		if (name != null && name != "") {
			$.ajax({
				url: 'addFile',
				data : {
					name : name,
					packageName : hash[2]
				},
				success: function(response){
					if(response == "exist")
						swal("Error", "There is already a file with the same name", "error").then(()=>{
							addFile();
						});
					else
						swal("Created", "File created successfully!", "success").then(()=> {
							showContent(value, "true");
						})
				},
				error : function(){ 
					alert("error");
				},
				type : 'POST'
			});
		}
	})
}

function removeProject() {
	$.ajax({
		url: 'removeProject',
		success: function(response){
			if(response == "online"){
				swal("You can't delete this project!", "Someone is working on it!", "warning");
			} else {
				swal("Deleted", "Project deleted successfully!", "success").then(() => {
					window.location.href = "page?action=homepage";
				});
			}
		},
		type: 'POST'
	});
}

function renameProject(){
	swal("Please enter new name:", {
		content: "input",
	})
	.then((value) => {
		if (value != null && value != "") {
			$.ajax({
				url: 'renameProject',
				data : {
					name :  value
				},
				success: function(response){
					if(response == "exist")
						swal("Error", "There is already a project with the same name", "error").then(()=>{
							renameProject();
						});
					else {
						swal("Renamed","Successful renamed!", "success").then(() => {
							$("#name").html($("<b></b>").text(value));

							var hash = location.hash.split("/"); 
							hash[hash.length-1] = value;
							location.hash = hash[0];
							for(i = 1; i<hash.length; i++)
								location.hash += "/" + hash[i];
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

function renamePackage(){
	swal("Please enter new name:", {
		content: "input",
	})
	.then((value) => {
		if (value != null && value != "") {
			$.ajax({
				url: 'renamePackage',
				data : {
					packageName : location.hash.split("/")[2],
					name :  value
				},
				success: function(response){
					if(response == "exist")
						swal("Error", "There is already a package with the same name", "error").then(()=>{
							renamePackage();
						});
					else {
						swal("Renamed","Successfull renamed!", "success").then(() => {
							$("#name").html($("<b></b>").text(value));		

							var hash = location.hash.split("/"); 
							hash[hash.length-1] = value;
							location.hash = hash[0];
							for(i = 1; i<hash.length; i++)
								location.hash += "/" + hash[i];
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

function showBranch(name,isCreator) {
	$('#'+name).removeAttr("onclick");
	var selected = null;
	$.ajax({
		url: 'takeBranch',
		data : {
			nameProject :  name
		},
		success: function(response){
			var div = document.createElement("div");
				div.className = "text-center";
			if(response == "onlyMaster")
				showContent(name,isCreator);
			else {
				$.each(JSON.parse(response), function(idx, obj) {
					var button = document.createElement("button");
					button.className = "btn btn-warning";

					button.onclick = function(){
						selected = obj.name;
					};
						
					var span = document.createElement("span");
						span.className = "info-box-icon bg-yellow";
					var iconFolder = document.createElement("i");
						iconFolder.className = "fa fa-folder icon_folder";
					var br = document.createElement("br");
					var folderName = document.createElement("p");
						folderName.className = "names";
						folderName.innerHTML = obj.name;
					iconFolder.appendChild(br);
					iconFolder.appendChild(folderName);
					span.appendChild(iconFolder);
					button.appendChild(span);
					div.appendChild(button);
				})
				swal("Branch detected!", "Which branch do you want to open? \nPlease select one of these and confirm", "info",{
					content:div,
					buttons : {
						cancel : "Cancel",
						catch : "Ok",
					}
				}).then((value)=>{
					if(value === "catch"){
						showContent(selected,isCreator);
					} else {
						$('#'+name).attr("onclick", "showBranch(\""+name+"\","+isCreator+")");
					}
				}); 
			}
		},
		error : function(){ 
			alert("error");
		},
		type : 'GET'
	});
}

function showContent(name, isCreator){
	$('#'+name).removeAttr("onclick");
	$('#chat_drop').show();
	$('#option_drop').show();
	$('#projectsTreeButton').show();
	location.hash += "/" + name;
	$.ajax({
		url : 'page',
		data : {
			action : "open",
			hash: location.hash
		},
		type: 'GET',
		success : function(response){

			var hash = location.hash.split("/");
			if(hash.length == 2){ // project content
				var section = $("<section></section>").addClass("content");

				var returnButton = $("<button></button>").addClass("btn btn-danger");
				returnButton.attr("onclick", "back("+isCreator+");");
				returnButton.attr("id","returnButton");

				var icon = $("<i></i>").addClass("fa fa-arrow-left");
				returnButton.append(icon);

				section.append(returnButton);
				
				var a1 = $("<a></a>").attr("id", "add");
				a1.text("Add Package");
				a1.attr("onclick", "addPackage('"+ hash[1] +"');");
				$("#lista_opzioni").append($("<li></li>").append(a1));
				
				if(isCreator){
					var a2 = $("<a></a>").attr("id", "rename");
					a2.text("Rename project");
					a2.attr("onclick", "renameProject();");
					$("#lista_opzioni").append($("<li></li>").append(a2));

					var a3 = $("<a></a>").attr("id", "delete");
					a3.text("Delete project");
					a3.attr("onclick", "removeProject();");
					$("#lista_opzioni").append($("<li></li>").append(a3));
				}

				var a4 = $("<a></a>").text("Settings");
				a4.attr("href", "page?action=settings");
				$("#lista_opzioni").append($("<li></li>").append(a4));

				var a5 = $("<a></a>").text("Compile");
				a5.attr("onclick", "compile();");
				$("#lista_opzioni").append($("<li></li>").append(a5));

				var a6 = $("<a></a>").text("Execute");
				a6.attr("onclick", "execute();");
				$("#lista_opzioni").append($("<li></li>").append(a6));

				var a7 = $("<a></a>").text("Create new branch");
				a7.attr("onclick", "createBranch();");
				$("#lista_opzioni").append($("<li></li>").append(a7));

				var a8 = $("<a></a>").text("Merge with master");
				a8.attr("onclick", "merge();");
				$("#lista_opzioni").append($("<li></li>").append(a8));

				var h3 = $("<h3></h3>").addClass("site-heading text-center");
				var spanName = $("<span></span>").addClass("site-heading-lower");
				spanName.attr("id", "name");
				spanName.append($("<b></b>").text(hash[1]));

				h3.append(spanName);
				section.append(h3);

				$('#sidebarProjName').text(name);
				
				var newDiv = $("<div></div>").addClass("text-center");
				newDiv.attr("id", "contentDiv");
				
				
				$.each(JSON.parse(response), function(idx, obj) {
					// Explorer
					var buttonFolder = $("<button></button>").addClass("btn btn-warning");
					buttonFolder.attr("onclick", "showContent(\"" + obj.name + "\" ," + isCreator + ");");
					buttonFolder.attr("id", obj.name);
					var spanFolderBG = $("<span></span>").addClass("info-box-icon bg-yellow");
					var iconFolder = $("<i></i>").addClass("fa fa-folder icon_folder");
					var br = $("<br>");
					var folderName = $("<p></p>").text(obj.name);
					folderName.addClass("names");

					iconFolder.append(br);
					iconFolder.append(folderName);
					spanFolderBG.append(iconFolder);
					buttonFolder.append(spanFolderBG);
					newDiv.append(buttonFolder);
					
					// Tree Sidebar
					createTreeSidebar(location.hash, obj.name, false);
					
				});
				section.append(newDiv);
				$('#explorer').html(section);

				var user = $('#user').html();
				load(user);
			}
			else if (hash.length == 3){ // package content
				if(isCreator == false) {
					var a2 = $("<a></a>").attr("id", "rename");
						a2.text("Rename project");
						$("#lista_opzioni").append($("<li></li>").append(a2));
	
					var a3 = $("<a></a>").attr("id", "delete");
						a3.text("Delete project");
						$("#lista_opzioni").append($("<li></li>").append(a3));
				}
				
				$('#add').text("Add File");
				$('#add').attr("onclick", "addFile();");

				$('#rename').text("Rename package");
				$('#rename').attr("onclick", "renamePackage();");

				$('#delete').text("Delete package");
				$('#delete').attr("onclick", "removePackage();");

				$('#name').html($('<b></b>').text(hash[2]));
				$('#contentDiv').html("");
				$.each(JSON.parse(response), function(idx, obj) {
					var buttonFolder = $("<button></button>").addClass("btn btn-warning overflow-ellipsis");
					buttonFolder.attr("onclick", "showContent(\"" + obj.name + "\" ," + isCreator +");");
					buttonFolder.attr("id", obj.name);
					var spanFolderBG = $("<span></span>").addClass("info-box-icon bg-yellow");
					var iconFolder = $("<i></i>").addClass("fa fa-file icon_folder");
					var br = $("<br>");
					var folderName = $("<p></p>").text(obj.name);
					folderName.addClass("names");

					iconFolder.append(br);
					iconFolder.append(folderName);
					spanFolderBG.append(iconFolder);
					buttonFolder.append(spanFolderBG);
					$('#contentDiv').append(buttonFolder);
				});
			}
			else if (hash.length == 4){ // file content
				var mode = response.split("!-")[0];
				document.location.href = "page?action=openFile&mode="+mode;
			}
		}
	});
}

function back(isCreator){
	var hash = location.hash.split("/");
	if(hash.length == 2){
		document.location.href= "page?action=homepage";
		return;
	}
	location.hash = hash[0];
	for(i = 1; i<hash.length-2; i++)
		location.hash += "/"+hash[i];
	showContent(hash[hash.length-2], isCreator);
}

