package com.nbl.controller.test;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.utils.json.ResponseJson;

@Controller
@RequestMapping("/restful/prid")
public class RestfulTempalte {
	
	private final static Logger logger = LoggerFactory.getLogger(RestfulTempalte.class); 
	
	@RequestMapping(value="/getName",method=RequestMethod.GET)
	public @ResponseBody ResponseJson getName(){
		
		return new ResponseJson().success("I am ok");
	}

	@RequestMapping(value="/getName",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody ResponseJson getNameT(HttpServletRequest request, HttpServletResponse response){
		
		request.getAttribute("de");
		
		return new ResponseJson().success("I am ok");
	}

	@RequestMapping(value="/getSession",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody ResponseJson getSession(HttpServletRequest request, HttpServletResponse response){
		
		logger.info("session-1:get-session:"+request.getSession().getAttribute("session-1"));
		
		return new ResponseJson().success("I am ok");
	}


	@RequestMapping(value="/setSession",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody ResponseJson setSession(HttpServletRequest request, HttpServletResponse response){
		
		request.getSession().setAttribute("session-1", request.getAttribute("sess"));
		logger.info("session-set-value:"+request.getAttribute("sess"));
		
		return new ResponseJson().success("I am ok");
	}


	
	
	@RequestMapping(value = "/person/{name}", method = RequestMethod.GET )
	public @ResponseBody ResponseJson getPerson(@PathVariable String name){
		Person p = new Person();		
		p.setName(name);
		p.setAddress("太阳宫");
		p.setAge(18);
		
		return new ResponseJson().success(p);
	}
	
	@RequestMapping(value = "/person/list", method = RequestMethod.GET )
	public @ResponseBody ResponseJson getPersonList(){
		List<Person> list = new ArrayList<Person>();
		Person p = new Person();		
		p.setName("tang1");
		p.setAddress("太阳宫");
		p.setAge(18);
		
		Person p2 = new Person();		
		p2.setName("tang2");
		p2.setAddress("太阳宫2");
		p2.setAge(20);
		
		Person p3 = new Person();		
		p3.setName("tang3");
		p3.setAddress("太阳宫3");
		p3.setAge(23);
		list.add(p);
		list.add(p2);
		list.add(p3);
		return new ResponseJson().success(list);
	}


	@RequestMapping(value = "/failure-get", method = RequestMethod.POST )
	public @ResponseBody ResponseJson getFailure(){

		return new ResponseJson().success();
	}

	
	@RequestMapping(value = "/requestJson", method = RequestMethod.POST )
	public @ResponseBody ResponseJson getRequestJson(@RequestBody @Valid RequestBodyTest req){
		logger.info("name=========="+req.getName());
		logger.info("message=========="+req.getMessage());
		return new ResponseJson().success();
	}
	
	@RequestMapping(value = "/requestJson1", method = RequestMethod.POST )
	public @ResponseBody ResponseJson getRequestJson1(@RequestBody Person person){
		logger.info("name=========="+person.getName());
		logger.info("message=========="+person.getAddress());
		return new ResponseJson().success();
	}

}
