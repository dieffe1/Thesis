
function createCheckpoint(){
	var name = null;
	swal("Please enter checkpoint description:", {
		content: "input",
		})
		.then((value) => {
			name = value;
			if (name != null) {
				$.ajax({
					url: 'createCheckpoint',
					data : {
						name : name
					},
					success: function(response){
						if(response=="equals"){
							swal("Unable to create a new checkpoint", "You have to make some changes", "error");
						} else {
							swal("Created", "Checkpoint created successfully!", "success").then(() => {
								document.location.href = "page?action=settings";								
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


function restoreCheckpoint(id){
	swal({
		  title: "Are you sure?",
		  text: "Do you want to restore this checkpoint?",
		  icon: "info",
		  buttons: true,
		  dangerMode: true,
		}).then((willRestore) => {
		  if (willRestore) {
			$.ajax({
				url: 'restoreCheckpoint',
				data:{
					checkpointId: id
				},
				success: function(){
					swal("Restored", "Checkpoint restored successfully!", "success");
				},
				error: function(){
					alert("error");
				},
				type: 'POST'
			});   
		  }
	});
}
