<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="sidebars.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>InstanText | Profile</title>
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css"
	rel="stylesheet" />
<link href="../css/profile.css" rel="stylesheet" />
</head>
<body class="skin-red-light">
	<div class="box box-primary center boxProfile">
		<section class="content">
		<div class="col-md-3 text-center" id="primaryBox">
			<div class="center">
				<div class="box box-widget widget-user">
					<div class="widget-user-header bg-aqua-active boxCollaborator">
						
					</div>
					<div class="widget-user-image">
						<img class="img-circle imageURL"
							src="../dist/img/user1-128x128.jpg" alt="User avatar">
					</div>
					<div class="text-center">
						<h3 class="widget-user-username">${user.getUsername()}</h3>
						<h4>${user.getMail()}</h4>
					</div>
				</div>
			</div>
			<h4>Projects created: ${user.getProjects().size()}</h4>
			<h4>Other projects: ${user.getOtherProjects().size()}</h4>
			<button class="btn btn-primary" onclick="insert('mail')">Change mail</button>
			<button class="btn btn-primary" onclick="insert('password')">Change password</button>
			<button class="btn btn-primary" onclick="insert('image')">Change image</button>
		</div>
		</section>
	</div>
	<script>
		$(document).ready(function() {
			$('.sidebar-menu').tree();
		});
	</script>
	<script src="../javascript/checkpoint.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
	<script src="../javascript/profile.js"></script>
</body>
</html>