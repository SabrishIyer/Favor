package com.neu.social.pojo;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="applypost")
@PrimaryKeyJoinColumn(name="postid")
public class ApplyPost extends SocialPost {

	private String postTitle;
	private String postSmallDescription;
	private String postLongDescription;
	private long postDate;
	
	public String getPostTitle() {
		return postTitle;
	}
	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}
	public long getPostDate() {
		return postDate;
	}
	public void setPostDate(long postDate) {
		this.postDate = postDate;
	}
	
	public String getPostSmallDescription() {
		return postSmallDescription;
	}
	public void setPostSmallDescription(String postSmallDescription) {
		this.postSmallDescription = postSmallDescription;
	}
	public String getPostLongDescription() {
		return postLongDescription;
	}
	public void setPostLongDescription(String postLongDescription) {
		this.postLongDescription = postLongDescription;
	}

}
