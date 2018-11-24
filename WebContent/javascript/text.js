var readIntervalID;
var saveIntervalID;
var checkErrorsIntervalID;
var loadChatIntervalID;
var checkModeIntervalID;

$(document).ready(function() { 
	var mode = location.hash.split("\=")[1];
	
	var a1 = $("<a></a>").text("Compile");
	a1.attr("onclick", "compile();");
	$("#lista_opzioni").append($("<li></li>").append(a1));

	var a2 = $("<a></a>").text("Execute");
	a2.attr("onclick", "execute();");
	$("#lista_opzioni").append($("<li></li>").append(a2));

	$('#close').attr("onclick", "closeFile(\"#firstFile\");");
	
	initEditor();
	
	if(mode == "read") 
		editor.setOption("readOnly", true);
	else 
		$('#lock').hide();
	
	checkModeIntervalID = window.setInterval(checkMode, 500, editor); 
	
	
	editor2 = CodeMirror.fromTextArea($('#textarea2')[0], {
		tabSize : 4,
		lineNumbers : true,
		mode : "text/x-java",
		readOnly: 'nocursor'
	});
	
	//autocompletamento
	$(document).keypress(function(e){
		// 46 == punto
		if(e.keyCode == '46') {
			var A1 = editor.getCursor().line;
		    var A2 = editor.getCursor().ch;
		    
		    //verifico che non venga premuto il punto in mezzo ad una parola (par.ola)
		    var afterDot = editor.getLine(A1)[A2];
		    if(afterDot == "" || afterDot == " " || afterDot == undefined) {
		    	// il -1 perché gli indici di posizione vengono presi prima dell'inserimento del punto
		    	var B1 = editor.findWordAt({line: A1, ch: A2-1}).anchor.ch;
		    	var B2 = editor.findWordAt({line: A1, ch: A2-1}).head.ch;
		    	
		    	var object = editor.getRange({line: A1,ch: B1}, {line: A1,ch: B2});
		    	
		    	var line = A1, begin = B1, end = B2;
		    	
		    	//verifico che ciò che è stato catturato possa essere effettivamente il nome di una variabile [aA0 - zZ9]
		    	if(object.match(/\w+/) != null){
		    		console.log("autoComplete -> " + object);
		    		$.ajax({
		    			url : 'compile',
		    			type : 'GET',
		    			data : {
		    				autocomplete : "true",
		    				line : line,
		    				begin : begin,
		    				end : end
		    			},
		    			success : function() {
		    				$.ajax({
								url : 'autocomplete',
								data : {
									obj : object
								},
								success : function(response) {
									 CodeMirror.registerHelper("hint", "myHint", function(editor, options) {
							                var cur = editor.getCursor(), token = editor.getTokenAt(cur);
							                var start = token.start+1, end = token.end;
							                var list = [];
							                $.each(JSON.parse(response), function(idx, obj) {
							                	list.push(obj);
							                });
							                return {
							                    list: list,
							                    from: CodeMirror.Pos(cur.line, start),
							                    to: CodeMirror.Pos(cur.line, end)
							                }
							            }); 
									 CodeMirror.showHint(editor, CodeMirror.hint.myHint);
								}, 
								error : function(){
									alert("autoCompl");
								},
								type : 'GET'
							})
						},
		    		});
		    		
		    	}
		    		
		    	
		    }
		    
		}
	})

	readAndSaveCode(editor);
	checkErrorsIntervalID = window.setInterval(checkErrors, 2000, editor); 
});

window.onload = function() {
	$('#chat_drop').show();
	$('#option_drop').show();
	$('#projectsTreeButton').show();
	initTreeSidebar();
	var user = $('#user').html();
	load(user);
	loadChatIntervalID = window.setInterval(load, 5000, $('#user').html());
	
}

function initEditor() {
	editor = CodeMirror.fromTextArea($('#fileCode')[0], {
		mode : "text/x-java",
		tabSize : 4,
		indentWithTabs: true,
		lineNumbers : true,
		matchBrackets : true,
		autoCloseBrackets: true,
		autofocus: true,
		scrollbarStyle: "overlay",
		extraKeys : {
			"Ctrl-Space" : "autocomplete"
		}
	});
}

function checkMode(editor) {
	var mode = location.hash.split("\=")[1];
	
	if(mode == "read") {
		$('#lock').show();
		editor.setOption("readOnly", true);
	} else {
		$('#lock').hide();
		editor.setOption("readOnly", false);
	}
}

