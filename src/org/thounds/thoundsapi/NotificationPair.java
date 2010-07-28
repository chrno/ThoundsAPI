package org.thounds.thoundsapi;
/**
 * 
 */
public class NotificationPair <T extends ThoundsObjectInterface>{

	private T obj;
	private int id;
	
	public NotificationPair(T obj, int id) {
		this.obj = obj;
		this.id = id;
	}
	
	public T getNotificationObject(){
		return obj;
	}
	
	public int getNotificationId(){
		return id;
	}
	
	public void setNotificationObject(T obj){
		this.obj = obj;
	}
	
	public void setNotificationId(int id){
		this.id = id;
	}
}
