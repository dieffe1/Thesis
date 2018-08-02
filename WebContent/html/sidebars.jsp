<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="theme-color" content="#d73925">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<link rel="stylesheet"
	href="../bootstrap/bootstrap/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="../bootstrap/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="../bootstrap/Ionicons/css/ionicons.min.css">
<link rel="stylesheet" href="../dist/css/AdminLTE.min.css">
<link rel="stylesheet" href="../dist/css/skins/_all-skins.min.css">
<link rel="stylesheet" href="../css/createProject.css">
<link rel="stylesheet" href="../css/settings.css">
<link rel="stylesheet" href="../css/swal.css">
<!-- Google Font -->
<link
	href="https://fonts.googleapis.com/css?family=Raleway:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
<link href="../css/business-casual.min.css" rel="stylesheet">
<link href="../css/sidebars.css" rel="stylesheet">
<link rel="shortcut icon" href="../index/img/icon.png" />
</head>
<body class="skin-red-light sidebar-collapse">

	<header class="main-header"> <a href="page?action=homepage"
		class="logo" id="logo_nav" data-toggle="tooltip" title="Homepage">
		<span class="logo-mini">I<b>T</b></span> <span class="logo-lg">Instan<b>Text</b></span>
	</a> <nav class="navbar navbar-static-top" id="navbar">
	
	<a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button" id="projectsTreeButton">
        <span class="sr-only">Toggle navigation</span>
      </a>
	
	<div class="navbar-custom-menu">
		<ul class="nav navbar-nav" id="navbar_menu">


			
			<li class="dropdown notifications-menu"><a href="#"
				class="dropdown-toggle" data-toggle="dropdown" id="option_drop">
					<i class="fa fa-gears"></i> Options	</a>
				<ul class="dropdown-menu" id="lista_opzioni">
				</ul>
			</li>


			<li class="dropdown messages-menu"><a href="#"
				class="dropdown-toggle" data-toggle="dropdown" id="chat_drop">
					<i class="fa fa-envelope-o"></i> Chat </a>
				<ul class="dropdown-menu">
					<li>
						<div class="box box-danger direct-chat direct-chat-danger">
							<div class="box-body" id="chatBox">
								<div class="direct-chat-messages" id="chat_zone"></div>
							</div>
						</div>
					</li>
					<li class="footer">
					<li class="box-footer">
						<div class="input-group">
							<input type="text" name="message" placeholder="Write message ..."
								class="form-control" id="chattext"> <span
								class="input-group-btn"><button class="btn btn-danger"
									onclick="sendMessage();" id="send">Send</button></span>
						</div>
					</li>
				</ul>
			</li>
				
			<li class="dropdown user user-menu"><a href="#"
				class="dropdown-toggle" data-toggle="dropdown"> <img
					src="../dist/img/user1-128x128.jpg" class="user-image imageURL"
					alt="User Image"> <span id="user" class="hidden-xs">${user.getUsername()}</span>
			</a>
				<ul class="dropdown-menu">

					<li class="user-header"><img
						src="../dist/img/user1-128x128.jpg" class="img-circle imageURL"
						alt="User Image">
						<p id="username">${user.getUsername()}<br>Java Developer
						</p></li>

					<li class="user-footer">
						<div class="pull-left">
							<a href="page?action=profile" class="btn btn-default btn-flat">Profile</a>
						</div>
						<div class="pull-right">
							<a onclick="logout()" class="btn btn-default btn-flat">Sign
								out</a>
						</div>
					</li>
				</ul>
		</ul>
	</div>
	</nav> </header>
	
	
	
	
	
	<aside class="main-sidebar" id="projectsTree">
	
	<section class="sidebar" style="height: auto;">
      <!-- Sidebar user panel -->
      <div class="user-panel">
        <div class="pull-left image">
          <img src="../dist/img/user1-128x128.jpg" class="img-circle imageURL" alt="User Image">
        </div>
        <div class="pull-left info">
          <p>${user.getUsername()}</p>
         
        </div>
      </div>
      <!-- search form -->
      <form action="#" method="get" class="sidebar-form">
        <div class="input-group">
          <input type="text" name="q" class="form-control" placeholder="Search...">
          <span class="input-group-btn">
                <button type="submit" name="search" id="search-btn" class="btn btn-flat">
                  <i class="fa fa-search"></i>
                </button>
              </span>
        </div>
      </form>
      <!-- /.search form -->
      <!-- sidebar menu: : style can be found in sidebar.less -->
      <ul class="sidebar-menu tree" data-widget="tree">
       <li class="header">THIS PROJECT</li>
			<li class="treeview">
		          <a href="">
		            <i class="fa fa-folder"></i> <span id="sidebarProjName"></span>
		            <span class="pull-right-container">
		              <i class="fa fa-angle-left pull-right"></i>
		            </span>
		          </a>
		          
		          <ul class="treeview-menu" id="sidebarPackagesZone">
		           
		          </ul>
        </li>
        </ul>
        </section>
	</aside>
	
	

	<script src="../bootstrap/jquery/dist/jquery.min.js"></script>
	<script src="../bootstrap/bootstrap/dist/js/bootstrap.min.js"></script>
	<script src="../bootstrap/jquery-slimscroll/jquery.slimscroll.min.js"></script>
	<script src="../bootstrap/fastclick/lib/fastclick.js"></script>
	<script src="../dist/js/adminlte.min.js"></script>
	<script src="../dist/js/demo.js"></script>
	<script src="../dist/js/notify.js"></script>
	<script src="../dist/js/notify.min.js"></script>
	<script src="../javascript/createProjects.js"></script>
	<script src="../javascript/collaborator.js"></script>
	<script src="../javascript/chat.js"></script>
	<script src="../javascript/sidebars.js"></script>
	<script src="../javascript/compiler.js"></script>
	<script src="../javascript/loginController.js"></script>
	<script src="../javascript/chat.js"></script>
	<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
</body>
</html>