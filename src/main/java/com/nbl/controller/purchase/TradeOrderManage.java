package com.nbl.controller.purchase;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ComConst;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.dto.CommRespDto;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.CanlTrdOrdInfo;
import com.nbl.domain.HisTrdOrdInfo;
import com.nbl.domain.InvestHistoryInfo;
import com.nbl.domain.SerFundQryInfo;
import com.nbl.domain.TrdOrdDtlQryInfo;
import com.nbl.domain.TrdOrdQueryInfo;
import com.nbl.service.business.app.TrdOrdMgeApp;
import com.nbl.service.business.app.TrdOrdQryApp;
import com.nbl.service.business.app.TrdOrdQryDtlApp;
import com.nbl.service.business.constant.OrdComOrderByCol;
import com.nbl.service.business.constant.OrderByFlag;
import com.nbl.service.business.constant.OrderStatus;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.dto.req.CanlTrdOrdDto;
import com.nbl.service.business.dto.req.HisTrdOrdDto;
import com.nbl.service.business.dto.req.InvHisDto;
import com.nbl.service.user.dto.req.SerFundQryDto;
import com.nbl.service.user.dto.req.TrdOrdDtlQryDto;
import com.nbl.service.user.dto.req.TrdOrdQryInfoDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.util.ErrCodeUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 交易信息查询
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/tradeorder")
public class TradeOrderManage {

	private final static Logger logger = LoggerFactory.getLogger(TradeOrderManage.class);

	@Resource
	private TrdOrdQryApp trdOrdQryApp;
	@Resource
	private TrdOrdQryDtlApp trdOrdQryDtlApp;
	@Resource
	private TrdOrdMgeApp trdOrdMgeApp;
	@Autowired
	private HttpSession session;

