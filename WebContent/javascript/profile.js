window.onload = function(){
	$('#a_drop').hide();
}

function insert(type){
	switch(type){
	case "image":
		swal({
			text : "Insert an external link",
			content : "input",
		}).then((value)=>{
			update(type, value);
		})
		break;
	case "mail":
		swal({
			text : "Insert new mail",
			content : "input",
		}).then((value)=>{
			update(type, value);
		})
		break;
	case "password":
		swal({
			text : "Insert new password",
			content : "input",
		}).then((value)=>{
			var pass = value;
			swal({
				text : "Confirm new password",
				content : "input",
			}).then((value)=>{
				if(value!==pass){
					swal("Error", "Passwords don't match!", "error").then(()=>{
						insert(type);
					})
				} else {
					update(type, value);
				}
			});
		});
		break;
	default:
		break;
	}
	
}

function update(type, value){
	$.ajax({
		url: "updateCredential",
		data : {
			type : type,
			value : value
		},
		success : function(){
			swal("Success!", "", "success").then(()=>{
				document.location.href = "page?action=profile";
			})
		},
		type:"POST"
	})
}