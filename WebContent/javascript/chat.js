var sessionUser ="";
var last = 0;

$('#chattext').unbind("keyup").bind("keyup",function check(event){
	if(event.key === 'Enter') { 
		sendMessage();   
	}
});

function sendMessage() {
	if($('#chattext').val() == "" || $('#chattext').val() == " ")
		return;
	
	$.ajax({
		url : 'load',
		data : {
			text : $('#chattext').val()
		},
		success : function() {
			$('#chattext').val("");
			olist = $('#chat_zone').val();
			olist.scrollTop = olist.scrollHeight;
		},
		type : 'POST'
	});
}

function load(user) {
	sessionUser = user;
	$.ajax({
		url : 'load',
		success : function(response) {
			if(response !== null && response !== ""){
				$("#chat_zone").html("");

				var lastTmp; 
				$.each(JSON.parse(response), function(idx, obj) {
					lastTmp = obj.id;
					var div;
					if(user == obj.user.username)
						div = $('<div></div>').addClass("direct-chat-msg right");
					else
						div = $('<div></div>').addClass("direct-chat-msg left");
					
					var div1 = $('<div></div>').addClass("direct-chat-info clearfix");
					var img = $('<img></img>').addClass("direct-chat-img");
					
					if(obj.user.image === "null")
						img.attr("src", "../dist/img/user1-128x128.jpg");
					else
						img.attr("src", obj.user.image);
					
						img.attr("alt", "Message User Image");
					var span;
					var span1;
					var div3;
					
					if(user == obj.user.username){
						span = $('<span></span>').addClass("direct-chat-name pull-right").text(obj.user.username);
						span1 = $('<span></span>').addClass("direct-chat-timestamp pull-left").text(obj.date);
						div3 = $('<div></div>').addClass("direct-chat-text pull-right").text(obj.text);
					}
					else{
						span = $('<span></span>').addClass("direct-chat-name pull-left").text(obj.user.username);
						span1 = $('<span></span>').addClass("direct-chat-timestamp pull-right").text(obj.date);
						div3 = $('<div></div>').addClass("direct-chat-text pull-left").text(obj.text);
					}
					
					div1.append(span);
					div1.append(span1);
					div.append(div1);
					div.append(img);
					div.append(div3);
					$('#chat_zone').append(div);
				});
				
				olist = $('#chat_zone');
				olist.scrollTop = olist.scrollHeight;
				
				var hash = location.hash.split("/");
			
				if($('#a_drop').attr("aria-expanded")!=="true" && (lastTmp > last) && (hash.length > 1)) {
					last = lastTmp;
					$("#a_drop").notify(
					 "New Messages!", { 
						 position:"left" }
					);
				}
				
			}
		},
		type : 'GET'
		
	});
}

if($("#chat_zone")[0] != undefined){
	var scrollHeight = $("#chat_zone")[0].scrollHeight;
	$("#chat_zone").scrollTop(scrollHeight);
}	

$('#chat_zone').scroll(function() { 
	if ($('#chat_zone').scrollTop() == 0) {
		$.ajax({ 
			url : 'loadOld',
			
			success : function(response) {
				var tmp="";
				
				if(response!=null && response != "") {
									
					$.each(JSON.parse(response), function(idx, obj) {	
						var div;
						
						if(sessionUser == obj.user.username)
							div = $('<div></div>').addClass("direct-chat-msg right");
						else
							div = $('<div></div>').addClass("direct-chat-msg left");
						
						var div1 = $('<div></div>').addClass("direct-chat-info clearfix");
						var img = $('<img></img>').addClass("direct-chat-img");
						if(obj.user.image === "null")
							img.attr("src", "../dist/img/user1-128x128.jpg");
						else
							img.attr("src", obj.user.image);
						
							img.attr("alt", "Message User Image");
						var span;
						var span1;
						var div3;
						
						if(user == obj.user.username){
							span = $('<span></span>').addClass("direct-chat-name pull-right").text(obj.user.username);
							span1 = $('<span></span>').addClass("direct-chat-timestamp pull-left").text(obj.date);
							div3 = $('<div></div>').addClass("direct-chat-text pull-right").text(obj.text);
						}
						else{
							span = $('<span></span>').addClass("direct-chat-name pull-left").text(obj.user.username);
							span1 = $('<span></span>').addClass("direct-chat-timestamp pull-right").text(obj.date);
							div3 = $('<div></div>').addClass("direct-chat-text pull-left").text(obj.text);
						}
						
						div1.append(span);
						div1.append(span1);
						div.append(div1);
						div.append(img);
						div.append(div3);
						$('#chat_zone').append(div);
					});

					$("#chat_zone").prepend(tmp);
					$('#chat_zone').scrollTop(30); 
				}
			}, 
			type : 'GET'
		}); 
	}
});
 
