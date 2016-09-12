package com.nbl.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nbl.domain.RepayTerm;
import com.nbl.service.business.constant.RepayMode;
import com.nbl.service.business.dto.res.RateCountMsgDto;
import com.nbl.service.business.dto.res.RateCountUtilResponseDto;
import com.nbl.util.DateTimeUtils;

public class RateCountUtil {
	
	/**
	 * @descripses 根据投资金额、投资期限（天）、年化利率三要素算出预期收益
	 * @param investAmt
	 *            投资金额
	 * @param investPeriodDay
	 *            投资期限（天数）
	 * @param investPeriodMonth
	 *            投资期限（月）
	 * @param yearRate
	 *            年化利率
	 * @param yearDay
	 *            年换天标准（360 或365 不输入则默认365天）
	 * @return
	 */
	public static RateCountUtilResponseDto getExpectEarning(Long investAmt, Long investPeriodDay, Long investPeriodMonth, BigDecimal yearRate, String type, String yearDay) {

		RateCountUtilResponseDto reDto = new RateCountUtilResponseDto();
		// 等额本息
		if (RepayMode.EQUALINTEREST.getValue().equals(type)) {
			reDto = getAverageCapital(investAmt, investPeriodMonth, yearRate);
			// 等额本金
		} else if (RepayMode.EQUALCAPITAL.getValue().equals(type)) {
			reDto = getAveragePrincipal(investAmt, investPeriodMonth, yearRate);
			// 一次还本付息
		} else if (RepayMode.ONCE.getValue().equals(type)) {
			reDto = getPeriodRepayCapital(investAmt, investPeriodDay, yearRate, yearDay);
			// 每月付息到期还本
		} else if (RepayMode.MONTHINTERESTCAPITAL.getValue().equals(type)) {
			reDto = getMonthCapPerPrincipal(investAmt, investPeriodMonth, yearRate, yearDay);
			// 每月付息到期还本,按实际天数计算总息
		} else if (RepayMode.MODEPERIODREPAYREALDAY.getValue().equals(type)) {
			reDto = getRealDayCapPerPrincipal(investAmt, investPeriodDay, investPeriodMonth, yearRate, yearDay);
		}

		return reDto;
	}

