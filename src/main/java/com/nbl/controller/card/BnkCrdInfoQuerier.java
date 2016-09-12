package com.nbl.controller.card;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.domain.QryBnkCrdInfo;
import com.nbl.service.user.app.BnkCrdInfoQryApp;
import com.nbl.service.user.dto.req.QryBnkCrdDto;
import com.nbl.service.user.dto.res.QryBnkCrdResultDto;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 银行卡信息查询器
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/querycard")
public class BnkCrdInfoQuerier {
	private final static Logger logger = LoggerFactory.getLogger(BnkCrdInfoQuerier.class);
	@Resource
	private BnkCrdInfoQryApp bnkCrdInfoQryApp;

	/**
	 * 查询默认银行卡信息
	 * 
	 * @param qryBnkCrdInfo
	 * @return
	 */
	@RequestMapping(value = "/default", method = RequestMethod.POST)
	public @ResponseBody ResponseJson queryDefaultCard(@RequestBody QryBnkCrdInfo qryBnkCrdInfo) {
		ResponseJson resp = null;

		QryBnkCrdDto qryBnkCrdDto = new QryBnkCrdDto();
		BeanParseUtils.copyProperties(qryBnkCrdInfo, qryBnkCrdDto);

		QryBnkCrdResultDto result = bnkCrdInfoQryApp.queryDefaultCard(qryBnkCrdDto);

		if (result != null) {
			resp = new ResponseJson().success(result);
		} else {
			resp = new ResponseJson().success();
		}
		return resp;
	}

}