function readAndSaveCode(editor) {
	$.ajax({
		url : 'readText',
		success: function(response){
			var string = response.substring(0,6);
			if(string == "locked") {
				editor.setOption("readOnly", true);
				readIntervalID = window.setInterval(function() {
					$.ajax({
						url : 'readText',
						success: function(responseText){ 
							if(responseText == "removed") {
								swal("Warning", "File was deleted!", "warning").then(() => {
									document.location.href = "page?action=homepage";								
								})
							} else if(responseText.substring(0,7) == "canLock") {
								location.hash = "mode=write";
								editor.setValue(responseText.substring(7));
								clearInterval(readIntervalID);
								clearInterval(saveIntervalID);
								readAndSaveCode(editor);
							} else
								editor.setValue(responseText.substring(6));
						},
						type : 'GET'
					});
				}, 500);	

			} else if(string != "locked" && string != "canLock"){		
				editor.setOption("readOnly", false);
				saveIntervalID = window.setInterval(function(){
					$.ajax({
						url : 'saveText',
						data : {
							text : editor.getValue()
						},
						type : 'POST'
					});
				}, 500);
			}
		},
		type : 'GET'
	});
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
// title: "String: " + name,
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

function closeFile(id) {
	$.ajax({
		url : 'page',
		data : {
			action : "closeFile"
		},
		success: function(response){ 
			if($('.nav-tabs').children().length == 1){
				document.location.href = "page?action=homepage";
			} else {
				$(id).remove();
				$('.nav-tabs').children()[0].click();
			}
		}
	});
	
}

function removeFile() {
	$.ajax({
		url : 'removeFile',
		success: function(response){
			if(response == "yes") {
				swal("Deleted", "File deleted successfully!", "success").then(() => {
					document.location.href = "page?action=homepage";								
				})
			}
			else if(response == "no") {
				swal("Error", "Impossible to delete file, edit in progress!","error");
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

var previousCode = "";

function checkErrors(editor) {
	if(previousCode !== editor.getValue()) {
		console.log("CheckErrors");		
		previousCode = editor.getValue();
		$.ajax({
			url : 'compile',
			data : {
				autocomplete : "false"
			},
			success : function(response){
				if(response.includes("error")) {	
					var text = "";
					$.each(JSON.parse(response), function(idx, obj) {
						text = text + obj + "\n";
					});
					
					var split = text.split("^");
					var numErrors = split[split.length-1].match(/.*(\d+).*/)[1];
					
					console.log("Found " + numErrors + " errors");
					$(".notifyjs-wrapper").remove();
					
					for(var i=0; i<numErrors; i++){
						var regex = /.*\/(\w+)\.java:(\d+).*/;
						var matches = split[i].match(regex);
						var fileName = matches[1];
						var lineNumber = matches[2];
						
						regex = /.*\s\error:\s(.*)/;
						var errorText = split[i].match(regex)[1];		
						
						if(fileName == $("#fileName").text())
							showErr(lineNumber, errorText);
					}
				}
			},
			error : function(){
				console.log("compilation error");
			},
			type : 'GET'
		})
	}
}

function showErr(line, error) {
	
	console.log("Found at: " + line)
	
	var currentLine = line-1;
	
	$(".CodeMirror-code").children()[currentLine].setAttribute("id", "line"+currentLine);
	
	$("#line" + currentLine + " pre span").attr("id", "showError"+currentLine);
	
	$("#showError" + currentLine).notify(error, {
		position: "right",
		autoHideDelay: 150000,
		clickToHide: false,
		className: 'error',
	});

//	$("#line"+currentLine+ " .notifyjs-container").attr("onclick", "showTextErr(\"" + error + "\", " + currentLine + ");");
}

function showTextErr(error, currentLine) {
	console.log(error);
	$("#line"+currentLine+ " pre div div div span").text(error);
}




jQuery.fn.tablerize = function(style) {
    return this.each(function() {
            var table = $('<table>');
            table.addClass("CodeMirror-hints");
            table.attr("style", style);
            var tbody = $('<tbody>');
            $(this).find('li').each(function(i) {
                   var values = $(this).html().split('*');
                   var tr = $('<tr>');
                   tr.addClass("CodeMirror-hint");
                   $.each(values, function(y) {
                	       split = values[y].split(" return ");
                           tr.append($('<td>').html(split[0]));
                           tr.append($('<td>').html("return " + split[1]));
                   });
                   tbody.append(tr);
            });
            $(this).after(table.append(tbody)).remove();
    });
};








