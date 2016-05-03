<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to Quora</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css"
	rel="stylesheet" />
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
	</script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.4.8/angular.min.js"></script>
    
<script language="JavaScript" type="text/javascript">

	jQuery(document).ready(function() {
	});

	function RefreshTable() {
		$("#userchanneltable").load("loginuser.html #mytable");
	}

	function addUserChannelPref(channelId, channelName) {
		jQuery.ajax({
			type : "POST",
			dataType : "html",
			url : "adduserchannel",
			data : "channelId=" + channelId + "&channelTitle=" + channelName,
			success : function(msg) {
				var row = document.getElementById(channelId);
				row.parentNode.removeChild(row);
				if (msg === "success") {
					location.reload();
				}

			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("some error");
			}
		});
	}

	var app = angular.module('myApp', []);
	
	  /* angular.element(document).ready(function ($rootScope) {
		  $rootScope.userChannelList=[];
		  <c:forEach var="channels" items="${selectedchannellist}">
			var channel = {id : '${channels.channelId}',
					       title :'${channels.channelTitle}'}
				$rootScope.userChannelList.push(${channel});
			 </c:forEach> 

			 alert($rootScope.length);
	    });  */

	app.filter('reverse', function() {
	    	  return function(items) {
	    	    return items.slice().reverse();
	    	  };
	    	}); 

    	 
	    app.directive('fileModel', ['$parse', function ($parse) {
            return {
               restrict: 'A',
               link: function(scope, element, attrs) {
                  var model = $parse(attrs.fileModel);
                  var modelSetter = model.assign;
                  
                  element.bind('change', function(){
                     scope.$apply(function(){
                        modelSetter(scope, element[0].files[0]);
                     });
                  });
               }
            };
         }]);

	    
	app.controller('postCtrl', ['$scope', '$http',function($scope,$http ) {
	    $scope.postQuestion={};
	    $scope.userPost = [];
	    $scope.filterChannel="";
	    $scope.commentError="";
	    $scope.postError="";
	    $scope.userId = "${userid}";
	    <c:forEach var="post" items="${postlist}">
		var userPost1 = {postTitle : "${post.postTitle}",
				postDescription :'${post.postLongDescription}',
				postDate :'${post.postDate}',
				postId :'${post.postId}',
				channelId :'${post.channelId}',
				postUserId :'${post.user.personID}',
				postUserName :	"${post.user.firstName}  ${post.user.lastName}" ,
				postImage:'${post.postImage}',
				comments : 	[]
				};
		 <c:forEach var="comment" items="${post.comments}">
		 userPost1.comments.push({commentText:"${comment.commentText}",commentId:"${comment.commentId}"
			 ,commentUserId:'${comment.user.personID}',commentUserName :	"${comment.user.firstName} ${comment.user.lastName}"});
		 </c:forEach> 
			$scope.userPost.push(userPost1);
		 </c:forEach> 
		
	   $scope.savePost = function() {
		   if(!$scope.postQuestion.postQuestion){
			   $scope.postError="please enter valid question"; 
			  }
			  else if(!$scope.postQuestion.postChannel){
			   $scope.postError="please select valid channel"; 
			  }else{
			  
		   $http.get('addPost', { params: $scope.postQuestion }).then(
			        function success(response) { 
			        	 $scope.postError=""
			        	var data = angular.toJson(response.data);
			        	//alert(response.data.redirecturl);
			        	if(response.data.redirecturl != null){
			        		var win = window.open(response.data.redirecturl,'_self' );
				        	}else{
				        		$scope.userPost.push(response.data);
		   						 $scope.postQuestion={};
					        	}
   						
   						 
			        	  /*  var fd = new FormData();
			               fd.append('file',  $scope.myFile);
                           fd.append('postid',response.data.postId);
			               console.log('file is ' );
			               console.dir($scope.myFile);
			               var uploadUrl = "addPostImage";
			               
			               $http.post(uploadUrl, fd, {
			                  transformRequest: angular.identity,
			                  headers: {'Content-Type': undefined}
			               }).then(
			            		   function success(imageresponse) {
			   						var data = angular.toJson(response.data);
			   						$scope.userPost.push(response.data);
			   						 $scope.postQuestion={};
				            		   },
				            	function failure(imagefailure){
							            	
						               }
					               ); */
				        },
			        function failure(failure) { 
				        	$scope.postError="error sending post"; 
					        }
				        );
	   }
	       
	   };
	   $scope.doFilterChannel = function(channel){
		   $scope.filterChannel = channel;
		   };

		 $scope.sendComment = function(postid,userid,item, index){
			 postId = postid;//$scope.userPost[index].postId;
			 userId = userid;//$scope.userPost[index].postUserId;
			 //alert("item " + item + " index "+index + " postId "+postId + " userId "+userId)
			 if(item){
			 $http.get('addComment', { params: {"postid":postId, "userid":userId,"postcomment":item}}).then(
				        function success(response) { 
				        	var data = angular.toJson(response.data);
				        	//alert(response.data.redirecturl);
				        	if(response.data.redirecturl != null){
				        		var win = window.open(response.data.redirecturl,'_self' );
					        	}else{
				        	//alert(postId);
					        for(i=0; i<$scope.userPost.length;i++){
									if($scope.userPost[i].postId === postId){
										//alert("post found");
										$scope.userPost[i].comments.push(response.data);
										$scope.item="";
										}
						        }
					         }
					        },
				        function failure(failure) { alert(failure) }); 
			 }
			 };  
		}]);

	 window.fbAsyncInit = function() {
   	  FB.init({
   	    appId      : '202240940154871',
   	    cookie     : true,  // enable cookies to allow the server to access 
   	                        // the session
   	    xfbml      : true,  // parse social plugins on this page
   	    version    : 'v2.5' // use graph api version 2.5
   	  });



   	  /* FB.getLoginStatus(function(response) {
   	    statusChangeCallback(response);
   	  }); */

   	  }; 

   	// Load the SDK asynchronously
   (function(d, s, id) {
   	  var js, fjs = d.getElementsByTagName(s)[0];
   	  if (d.getElementById(id)) return;
   	  js = d.createElement(s); js.id = id;
   	  js.src = "//connect.facebook.net/en_US/sdk.js";
   	  fjs.parentNode.insertBefore(js, fjs);
   	 }(document, 'script', 'facebook-jssdk'));	
	

	function logout(){
		 FB.getLoginStatus(function(response) {
		        if (response && response.status === 'connected') {
		            FB.logout(function(response) {
		            	var win = window.open("logout", '_self');
		            });
		        }else{
		        	var win = window.open("logout", '_self');
			        }
		    });

		
		
		}
