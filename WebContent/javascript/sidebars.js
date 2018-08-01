$(document).ready(function() { 
	$.ajax({  
		url: 'checkLogin',
		success: function(response){				
			if(response=="false"){
				swal("You are not logged in!", "You will be redirected to the login page", "warning")
				.then(() => {
					document.location.href = "page?action=login";
				});
			} else {
				setImage(response);
			}
		},
		type: 'GET'
	});
})


function setImage(img){
	if(img!=="null"){
		$('.imageURL').attr("src", img);
	}
}
