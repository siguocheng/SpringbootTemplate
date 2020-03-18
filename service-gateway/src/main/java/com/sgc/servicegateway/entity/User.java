package com.sgc.servicegateway.entity;

import java.util.Date;

/**
 * @ClassName: User
 * @Description: TODO
 * @author: shengling.guan
 * date: 2018年8月15日 上午11:26:19
 */
public class User {
	private Integer id;

	private String nickName;

	private String password;

	private String username;

	private String userTel;

	private Integer upperId;

	private Date lastLoginTime;

	private Integer operaterId;

	private String email;

	private Integer userStatus;

	private Date gmtCreate;

	private Date gmtModified;

	private boolean isDeleted;

	public void setIsDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public User() {}
	
	/**
	 * @param userName
	 */
	public User(String userName) {
		this.username = userName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserTel() {
		return userTel;
	}

	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

	public Integer getUpperId() {
		return upperId;
	}

	public void setUpperId(Integer upperId) {
		this.upperId = upperId;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Integer getOperaterId() {
		return operaterId;
	}

	public void setOperaterId(Integer operaterId) {
		this.operaterId = operaterId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
}