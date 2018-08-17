
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
				createTreeSidebar(address, obj.name);
			});
		}
	});

	
}




function createTreeSidebar(hash, packName) { console.log("inside " + packName + " " + hash)
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
				fileA.attr("onclick", "location.hash = \"" + newhash + "\"; " +
							"showContent(\"" + obj.name + "\" , false);"); // il parametro isCreator quando viene invocato showContent su un file è irrilevante
				fileList.append(fileA);
				packUl.append(fileList);
			});
		}
	});

	packList.append(packUl);
	$('#sidebarPackagesZone').append(packList);
}
