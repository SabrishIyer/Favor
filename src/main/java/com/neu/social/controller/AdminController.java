package com.neu.social.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.neu.social.dao.ChannelDao;
import com.neu.social.dao.ErrorType;
import com.neu.social.dao.PostDao;
import com.neu.social.dao.UserDAO;
import com.neu.social.exception.SocialException;
import com.neu.social.pojo.Channel;
import com.neu.social.pojo.Person;
import com.neu.social.pojo.SocialPost;
import com.neu.social.pojo.User;
import com.neu.social.utils.UrlConstants;

@Controller
public class AdminController {

	
	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage(HttpServletRequest request, HttpServletResponse response) {

		if(isUserLoggedIn(request, response)){
				return loadHome(request, response);
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("title", "Login as Admin");
		model.addObject("message", "This page is for ROLE_ADMIN only!");
		model.setViewName("admin");

		return model;

	}
	
	@RequestMapping(value = "/loginadmin**", method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, Model model) {

		ModelAndView mv = new ModelAndView();
	
		try {
			UserDAO userDao = new UserDAO();	
			String role = request.getParameter(UrlConstants.Parameters.USER_ROLE);
			if(role != null && role.equals(UrlConstants.ParameterValue.USER_ROLE_ADMIN)){
				User user2 = userDao.get(request.getParameter(UrlConstants.Parameters.EMAIL)
						,request.getParameter(UrlConstants.Parameters.PASSWORD)
						,Person.USER_ROLE_ADMIN);
				
				
				if(user2 != null){
					System.out.print("User Name = "+user2.getFirstName());
					HttpSession httpSession = request.getSession();
					httpSession.setAttribute(UrlConstants.SessionParameter.ADMIN_LOGGED_IN, user2.getPersonID());
					return loadHome(request, response);
				}
				
				mv.setViewName("admin");		
				
			}
			
		
			} catch (SocialException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		
		

		return mv;

	}
	
	@RequestMapping(value = "/category**", method = RequestMethod.GET)
	public ModelAndView showCategory(Model model,HttpServletRequest request, HttpServletResponse response){
		System.out.print("showCategory ");
		

		if(!isUserLoggedIn(request, response)){
			System.out.print(" AdminController :: showCategory user not logged in redirictin to login page");
			try {
				request.getRequestDispatcher("redirect").forward(request, response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		
		ModelAndView modelview = new ModelAndView();
		
		ChannelDao channelDao = null;
        List channelList = null;
       
        try{
        channelDao = new ChannelDao();	
        channelList = channelDao.getAllChannels();
        modelview.addObject("channelList", channelList);
        
        }catch(SocialException exception){
        	exception.printStackTrace();
        }
	
		model.addAttribute("channel", new Channel());
		modelview.setViewName("addCategoryForm");	
		return modelview;
	}
	
	//@RequestMapping(value = "/addcategory**", method = RequestMethod.POST)
	public String addCategory(@ModelAttribute("channel") Channel channel,BindingResult result, Model model ){
		System.out.print("addCategory ");
		ChannelDao channelDao = new ChannelDao();
		channelDao.createChannel(channel.getChannelTitle(), channel.getChannelDescription());
		return "addCategoryForm";
	}
	
	@RequestMapping(value = "/addcategory**", method = RequestMethod.GET)
	public void addCategory(HttpServletRequest request, HttpServletResponse response ){
		System.out.print("addCategory ");

		if(!isUserLoggedIn(request, response)){
			System.out.print(" AdminController :: addCategory user not logged in redirictin to login page");
			try {
				//request.getRequestDispatcher("redirect").forward(request, response);
				JSONObject obj = new JSONObject();
				obj.put("redirecturl", "redirect");
				System.out.println("Jsn - " + obj);
				response.setContentType("application/json");
				response.getWriter().println(obj);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return ;
		}
		
		ChannelDao channelDao = new ChannelDao();
		ErrorType errorType =   channelDao.createChannel(request.getParameter(UrlConstants.Parameters.CHANNEL_TITLE), 
				request.getParameter(UrlConstants.Parameters.CHANNEL_DESCRIPTION));
		try {
			if(errorType == ErrorType.CHANNEL_EXIST){
				response.getWriter().write("channelexist");
			}else{
				response.getWriter().write("success");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/loadhome**", method = RequestMethod.GET)
	public ModelAndView loadHome(HttpServletRequest request, HttpServletResponse response ){
		ModelAndView mv = new ModelAndView();
		System.out.print("loadHome ");
		
		try {

			HttpSession httpSession = request.getSession();
			if(httpSession.getAttribute(UrlConstants.SessionParameter.ADMIN_LOGGED_IN)!=null){
			long adminId = (Long)httpSession.getAttribute(UrlConstants.SessionParameter.ADMIN_LOGGED_IN);
			UserDAO userDao = new UserDAO();
			User user2 = userDao.getUser(adminId);
			ChannelDao channelDao = new ChannelDao();
			PostDao postDao = new PostDao();
			List allChannelsList;
			allChannelsList = channelDao.getAllChannels();
			List allPost = postDao.getAllPost();
			
			mv.addObject(UrlConstants.Parameters.USERNAME, user2.getFirstName() +" "+user2.getLastName());
			mv.addObject(UrlConstants.Parameters.CHANNEL_LIST, allChannelsList);
			mv.addObject(UrlConstants.Parameters.POST_LIST, allPost);
			mv.setViewName("adminHome");
			System.out.print(" Channel List Size - " + allChannelsList.size());
			}
		} catch (SocialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return mv;

	}
	
	
	/*//@RequestMapping(value = "/addcategory**", method = RequestMethod.POST)
	public ModelAndView adminHomemm(Model model){
		System.out.println("AdminHomecontroller :: adminHome");
		 ArrayList list = new ArrayList();
		   Map map = new HashMap();
		   map.put("name", "horse");
		   map.put("price", "00.00");
		   list.add( map );
		 
		   map = new HashMap();
		   map.put("name", "dog");
		   map.put("price", "9.99");
		   list.add( map );
		   map = new HashMap();
		   map.put("name", "bear");
		   map.put("price", ".99");
		   list.add( map );
		ModelAndView modelview = new ModelAndView("adminHome","name","Sabrish");
	model.addAttribute("petList", list);
		return modelview;
	}
	*/

	private boolean isUserLoggedIn(HttpServletRequest request, HttpServletResponse response){
		
		HttpSession httpSession = request.getSession();
		if(httpSession.getAttribute(UrlConstants.SessionParameter.ADMIN_LOGGED_IN)!= null){
			
			long personId = (Long)httpSession.getAttribute(UrlConstants.SessionParameter.ADMIN_LOGGED_IN);
			if(personId > 0){
				return true;
			}
		}
		
		return false;
	}
	
	@RequestMapping(value = "/removepost**", method = RequestMethod.GET)
	public void removePost(HttpServletRequest request, HttpServletResponse response){
		

		if(!isUserLoggedIn(request, response)){
			System.out.print(" AdminController :: removePost user not logged in redirictin to login page");
			try {
				//request.getRequestDispatcher("redirect").forward(request, response);
				JSONObject obj = new JSONObject();
				obj.put("redirecturl", "redirect");
				System.out.println("Jsn - " + obj);
				response.setContentType("application/json");
				response.getWriter().println(obj);
				
			}  catch (Exception e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		String postId = request.getParameter(UrlConstants.Parameters.POST_ID);
		System.out.println("AdminController :: removePost postId " + postId);
		
		if(postId != null){
			
			try {
				PostDao postDao = new PostDao();
				boolean isDeleted = postDao.deletePost(Long.parseLong(postId));
				
				if(isDeleted){
					JSONObject obj = new JSONObject();
					obj.put("deletedpostId",postId);
					response.setContentType("application/json");
					response.getWriter().println(obj);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	 @RequestMapping(value = "/logoutadmin", method = RequestMethod.GET)
		private void logout(HttpServletRequest request, HttpServletResponse response){
			 request.getSession().invalidate();
			 request.getSession().setAttribute(UrlConstants.SessionParameter.ADMIN_LOGIN_STATUS,
					 UrlConstants.SessionParameterValues.USER_LOGGED_OUT);
			 Enumeration<String>parameterNames = request.getParameterNames();
			 
			 while (parameterNames.hasMoreElements()) {
			        String parameterName = parameterNames.nextElement();
			        request.removeAttribute(parameterName);
			    }
			 try {
					request.getRequestDispatcher("redirect").forward(request, response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	 
	 @RequestMapping(value = "/removeComment**", method = RequestMethod.GET)
		public void removeComment(HttpServletRequest request, HttpServletResponse response){
			

			if(!isUserLoggedIn(request, response)){
				System.out.print(" AdminController :: removeComment user not logged in redirictin to login page");
				try {
					//request.getRequestDispatcher("redirect").forward(request, response);
					JSONObject obj = new JSONObject();
					obj.put("redirecturl", "redirect");
					System.out.println("Jsn - " + obj);
					response.setContentType("application/json");
					response.getWriter().println(obj);
					
				}  catch (Exception e) {
					e.printStackTrace();
				}
				
				return;
			}
			
			String postId = request.getParameter(UrlConstants.Parameters.POST_ID);
			String channelId = request.getParameter(UrlConstants.Parameters.CHANNEL_ID);
			String commentId = request.getParameter(UrlConstants.Parameters.COMMENT_ID);
			System.out.println("AdminController :: removeComment postId " + postId + " channelId "+channelId+"commentId "+commentId);
			
			if(postId != null){
				
				try {
					PostDao postDao = new PostDao();
					boolean isDeleted = postDao.deleteComment(Long.parseLong(postId), Long.parseLong(channelId), Long.parseLong(commentId));
					
					if(isDeleted){
						JSONObject obj = new JSONObject();
						obj.put("deletedcommentId",postId);
						response.setContentType("application/json");
						response.getWriter().println(obj);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
}
