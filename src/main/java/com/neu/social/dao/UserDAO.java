package com.neu.social.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import com.neu.social.exception.SocialException;
import com.neu.social.pojo.Address;
import com.neu.social.pojo.Channel;
import com.neu.social.pojo.User;
import com.neu.social.utils.UrlConstants;


public class UserDAO extends DAO {

    public UserDAO() {
    }

    public User get(String username, String password, String role)
            throws SocialException {
    	
    	System.out.println("username "+username + " password "+password);
        try {
            begin();
            Query q = getSession().createQuery("from User where email = :email and password = :password and role = :role");
            q.setString("email", username);
            q.setString("password", password);
            q.setString("role", role);
            User user = (User) q.uniqueResult();
            commit();
            return user;
        } catch (HibernateException e) {
            rollback();
            throw new SocialException("Could not get user " + username, e);
        }
    }
    
    public User getUser(long personID)
            throws SocialException {
    	
    	System.out.println("personID "+personID );
        try {
            begin();
            Query q = getSession().createQuery("from User where personID = :personID");
            q.setLong("personID", personID);
            User user = (User) q.uniqueResult();
            commit();
            return user;
        } catch (HibernateException e) {
            rollback();
            throw new SocialException("Could not get user " + personID, e);
        }
    }
    
    public boolean addChannel(long personID, long channelId)  throws SocialException{
    	 try {
    	User user = getUser(personID);
    	ChannelDao channelDao = new ChannelDao();
    	Channel channel = channelDao.getChannel(channelId);
    	user.getChannels().add(channel);
    	begin();
    	getSession().update(user);
    	commit();
    	 } catch (HibernateException e) {
             rollback();
             throw new SocialException("Could not addChannel " + personID +" "+ channelId, e);
         }
    	return true;
    }
    
    public boolean checkIfUserExist(String emailId,String name, String loginType){
    	
    	 Query q = null;
    	 User user = null;
    	 try {
    	 begin();
    	if(loginType != null && loginType.equals(UrlConstants.ParameterValue.SOCIAL_FACEBOOK)){
    		q = getSession().createQuery("from User where name = :name and loginType = :loginType");
            q.setString("name", name);
            q.setString("loginType", loginType);
    	}else if(loginType != null && loginType.equals(UrlConstants.ParameterValue.LOGIN_TYPE_MYAPP)){
    		q = getSession().createQuery("from User where email = :email and loginType = :loginType");
            q.setString("email", emailId);
            q.setString("loginType", loginType);
    	}
    	
    	user = (User) q.uniqueResult();
    	if(user != null){
    		return true;
    	}
        commit();
    	 } catch (HibernateException e) {
             rollback();
            
         }
    	 
    	 return false;
    }
    
    public User getUser(String emailId,String name, String loginType){
    	
   	 Query q = null;
   	 User user = null;
   	 try {
   	 begin();
   	if(loginType != null && loginType.equals(UrlConstants.ParameterValue.SOCIAL_FACEBOOK)){
   		q = getSession().createQuery("from User where name = :name and loginType = :loginType");
           q.setString("name", name);
           q.setString("loginType", loginType);
   	}else if(loginType != null && loginType.equals(UrlConstants.ParameterValue.LOGIN_TYPE_MYAPP)){
   		q = getSession().createQuery("from User where email = :email and loginType = :loginType");
           q.setString("email", emailId);
           q.setString("loginType", loginType);
   	}
   	
   	user = (User) q.uniqueResult();
    //commit();
   	//return user;
      
   	 } catch (HibernateException e) {
            rollback();
           
        }
   	 
   	 return user;
   }

    public ErrorType create(String username, String password,String emailId, String firstName, String lastName, String loginType, String role)
            throws SocialException {
    	
    	if(!checkIfUserExist(emailId, username, loginType)){
        try {
            begin();
            System.out.println("create "+username);
            
            Address address=new Address();
            User user=new User(username,password);
            
            user.setFirstName(firstName);
            user.setLastName(lastName);
            
            user.setEmail(emailId);
            user.setAddress(address);
            
            user.setRole(role);
            
            user.setLoginType(loginType);
            
            address.setUser(user);
            
            getSession().save(user);
            
            commit();
            return ErrorType.USER_CREATED;
        } catch (HibernateException e) {
            rollback();
            //throw new AdException("Could not create user " + username, e);
            
            throw new SocialException("Exception while creating user: " + e.getMessage());
        }
      }else{
    	  return ErrorType.USER_EXIST;
      }
    	
    }

    public void delete(User user)
            throws SocialException {
        try {
            begin();
            getSession().delete(user);
            commit();
        } catch (HibernateException e) {
            rollback();
            throw new SocialException("Could not delete user " + user.getName(), e);
        }
    }
}