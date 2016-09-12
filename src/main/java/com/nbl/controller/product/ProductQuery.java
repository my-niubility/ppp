package com.nbl.controller.product;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.nbl.domain.MutiCondQryPrdsInfo;
import com.nbl.domain.PrdDetailInfo;
import com.nbl.domain.PrdExhiInfo;
import com.nbl.service.business.app.ProductQueryApp;
import com.nbl.service.business.constant.ExhType;
import com.nbl.service.business.constant.OrderByFlag;
import com.nbl.service.business.constant.PrdIdxOrderByCol;
import com.nbl.service.business.constant.ProductLittleType;
import com.nbl.service.business.constant.ProductType;
import com.nbl.service.business.dto.req.MutiCndQryPrdsDto;
import com.nbl.service.business.dto.req.PrdDtlInfoQryDto;
import com.nbl.service.business.dto.req.PrdExhiInfoDto;
import com.nbl.util.AmtParseUtil;
import com.nbl.util.ErrCodeUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 产品信息查询
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/product/query")
public class ProductQuery {
	private final static Logger logger = LoggerFactory.getLogger(ProductQuery.class);
	@Resource
	private ProductQueryApp productQueryApp;

	/**
	 * 产品展示信息查询
	 * 
	 * @param prdExhiInfo
	 * @return
	 */
	@RequestMapping(value = "/exhibition", method = RequestMethod.POST)
	public @ResponseBody ResponseJson prdExhiQuery(@Valid @RequestBody PrdExhiInfo prdExhiInfo) {
		try {
			prdExhiQueryCheck(prdExhiInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		ResponseJson resp = null;
		PrdExhiInfoDto rchgQryInfoDto = new PrdExhiInfoDto();
		BeanParseUtils.copyProperties(prdExhiInfo, rchgQryInfoDto);
		CommRespDto result = productQueryApp.productExhibitionQuery(rchgQryInfoDto);
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

	private void prdExhiQueryCheck(PrdExhiInfo prdExhiInfo) throws MyBusinessCheckException {
		if (ExhType.parseOf(prdExhiInfo.getExhType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "exhType");
	}

	/**
	 * 组合条件产品列表查询
	 * 
	 * @param mutiCondQryPrdsInfo
	 * @return
	 */
	@RequestMapping(value = "/multicond", method = RequestMethod.POST)
	public @ResponseBody ResponseJson mutiCondPrdsQuery(@Valid @RequestBody MutiCondQryPrdsInfo mutiCondQryPrdsInfo) {
		try {
			mutiCondPrdsQueryCheck(mutiCondQryPrdsInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		ResponseJson resp = null;
		MutiCndQryPrdsDto mutiCndQryPrdsDto = copyProperties(mutiCondQryPrdsInfo);

		CommRespDto result = productQueryApp.productsMutiCondQuery(mutiCndQryPrdsDto);
		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		logger.debug("[return msg]:" + resp.toString());

		return resp;
	}

	private void mutiCondPrdsQueryCheck(MutiCondQryPrdsInfo mutiCondQryPrdsInfo) throws MyBusinessCheckException {
		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getProductType()) && ProductType.parseOf(mutiCondQryPrdsInfo.getProductType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "productType");
		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getProductLittleType()) && ProductLittleType.parseOf(mutiCondQryPrdsInfo.getProductLittleType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "productLittleType");
		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getOrderFlag()) && OrderByFlag.parseOf(mutiCondQryPrdsInfo.getOrderFlag()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderFlag");
		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getOrderColumn()) && PrdIdxOrderByCol.parseOf(mutiCondQryPrdsInfo.getOrderColumn()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderColumn");
	}

	/**
	 * 组合条件产品列表数量查询
	 * 
	 * @param mutiCondQryPrdsInfo
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public @ResponseBody ResponseJson mutiCondPrdsCountQuery(@Valid @RequestBody MutiCondQryPrdsInfo mutiCondQryPrdsInfo) {
		logger.info("[enter mutiCondPrdsQuery inparam is :]" + mutiCondQryPrdsInfo.toString());
		try {
			mutiCondPrdsQueryCheck(mutiCondQryPrdsInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		ResponseJson resp = null;
		MutiCndQryPrdsDto mutiCndQryPrdsDto = copyProperties(mutiCondQryPrdsInfo);

		String count = productQueryApp.productsMutiCondCountQuery(mutiCndQryPrdsDto);
		logger.debug("[return msg]:" + count);
		resp = new ResponseJson().success(count);
		return resp;
	}

	private MutiCndQryPrdsDto copyProperties(MutiCondQryPrdsInfo mutiCondQryPrdsInfo) {
		MutiCndQryPrdsDto mutiCndQryPrdsDto = new MutiCndQryPrdsDto();
		BeanParseUtils.copyProperties(mutiCondQryPrdsInfo, mutiCndQryPrdsDto);

		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getUnitCostMinY())) {
			mutiCndQryPrdsDto.setUnitCostMin(AmtParseUtil.strToLongAmt(mutiCondQryPrdsInfo.getUnitCostMinY()));
		}
		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getUnitCostMaxY())) {
			mutiCndQryPrdsDto.setUnitCostMax(AmtParseUtil.strToLongAmt(mutiCondQryPrdsInfo.getUnitCostMaxY()));
		}

		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getbLockPeriodMin())) {
			mutiCndQryPrdsDto.setLockPeriodMin(Long.parseLong(mutiCondQryPrdsInfo.getbLockPeriodMin()));
		}
		if (!StringUtils.isEmpty(mutiCondQryPrdsInfo.getbLockPeriodMax())) {
			mutiCndQryPrdsDto.setLockPeriodMax(Long.parseLong(mutiCondQryPrdsInfo.getbLockPeriodMax()));
		}
		return mutiCndQryPrdsDto;
	}

	/**
	 * 产品详细信息查询
	 * 
	 * @param prdExhiInfo
	 * @return
	 */
	@RequestMapping(value = "/prddetail", method = RequestMethod.POST)
	public @ResponseBody ResponseJson prdDetailQuery(@Valid @RequestBody PrdDetailInfo prdDetailInfo) {
		ResponseJson resp = null;
		PrdDtlInfoQryDto prdDtlInfoQryDto = new PrdDtlInfoQryDto();
		BeanParseUtils.copyProperties(prdDetailInfo, prdDtlInfoQryDto);
		CommRespDto result = productQueryApp.productDetailsQuery(prdDtlInfoQryDto);
		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}
		return resp;
	}

}
