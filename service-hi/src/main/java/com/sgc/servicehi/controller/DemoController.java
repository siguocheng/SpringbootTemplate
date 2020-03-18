package com.sgc.servicehi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgc.common.entity.ResultBean;
import com.sgc.servicehi.dto.PostDto;
import com.sgc.servicehi.entity.GetEntity;
import com.sgc.servicehi.service.DemoService;
import com.sgc.servicehi.vo.PostVo;

@RestController
@RequestMapping(value = "/demo")
public class DemoController {


	@Autowired
	DemoService demoService;
	
	@PostMapping("/hi")
	public ResultBean<PostVo> home(@RequestBody PostDto postDto) {
		
		return new ResultBean<PostVo>(demoService.getData(postDto));
	}

	@GetMapping("/getData")
	public ResultBean<GetEntity> getEntity(GetEntity getEntity) {
		
		return new ResultBean<GetEntity>(getEntity);
	}
	
	@RequestMapping("/ha")
	public String hame(@RequestBody String name) {
		return "ha " + name + " ,i am from port:";
	}

	// @RequestMapping("/demo/hi")
	// public String qsw(@RequestParam(value = "name", defaultValue = "forezp")
	// String name) {
	// return "hi " + name + " ,i am from port:" + port;
	// }
}
