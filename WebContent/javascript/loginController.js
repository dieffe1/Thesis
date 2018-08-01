function checkEnter(){
	document.getElementById('username').addEventListener('keypress', function check(event){
		var keycode = event.keyCode;
		if(keycode == '13') {
		    $('#button').click();   
		}
	});
	document.getElementById('password').addEventListener('keypress', function check(event){
		var keycode = event.keyCode;
		if(keycode == '13') {
		    $('#button').click();   
		}
	});
	
};

function login(){
	$.ajax({
		url : 'login',
		data : {
			username : $('#username').val(),
			password : $('#password').val()
		},
		success : function(response) {
			if(response == "password"){
				swal("Error", "Wrong password!", "error")
					.then(() => {
						$('#password').val("")
						$('#password').css("border-color","red")
					});
			}
			else if(response == "user"){
				swal("Error","User not found!", "error")
					.then(() => {
						$('#username').css("border-color","red")
						$('#username').val("")
						$('#password').val("")
					});
			} else {
				document.location.href = "page?action=homepage";
			}
		},
		type : 'GET',
	});
}


function onSignIn(googleUser) {
	  var profile = googleUser.getBasicProfile();
	  $.ajax({
			url : 'loginAPI',
			data : {
				email : profile.getEmail()
			},
			success : function(responseText) {
				if(responseText == "register"){
					swal("Please choose your username:", {
						content: "input",
						})
						.then((value) => {
							if (value != null && value != "") {
								$.ajax({
									url: 'loginAPI',
									data : {
										email : profile.getEmail(),
										name :  value,
										image : profile.getImageUrl()
									},
									success : function(){
										document.location.href = "page?action=homepage";
									},
									error : function(){ 
										alert("error");
									},
									type : 'POST'
								});
							}
						})
				}	
				else
					document.location.href = "page?action=homepage";
			},
			type : 'GET'
	});
}

function logout() {
	 document.location.href = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:8080/SIW_InstanText/html/page?action=logout";
}
