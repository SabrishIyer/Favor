package com.neu.social.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.neu.social.dao.ChannelDao;
import com.neu.social.dao.PostDao;
import com.neu.social.dao.UserDAO;
import com.neu.social.exception.SocialException;
import com.neu.social.pojo.Channel;
import com.neu.social.pojo.Comment;
import com.neu.social.pojo.RequestPost;
import com.neu.social.pojo.User;
import com.neu.social.utils.UrlConstants;

@Controller
public class PostController {

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/addPost**", method = RequestMethod.GET)
	public void addPost(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("PostController :: addPost ");
		
		if(!isUserLoggedIn(request, response)){
			System.out.print("addPost user not logged in redirictin to login page");
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
			
			
			return;
		}
		
		String question = null;
		String description = null;
		int channelId = -1;
		if (request.getParameter(UrlConstants.Parameters.POST_QUESTION) != null) {
			question = request.getParameter(UrlConstants.Parameters.POST_QUESTION);
		}
		if (request.getParameter(UrlConstants.Parameters.POST_DESCRIPTION) != null) {
			description = request.getParameter(UrlConstants.Parameters.POST_DESCRIPTION);
		}

		if (request.getParameter(UrlConstants.Parameters.POST_CHANNEL) != null) {
			channelId = Integer.parseInt(request.getParameter(UrlConstants.Parameters.POST_CHANNEL));
		}

		if (question != null && channelId > -1) {

			HttpSession httpSession = request.getSession();
			long personID = (Long) httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN);

			System.out.print("addPost channelId - " + channelId + " question " + question + " personId - " + personID
					+ " description - " + description);

			try {
				User user = new UserDAO().getUser(personID);

				Channel channel = new ChannelDao().getChannel(channelId);

				PostDao postDao = new PostDao();

				RequestPost requestPost = postDao.create(question, description, user, channel.getChannelId(),
						channel.getChannelTitle());

				if (requestPost != null) {

					try {

						JSONArray jsonArray = new JSONArray();

						if (requestPost.getComments() != null) {
							for (Comment comment : requestPost.getComments()) {
								jsonArray.equals(comment);
							}
						}

						JSONObject obj = new JSONObject();
						// obj.put("status", "success");
						obj.put("postId", requestPost.getPostId());
						obj.put("postTitle", requestPost.getPostTitle());
						obj.put("postDescription", requestPost.getPostLongDescription());
						obj.put("postDate", requestPost.getPostDate());
						obj.put("postUserId", requestPost.getUser().getPersonID());
						obj.put("postUserName",
								requestPost.getUser().getFirstName() + " " + requestPost.getUser().getLastName());
						obj.put("channelId", requestPost.getChannelId());
						obj.put("channelName", requestPost.getChannelTitle());
						obj.put("comments", jsonArray);
						System.out.println("Jsn - " + obj);
						response.setContentType("application/json");
						response.getWriter().println(obj);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			} catch (SocialException e) {
				e.printStackTrace();
			}

		}

	}

	@RequestMapping(value = "/addComment**", method = RequestMethod.GET)
	public void addComment(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("PostController :: addComment ");
		
		if(!isUserLoggedIn(request, response)){
			System.out.print(" PostController :: addComment addPost user not logged in redirictin to login page");
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
			
			return;
		}

		long postId = -1;
		long userId = -1;

		if (request.getParameter(UrlConstants.Parameters.POST_ID) != null) {
			postId = Long.parseLong(request.getParameter(UrlConstants.Parameters.POST_ID));
		}

		if (request.getParameter(UrlConstants.Parameters.USER_ID) != null) {
			userId = Long.parseLong(request.getParameter(UrlConstants.Parameters.USER_ID));
		}
		HttpSession httpSession = request.getSession();
		long personID = (Long) httpSession.getAttribute(UrlConstants.SessionParameter.USER_LOGGED_IN);

		try {
			User user = new UserDAO().getUser(personID);
			PostDao postDao = new PostDao();
			RequestPost requestPost = postDao.getRequestPost(postId);
			String commentText = request.getParameter(UrlConstants.Parameters.POST_COMMENT);
			System.out.println("PostController :: addComment commentText : " + commentText);
			System.out.println("PostController :: addComment requestPost Title : " + requestPost.getPostTitle());
			System.out.println("PostController :: addComment requestPost user : " + user.getFirstName());
			if (commentText != null) {
				Comment comment = postDao.addComment(requestPost, user, commentText);

				
				if(comment != null){
					

					JSONObject obj = new JSONObject();
					// obj.put("status", "success");
					obj.put("commentText", comment.getCommentText());
					obj.put("commentId", comment.getCommentId());
					obj.put("commentUserId", comment.getUser().getPersonID());
					obj.put("commentUserName", comment.getUser().getFirstName() + " "+comment.getUser().getLastName());
					System.out.println("Jsn - " + obj);
					response.setContentType("application/json");
					response.getWriter().println(obj);
					
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/addPostImage**", method = RequestMethod.POST)
	public void addPostImage(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "file", required = false) MultipartFile multipartFile) {

		System.out.println("PostController :: addPostImage ");
		
		if(!isUserLoggedIn(request, response)){
			System.out.print(" PostController :: addPostImage addPost user not logged in redirictin to login page");
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

		String orgName = multipartFile.getOriginalFilename();
		String postId = request.getParameter(UrlConstants.Parameters.POST_ID);
		System.out.println("PostController :: addPostImage orgName " + orgName);
		// MultipartFile file = request.getPar;
		Map<String, String[]> map = request.getParameterMap();
		for (Entry<String, String[]> entry : map.entrySet()) {
			System.out.println("PostController: addPostImage :: Key = " + entry.getKey() + ", Value = " + Arrays.toString(entry.getValue()));
		}

		if (postId != null) {
			try {
				String imageName = UUID.randomUUID().toString().replace(",", "").substring(0, 6) + ".jpg";
				if (saveImage(imageName, multipartFile)) {

					PostDao postDao = new PostDao();
					RequestPost requestPost = postDao.getRequestPost(Long.parseLong(postId));
					if(requestPost != null){
						requestPost = postDao.addPostImage(requestPost, "resources/img/dashboard/"+imageName);
						
						try {

							JSONArray jsonArray = new JSONArray();

							if (requestPost.getComments() != null) {
								for (Comment comment : requestPost.getComments()) {
									jsonArray.equals(comment);
								}
							}

							JSONObject obj = new JSONObject();
							// obj.put("status", "success");
							obj.put("postId", requestPost.getPostId());
							obj.put("postTitle", requestPost.getPostTitle());
							obj.put("postDescription", requestPost.getPostLongDescription());
							obj.put("postDate", requestPost.getPostDate());
							obj.put("postUserId", requestPost.getUser().getPersonID());
							obj.put("postUserName",
									requestPost.getUser().getFirstName() + " " + requestPost.getUser().getLastName());
							obj.put("channelId", requestPost.getChannelId());
							obj.put("channelName", requestPost.getChannelTitle());
							obj.put("postImage", requestPost.getPostImage());
							obj.put("comments", jsonArray);
							System.out.println("Jsn - " + obj);
							response.setContentType("application/json");
							response.getWriter().println(obj);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean saveImage(String filename, MultipartFile image) throws RuntimeException, IOException {
		try {
			File file = new File(context.getRealPath("/") + "/resources/img/dashboard/" + filename); // Creates
																										// a
																										// new
																										// File

			FileUtils.writeByteArrayToFile(file, image.getBytes()); // Transfers
																	// Byte by
																	// Byte to
																	// location
			System.out.println("Go to the location:  " + file.toString()
					+ " on your computer and verify that the image has been stored.");

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
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