	/**
	 * @descripses 收益计算工具：等额本息--->预期收益
	 * @param investAmt
	 *            投资金额
	 * @param investPeriod
	 *            投资期限（月）
	 * @param yearRate
	 *            年化利率
	 * @return 预期收益
	 */
	private static RateCountUtilResponseDto getAverageCapital(Long investAmt, Long investPeriod, BigDecimal yearRate) {

		// 融资期限（月）
		Long financeTerm = investPeriod;
		// 年化利率
		BigDecimal yearRate_ = yearRate.divide(new BigDecimal(100), 15, RoundingMode.HALF_UP);
		// 月利率
		BigDecimal monthRate = yearRate_.divide(new BigDecimal(12), 15, RoundingMode.HALF_UP);

		/**
		 * 等额本息（本+息）计算公式：P 投资额 R 月利率 N 贷款期数（月数） N R x (R+1) 本+息 = P x
		 * ———————————— N (1+R) - 1
		 */
		// 已融资到的资金规模
		BigDecimal scale = new BigDecimal(investAmt);
		BigDecimal radd1n_benxi = monthRate.add(new BigDecimal(1)).pow(financeTerm.intValue());
		BigDecimal up_benxi = monthRate.multiply(radd1n_benxi);
		BigDecimal down_benxi = radd1n_benxi.subtract(new BigDecimal(1));
		BigDecimal rate_benxi = scale.multiply(up_benxi.divide(down_benxi, 15, RoundingMode.HALF_UP));

		/**
		 * 等额本息（每月本金）计算公式：P 投资额 R 月利率 n 第n个月 N 贷款期数（月数） n-1 R x (R+1) 本金 = P x
		 * —————————————— N (1+R) - 1
		 */
		// 总共的期数
		int n = financeTerm.intValue();
		// 总共利息
		BigDecimal rate_total_lixi = new BigDecimal(0);
		// 总共本金
		BigDecimal rate_total_benjin = new BigDecimal(0);

		RateCountUtilResponseDto reDto = new RateCountUtilResponseDto();
		List<RateCountMsgDto> list = new ArrayList<RateCountMsgDto>();
		for (int j = 1; j <= n; j++) {
			if (j < n) {
				BigDecimal radd1n_benjin = monthRate.add(new BigDecimal(1)).pow(j - 1);
				BigDecimal up_benjin = monthRate.multiply(radd1n_benjin);
				BigDecimal down_benjin = radd1n_benxi.subtract(new BigDecimal(1));
				BigDecimal rate_benjin = scale.multiply(up_benjin.divide(down_benjin, 15, RoundingMode.HALF_UP));
				rate_total_lixi = rate_total_lixi.add((rate_benxi.subtract(rate_benjin)));
				rate_total_benjin = rate_total_benjin.add(rate_benjin);
				RateCountMsgDto msg = new RateCountMsgDto();
				long benjin = rate_benjin.setScale(0, RoundingMode.HALF_UP).longValue();
				long lixi = rate_benxi.subtract(rate_benjin).setScale(0, RoundingMode.HALF_UP).longValue();
				msg.setBenjin(benjin);
				msg.setLixi(lixi);
				msg.setBenxi(benjin + lixi);
				msg.setPeriod(j);
				list.add(msg);
			} else {
				// 最后一期的本金 = 投资金额 - 前n-1期的本金总和
				BigDecimal last_benjin = scale.subtract(rate_total_benjin);
				// 总利息
				rate_total_lixi = rate_total_lixi.add((rate_benxi.subtract(last_benjin)));
				RateCountMsgDto msg = new RateCountMsgDto();
				long benjin = last_benjin.setScale(0, RoundingMode.HALF_UP).longValue();
				long lixi = rate_benxi.subtract(last_benjin).setScale(0, RoundingMode.HALF_UP).longValue();
				msg.setBenjin(benjin);
				msg.setLixi(lixi);
				msg.setBenxi(benjin + lixi);
				msg.setPeriod(j);
				list.add(msg);
			}
		}

		reDto.setTotal_lixi(rate_total_lixi.setScale(0, RoundingMode.HALF_UP).longValue());
		reDto.setList(list);
		return reDto;
	}

	/**
	 * @descripses 收益计算工具：等额本金--->预期收益
	 * @param investAmt
	 *            投资金额
	 * @param investPeriod
	 *            投资期限（月）
	 * @param yearRate
	 *            年化利率
	 * @return 预期收益
	 */
	private static RateCountUtilResponseDto getAveragePrincipal(Long investAmt, Long investPeriod, BigDecimal yearRate) {

		// 融资期限（月）
		Long financeTerm = investPeriod;
		// 年化利率
		BigDecimal yearRate_ = yearRate.divide(new BigDecimal(100), 15, RoundingMode.HALF_UP);
		// 月利率
		BigDecimal monthRate = yearRate_.divide(new BigDecimal(12), 15, RoundingMode.HALF_UP);

		/**
		 * 每月还本付息金额 = P/N + (P-L)*R 其中，P 投资总额 N 投资总期数 R 月利率 L 累计还款总额 每月本金 = P/N
		 * 每月利息 = (P-L)*R
		 */
		// 总共的期数
		int n = financeTerm.intValue();
		// 已融资到的资金规模
		BigDecimal scale = new BigDecimal(investAmt);

		// 每月本金
		BigDecimal month_benjin = scale.divide(new BigDecimal(financeTerm), 15, RoundingMode.HALF_UP);
		long month_benjin_long = month_benjin.setScale(0, RoundingMode.HALF_UP).longValue();
		long add_month_benjin = 0L;
		long last_month_benjin = 0L;
		for (int k = 1; k <= n; k++) {
			add_month_benjin = add_month_benjin + month_benjin_long;
		}
		// 比较每月平均本金与投资总金额，处理精度问题，消除差异
		if (investAmt > add_month_benjin) {
			last_month_benjin = month_benjin_long + (investAmt - add_month_benjin);
		} else {
			last_month_benjin = month_benjin_long;
		}
		// n-1个月还款本金总额
		BigDecimal month_repay_benjin = new BigDecimal(0);
		BigDecimal total_lixi = new BigDecimal(0);

		BigDecimal total_benjin = new BigDecimal(0);

		RateCountUtilResponseDto reDto = new RateCountUtilResponseDto();
		List<RateCountMsgDto> list = new ArrayList<RateCountMsgDto>();
		for (int j = 1; j <= n; j++) {
			if (j > 1) {
				month_repay_benjin = month_repay_benjin.add(month_benjin);
			}
			// 每月利息
			BigDecimal month_lixi = new BigDecimal(0);
			month_lixi = (scale.subtract(month_repay_benjin)).multiply(monthRate);
			long month_lixi_long = month_lixi.setScale(0, RoundingMode.HALF_UP).longValue();
			total_lixi = total_lixi.add(month_lixi);
			total_benjin = total_benjin.add(month_benjin);
			if (j < n) {
				RateCountMsgDto msg = new RateCountMsgDto();
				msg.setBenjin(month_benjin_long);
				msg.setLixi(month_lixi_long);
				msg.setBenxi(month_benjin_long + month_lixi_long);
				msg.setPeriod(j);
				list.add(msg);
			} else {
				RateCountMsgDto msg = new RateCountMsgDto();
				msg.setBenjin(last_month_benjin);
				msg.setLixi(month_lixi_long);
				msg.setBenxi(last_month_benjin + month_lixi_long);
				msg.setPeriod(j);
				list.add(msg);
			}
		}
		reDto.setTotal_lixi(total_lixi.setScale(0, RoundingMode.HALF_UP).longValue());
		reDto.setList(list);
		return reDto;
	}

