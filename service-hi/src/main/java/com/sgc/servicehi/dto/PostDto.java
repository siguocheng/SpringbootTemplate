package com.sgc.servicehi.dto;

import com.sgc.common.entity.BaseDto;

public class PostDto extends BaseDto {

	private String name;

	private Integer id;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
