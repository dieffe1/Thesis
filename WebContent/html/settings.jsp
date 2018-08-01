<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="sidebars.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>InstanText | Settings</title>
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css" rel="stylesheet" />
<link href="../css/settings.css" rel="stylesheet" />
</head>

<body class="skin-red-light" onload="loadCollaboratorPage('${project.getCreator().getUsername()}','${user.getUsername()}')">   
	<section class="box box-primary center">
	<div class="nav-tabs-custom">
		<ul class="nav nav-tabs">
			<li class="active"><a href="#tab_1" data-toggle="tab"> Checkpoint 
				<i class="fa fa-flag-checkered"></i>
			</a></li>
			<li onclick="findUsers();"><a href="#tab_2" data-toggle="tab"> Collaborator
				<i class="fa fa-user"></i>
			</a></li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane active" id="tab_1">
				<a class="btn bg-blue" onclick="createCheckpoint();">Create Checkpoint</a> <br> <br>

				<ul class="timeline">
					<c:forEach var="check" items="${project.getCheckpoints()}">

						<li><i class="fa bg-blue"></i>
							<div class="timeline-item">
								<span class="time"><i class="fa fa-clock-o"></i>${check.getDate()}</span>
								<!-- data del checkpoint -->

								<h3 class="timeline-header">
									<a href="#">${check.getCreator().getUsername()} </a>
									<!-- utente checkpiont -->
								</h3>

								<div class="timeline-body">${check.getDescription()}</div>
								<div class="timeline-footer">
									<a class="btn btn-success btn-xs bg-green" onclick="restoreCheckpoint(${check.getId()});"> Restore</a>
								</div>
							</div></li>
					</c:forEach>
				</ul>
			</div>

			<div class="tab-pane" id="tab_2">
				<div class="form-group">
					<c:if test="${user.getUsername() == project.getCreator().getUsername()}">
						<label>Select Collaborator</label>
						<select	class="js-example-basic-multiple" id="selectUsers" name="states[]" multiple style="width:100%"> </select> <br> <br>
						<a class="btn btn-success btn bg-green" onclick="addCollaborator('${project.getId()}','${project.getCreator().getUsername()}','${user.getUsername()}');">Add</a>
					</c:if>
				</div>
				<div class="row" id="collaborators"></div>
			</div>
		</div>
	</div>
	</section>
	<script>
			$(document).ready(function() {
				$('.sidebar-menu').tree();
			});
	</script>
	<script src="../javascript/checkpoint.js"></script>
	<script	src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>

</body>
</html>