	/**
	 * @descripses 收益计算工具：到期一次还本付息--->预期收益
	 * @param investAmt
	 *            投资金额
	 * @param investPeriod
	 *            投资期限（天数）
	 * @param yearRate
	 *            年化利率
	 * @param yearDay
	 *            年换天标准（360 或365）
	 * @return 预期收益
	 */
	private static RateCountUtilResponseDto getPeriodRepayCapital(Long investAmt, Long investPeriod, BigDecimal yearRate, String yearDay) {
		// 年化收益率
		BigDecimal expectEarnRate = yearRate.divide(new BigDecimal(100), 15, RoundingMode.HALF_UP);
		// 融资期限（天数）
		BigDecimal termDate = new BigDecimal(investPeriod);
		// 已融资到的资金规模
		BigDecimal scale = new BigDecimal(investAmt);

		/**
		 * 融资规模应还的利息(公式：投资规模*年化收益率*融资期限（月）*30/360 或者 365)
		 */
		String ydF = "365";
		if (yearDay != null && !"".equals(yearDay)) {
			ydF = yearDay;
		}
		// 投资时间转换
		BigDecimal tranferDateScale = termDate.divide(new BigDecimal(ydF), 15, RoundingMode.HALF_UP);
		// 投资总额对应的利息
		BigDecimal totalRate = scale.multiply(tranferDateScale).multiply(expectEarnRate);
		// 总利息精度处理，金额精度保留小数点后两位
		BigDecimal scaleTotalRate = totalRate.divide(new BigDecimal(1), 15, RoundingMode.HALF_UP);

		long total_lixi = scaleTotalRate.setScale(0, RoundingMode.HALF_UP).longValue();

		RateCountUtilResponseDto reDto = new RateCountUtilResponseDto();
		List<RateCountMsgDto> list = new ArrayList<RateCountMsgDto>();
		RateCountMsgDto msg = new RateCountMsgDto();
		msg.setBenjin(investAmt);
		msg.setLixi(total_lixi);
		msg.setBenxi(investAmt + total_lixi);
		msg.setPeriod(1L);
		reDto.setTotal_lixi(total_lixi);
		reDto.setList(list);
		return reDto;
	}

