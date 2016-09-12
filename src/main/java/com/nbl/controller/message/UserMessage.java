package com.nbl.controller.message;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ErrorCode;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.MessageDelInfo;
import com.nbl.domain.MessageQryDetailInfo;
import com.nbl.domain.MessageQryInfo;
import com.nbl.domain.MessageQryUnredInfo;
import com.nbl.service.business.app.UserMessageApp;
import com.nbl.service.business.constant.UserMessageType;
import com.nbl.service.business.dto.req.MessageReqDto;
import com.nbl.service.business.dto.res.MessageResDto;
import com.nbl.service.business.dto.res.MessageResItemDto;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;



/**
*@author:chenhongji
*@createdate：2016年8月24日 
*@version: 1.0 
*/
@Controller
@RequestMapping("/restful/message")
public class UserMessage {
	
	private final static Logger logger = LoggerFactory.getLogger(UserMessage.class);
	
	@Resource
	UserMessageApp userMessageApp;
	/**
	 * 分页查询用户消息
	 * @param messageQryInfo
	 * @return
	 * @throws MyBusinessCheckException
	 */
	@RequestMapping(value="/queryAll",method = RequestMethod.POST)
	public @ResponseBody ResponseJson queryMessage(@Valid @RequestBody MessageQryInfo messageQryInfo) throws MyBusinessCheckException{
		logger.info("[enter queryAll inParams is:]"+messageQryInfo.toString());
		ResponseJson resp = null;
		MessageReqDto reqDto = new MessageReqDto();
		/*if(messageQryInfo.getMessageType()!=null && UserMessageType.parseOf(messageQryInfo.getMessageType())==null){
			throw new MyBusinessCheckException(ErrorCode.POC008, "messageType");
		}*/
		BeanParseUtils.copyProperties(messageQryInfo, reqDto);
		List<String> userIds = new ArrayList<>();
		userIds.add(messageQryInfo.getCustId());
		reqDto.setUserIds(userIds);
		reqDto.setMessageType(messageQryInfo.getMessageType());
		List<MessageResDto> messagePage = userMessageApp.getMessagePage(reqDto);
		if(messagePage==null||messagePage.size()==0){
			resp=new ResponseJson().failure("用户消息为空");
		}else{
			resp=new ResponseJson().success(new MessageResItemDto(messagePage));
		}
		return resp;
	}
	