	/**
	 * 交易记录查询
	 * 
	 * @param trdOrdQueryInfo
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public @ResponseBody ResponseJson tradeOrderQuery(@Valid @RequestBody TrdOrdQueryInfo trdOrdQueryInfo) {
		try {
			tradeOrderQueryCheck(trdOrdQueryInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		ResponseJson resp = null;
		TrdOrdQryInfoDto trdOrdQryInfoDto = new TrdOrdQryInfoDto();
		BeanParseUtils.copyProperties(trdOrdQueryInfo, trdOrdQryInfoDto);
		trdOrdQryInfoDto.setUserId(userInfo.getLoginName());
		CommRespDto result = trdOrdQryApp.queryTradeOrder(trdOrdQryInfoDto);
		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	private void tradeOrderQueryCheck(TrdOrdQueryInfo trdOrdQueryInfo) throws MyBusinessCheckException {
		if (!StringUtils.isEmpty(trdOrdQueryInfo.getStatus()) && OrderStatus.parseOf(trdOrdQueryInfo.getStatus()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "status");
		if (!StringUtils.isEmpty(trdOrdQueryInfo.getOrderFlag()) && OrderByFlag.parseOf(trdOrdQueryInfo.getOrderFlag()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderFlag");
		if (!StringUtils.isEmpty(trdOrdQueryInfo.getOrderColumn()) && OrdComOrderByCol.parseOf(trdOrdQueryInfo.getOrderColumn()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderColumn");
	}

	/**
	 * 交易记录条数查询
	 * 
	 * @param trdOrdQueryInfo
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public @ResponseBody ResponseJson tradeOrderCountQuery(@Valid @RequestBody TrdOrdQueryInfo trdOrdQueryInfo) {
		ResponseJson resp = null;
		TrdOrdQryInfoDto trdOrdQryInfoDto = new TrdOrdQryInfoDto();
		BeanParseUtils.copyProperties(trdOrdQueryInfo, trdOrdQryInfoDto);
		String count = trdOrdQryApp.queryTradeOrderCount(trdOrdQryInfoDto);
		logger.debug("[return msg]:" + count);
		resp = new ResponseJson().success(count);
		return resp;
	}

	/**
	 * 交易记录详情查询
	 * 
	 * @param trdOrdQueryInfo
	 * @return
	 */
	@RequestMapping(value = "/querydetail", method = RequestMethod.POST)
	public @ResponseBody ResponseJson trdOrdDtlQuery(@Valid@RequestBody TrdOrdDtlQryInfo trdOrdDtlQryInfo) {
		ResponseJson resp = null;
		TrdOrdDtlQryDto trdOrdDtlQryDto = new TrdOrdDtlQryDto();
		BeanParseUtils.copyProperties(trdOrdDtlQryInfo, trdOrdDtlQryDto);

		CommRespDto result = trdOrdQryDtlApp.queryTrdOrdDtl(trdOrdDtlQryDto);

		if (result == null) {
			return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC007));
		}

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	/**
	 * 资金流水记录查询
	 * 
	 * @param serFundQryInfo
	 * @return
	 */
	@RequestMapping(value = "/queryfund", method = RequestMethod.POST)
	public @ResponseBody ResponseJson serialFundQuery(@Valid@RequestBody SerFundQryInfo serFundQryInfo) {
		ResponseJson resp = null;
		SerFundQryDto serFundQryDto = new SerFundQryDto();
		BeanParseUtils.copyProperties(serFundQryInfo, serFundQryDto);
		CommRespDto result = trdOrdQryApp.querySerialFund(serFundQryDto);
		if (result==null) {
			resp = new ResponseJson().success();
		}else if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}
		return resp;
	}

	/**
	 * 资金流水记录条数查询
	 * 
	 * @param serFundQryInfo
	 * @return
	 */
	@RequestMapping(value = "/queryfundCount", method = RequestMethod.POST)
	public @ResponseBody ResponseJson serialFundCountQuery(@Valid@RequestBody SerFundQryInfo serFundQryInfo) {
		ResponseJson resp = null;
		SerFundQryDto serFundQryDto = new SerFundQryDto();
		BeanParseUtils.copyProperties(serFundQryInfo, serFundQryDto);
		String count = trdOrdQryApp.querySerialFundCount(serFundQryDto);
		logger.debug("[return msg]:" + count);
		resp = new ResponseJson().success(count);
		return resp;
	}

	/**
	 * 历史成交订单信息查询
	 * 
	 * @param hisTrdOrdInfo
	 * @return
	 */
	@RequestMapping(value = "/qrytrdord", method = RequestMethod.POST)
	public @ResponseBody ResponseJson historyTrdOrdInfoQry(@RequestBody HisTrdOrdInfo hisTrdOrdInfo) {
		logger.info("[enter historyTrdOrdInfoQry inparam is :]" + hisTrdOrdInfo.toString());
		ResponseJson resp = null;
		HisTrdOrdDto hisTrdOrdDto = new HisTrdOrdDto();
		BeanParseUtils.copyProperties(hisTrdOrdInfo, hisTrdOrdDto);

		CommRespDto result = trdOrdQryApp.queryHisTrdOrd(hisTrdOrdDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	/**
	 * 订单产品交易记录查询(根据更新时间倒序20条)
	 * 
	 * @param hisTrdOrdInfo
	 * @return
	 */
	@RequestMapping(value = "/prdtrdhis", method = RequestMethod.POST)
	public @ResponseBody ResponseJson prdTradeHistoryQry() {
		ResponseJson resp = null;

		CommRespDto result = trdOrdQryApp.queryHisPrdTrd();

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	/**
	 * 客户最近投资记录（10条）
	 * 
	 * @param hisTrdOrdInfo
	 * @return
	 */
	@RequestMapping(value = "/custinvhis", method = RequestMethod.POST)
	public @ResponseBody ResponseJson custInvestHistoryQuery(@RequestBody InvestHistoryInfo investHistoryInfo) {
		ResponseJson resp = null;
		InvHisDto invHisDto = new InvHisDto();
		BeanParseUtils.copyProperties(investHistoryInfo, invHisDto);

		CommRespDto result = trdOrdQryApp.qryCustInvestHistory(invHisDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}
		return resp;
	}

	/**
	 * 取消订单
	 * 
	 * @param hisTrdOrdInfo
	 * @return
	 */
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public @ResponseBody ResponseJson cancelTradeOrder(@RequestBody CanlTrdOrdInfo cancelTradeOrderInfo) {
		ResponseJson resp = null;
		CanlTrdOrdDto canlTrdOrdDto = new CanlTrdOrdDto();
		BeanParseUtils.copyProperties(cancelTradeOrderInfo, canlTrdOrdDto);

		CommRespDto result = trdOrdMgeApp.cancelTradeOrder(canlTrdOrdDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}
		return resp;
	}
}
