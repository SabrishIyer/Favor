package com.neu.social.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
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
public class SocialCredentialController {

	@RequestMapping(value = "/sociallogin**",method = RequestMethod.GET)
	protected ModelAndView socialLogin(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String action = request.getParameter(UrlConstants.Parameters.ACTION);
		System.out.println("In SocialCredentialController action " + action);
		ModelAndView modelAndView = new ModelAndView();
		if (action != null && action.equals(UrlConstants.ParameterValue.SIGNUP)) {
			try {
				UserDAO userDao = new UserDAO();
				ErrorType errorType = userDao.create(request.getParameter(UrlConstants.Parameters.USERNAME),
						request.getParameter(UrlConstants.Parameters.PASSWORD),
						request.getParameter(UrlConstants.Parameters.EMAIL),
						request.getParameter(UrlConstants.Parameters.FIRST_NAME),
						request.getParameter(UrlConstants.Parameters.LAST_NAME),
						request.getParameter(UrlConstants.Parameters.SOCIAL_TYPE), Person.USER_ROLE_USER);
				if (errorType != null) {
					User user = userDao.getUser(request.getParameter(UrlConstants.Parameters.EMAIL),
							request.getParameter(UrlConstants.Parameters.USERNAME),
							request.getParameter(UrlConstants.Parameters.SOCIAL_TYPE));
					if(user != null){
						System.out.println("In SocialCredentialController userId " + user.getPersonID());
						return onSignupLogin(request, user);
					}
				}
			
				modelAndView.setViewName("userMain");
			} catch (SocialException e) {
				System.out.println("Exception: " + e.getMessage());
			}

		} 

		return modelAndView;
	}

	private ModelAndView onSignupLogin(HttpServletRequest request, User user2) {
		ModelAndView mv = new ModelAndView();
		try {
			if (user2 != null) {

				HttpSession httpSession = request.getSession();
				httpSession.setAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN, user2.getPersonID());
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
				for (Object socialPost : allPost) {

					SocialPost post = (SocialPost) socialPost;
					System.out.println("Comment size " + post.getComments().size());
				}
				mv.addObject(UrlConstants.Parameters.CHANNEL_LIST, allChannelsList);
				mv.addObject(UrlConstants.Parameters.POST_LIST, allPost);
				mv.setViewName("userMain");
				System.out.println("User Name = " + user2.getName() + " Selected Channel Size - " + userChannelList.size()
						+ " Channel List Size - " + allChannelsList.size() +" allPost size - "+allPost.size());
			}

		} catch (SocialException e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return mv;
	}
	

	private boolean isUserLoggedIn(HttpServletRequest request){
		
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
