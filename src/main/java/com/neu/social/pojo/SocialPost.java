package com.neu.social.pojo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="socialpost")
@Inheritance(strategy=InheritanceType.JOINED) //table per subclass
public class SocialPost {

	@Id
	@GeneratedValue
	@Column(name = "postid", unique=true, nullable = false)
	private long postId;
	
	//keep one and comment other and vice-versa to explain the result in console
    @ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
    //@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="user")
    private User user;
    
    @Transient //will stored in advert table
    private String channelTitle;
    
    @JoinColumn(name="channelId")
    private long channelId;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "socialPost")
	private Set<Comment> comments = new HashSet<Comment>(0);
    
    
    private String postImage;
    

    
    public SocialPost(User user, String channelTitle, long channelId) {
		this.user = user;
		this.channelTitle = channelTitle;
		this.channelId = channelId;
	}

	public SocialPost(){
		
	}
	
	public long getPostId() {
		return postId;
	}
	public void setPostId(long postId) {
		this.postId = postId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getChannelTitle() {
		return channelTitle;
	}
	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}
	public long getChannelId() {
		return channelId;
	}
	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public String getPostImage() {
		return postImage;
	}

	public void setPostImage(String postImage) {
		this.postImage = postImage;
	}

	
}
