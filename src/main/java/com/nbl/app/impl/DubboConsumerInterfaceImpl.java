package com.nbl.app.impl;

import org.springframework.stereotype.Service;

import com.nbl.service.DubboConsumerInterface;

@Service("portalInterfaceImpl")
public class DubboConsumerInterfaceImpl implements DubboConsumerInterface {

	@Override
	public String getName(String name) {
		return "portal:"+name;
	}

}