	/**
	 * @descripses 收益计算工具：每月付息到期还本--->预期收益
	 * @param investAmt
	 *            投资金额
	 * @param investPeriod
	 *            投资期限（月）
	 * @param yearRate
	 *            年化利率
	 * @param yearDay
	 *            年换天标准（360 或365）
	 * @return 预期收益
	 */
	private static RateCountUtilResponseDto getMonthCapPerPrincipal(Long investAmt, Long investPeriod, BigDecimal yearRate, String yearDay) {
		// 年化收益率
		BigDecimal expectEarnRate = yearRate.divide(new BigDecimal(100), 15, RoundingMode.HALF_UP);
		// 融资期限（月）
		BigDecimal termDate = new BigDecimal(investPeriod);
		// 融资期限（天）
		BigDecimal date = termDate.multiply(new BigDecimal(30));

		// 已融资到的资金规模
		BigDecimal scale = new BigDecimal(investAmt);

		/**
		 * 融资规模应还的利息(公式：投资规模*年化收益率*融资期限（月）*30/360或者365)
		 */
		String ydF = "365";
		if (yearDay != null && !"".equals(yearDay)) {
			ydF = yearDay;
		}

		// 投资时间转换
		BigDecimal tranferDateScale = date.divide(new BigDecimal(ydF), 15, RoundingMode.HALF_UP);
		// 投资总额对应的利息
		BigDecimal totalRate = scale.multiply(tranferDateScale).multiply(expectEarnRate);
		long totalRate_long = totalRate.setScale(0, RoundingMode.HALF_UP).longValue();
		// 每个月的利息
		BigDecimal perMonthRate = totalRate.divide(termDate, 15, RoundingMode.HALF_UP);
		long perMonthRate_long = perMonthRate.setScale(0, RoundingMode.HALF_UP).longValue();

		RateCountUtilResponseDto reDto = new RateCountUtilResponseDto();
		List<RateCountMsgDto> list = new ArrayList<RateCountMsgDto>();
		long lixi_total = 0l;
		// 如果是一个月，则本息一块还
		if (investPeriod == 1) {
			RateCountMsgDto msg = new RateCountMsgDto();
			msg.setBenjin(investAmt);
			msg.setLixi(perMonthRate_long);
			msg.setBenxi(investAmt + perMonthRate_long);
			msg.setPeriod(1L);
			list.add(msg);
		} else if (investPeriod >= 1) {
			for (int i = 1; i <= investPeriod; i++) {

				if (i < investPeriod) {
					lixi_total = lixi_total + perMonthRate_long;
					RateCountMsgDto msg = new RateCountMsgDto();
					msg.setBenjin(0);
					msg.setLixi(perMonthRate_long);
					msg.setBenxi(perMonthRate_long);
					msg.setPeriod(i);
					list.add(msg);
				} else {
					RateCountMsgDto msg = new RateCountMsgDto();
					msg.setBenjin(investAmt);
					msg.setLixi(totalRate_long - lixi_total);
					msg.setBenxi(investAmt + (totalRate_long - lixi_total));
					msg.setPeriod(i);
					list.add(msg);
				}
			}
		}

		reDto.setTotal_lixi(totalRate_long);
		reDto.setList(list);
		return reDto;
	}

