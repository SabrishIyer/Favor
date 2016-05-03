<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to Quora</title>
   <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
    
    .mainContainer{
    margin-left: 20px
    }
    
    .button {
    background-color: #008CBA;
    border: none;
    color: white;
    padding: 10px 10px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 14px;
    margin: 4px 2px;
    cursor: pointer;
    border-radius: 4px;
	}
    
    tr{
    margin-bottom : 20px;
    }
       .headernav {
		    list-style-type: none;
		    margin: 0;
		    padding: 0;
		    overflow: hidden;
		    background-color: #333;
		    color:grey;
		    cursor: pointer;
		  }
       
        
    </style>
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
      <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.4.8/angular.min.js"></script>
    <script>
		function loadCategory(){

			 $(".mainContainer").load("category");
		}

		function loadHome(){
			 $(".mainContainer").load("loadhome");
			}
		jQuery(document).ready(function() {
			loadHome();
		});

		var app = angular.module('myApp', []);
		app.controller('categoryCtrl', ['$scope', '$http',function($scope,$http ) {
			 $scope.channel={};
			 $scope.channelerror="";
			$scope.insertChannel = function(){
				
				 $http.get('addcategory', { params: {"channelTitle":$scope.channel.channelTitle, "channelDescription":$scope.channel.channelDescription}}).then(
					        function success(response) { 
						        alert(response.data);
								  if(response.data == "success"){
									  $scope.channelerror = "";
								 			window.open("category", '_self');
								  }else if(response.data == "channelexist"){
									  $scope.channelerror = "channel already exist, please select different channel name";
									}else if(response.data.redirecturl != null){
						        		var win = window.open(response.data.redirecturl,'_self' );
						        	}
						        },
					        function failure(failure) { alert(failure) }); 

				/* jquery.ajax({
					  type: "POST",
					  dataType: "html",
					  url: "addcategory",
					  data: "channelTitle="+$('#channelTitle').val()+"&channelDescription="+$('#channelDescription').val(),
					  success: function(msg){
						  if(msg == "success"){
						 			window.open("category", '_self');
						  }else if(msg == "channelexist"){

							  }
					  },
					  error: function(XMLHttpRequest, textStatus, errorThrown) {
					     alert("some error");
					  }
					}); */
			}

		}]);
			
		function insertChannel(){

			$.ajax({
				  type: "POST",
				  dataType: "html",
				  url: "addcategory",
				  data: "channelTitle="+$('#channelTitle').val()+"&channelDescription="+$('#channelDescription').val(),
				  success: function(msg){
					  if(msg == "success"){
					 			window.open("category", '_self');
					  }else if(msg == "channelexist"){

						  }
				  },
				  error: function(XMLHttpRequest, textStatus, errorThrown) {
				     alert("some error");
				  }
				});

		}
		function getChannelIndex(title, description){
			$("#channelDetailTitle").text(title);
			$("#channelDetailDescription").text(title);
			}


		function logout(){
			var win = window.open("logoutadmin", '_self');
			}
		jQuery(document).ready(function() {
			if(${fn:length(channelList)} > 0)
			 getChannelIndex('${channelList[0].channelTitle}','${channelList[0].channelDescription}');

			 $('.channelsubmit').attr('disabled',true);
			    $('#channelTitle').keyup(function(){
			        if($(this).val().length !=0)
			            $('.channelsubmit').attr('disabled', false);            
			        else
			            $('.channelsubmit').attr('disabled',true);
			    })
			    
		});
	
		
    </script>
</head>
<body ng-app="myApp" ng-controller="categoryCtrl">
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="loadhome">Quora</a>
		</div>
		<ul class="nav navbar-nav">
			<li ><a  href="loadhome">Home</a></li>
		</ul>
		<ul class="nav navbar-nav">
			<li  class="active"><a href="#">Channel</a></li>
		</ul>
		<ul onclick="logout()" class="nav navbar-nav pull-right">
			<li><a >Logout</a></li>
		</ul>
	</div>
	</nav>
<div class="container-fluid">   

<%-- <h6 class="col-sm-3">Welcome ${requestScope.username} </h6> --%>
<div class="row">
			</br>
			</br>
			</br>
		</div>
<div class="row">
 <div class="col-sm-3">
 <table id="channeltable" class="table table-hover table-bordered">
    <thead>
      <tr>
        <th>
        	<div>Channels
	           <button type="button" class="btn btn-info btn-sm pull-right" data-toggle="modal" data-target="#addChannel">
	           <span class="glyphicon glyphicon-plus"></span></button>
            </div>
        </th>
      </tr>
    </thead>
    <tbody>
      		<c:forEach var="channels" items="${channelList}" varStatus="loop">
                <tr onclick="getChannelIndex('${channels.channelTitle}', '${channels.channelDescription}')">
                    <td>${channels.channelTitle}</td>
                </tr>
            </c:forEach>
     </tbody>
  </table>
 </div>
  <div class="col-sm-9 channelDetail">
   <h4>Channel Title</h4> 
   <h6 id="channelDetailTitle"> </h6>
   <h4>Channel Decription</h4> 
   <h6 id="channelDetailDescription"> </h6>
  </div>
</div>

<div class="modal fade" id="addChannel" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Add New Channel</h4>
        </div>
        <div class="modal-body">
	        <div class="form-group">
	         <p><font color="red">{{channelerror}}</font></p>
			  <label for="channelTitle">Channel Title:</label>
			  <input type="text" class="form-control" id="channelTitle" name="channelTitle" ng-model="channel.channelTitle" size="25" required/>
			</div>
			<div class="form-group">
			  <label for="channelDescription">Channel Description:</label>
			  <input type="text" class="form-control" id="channelDescription" name="channelDescription" ng-model="channel.channelDescription" size="25"/>
			</div>
        	<div class="form-group">
        	<input class="button channelsubmit" type="button" value="Create Channel" ng-click="insertChannel()"  ng-disabled="myForm.channel.channelTitle.$dirty && myForm.channel.channelTitle.$invalid"/>
        	</div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
      
    </div>
  </div>

</div>

</body>
</html>
 

