package com.neu.social.pojo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="comment")
public class Comment {

	@Id
	@GeneratedValue
	@Column(name = "commentid", unique=true, nullable = false)
	private long commentId;
	
	private String commentText;
	
	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="user")
	private User user;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "postid", nullable = false)
	private SocialPost socialPost;
	
	
	private String commentImage;
	
	private long commentDate;
	 
	public Comment(){
		commentDate = System.currentTimeMillis();
	}
	
	public long getCommentId() {
		return commentId;
	}
	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public SocialPost getSocialPost() {
		return socialPost;
	}

	public void setSocialPost(SocialPost socialPost) {
		this.socialPost = socialPost;
	}

	public long getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(long commentDate) {
		this.commentDate = commentDate;
	}

	public String getCommentImage() {
		return commentImage;
	}

	public void setCommentImage(String commentImage) {
		this.commentImage = commentImage;
	}

}
