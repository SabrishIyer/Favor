package com.neu.social.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="usertable")
@PrimaryKeyJoinColumn(name="userId")
public class User extends Person {
	
	private String name;
	private String gender;
	private String email;
	private String telephoneNumber;
	private String website;
	private String profilePhoto;
	private String headerPhoto;
	private String birthDate;
	private String bio;
	@OneToOne(fetch=FetchType.EAGER, mappedBy="user", cascade=CascadeType.ALL)
	private Address address;
	
	private String password;
	private String loginType;
	

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "user_channel", joinColumns = { 
			@JoinColumn(name = "personID", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "channelId", 
					nullable = false, updatable = false) })
	private Set<Channel> channels = new HashSet<Channel>(0);
	
	 public User(String name, String password) {
	        this.name = name;
	        this.password = password;
	    }

	public User(){
		
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}
	public String getHeaderPhoto() {
		return headerPhoto;
	}
	public void setHeaderPhoto(String headerPhoto) {
		this.headerPhoto = headerPhoto;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	
	public Set<Channel> getChannels() {
		return this.channels;
	}

	public void setCategories(Set<Channel> channels) {
		this.channels = channels;
	}
	
}
