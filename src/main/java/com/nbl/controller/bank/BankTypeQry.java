package com.nbl.controller.bank;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.vo.PageVO;
import com.nbl.domain.BankTypeInfo;
import com.nbl.service.manager.app.BankTypeApp;
import com.nbl.service.manager.dto.BankTypeDto;
import com.nbl.utils.BeanUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 查询银行信息
 * @author chenhongji
 *
 */
@Controller
@RequestMapping("/restful/bank")
public class BankTypeQry {
	private final static Logger logger = LoggerFactory.getLogger(BankTypeQry.class);
	
	@Resource
	BankTypeApp bankTypeApp;
	
	/**
	 * 根据banktypeInfo(bankType,bankName)查询银行信息.param为null时返回所有银行信息
	 * @param bankTypeInfo
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public @ResponseBody ResponseJson qryBankType(@RequestBody(required=false) BankTypeInfo bankTypeInfo ){
		logger.info("[enter bankType inparam is :]" + bankTypeInfo.toString());
		ResponseJson resp = null;
		BankTypeDto bankTypeDto = new BankTypeDto();
		if((bankTypeInfo.getBankType()!=null&&bankTypeInfo.getBankType()!="")||
				(bankTypeInfo.getBankName()!=null&&bankTypeInfo.getBankName()!="")){
			BeanUtils.copyProperties(bankTypeInfo, bankTypeDto);
		}
		PageVO<BankTypeDto> pageVO = new PageVO<>();
		pageVO.setSize(-1);
		List<BankTypeDto> pageListQueryBankType = bankTypeApp.pageListQueryBankType(pageVO, bankTypeDto);
		resp = new ResponseJson().success(pageListQueryBankType);
		return resp;
		
	}
	
		
}

