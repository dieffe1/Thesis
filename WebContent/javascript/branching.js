var div = document.createElement("div");
var area1 = document.createElement("textarea");
area1.style.width = "50%";
area1.rows = 10;
area1.disabled = true;
var area2 = document.createElement("textarea");
area2.style.width = "50%";
area2.rows = 10;
area2.disabled = true;


function createBranch() {

	$.ajax({
		url:'createBranch',
		success:function(response) {

			if(response == "noMaster"){
				swal("You can't create branch!", "This isn't branch master!", "error").then(()=>{
					$('#returnButton').click();
				})
			}
			else if(response == "ok"){
				swal("Please enter branch name", {
					content: "input",
				})
				.then((value) => {
					if(value != "" && value != null){
						$.ajax({
							url : 'createBranch',
							data: {
								name : value
							},
							success : function(response){
								if(response == "equalMaster") {
									swal("Invalid branch name!", "Name equal branch Master", "warning").then(()=>{
										$('#returnButton').click();
									})
								}
								else if(response == "equalBranch") {
									swal("Invalid branch name!", "Name equal other branch", "warning").then(()=>{
										$('#returnButton').click();
									})
								}
								else {
									swal("Created", "Branch created successfully!", "success").then(()=>{
										$('#returnButton').click();
									})
								}
							},
							error : function(){
								alert("branch error");
							},
							type : 'POST'
						});
					}
				})
			}
		},
		error: function() {
			alert("branch error");
		},
		type: 'GET'
	});
}

function merge() {
	$.ajax ({
		url: "mergeBranch",
		success : function(response){
			if(response == "master"){
				swal("Operation denied!", "This is branch master!", "error").then(()=>{
					$("#returnButton").click();
				});
			} else if(response=="zero") {
				swal("Merged!", "Your branch has been merged!", "success").then(()=>{
					$("#returnButton").click();
				});
			} else {
				var list = [];
				$.each(JSON.parse(response), function(idx, obj){
					list[idx] = obj;
				});

				checkFilesToMerge(list, 0);
			}
		},
		type : 'POST'
	});

}

function checkFilesToMerge(list, index){
	if(index == list.length){
		swal("Merged!", "Your branch has been merged!", "success").then(()=>{
			$("#returnButton").click();
			return;
		});
	}
	return showFilesToMerge(list, index);
}

function showFilesToMerge(list, index){
	area1.value = list[index].key.code;
	area2.value = list[index].value.code;
	div.append(area1);
	div.append(area2);
	swal("Conflict!", "Please resolve merge conflicts manually", "error",{
		content : div,
		buttons: {
			cancel : "Cancel",
			catch: "Master",
			defeat:  "Branch"
		},
	}).then((value)=>{
		switch(value){
		case "catch":
//			alert("Master");
			break;
		case "defeat":
//			alert("Branch");
			$.ajax({
				url : 'updateFile',
				data : {
					id : list[index].key.id,
					code : list[index].value.code
				},
				type : 'POST'
			});
			break;
		default:
			return;
		}
		return checkFilesToMerge(list, ++index);
	});
}
