package com.nbl.controller.purchase;

import javax.annotation.Resource;
import javax.print.attribute.standard.MediaName;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ComConst;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.dto.CommRespDto;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.PrdPchInfo;
import com.nbl.service.business.app.PrdPchApp;
import com.nbl.service.business.constant.BusCase;
import com.nbl.service.business.constant.InvestType;
import com.nbl.service.business.constant.ParamKeys;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.dto.req.PrdPchInfoDto;
import com.nbl.service.business.dto.res.PrdCheckResDto;
import com.nbl.service.business.dto.res.PrdPchResultDto;
import com.nbl.service.manager.app.CustPersonApp;
import com.nbl.service.manager.app.GeneralParameterApp;
import com.nbl.service.manager.dto.CustPersonReqDto;
import com.nbl.util.AmtParseUtil;
import com.nbl.utils.jjwt.JWTutils;
import com.nbl.utils.json.ResponseJson;

/**
 * 商品购买
 * 
 * @author AlanMa
 * 
 */
@Controller
@RequestMapping("/restful/product/purchase")
public class ProductPurchase {
	private final static Logger logger = LoggerFactory.getLogger(ProductPurchase.class);
	@Resource
	private PrdPchApp prdPchApp;
	@Resource
	private GeneralParameterApp generalParameterApp;
	@Resource
	private CustPersonApp custPersonApp;
	@Autowired
	private HttpSession session;

	/**
	 * 商品购买
	 * 
	 * @param prdPchInfo
	 * @return
	 */
	@RequestMapping(value = "/buynow", method = RequestMethod.POST)
	public @ResponseBody ResponseJson productPurchase(@Valid @RequestBody PrdPchInfo prdPchInfo) {
		ResponseJson resp = null;
		PrdCheckResDto prdInfo = null;
		PrdPchInfoDto prdPchInfoDto = null;
		try {
			// 投资类型校验
			buyNowCheck(prdPchInfo);
			// 投资人校验
			CustPersonReqDto custPersonDto = investorCheck(prdPchInfo.getCustId());

			prdPchInfoDto = new PrdPchInfoDto();
			BeanUtils.copyProperties(prdPchInfo, prdPchInfoDto);
			// 设置投资类型
			prdPchInfoDto.setInvenstType(prdPchInfo.getInvenstType());
			// 设置利息
			if (StringUtils.isNotEmpty(prdPchInfo.getInterest()))
				prdPchInfoDto.setInterest(AmtParseUtil.strToLongAmt(prdPchInfo.getInterest()));
			// 设置本金
			if (StringUtils.isNotEmpty(prdPchInfo.getPrincipal()))
				prdPchInfoDto.setPrincipal(AmtParseUtil.strToLongAmt(prdPchInfo.getPrincipal()));
			// 购买单位数
			if (StringUtils.isNotEmpty(prdPchInfo.getPurchasePortion()))
				prdPchInfoDto.setPurchasePortion(Long.parseLong(prdPchInfo.getPurchasePortion()));
			// 还款期次
			if (StringUtils.isNotEmpty(prdPchInfo.getRepayTerm()))
				prdPchInfoDto.setRepayTerm(Long.parseLong(prdPchInfo.getRepayTerm()));
			// 红包抵扣金额
			if (StringUtils.isNotEmpty(prdPchInfo.getRedEnvAmt()))
				prdPchInfoDto.setRedEnvAmt(AmtParseUtil.strToLongAmt(prdPchInfo.getRedEnvAmt()));
			// 用户编号
			if (StringUtils.isNotEmpty(prdPchInfo.getCustId()))
				prdPchInfoDto.setPurchaseCustId(prdPchInfo.getCustId());

			// 产品信息校验
			prdInfo = prdPchApp.buyNowChkPrdInfo(prdPchInfoDto);
			// 设置产品名称
			prdPchInfoDto.setProductNane(prdInfo.getProductName());
			// 设置合同id
			prdPchInfoDto.setContractId(prdInfo.getContractId());

			// 设置投资方客户号
			prdPchInfoDto.setPurchaseCustName(custPersonDto.getName());
			// 设置资产管理人客户号
			prdPchInfoDto.setAssetCustId(generalParameterApp.getValueByCode(ParamKeys.CUST_ID_ZGPT.getValue()));
			// 设置资产管理人客户名
			prdPchInfoDto.setAssetCustName(generalParameterApp.getValueByCode(ParamKeys.CUST_NAME_ZGPT.getValue()));

		} catch (MyBusinessCheckException e) {
			logger.error("[buyNowChkPrdInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		// 获取产品单价
		Long tradeTalAmt = prdInfo.getUnitCost() * Long.parseLong(prdPchInfo.getPurchasePortion());
		// 获取支付金额(总金额-红包抵扣金额)
		Long amt = tradeTalAmt - prdPchInfoDto.getRedEnvAmt();
		prdPchInfoDto.setAmt(amt);
		prdPchInfoDto.setTradeTalAmt(tradeTalAmt);
		// 设置操作码(购买商品)
		prdPchInfoDto.setOptCode(ComConst.PURCH_PRD.OPT_CODE);

		prdPchInfoDto.setStepKey(ComConst.PURCH_PRD.TRADE_ORDER);

		// PrdPchResultDto
		CommRespDto result = prdPchApp.buyNow(prdPchInfoDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			PrdPchResultDto prdPchRes = (PrdPchResultDto) result.getData();
			prdPchRes.setToken(JWTutils.generateRepeateRequestToken(prdPchInfo.getCustId(), 0));
			session.setAttribute(SessionKeys.TOKEN_CASE.getValue(), BusCase.PAYMENT.getValue());
			resp = new ResponseJson().success(prdPchRes);
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	private CustPersonReqDto investorCheck(String custId) throws MyBusinessCheckException {
		CustPersonReqDto custPersonDto = custPersonApp.getCustPerDetail(custId);
		if (custPersonDto == null) {
			throw new MyBusinessCheckException(ErrorCode.POB001, "用户编号错误！！！");
		}
		if(custPersonDto.getPayPassword()==null){
			throw new MyBusinessCheckException(ErrorCode.POC019);
		}
		return custPersonDto;
	}

	private void buyNowCheck(PrdPchInfo prdPchInfo) throws MyBusinessCheckException {
		if (InvestType.parseOf(prdPchInfo.getInvenstType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "investType");
		//判断传入的块数是否为整数，如果遍历发现小数点则表明为非整数
		if(prdPchInfo.getPurchasePortion().indexOf(".") >= 0){
			throw new MyBusinessCheckException(ErrorCode.POC008, "purchasePortion");
		}
		
	}

}
