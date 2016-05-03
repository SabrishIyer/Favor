package com.neu.social.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import com.neu.social.exception.SocialException;
import com.neu.social.pojo.Comment;
import com.neu.social.pojo.RequestPost;
import com.neu.social.pojo.User;

public class PostDao extends DAO {

	public RequestPost create(String question, String description, User user, long channelId, String channelTitle)
			throws SocialException {
		try {
			begin();
			RequestPost requestPost = new RequestPost();
			requestPost.setChannelId(channelId);
			requestPost.setChannelTitle(channelTitle);
			requestPost.setPostLongDescription(description);
			requestPost.setPostTitle(question);
			requestPost.setUser(user);
			getSession().save(requestPost);
			commit();
			return requestPost;
		} catch (HibernateException e) {
			rollback();
			// throw new AdException("Could not create advert", e);
			throw new SocialException("Exception while creating Post: " + e.getMessage());
		}
	}

	public List getAllPost() throws SocialException {

		try {
			begin();
			System.out.println("getAllPost ");
			Query q = getSession().createQuery("From RequestPost");
			List list = q.list();
			commit();
			return list;

		} catch (HibernateException e) {
			rollback();
			throw new SocialException("Could not list the Channels", e);
		}

	}

	public RequestPost getRequestPost(long postId) throws SocialException {

		System.out.println("postId " + postId);
		try {
			begin();
			Query q = getSession().createQuery("from RequestPost where postid = :postid");
			q.setLong("postid", postId);
			RequestPost requestPost = (RequestPost) q.uniqueResult();
			commit();
			return requestPost;
		} catch (HibernateException e) {
			rollback();
			throw new SocialException("Could not get RequestPost " + postId, e);
		}
	}

	public Comment addComment(RequestPost post, User user, String commentText) throws SocialException {

		System.out.println("addComment ");

		try {
			begin();
			Comment comment = new Comment();
			comment.setCommentText(commentText);
			comment.setUser(user);
			comment.setSocialPost(post);
			post.getComments().add(comment);
			getSession().save(comment);
			commit();
			return comment;
		} catch (HibernateException e) {
			rollback();
			throw new SocialException("Could not get addComment ", e);
		}
		
	}
	
	public RequestPost addPostImage(RequestPost post, String postImage) throws SocialException{
		System.out.println("addPostImage "+postImage);
		try{
			begin();
			post.setPostImage(postImage);
			getSession().update(post);
			commit();
			return post;
		}catch (HibernateException e) {
			rollback();
			throw new SocialException("Could not get addPostImage ", e);
		}
	}
	
	public boolean deletePost(long postId) throws SocialException{
		try{
		begin();
		RequestPost post = (RequestPost) getSession().get(RequestPost.class, postId);
		getSession().delete(post);
		commit();
		return true;
		}catch (HibernateException e) {
			rollback();
			e.printStackTrace();
			throw new SocialException("Could not get addPostImage ", e);
		}
	}
	
	public boolean deleteComment(long postId, long channelId, long commentId) throws SocialException{
		System.out.println("deleteComment commentId "+commentId);
		try{
		begin();
		RequestPost post = (RequestPost) getSession().get(RequestPost.class, postId);
		if(post != null && post.getComments() != null){
			System.out.println("deleteComment post "+post.getPostId()+" comments size = "+post.getComments().size());
			Iterator<Comment> iterator = post.getComments().iterator();
			while (iterator.hasNext()) {
				Comment comment = iterator.next();
			    if(comment.getCommentId() == commentId){
			    	System.out.println("deleteComment comment  found");
			    	iterator.remove();
			    	getSession().delete(comment);
			    	commit();
				}
			}
		}
		System.out.println("deleteComment After remove post "+post.getPostId()+" comments size = "+post.getComments().size());
		begin();
		getSession().save(post);
		commit();
		return true;
		}catch (HibernateException e) {
			rollback();
			e.printStackTrace();
			throw new SocialException("Could not get addPostImage ", e);
		}
	}
}
