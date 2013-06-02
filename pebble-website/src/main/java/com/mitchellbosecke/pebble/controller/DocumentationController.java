package com.mitchellbosecke.pebble.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/documentation")
public class DocumentationController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String getView() {
		return "documentation/index";
	}
}
