<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="sidebars.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
<title>InstanText | Editor</title>
<link rel="stylesheet"
	href="../plugins/codemirror/addon/hint/show-hint.css">
<link rel="stylesheet"
	href="../plugins/codemirror/addon/dialog/dialog.css">
<link rel="stylesheet"
	href="../plugins/codemirror/addon/scroll/simplescrollbars.css">

<link rel="stylesheet" type="text/css"
	href="../plugins/codemirror/lib/codemirror.css">
<link rel="stylesheet" type="text/css" href="../css/text.css">
</head>
<body class="skin-red sidebar-collapse">
	<div class="box box-danger center" id="mainarea">
		<div class="nav-tabs-custom">
			<ul class="nav nav-tabs">
				<li class="active" id="firstFile"><a href="#tab_1" data-toggle="tab"> ${file.getName()} 
				</a></li>
			</ul>
	
			<div class="tab-content">
				<div class="tab-pane active" id="tab_1">
					<section class="content">
					<div id="titles">
						<h5 style="display: none" id="projectID">${project.getName()}</h5>
						<h5 style="display: none">Project:</h5>
						<h2 id="lock" style="display: none">${file.getUser().getUsername()} is editing this
							file!</h2>
					</div>
					<div class="input-group-btn">
						<button id="close" class="btn btn-danger"
							data-toggle="tooltip" title="Close this tab">
							<i class="fa fa-close"></i>
						</button>
						<button class="btn btn-default" onclick="findString();"
							data-toggle="tooltip" title="Find String">
							<i class="fa fa-search"></i>
						</button>
						<button class="btn btn-default" onclick="createCheckfile();"
							data-toggle="tooltip" title="Create Checkpoint">
							<i class="fa fa-flag-checkered"></i>
						</button>
						<button class="btn btn-default" onclick="consult();"
							data-toggle="tooltip" title="Consult previous version">
							<i class="fa fa-mail-reply"></i>
						</button>
						<button class="btn btn-default" onclick="renameFile();"
							data-toggle="tooltip" title="Rename">
							<i class="fa fa-pencil"></i>
						</button>
						<button class="btn btn-danger" onclick="removeFile();"
							data-toggle="tooltip" title="Delete">
							<i class="fa fa-trash"></i>
						</button>
					</div>
					<article> <textarea class="codemirror-textarea"
						name="preview-form-comment" id="fileCode">${file.getCode()}</textarea>
					</article> </section>
				</div>
			</div>
		</div>
	</div>
	<div class="box box-danger center" id="contenuto">
		<section class="content">
		<h2 id="name"></h2>
		<div style="margin: 1%;">
			<button class="btn bg-green" onclick="nascondi();">Hide</button>
			<button class="btn bg-green" onclick="ripristina();" id="ripristina">Restore</button>
		</div>
		<article> <textarea class="codemirror-textarea"
			name="preview-form-comment" id="textarea2">  </textarea> </article> </section>
	</div>

	<!-- javascript -->
	<script src="../javascript/jquery-3.2.1.min.js"></script>
	<script src="../plugins/codemirror/lib/codemirror.js"></script>
	<script src="../plugins/codemirror/mode/clike/clike.js "></script>
	<script src="../plugins/codemirror/addon/hint/css-hint.js"></script>
	<script src="../plugins/codemirror/addon/hint/show-hint.js"></script>
	<script src="../plugins/codemirror/addon/edit/closebrackets.js"></script>
	<script src="../plugins/codemirror/addon/edit/matchbrackets.js"></script>
	<script src="../plugins/codemirror/addon/search/search.js"></script>
	<script src="../plugins/codemirror/addon/search/searchcursor.js"></script>
	<script src="../plugins/codemirror/addon/search/jump-to-line.js"></script>
	<script src="../plugins/codemirror/addon/dialog/dialog.js"></script>
	<script src="../plugins/codemirror/addon/scroll/simplescrollbars.js"></script>
	<script src="../plugins/codemirror/mode/css.js"></script>
	<script src="../javascript/text.js"></script>
	<script src="../javascript/treesidebar.js"></script>



	<script src="../dist/js/notify.js"></script>
	<script src="../dist/js/notify.min.js"></script>

</body>

</html>