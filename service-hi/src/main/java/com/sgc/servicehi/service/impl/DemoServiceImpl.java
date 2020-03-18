package com.sgc.servicehi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgc.common.exception.CheckException;
import com.sgc.servicehi.dao.UserMapper;
import com.sgc.servicehi.dto.PostDto;
import com.sgc.servicehi.entity.User;
import com.sgc.servicehi.service.DemoService;
import com.sgc.servicehi.vo.PostVo;

@Service
public class DemoServiceImpl implements DemoService {

	@Autowired
	UserMapper userMapper;
	
	@Override
	public PostVo getData(PostDto postDto) {

		User user = userMapper.selectByPrimaryKey(postDto.getId());
		
		if (user == null) {
			throw new CheckException("用户不存在");
		} else {
			PostVo postVo = new PostVo();
			postVo.setName(user.getUserName());
			
			return postVo;
		}		
	}

}
