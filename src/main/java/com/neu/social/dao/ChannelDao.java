package com.neu.social.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import com.neu.social.exception.SocialException;
import com.neu.social.pojo.Channel;
import com.neu.social.pojo.User;

public class ChannelDao extends DAO {
	
	public ChannelDao(){
		
	}
	
	public ErrorType createChannel(String channelTitle, String channelDescription){
		System.out.println("channeltitle "+channelTitle );
		try{
			if(channelTitle != null && isChannelExist(channelTitle) == null ){
			 begin();
	         System.out.println("createChannel doesnot exist "+channelTitle);
	         Channel channel = new Channel();
	         channel.setChannelTitle(channelTitle);
	         channel.setChannelDescription(channelDescription);
	         
	         getSession().save(channel);
	         commit();
	         return ErrorType.CHANNEL_CREATED;
			}else{
				 return ErrorType.CHANNEL_EXIST	;
			}
		}catch (HibernateException e) {
            rollback();
            return ErrorType.CHANNEL_CREATE_ERROR;
        }catch(Exception e){
        	  rollback();
              return ErrorType.CHANNEL_CREATE_ERROR;
        }
		
	}
	
public Channel isChannelExist(String channeltitle) throws SocialException{
		
		System.out.println("channeltitle "+channeltitle );
        try {
            begin();
            Query q = getSession().createQuery("from Channel where channelTitle = :channelTitle");
            q.setString("channelTitle", channeltitle);
            if(q.uniqueResult() != null){
            	Channel channel = (Channel) q.uniqueResult();
            	//commit();
                return channel;
            }
            
           return null;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            throw new SocialException("Could not get channel " + channeltitle, e);
        }
        
	}
	
	public Channel getChannel(long channelId) throws SocialException{
		
		System.out.println("channelId "+channelId );
        try {
            begin();
            Query q = getSession().createQuery("from Channel where channelId = :channelId");
            q.setLong("channelId", channelId);
            Channel channel = (Channel) q.uniqueResult();
            commit();
            return channel;
        } catch (HibernateException e) {
            rollback();
            throw new SocialException("Could not get channel " + channelId, e);
        }
	}
	
	public List getAllChannels() throws SocialException{
		
		try{
			 begin();
	         System.out.println("getAllChannels ");
	         Query q =  getSession().createQuery("From Channel");
	         List list = q.list();
	         commit();
	         return list;
	         
			
		}catch (HibernateException e) {
           rollback();
           throw new SocialException("Could not list the Channels", e);
       }
		
	}

}