</script>

</head>
<body  ng-app="myApp" ng-controller="postCtrl">
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" ng-click="doFilterChannel('')">Quora</a>
		</div>
		<ul class="nav navbar-nav">
			<li class="active"><a ng-click="doFilterChannel('')">Home</a></li>
		</ul>
		<ul onclick="logout()" class="nav navbar-nav pull-right">
			<li><a >Logout</a></li>
		</ul>
	</div>
	</nav>
	<div class="container-fluid">
		<div class="row">
			</br>
			</br>
			</br>
		</div>
		<div class="row" >
			<div class="col-sm-3">
				<table id="userchanneltable" class="table table-hover">
					<thead>
						<tr>
							<th>
								<div>
									Channels
									<button type="button" class="btn btn-info btn-sm pull-right"
										data-toggle="modal" data-target="#addChannel">
										<span class="glyphicon glyphicon-plus"></span>
									</button>
								</div>
							</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="channels" items="${selectedchannellist}"
							varStatus="loop">
							<tr ng-click="doFilterChannel('${channels.channelId}')">
								<td>${channels.channelTitle}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="col-sm-9">
				<div class="panel-group">
					<div class="panel panel-default">
						<div class="panel-heading" style="background-color: #286090; color : white">
							<h4 class="panel-title">
								<a data-toggle="collapse" href="#collapse1">Add Post</a>
							</h4>
						</div>
						<div id="collapse1" class="panel-collapse collapse" >
							<div class="panel-body">
							<p><font color="red">{{postError}}</font></p>
								<div class="form-group">
								<label for="postQuestion">Enter your Question:</label> 
									<input type="text" id="postQuestion"class="form-control" placeholder="post question" ng-model="postQuestion.postQuestion" required>
									<p>{{postQuestion.question}}</p>
									<div class="form-group">
										<label for="sel1">Select Channel:</label> 
										<%-- <select ng-model="selectedChannel" class="form-control" id="sel1">
										<ng-option x.title for x in userChannelList></ng-option>
											<c:forEach var="channels" items="${selectedchannellist}">
											<ng-option>${channels.channelTitle}</ng-option>
											</c:forEach>
										</select> --%>
									 <select ng-model="postQuestion.postChannel" class="form-control" id="sel1">
									     <c:forEach var="channels" items="${selectedchannellist}">
											<option value='${channels.channelId}'>${channels.channelTitle}</option>
										 </c:forEach>
										</select>
										<p>{{postQuestion.selectedChannel}}</p>
									</div>
									<div class="form-group">
										<label for="comment">Description:</label>
										<textarea class="form-control" rows="3" id="comment" ng-model="postQuestion.postDescription" required></textarea>
											<p>{{postQuestion.description}}</p>
											<!-- <input type = "file" file-model = "myFile" accept="image/x-png, image/gif, image/jpeg"></input> -->
									</div>
								</div>
							</div>
							<div class="panel-footer"><button type="button" ng-click="savePost()" class="btn btn-primary btn-md">ADD</button></div>
						</div>
					</div>
				</div>
				 
				 <div ng-repeat="x in userPost| reverse |filter:{channelId:filterChannel}">
					 
					  <div class="panel panel-default">
  							<div class="panel-heading"> 
  							<h5>  <a href="#"><span class="glyphicon glyphicon-user "></span> </a>&nbsp;&nbsp;<strong>{{x.postUserName}}</strong></h5>
  							</div>
  							<div class="panel-body">
  							<h4 style="font-family: 'Times New Roman'"><strong>{{ x.postTitle }} </strong> 
  							<span  ng-if="x.postUserId == userId"ng-click="" class="glyphicon glyphicon-pencil pull-right"></span></h4>
  							<div style="font-family: 'sans-serif'">{{ x.postDescription }}</div>
  							<%-- <div ng-if="x.postImage" style="padding-top:10px">
        						<img ng-src="<c:url value=/{{x.postImage}}/>" class="img-responsive img-rounded" />
      						</div> --%>
  							<hr>
  							<div data-toggle="collapse" data-target="{{'#' + x.postId}}"><a style="font-size:small"><span class="glyphicon glyphicon-comment"></span>&nbsp;&nbsp;Comment&nbsp;<span class="badge">{{x.comments.length}}</span></a></div>
  							</div>
  							<div id="{{x.postId}}" class="panel-footer collapse">
  							   <div class="input-group ">
	  							   <input type="text"  class="form-control" placeholder="write a comment" ng-model="item"> 
	  							    <span class="input-group-btn">
	  							   <button type="button" class="btn btn-primary" ng-click="sendComment(x.postId,x.postUserId,item,$index)">Comment</button>
	  							   </span>
	  							</div>
	  							 </br>
  							 <div ng-repeat="y in x.comments|reverse">
  							  <div >
  							  <span class="glyphicon glyphicon-user .img-rounded">&nbsp;<strong style="font-family: 'courier'">{{y.commentUserName}}</strong></span>
  							  </br>
  							 <h5 style="font-family: 'calibiri'">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{y.commentText}}
  							 <span ng-if="y.commentUserId == userId" ng-click="" class="glyphicon glyphicon-pencil pull-right"></span></h5>
  							 </br>
  							  </div>
  							 </div>
  							</div>
					 </div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="addChannel" role="dialog">
		<div class="modal-dialog"> 

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Select New Channel</h4>
				</div>
				<div class="modal-body">

					<table id="channeltable" class="table table-hover ">
						<thead>
							<tr>
								<th>Select channels to personalise your feed page</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="channel" items="${channellist}" varStatus="loop">
								<tr id='${channel.channelId}'
									onclick="addUserChannelPref('${channel.channelId}', '${channel.channelTitle}')">
									<td>${channel.channelTitle}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>

		</div>
	</div>

</body>
</html>