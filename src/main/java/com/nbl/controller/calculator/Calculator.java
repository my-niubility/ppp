package com.nbl.controller.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ErrorCode;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.CalculatorInfo;
import com.nbl.service.business.constant.RepayMode;
import com.nbl.service.business.constant.YearDay;
import com.nbl.service.business.dto.res.CalculatorResDto;
import com.nbl.service.business.dto.res.RateCountMsgDto;
import com.nbl.service.business.dto.res.RateCountMsgStrDto;
import com.nbl.service.business.dto.res.RateCountUtilResponseDto;
import com.nbl.util.AmtParseUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.RateCountUtil;
import com.nbl.utils.json.ResponseJson;
import com.nbl.controller.calculator.Calculator;

/**
 * 预期收益计算器
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/calculator")
public class Calculator {
	private final static Logger logger = LoggerFactory.getLogger(Calculator.class);

	/**
	 * 提交注册信息
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/expben", method = RequestMethod.POST)
	public @ResponseBody ResponseJson calExpBen(@Valid @RequestBody CalculatorInfo calculator) {

		RateCountUtilResponseDto result = null;
		CalculatorResDto calRes = new CalculatorResDto();
		try {
			calExpBenCheck(calculator);
			Long investAmt = AmtParseUtil.strToLongAmt(calculator.getInvestAmt());
			Long investPeriodDay = calculator.getInvestPeriodDay() == null ? null : Long.parseLong(calculator.getInvestPeriodDay());
			Long investPeriodMonth = calculator.getInvestPeriodMonth() == null ? null : Long.parseLong(calculator.getInvestPeriodMonth());
			BigDecimal yearRate = new BigDecimal(calculator.getYearRate());
			result = RateCountUtil.getExpectEarning(investAmt, investPeriodDay, investPeriodMonth, yearRate, calculator.getRepayMode(), calculator.getYearDay());
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		calRes.setInterest(AmtParseUtil.longToStrAmt(result.getTotal_lixi()));
		List<RateCountMsgDto> rateCountMsgFens = result.getList();
		List<RateCountMsgStrDto> rateCountMsgYuans = new ArrayList<RateCountMsgStrDto>();
		for (RateCountMsgDto rateCountMsgFen : rateCountMsgFens) {
			RateCountMsgStrDto rateCountMsgYuan = new RateCountMsgStrDto();
			BeanParseUtils.copyPropertiesAmtToYuan(rateCountMsgFen, rateCountMsgYuan, "period", "benjin", "lixi", "benxi");
			rateCountMsgYuans.add(rateCountMsgYuan);
		}
		calRes.setRateCountMsgs(rateCountMsgYuans);

		return new ResponseJson().success(calRes);
	}

	private static void calExpBenCheck(CalculatorInfo calculator) throws MyBusinessCheckException {
		if (RepayMode.parseOf(calculator.getRepayMode()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "repayMode");
		if (!StringUtils.isEmpty(calculator.getYearDay()) && YearDay.parseOf(calculator.getYearDay()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "yearDay");
		if (RepayMode.ONCE.getValue().equals(calculator.getRepayMode())) {
			if (StringUtils.isEmpty(calculator.getInvestPeriodDay()))
				throw new MyBusinessCheckException(ErrorCode.POC006, "investPeriodDay");
		} else {
			if (StringUtils.isEmpty(calculator.getInvestPeriodMonth()))
				throw new MyBusinessCheckException(ErrorCode.POC006, "investPeriodMonth");
		}
		//金额是否为保留两位的小数
		if(!(Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}$").matcher(calculator.getInvestAmt()).matches())){
			throw new MyBusinessCheckException(ErrorCode.POC008, "investAmt");
		}
	}
	
	public static void main(String[] args) {
		System.out.println("-------start---------");
		CalculatorInfo calculatorInfo = new CalculatorInfo();
		calculatorInfo.setInvestAmt("10000000");
		calculatorInfo.setInvestPeriodDay("1");
		calculatorInfo.setRepayMode("1");
		calculatorInfo.setYearRate("8.00");
		calculatorInfo.setYearDay("360");
		try {
			calExpBenCheck(calculatorInfo);
		} catch (MyBusinessCheckException e) {
			System.out.println(e.getErrMsgKey());
		}
		System.out.println("-------end---------");
	}

}
