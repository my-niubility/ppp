<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.zlebank.domain.MsgCertCodeBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		List<MsgCertCodeBean> orgList = (List<MsgCertCodeBean>) application.getAttribute("msgCertBeans");
		List<MsgCertCodeBean> list = new ArrayList<MsgCertCodeBean>();
		if (orgList != null && orgList.size() > 0) {
			for (int i = orgList.size() - 1; i >= 0; i--) {
				list.add(orgList.get(i));
			}
		}
	%>
	<h3>查看短信验证码（测试专用）</h3>
	<table width="100%" border="1">
		<tr>
			<th>序号</th>
			<th>短信验证码</th>
			<th>电话号码</th>
			<th>验证码场景</th>
			<th>用户编号</th>
			<th>交易订单号</th>
			<th>发送时间</th>
			<th>失效时间</th>
		</tr>
		<c:forEach var="MsgCertBean" items="<%=list%>" varStatus="status">
			<tr class== "row" <c:if test="${status.count%2!=0}">bgcolor="#B0E0E6"</c:if>>
				<td width="5%" align="center"><c:out value="${status.index + 1}" /></td>
				<td width="10%" align="center"><c:out value="${MsgCertBean.msgCertCode}" /></td>
				<td width="10%" align="center"><c:out value="${MsgCertBean.phoneNum}" /></td>
				<td width="10%" align="center"><c:choose>
						<c:when test="${MsgCertBean.certBusCase eq '01'}">
							<c:out value="用户注册" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '02'}">
							<c:out value="实名认证" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '03'}">
							<c:out value="绑卡" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '04'}">
							<c:out value="充值" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '05'}">
							<c:out value="提现" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '06'}">
							<c:out value="重置支付密码" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '07'}">
							<c:out value="重置登陆密码" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '08'}">
							<c:out value="快捷支付" />
						</c:when>
						<c:when test="${MsgCertBean.certBusCase eq '09'}">
							<c:out value="余额支付" />
						</c:when>
					</c:choose></td>
				<td width="15%" align="center"><c:out value="${MsgCertBean.custId}" /></td>
				<td width="20%" align="center"><c:out value="${MsgCertBean.tradeOrderId}" /></td>
				<td width="15%" align="center"><c:out value="${MsgCertBean.sendTime}" /></td>
				<td width="15%" align="center"><c:out value="${MsgCertBean.deadTime}" /></td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>