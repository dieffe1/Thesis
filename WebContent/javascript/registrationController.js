function checkEnter(){
	document.getElementById('username').addEventListener('keypress', function check(event){
		var keycode = event.keyCode;
		if(keycode == '13') {
		    $('#button').click();   
		}
	});
	document.getElementById('email').addEventListener('keypress', function check(event){
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
	document.getElementById('retype_password').addEventListener('keypress', function check(event){
		var keycode = event.keyCode;
		if(keycode == '13') {
		    $('#button').click();   
		}
	});
	
};

function register(){
	var str = $('#email').val(); 
	var res = str.match(/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/g);
	if(res == null ){
		swal("Error", "Please insert a valid email!", "error")
		.then(() => {
			$('#email').val("");
			$('#email').css("border-color","red");
			$('#email').focus();					
			$('#password').val("");
			$('#retype_password').val("");
		});
		return;
	}
	$.ajax({
		url : 'register',
		data : {
			username : $('#username').val(),
			email : $('#email').val(),
			password : $('#password').val(),
			rePassword : $('#retype_password').val()
		},
		success : function(response) {
			if(response == "exist"){
				swal("Username not valid", "Username already used!", "error")
					.then(() => {
						$('#username').val("");
						$('#username').css("border-color","red");
						$('#username').focus();
						$('#password').val("");
						$('#retype_password').val("");
					});
			}
			
			else if(response == "email_exist"){
				swal("E-mail not valid", "E-mail already used!", "error")
				.then(() =>{
					$('#email').val("");
					$('#email').css("border-color","red");
					$('#email').focus();					
					$('#password').val("");
					$('#retype_password').val("");
				});
				
			}
			else if(response == "not_match"){

				swal("Password don't match", "Please retype the password!", "warning")
				.then(() =>{
					$('#password').css("border-color","red");
					$('#retype_password').css("border-color","red");
					$('#password').val("");
					$('#retype_password').val("");
				});
			}
			else document.location.href = "page?action=login";

		},
		type : 'POST',

	});
}