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

	

	var app = angular.module('myApp', []);

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

		$scope.onPostRemove = function(postId, channelId){
			  $http.get('removepost', { params:{"postid":postId, "channelId":channelId}}).then(
				        function success(response) { 

				        	var data = angular.toJson(response.data);
				        	//alert(response.data.redirecturl);
				        	if(response.data.redirecturl != null){
				        		var win = window.open(response.data.redirecturl,'_self' );
					        	}else{
						        	
					        		 for (var i = $scope.userPost.length - 1; i >= 0; i--) {
					        			 if($scope.userPost[i].postId === postId) {
					        			        $scope.userPost.splice(i, 1);
					        			    }
					        			}
						        	}
					        },
				           function failure(failure) { alert(failure) });
		       

			};	


			$scope.onCommentRemove = function(postId, channelId, commentId){
				  $http.get('removeComment', { params:{"postid":postId, "channelId":channelId, "commentId":commentId}}).then(
					        function success(response) { 

					        	var data = angular.toJson(response.data);
					        	//alert(response.data.redirecturl);
					        	if(response.data.redirecturl != null){
					        		var win = window.open(response.data.redirecturl,'_self' );
						        	}else{
						        		 for (var i = $scope.userPost.length - 1; i >= 0; i--) {
						        			 if($scope.userPost[i].postId === postId) {
						        				 for (var j = $scope.userPost[i].comments.length - 1; j >= 0; j--){
							        				 if($scope.userPost[i].comments[j].commentId == commentId)
							        				 $scope.userPost[i].comments.splice(j, 1);
							        				 }
						        			       
						        			    }
						        			}
							        	}
						        },
					           function failure(failure) { alert(failure) });
			       

				};	
	  
	   $scope.doFilterChannel = function(channel){
		   $scope.filterChannel = channel;
		   };

		}]);

	function logout(){
		var win = window.open("logoutadmin", '_self');
		}

</script>

</head>
<body ng-app="myApp" ng-controller="postCtrl">
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" ng-click="doFilterChannel('')">Quora</a>
		</div>
		<ul class="nav navbar-nav">
			<li class="active" ng-click="doFilterChannel('')"><a >Home</a></li>
		</ul>
		<ul class="nav navbar-nav">
			<li><a href="category">Channel</a></li>
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
		<div class="row"  >
			<div class="col-sm-3">
				<table id="channeltable" class="table table-hover">
					<thead>
						<tr>
							<th>
								<div>
									Channels
								</div>
							</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="channels" items="${channellist}"
							varStatus="loop">
							<tr ng-click="doFilterChannel('${channels.channelId}')">
								<td>${channels.channelTitle}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="col-sm-9">
				 <div ng-repeat="x in userPost| reverse |filter:{channelId:filterChannel}">
					 
					  <div class="panel panel-default">
  							<div class="panel-heading"> 
  							<h5>  <a href="#"><span class="glyphicon glyphicon-user "></span> </a>&nbsp;&nbsp;<strong>{{x.postUserName}}</strong> <span ng-click="onPostRemove(x.postId, x.channelId)" class="glyphicon glyphicon-trash pull-right"></span> </h5>
  							</div>
  							<div class="panel-body">
  							<h4 style="font-family: 'Times New Roman'"><strong>{{ x.postTitle }} </strong></h4>
  							<div style="font-family: 'sans-serif'">{{ x.postDescription }}</div>
  							<%-- <div ng-if="x.postImage" style="padding-top:10px">
        						<img ng-src="<c:url value=/{{x.postImage}}/>" class="img-responsive img-rounded" />
      						</div> --%>
  							<hr>
  							<div data-toggle="collapse" data-target="{{'#' + x.postId}}"><a style="font-size:small"><span class="glyphicon glyphicon-comment"></span>&nbsp;&nbsp;Comment&nbsp;<span class="badge">{{x.comments.length}}</span></a></div>
  							</div>
  							<div id="{{x.postId}}" class="panel-footer collapse">
	  							 </br>
  							 <div ng-repeat="y in x.comments|reverse">
  							  <div >
  							  <span class="glyphicon glyphicon-user .img-rounded">&nbsp;<strong style="font-family: 'courier'">{{y.commentUserName}}</strong></span>
  							  </br>
  							 <h5 style="font-family: 'calibiri'">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{y.commentText}} <span ng-click="onCommentRemove(x.postId, x.channelId, y.commentId)" class="glyphicon glyphicon-remove pull-right"></span></h5>
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