	/**
	 * @Description: 收益计算工具：每月付息到期还本(按实际天数计算总息)--->预期收益
	 * @param @param
	 *            investAmt 投资总额
	 * @param @param
	 *            investPeriodDay 投资期限(天)
	 * @param @param
	 *            investPeriodMonth 投资期限(月)
	 * @param @param
	 *            yearRate 年化率
	 * @param @param
	 *            yearDay
	 * @param @return
	 * @return RateCountUtilResponseDto
	 */
	private static RateCountUtilResponseDto getRealDayCapPerPrincipal(Long investAmt, Long investPeriodDay, Long investPeriodMonth, BigDecimal yearRate, String yearDay) {
		// 年化收益率
		BigDecimal expectEarnRate = yearRate.divide(new BigDecimal(100), 15, RoundingMode.HALF_UP);
		// 融资期限（月）
		// BigDecimal termDate = new BigDecimal(investPeriodMonth);
		// 融资期限（天）
		BigDecimal date = new BigDecimal(investPeriodDay);

		// 已融资到的资金规模
		BigDecimal scale = new BigDecimal(investAmt);

		// 起息日期
		Date establishDate = new Date();
		// 计算出结束日期（根据起息日期和投资天数）
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(establishDate);
		calendar.add(Calendar.DATE, investPeriodDay.intValue());
		Date endDate = calendar.getTime();
		calendar.setTime(establishDate);
		Date flagDate = establishDate;

		// 存储每一期的天数LIst
		List<RepayTerm> repayTermList = new ArrayList<RepayTerm>();
		// 计算从成立日期到结束日期的月数已经每月的天数，每月的结束日期
		int monthCount = 0;
		while (endDate.compareTo(flagDate) > 0) {
			++monthCount;
			calendar.setTime(establishDate);
			calendar.add(Calendar.MONTH, monthCount);
			// 记录上次计息结束日期
			Date preDate = flagDate;
			flagDate = calendar.getTime();
			if (flagDate.compareTo(endDate) > 0) {
				flagDate = endDate;
			}

			RepayTerm repayTerm = new RepayTerm();
			repayTerm.setTerm(monthCount);
			repayTerm.setPeriod(DateTimeUtils.getDistanceTime(preDate, flagDate));
			repayTerm.setRepayEndDate(new DateTimeUtils(flagDate).toDate8String());
			repayTermList.add(repayTerm);
		}

		// 融资期限（月）
		Long financeTerm = new BigDecimal(monthCount).longValue();
		int n = financeTerm.intValue();

		/**
		 * 融资规模应还的利息(公式：投资规模*年化收益率*融资期限（天）/360或者365)
		 */
		String ydF = "365";
		if (yearDay != null && !"".equals(yearDay)) {
			ydF = yearDay;
		}

		// 投资时间转换
		BigDecimal tranferDateScale = date.divide(new BigDecimal(ydF), 15, RoundingMode.HALF_UP);
		// 投资总额对应的利息
		BigDecimal totalRate = scale.multiply(tranferDateScale).multiply(expectEarnRate);
		long totalRate_long = totalRate.setScale(0, RoundingMode.HALF_UP).longValue();

		RateCountUtilResponseDto reDto = new RateCountUtilResponseDto();
		List<RateCountMsgDto> list = new ArrayList<RateCountMsgDto>();
		// 平均每一天的利息
		BigDecimal perDayRate = totalRate.divide(new BigDecimal(investPeriodDay), 15, RoundingMode.HALF_UP);

		long add_month_rate_long = 0L;
		long last_month_rate_long = 0L;
		for (int j = 1; j <= n; j++) {
			// 计算 本期利息=每天的利息*天数
			RepayTerm repayTerm = repayTermList.get(j - 1);
			BigDecimal periodDay = new BigDecimal(repayTerm.getPeriod());
			BigDecimal earning = periodDay.multiply(perDayRate);
			long earning_long = earning.setScale(0, RoundingMode.HALF_UP).longValue();
			// 记录还款结果
			RateCountMsgDto rDto = new RateCountMsgDto();
			if (j < n) {
				add_month_rate_long = add_month_rate_long + earning_long;
				// 投资者收入记录

				rDto.setBenjin(0l);
				rDto.setBenxi(earning_long);
				rDto.setLixi(earning_long);
				rDto.setPeriod(j);
				// // 本金
				// String benjin = "0";
				// // 本息
				// String benxi = earning_long + "";
				// // 利息
				// String lixi = earning_long + "";
				// // 期次
				// String qici = j + "";
				//
				// System.out.println("期次:" + qici);
				// System.out.println("本金:" + benjin);
				// System.out.println("本息:" + benxi);
				// System.out.println("利息:" + lixi);

			} else if (j == n) {
				last_month_rate_long = (totalRate_long - add_month_rate_long);
				// 投资者收入记录

				rDto.setBenjin(investAmt);
				rDto.setBenxi(last_month_rate_long + investAmt);
				rDto.setLixi(last_month_rate_long);
				rDto.setPeriod(j);
				// // 本金
				// String benjin = investAmt + "";
				// // 本息
				// long benxiL = last_month_rate_long + investAmt;
				// String benxi = benxiL + "";
				// // 利息
				// String lixi = last_month_rate_long + "";
				// // 期次
				// String qici = j + "";
				//
				// System.out.println("期次:" + qici);
				// System.out.println("本金:" + benjin);
				// System.out.println("本息:" + benxi);
				// System.out.println("利息:" + lixi);
			}
			list.add(rDto);
		}

		reDto.setTotal_lixi(totalRate_long);
		reDto.setList(list);
		return reDto;
	}
}