	/**
	 * 分页查询时显示消息总条数
	 * @param messageQryInfo
	 * @return
	 * @throws MyBusinessCheckException
	 */
	@RequestMapping(value="/count",method = RequestMethod.POST)
	public @ResponseBody ResponseJson queryMessageCount(@Valid @RequestBody MessageQryInfo messageQryInfo) throws MyBusinessCheckException{
		logger.info("[enter queryMessageCount inParams is:]"+messageQryInfo.toString());
		ResponseJson resp = null;
		MessageReqDto reqDto = new MessageReqDto();
		/*if(messageQryInfo.getMessageType()!=null && UserMessageType.parseOf(messageQryInfo.getMessageType())==null){
			throw new MyBusinessCheckException(ErrorCode.POC008, "messageType");
		}*/
		//BeanParseUtils.copyProperties(messageQryInfo, reqDto);
		List<String> userIds = new ArrayList<>();
		userIds.add(messageQryInfo.getCustId());
		reqDto.setUserIds(userIds);
		reqDto.setMessageType(messageQryInfo.getMessageType());
		String totalCount = userMessageApp.getMessagePageCount(reqDto);
		
		resp=new ResponseJson().success(totalCount);
		
		return resp;
	}
	/**
	 * 显示用户未读消息条数
	 * @param messageQryInfo
	 * @return
	 * @throws MyBusinessCheckException
	 */
	@RequestMapping(value="/unreadCount",method = RequestMethod.POST)
	public @ResponseBody ResponseJson queryUnreadCount(@Valid @RequestBody MessageQryUnredInfo messageQryUnredInfo) throws MyBusinessCheckException{
		logger.info("[enter queryUnreadCount inParams is:]"+messageQryUnredInfo.toString());
		ResponseJson resp = null;
		MessageReqDto reqDto = new MessageReqDto();
		//BeanParseUtils.copyProperties(messageQryUnredInfo, reqDto);
		List<String> userIds = new ArrayList<>();
		userIds.add(messageQryUnredInfo.getCustId());
		reqDto.setUserIds(userIds);
		reqDto.setMessageType(messageQryUnredInfo.getMessageType());
		String totalCount = userMessageApp.getUnreadMessageCount(reqDto);
		
		resp=new ResponseJson().success(totalCount);
		
		return resp;
	}
	/**
	 * 删除用户信息
	 * @param messageDelInfo
	 * @return
	 * @throws MyBusinessCheckException
	 */
	@RequestMapping(value="/delete",method = RequestMethod.POST)
	public @ResponseBody ResponseJson deleteMessage(@Valid @RequestBody MessageDelInfo messageDelInfo) throws MyBusinessCheckException{
		logger.info("[enter queryAll inParams is:]"+messageDelInfo.toString());
		ResponseJson resp = null;
		MessageReqDto reqDto =new MessageReqDto();
		if(messageDelInfo.getMessageIds()==null ||messageDelInfo.getMessageIds().size()==0){
			resp=new ResponseJson().failure("待删除的messageId不能为空");
		}else{
			BeanParseUtils.copyProperties(messageDelInfo, reqDto);
			List<String> userIds = new ArrayList<>();
			userIds.add(messageDelInfo.getCustId());
			reqDto.setUserIds(userIds);
			String count = userMessageApp.deleteMessage(reqDto);
			resp=new ResponseJson().success(count);
		}
		
		return resp;
	}
	
	/**
	 * 查询消息详情
	 * @param messageQryDetailInfo
	 * @return
	 * @throws MyBusinessCheckException
	 */
	@RequestMapping(value="/detail",method = RequestMethod.POST)
	public @ResponseBody ResponseJson getMessageDetail(@Valid @RequestBody MessageQryDetailInfo messageQryDetailInfo) throws MyBusinessCheckException{
		logger.info("[enter getMessageDetail inParams is:]"+messageQryDetailInfo.toString());
		ResponseJson resp = null;
		MessageReqDto reqDto =new MessageReqDto();
		//BeanParseUtils.copyProperties(messageDelInfo, reqDto);
		reqDto.setId(messageQryDetailInfo.getMessageId());
		List<String> userIds = new ArrayList<>();
		userIds.add(messageQryDetailInfo.getCustId());
		reqDto.setUserIds(userIds);
		MessageResDto message = userMessageApp.getMessage(reqDto);
		if(message==null){
			resp=new ResponseJson().failure("指定消息不存在");
		}else {
			resp=new ResponseJson().success(message);
		}
		
		return resp;
	}
	/**
	 * 批量将信息标记为已读
	 * @param messageDelInfo
	 * @return
	 * @throws MyBusinessCheckException
	 */
	@RequestMapping(value="/read",method = RequestMethod.POST)
	public @ResponseBody ResponseJson setMessageReadStatus(@Valid @RequestBody MessageDelInfo messageDelInfo) throws MyBusinessCheckException{
		logger.info("[enter setMessageReadStatus inParams is:]"+messageDelInfo.toString());
		ResponseJson resp = null;
		MessageReqDto reqDto =new MessageReqDto();
		if(messageDelInfo.getMessageIds()==null ||messageDelInfo.getMessageIds().size()==0){
			resp=new ResponseJson().failure("标记已读的messageId不能为空");
		}else{
			BeanParseUtils.copyProperties(messageDelInfo, reqDto);
			List<String> userIds = new ArrayList<>();
			userIds.add(messageDelInfo.getCustId());
			reqDto.setUserIds(userIds);
			String count = userMessageApp.setMessageReadStatus(reqDto);
			resp=new ResponseJson().success(count);
		}
		
		return resp;
	}
}


