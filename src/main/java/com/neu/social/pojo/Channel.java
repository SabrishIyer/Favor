package com.neu.social.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="channeltable")
public class Channel {

	@Id @GeneratedValue
	@Column(name="channelId", unique = true, nullable = false)
	private long channelId;
	
	private String channelTitle;
	
	private String channelDescription;
	private String channelImage;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "channels")
	private Set<User> users = new HashSet<User>(0);
	
	
	public Channel(String channelTitle, String channelDescription){
		this.channelTitle = channelTitle;
		this.channelDescription = channelDescription;
	}
	
	public Channel(){
		
	}
	
	public long getChannelId() {
		return channelId;
	}
	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}
	public String getChannelTitle() {
		return channelTitle;
	}
	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}
	public String getChannelDescription() {
		return channelDescription;
	}
	public void setChannelDescription(String channelDescription) {
		this.channelDescription = channelDescription;
	}
	public String getChannelImage() {
		return channelImage;
	}
	public void setChannelImage(String channelImage) {
		this.channelImage = channelImage;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
}
