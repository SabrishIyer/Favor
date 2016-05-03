package com.neu.social.controller;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
public class HomeController {

	@Autowired
	@Qualifier("userValidator")
	UserValidator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView doSubmitAction(@ModelAttribute("user") User user,HttpServletRequest webRequest,HttpServletResponse response, BindingResult result) throws Exception {

		System.out.println("doSubmitAction ");
		ModelAndView modelAndView = new ModelAndView();
		HttpSession httpSession = webRequest.getSession();
		if((httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS ) != null) &&
		httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS ).equals(UrlConstants.SessionParameterValues.USER_LOGGED_OUT)){
			try {
				System.out.println("loginUser status - "+httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS ));
				httpSession.invalidate();
				//request.getRequestDispatcher("redirect").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelAndView.setViewName("home");
			return modelAndView;
		}
		validator.validate(user, result);
		if (result.hasErrors()) {
			 modelAndView.setViewName("home");
			 return modelAndView;
		}
		String action = webRequest.getParameter(UrlConstants.Parameters.ACTION); 
		System.out.print("Action = "+action);
		if(action!= null && action.equals(UrlConstants.ParameterValue.SIGNUP)){
			try {
				System.out.print("test");
				UserDAO userDao = new UserDAO();
				System.out.print("test1");
				
				ErrorType errorType = userDao.create(user.getName(), user.getPassword(), user.getEmail(), user.getFirstName(),
						user.getLastName(),UrlConstants.ParameterValue.LOGIN_TYPE_MYAPP, Person.USER_ROLE_USER);
				
				
				if (errorType != null) {
					User user2 = userDao.getUser(user.getEmail(), user.getName(), UrlConstants.ParameterValue.LOGIN_TYPE_MYAPP);
					if(user2 != null){
						System.out.println("In SocialCredentialController userId " + user2.getPersonID());
						return onSignupLogin(webRequest, user2);
					}
				}
				
				// DAO.close();
			} catch (SocialException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		}else if(action!= null && action.equals(UrlConstants.ParameterValue.LOGIN)){
			try {
			UserDAO userDao = new UserDAO();		
			User user2 = userDao.get(user.getEmail(),user.getPassword(), Person.USER_ROLE_USER);
			
			if(user2 != null){
				
				httpSession = webRequest.getSession();
				httpSession.setAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN, user2.getPersonID());
			}
			System.out.print("User Name = "+user2.getName());
			} catch (SocialException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		}
		modelAndView.setViewName("userMain");
		return modelAndView;
	}
	
	@RequestMapping(value = "/loginuser**", method = RequestMethod.POST)
	public ModelAndView loginUser(@ModelAttribute("user") User user,HttpServletRequest request,HttpServletResponse response, Model model) {
		
		System.out.println("loginUser ");
		ModelAndView mv = new ModelAndView();
		HttpSession httpSession = request.getSession();
		if((httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS ) != null) &&
		httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS ).equals(UrlConstants.SessionParameterValues.USER_LOGGED_OUT)){
			try {
				System.out.println("loginUser status - "+httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS ));
				httpSession.invalidate();
				//request.getRequestDispatcher("redirect").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mv.setViewName("home");
			return mv;
		}else if(isUserLoggedIn(request, response)){
		    long personId = (Long)httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN);
		    UserDAO userDao = new UserDAO();		
			User user2;
			try {
				user2 = userDao.getUser(personId);
				return onSignupLogin(request, user2);
			} catch (SocialException e) {
				e.printStackTrace();
			}
			
		}

		
		
		try {
			UserDAO userDao = new UserDAO();		
			User user2 = userDao.get(user.getEmail(),user.getPassword(), Person.USER_ROLE_USER);
			
			if(user2 != null){
				
				return onSignupLogin(request, user2);
			}
		
			} catch (SocialException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		
		return mv;
	}
	
	private ModelAndView onSignupLogin(HttpServletRequest request, User user2) {
		ModelAndView mv = new ModelAndView();
		System.out.println("onSignupLogin ");
		try {
			if (user2 != null) {

				HttpSession httpSession = request.getSession();
				httpSession.setAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN, user2.getPersonID());
				httpSession.setAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS,
						 UrlConstants.SessionParameterValues.USER_LOGGED_IN);
				mv.addObject(UrlConstants.Parameters.USERNAME, user2.getFirstName() + " " + user2.getLastName());
				Set<Channel> userChannelList = user2.getChannels();
				mv.addObject(UrlConstants.Parameters.SELECTED_CHANNEL_LIST, userChannelList);
				ChannelDao channelDao = new ChannelDao();
				PostDao postDao = new PostDao();
				List allChannelsList = channelDao.getAllChannels();

				for (Iterator<Channel> iterator = allChannelsList.iterator(); iterator.hasNext();) {
					Channel channel = iterator.next();
					if (userChannelList.contains(channel)) {
						iterator.remove();
					}
				}
				List allPost = postDao.getAllPost();
				/*for (Object socialPost : allPost) {

					SocialPost post = (SocialPost) socialPost;
				}*/
				mv.addObject(UrlConstants.Parameters.CHANNEL_LIST, allChannelsList);
				mv.addObject(UrlConstants.Parameters.POST_LIST, allPost);
				mv.addObject(UrlConstants.Parameters.USER_ID, user2.getPersonID());
				mv.setViewName("userMain");
				System.out.print("User Name = " + user2.getName() + " Selected Channel Size - " + userChannelList.size()
						+ " Channel List Size - " + allChannelsList.size());
			}

		} catch (SocialException e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return mv;
	}

	@RequestMapping(value = "/adduserchannel**", method = RequestMethod.POST)
	public void addUserChannel(HttpServletRequest request, HttpServletResponse response){
		System.out.print("addUserChannel ");
		if(!isUserLoggedIn(request, response)){
			System.out.print("addUserChannel user not logged in redirictin to login page");
			try {
				request.getRequestDispatcher("redirect").forward(request, response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}

		
		String channelId = request.getParameter(UrlConstants.Parameters.CHANNEL_ID);
		String channelTitle = request.getParameter(UrlConstants.Parameters.CHANNEL_TITLE);
		HttpSession httpSession = request.getSession();
		long personID = (Long)httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN);
		
		System.out.print("addUserChannel channelId - "+channelId + " channelTitle "+channelTitle + " personId - "+personID);
		UserDAO userDAO = new UserDAO();
		boolean status = false;
		
		try {
			status = userDAO.addChannel(personID, Long.parseLong(channelId));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SocialException e) {
			e.printStackTrace();
		}
		System.out.print("addUserChannel status -  "+status);
		try {
			response.getWriter().write(status?"success":"failed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initializeForm(@ModelAttribute("user") User user, BindingResult result,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView modelAndView = new ModelAndView();
		if(isUserLoggedIn(request, response)){
			HttpSession httpSession = request.getSession();	
		    long personId = (Long)httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN);
		    UserDAO userDao = new UserDAO();		
			User user2;
			try {
				user2 = userDao.getUser(personId);
				return onSignupLogin(request, user2);
			} catch (SocialException e) {
				e.printStackTrace();
			}
			
		}
		modelAndView.setViewName("home");
		return  modelAndView;
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
	
	 @RequestMapping(value = "/logout", method = RequestMethod.GET)
	private void logout(HttpServletRequest request, HttpServletResponse response){
		 request.getSession().invalidate();
		 request.getSession().setAttribute(UrlConstants.SessionParameter.USER_LOGIN_STATUS,
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
	
	 @RequestMapping(value = "/redirect", method = RequestMethod.GET)
	 public String redirect(HttpServletRequest request, HttpServletResponse response) {
		 System.out.println("redirect ");
		 Enumeration<String>parameterNames = request.getParameterNames();
		 
		 if(parameterNames != null){
		 while (parameterNames.hasMoreElements()) {
		        String parameterName = parameterNames.nextElement();
		        request.removeAttribute(parameterName);
		    }
		 }
	      return "redirect:home";
	 }
}