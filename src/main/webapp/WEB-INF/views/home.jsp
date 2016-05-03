<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Sign-Up/Login Form</title>
       <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        
    </style>
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script>
    window.fbAsyncInit = function() {
    	  FB.init({
    	    appId      : '202240940154871',
    	    cookie     : true,  // enable cookies to allow the server to access 
    	                        // the session
    	    xfbml      : true,  // parse social plugins on this page
    	    version    : 'v2.5' // use graph api version 2.5
    	  });


    	  // Now that we've initialized the JavaScript SDK, we call 
    	  // FB.getLoginStatus().  This function gets the state of the
    	  // person visiting this page and can return one of three states to
    	  // the callback you provide.  They can be:
    	  //
    	  // 1. Logged into your app ('connected')
    	  // 2. Logged into Facebook, but not your app ('not_authorized')
    	  // 3. Not logged into Facebook and can't tell if they are logged into
    	  //    your app or not.
    	  //
    	  // These three cases are handled in the callback function.

    	  FB.getLoginStatus(function(response) {
    	    statusChangeCallback(response);
    	  });

    	  };

    	// Load the SDK asynchronously
    (function(d, s, id) {
    	  var js, fjs = d.getElementsByTagName(s)[0];
    	  if (d.getElementById(id)) return;
    	  js = d.createElement(s); js.id = id;
    	  js.src = "//connect.facebook.net/en_US/sdk.js";
    	  fjs.parentNode.insertBefore(js, fjs);
    	 }(document, 'script', 'facebook-jssdk'));	

 // This is called with the results from from FB.getLoginStatus().
    function statusChangeCallback(response) {
      console.log('statusChangeCallback');
      console.log(response);
      // The response object is returned with a status field that lets the
      // app know the current login status of the person.
      // Full docs on the response object can be found in the documentation
      // for FB.getLoginStatus().
      if (response.status === 'connected') {
        // Logged into your app and Facebook.
        facebookConnected(response);
      } else if (response.status === 'not_authorized') {
        // The person is logged into Facebook, but not your app.
       openCredential()
      } else {
        // The person is not logged into Facebook, so we're not sure if
        // they are logged into this app or not.
          openCredential()
      }
    }

    // This function is called when someone finishes with the Login
    // Button.  See the onlogin handler attached to it in the sample
    // code below.
    function checkLoginState() {
    	
      FB.getLoginStatus(function(response) {
        statusChangeCallback(response);
      });
    }  

    // Here we run a very simple test of the Graph API after login is
    // successful.  See statusChangeCallback() for when this call is made.
    function facebookConnected(a_response) {
      console.log('Welcome!  Fetching your information.... ');

      FB.api('/me',{fields: 'id,last_name,first_name,email,gender'}, function(response) {
        console.log('Successful login for: ' + response.id+","+response.last_name+","+response.first_name+","+response.email+","+response.gender);

        var url =  "sociallogin.htm?action=signup&socialtype=facebook&firstname="+response.first_name+"&lastname="+response.last_name+"&gender="+response.gender+"&email="+response.email+"&username="+response.id;
		var win = window.open(url, '_self');
		// win.focus();
       /*  $.ajax({
      	  method: "POST",
      	  url: "sociallogin.htm",
      	  data: {action: "signup",
          	  	 socialtype: "facebook",
          	  	 firstname: response.first_name,
          	  	 lastname:response.last_name,
          	  	 gender:response.gender,
          	  	 email:response.email,
          	  	 username: response.id
          	  	 },
      	  async:true
      	}).done(function( msg ) {
          	window.self.document.write(msg);
      	    //alert( "Data Saved: " + msg );
      	  }); */
    	  
      });
    }

    function facebookLogin(){

    	FB.login(function(response) {
    	    if (response.authResponse) {
    	     console.log('Welcome!  Fetching your information.... ');
    	     facebookConnected();
    	   }
    	});

        }

    function openCredential(){

        $(".credential").show();
      }

    $(document).ready(function(){
    	$(".credential").hide();
    });

    </script>
  </head>

  <body>
    <div class="container credential">    
        <div id="loginbox" style="margin-top:50px;" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">                    
            <div class="panel panel-info" >
                    <div class="panel-heading">
                        <div class="panel-title">Sign In</div>
                        <div style="float:right; font-size: 80%; position: relative; top:-10px"><a href="#">Forgot password?</a></div>
                    </div>     

                    <div style="padding-top:30px" class="panel-body" >

                        <div style="display:none" id="login-alert" class="alert alert-danger col-sm-12"></div>
                            
                      <form:form action="loginuser" commandName="user" method="post" id="loginform" class="form-horizontal" role="form">
                      
                                    
                            <div style="margin-bottom: 25px" class="input-group">
                                        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                                         <form:input path="email" type="email" id="login-username" class="form-control" />
                                                                           
                                    </div>
                                
                            <div style="margin-bottom: 25px" class="input-group">
                                        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                                         <form:password path="password"  placeholder="password" id="login-password" class="form-control" />
                                          <font color="red"><form:errors path="password"/></font> 
                                    </div>
                                    

                         <!--        
                            <div class="input-group">
                                      <div class="checkbox">
                                        <label>
                                          <input id="login-remember" type="checkbox" name="remember" value="1"> Remember me
                                        </label>
                                      </div>
                                    </div> -->


                                <div style="margin-top:10px" class="form-group">
                                    <!-- Button -->

                                    <div class="col-sm-12 controls">
                                      <button type='submit' id="btn-login" class="btn btn-success">Login  </button>
                                       <a id="btn-fblogin" class="btn btn-primary" onClick=" facebookLogin()" >Login with Facebook</a>  
                                    </div>
                                </div>


                                <div class="form-group">
                                    <div class="col-md-12 control">
                                        <div style="border-top: 1px solid#888; padding-top:15px; font-size:85%" >
                                            Don't have an account! 
                                        <a href="#" onClick="$('#loginbox').hide(); $('#signupbox').show()">
                                            Sign Up Here
                                        </a>
                                        </div>
                                    </div>
                                </div>    
                            </form:form> 
                              
                        </div>                     
                    </div>  
        	</div>
        <div id="signupbox" style="display:none; margin-top:50px" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <div class="panel-title">Sign Up</div>
                            <div style="float:right; font-size: 85%; position: relative; top:-10px"><a id="signinlink" href="#" onclick="$('#signupbox').hide(); $('#loginbox').show()">Sign In</a></div>
                        </div>  
                        <div class="panel-body" >
                        
                          <form:form action="adduser.htm?action=signup" commandName="user" method="post" id="signupform" class="form-horizontal" role="form">
                         
                                
                                <div id="signupalert" style="display:none" class="alert alert-danger">
                                    <p>Error:</p>
                                    <span></span>
                                </div>
                                    
                                
                                  
                                <div class="form-group">
                                    <label for="email" class="col-md-3 control-label">Email</label>
                                    <div class="col-md-9">
                                     <form:input path="email" type="email" class="form-control" name="email" placeholder="Email Address"/>  
                                      <font color="red"><form:errors path="email"/></font>                                    
                                    </div>
                                </div>
                                    
                                <div class="form-group">
                                    <label for="firstname" class="col-md-3 control-label">First Name</label>
                                    <div class="col-md-9">
                                     <form:input path="firstName"  class="form-control"  name="firstName" placeholder="First Name"/>
                                      <font color="red"><form:errors path="firstName"/></font> 
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="lastname" class="col-md-3 control-label">Last Name</label>
                                    <div class="col-md-9">
                                      <form:input path="lastName" class="form-control" name="lastName" placeholder="Last Name"/>
                                        <font color="red"><form:errors path="lastName"/></font> 
                                    </div>
                                </div>
                                
                                  <div class="form-group">
                                    <label for="icode" class="col-md-3 control-label">Username</label>
                                    <div class="col-md-9">
                                     <form:input path="name" class="form-control" name="name" placeholder="Username"/>
                                      <font color="red"><form:errors path="name"/></font> 
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="password" class="col-md-3 control-label">Password</label>
                                    <div class="col-md-9">
                                     <form:password path="password"  class="form-control" name="password" placeholder="Password"/>
                                      <font color="red"><form:errors path="password"/></font> 
                                    </div>
                                </div>
                                    
                                <div class="form-group">
                                    <!-- Button -->                                        
                                    <div class="col-md-offset-3 col-md-9">
                                        <button id="btn-signup" type="submit" class="btn btn-info"><i class="icon-hand-right"></i> &nbsp Sign Up</button>
                                        <span style="margin-left:8px;">or</span>  
                                    </div>
                                </div>
                                
                                <div style="border-top: 1px solid #999; padding-top:20px"  class="form-group">
                                    
                                    <div class="col-md-offset-3 col-md-9">
                                        <button id="btn-fbsignup" type="button" class="btn btn-primary"><i class="icon-facebook"></i>   Sign Up with Facebook</button>
                                    </div>                                           
                                        
                                </div>
                                
                                
                                
                            </form:form>
                         </div>
                    </div>             
                
         </div> 
    </div>
    
  </body>
</html>
