
function initTreeSidebar(){ 
	var user = $('#user').html();
	var projectName = $('#projectID').html();

	var address = "#" + user + "/" + projectName;
	$.ajax({
		url : 'page',
		data : {
			action : "open",
			hash: address
		},
		type: 'GET',
		success : function(response){
			$('#sidebarProjName').text(projectName);
			
			$.each(JSON.parse(response), function(idx, obj) {
				createTreeSidebar(address, obj.name, true);
			});
			//creating first tab
			var name = $('#firstFile').text().match(/(\S+)/)[1];
			var pack = $('#fileCode').text().match(/.*\package\s(\w+)\.*/)[1];
			var newhash = address + "/" + pack + "/" + name;
			$('#firstFile').attr("onclick", "openFile(\"" + newhash + "\", false);");
			$('#fileName').html(name);
		}
	});


}

function createTreeSidebar(hash, packName, editorPage) { 
	console.log("inside " + packName + " " + hash)
	var packList = $('<li></li>').addClass("treeview");
	var iconPack = $('<i></i>').addClass("fa fa-circle-o");
	var packA = $('<a></a>');
	packA.append(iconPack);
	packA.append(packName);
	var spanPack = $('<span></span>').addClass("pull-right-container");
	var iconAngle = $('<i></i>').addClass("fa fa-angle-left pull-right");
	spanPack.append(iconAngle);
	packA.append(spanPack);
	packList.append(packA);
	
	var packUl = $('<ul></ul>').addClass("treeview-menu");

	var newhash = hash+"/"+packName;
	
	$.ajax({
		url : 'page',
		data : {
			action : "open",
			hash: newhash
		},
		type: 'GET',
		success : function(response){
			$.each(JSON.parse(response), function(idx, obj) {
				var fileList = $('<li></li>').addClass("treeview");
				var iconFile = $('<i></i>').addClass("fa fa-circle");
				var fileA = $('<a></a>');
				fileA.append(iconFile);
				fileA.append(obj.name);
				fileA.attr("id", obj.name);
				if(editorPage) {
					fileA.attr("onclick", "openFile(\"" + newhash + "/" + 
							obj.name + "\",true);");
				} else {
					fileA.attr("onclick", "location.hash = \"" + newhash + "\"; " +
							"showContent(\"" + obj.name + "\" , false);"); 
					// il parametro isCreator quando viene invocato showContent su un file è irrilevante
				}
				fileList.append(fileA);
				packUl.append(fileList);
			});
		}
	});

	packList.append(packUl);
	$('#sidebarPackagesZone').append(packList);
}

function openFile(hash, sidebar) {
	$.ajax({
		url : 'page',
		data : {
			action : "open",
			hash: hash
		},
		type: 'GET',
		success : function(response) {
			//get response values
			var values = response.split("!-");
			var mode = values[0]; 
			var userWriting = values[1];
			var fileName = values[2];
			var fileCode = values[3];
			// add tab
			if(sidebar) {
				var numTabs = $('.nav-tabs').children().length;
				$('li').removeClass("active");
				var li = $('<li></li>').addClass("active");
					li.attr("onclick", "openFile(\"" + hash + "\",false); $('#lock').hide();");
					li.attr("id", "tab-"+fileName+numTabs+1);
				var a = $('<a></a>');
				a.attr("data-toggle", "tab");
				a.html(fileName);
				li.append(a);
				$('.nav-tabs').append(li);
			}
			//check new file lock
			var currentMode = location.hash.split("mode=");
			if(currentMode[1] != mode)
				location.hash = "mode=" + mode;
			//manage editor content
			$('.tab-pane').removeAttr("id");
			$('.tab-pane').attr("id", "tab_" + numTabs+1); 
			$('#lock').html(userWriting + ' is editing this file!');
			clearInterval(readIntervalID);
			clearInterval(saveIntervalID);
			$('#fileName').html(fileName);
			$('#fileCode').html(fileCode);
			if(sidebar){
				$('#close').attr("onclick", "closeFile(\"#tab-" + fileName + numTabs+1 + "\");")
			} else {
				$('#close').attr("onclick", "closeFile(\"#firstFile\");")
			}
			$('body').addClass("sidebar-collapse");
			$('.CodeMirror').remove();
			
			//reinitialize everything
			initEditor();
			readAndSaveCode(editor);
			clearInterval(checkErrorsIntervalID);
			checkErrorsIntervalID = window.setInterval(checkErrors, 2000, editor); 
			clearInterval(checkModeIntervalID);
			checkModeIntervalID = window.setInterval(checkMode, 500, editor); 
			
		}
	});
}




