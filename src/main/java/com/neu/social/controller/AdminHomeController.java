package com.neu.social.controller;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.neu.social.utils.UrlConstants;

@Controller
public class AdminHomeController {

	//@RequestMapping(value = "/addcategory**", method = RequestMethod.POST)
	public Template adminHome(HttpServletRequest request, HttpServletResponse response,Model model){
		
		System.out.println("adminHome");
		

		if(!isUserLoggedIn(request, response)){
			System.out.print(" PostController :: adminHome addPost user not logged in redirictin to login page");
			try {
				request.getRequestDispatcher("redirect").forward(request, response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		 VelocityEngine ve = new VelocityEngine();
		// ve.setProperty("resource.loader", "class");
		// ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	     ve.init();
		Template template = ve.getTemplate("adminHome");
		 VelocityContext context = new VelocityContext();
	     context.put("name", "World");
	       
	    StringWriter writer = new StringWriter();
	    template.merge( context, writer );
	    
	    return template;
		/*System.out.println("AdminHomeController :: adminHome");
		ModelAndView modelview = new ModelAndView("adminHome","name","Sabrish");
	
		return modelview;*/
	}
	

	private boolean isUserLoggedIn(HttpServletRequest request, HttpServletResponse response){
		
		HttpSession httpSession = request.getSession();
		if(httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN)!= null){
			
			long personId = (Long)httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN);
			if(personId > 0){
				return true;
			}
		}
		
		return false;
	}
}
