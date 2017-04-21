package com.ds.coordinator;

public class MessageBean {

	String message;
	Long timestamp;
	
	public MessageBean(String message2, long currentTimeMillis) {
		// TODO Auto-generated constructor stub
		this.message = message2;
		this.timestamp = currentTimeMillis;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
