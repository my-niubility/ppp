
package com.nbl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbl.common.exception.MyBusinessRuntimeException;


/**
 * @author xuchu-tang
 * @version 1.0, 2015年12月12日
 * @description ftp工具类
 */
public class FtpUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(FtpUtils.class);
	
	/**
	 * @param ip FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param remotePath  FTP服务器保存目录
	 * @param fileName 上传到FTP服务器上的文件名
	 * @param input 输入流
	 * @return
	 * @description 上传文件
	 */
	public static boolean upload(String ip, int port, String username, String password, 
			String remotePath, String fileName, FileInputStream input) {
		FTPClient ftp = new FTPClient();

		try {
			InetAddress addr = InetAddress.getByName(ip);
			ftp.connect(addr, port);
			ftp.login(username, password);

			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				logger.error("ftp登录失败！");
				throw new MyBusinessRuntimeException("ftp登录失败！");
			}
			
			if (StringUtils.isNotEmpty(remotePath)) {
				boolean isOk = ftp.changeWorkingDirectory(remotePath);
				if(!isOk){
					
					//创建目录
					String[] filePaths=remotePath.split("\\"+File.separator);
					
					for(String pathName:filePaths){
						isOk= ftp.changeWorkingDirectory(pathName);
						if(!isOk){
							isOk=ftp.makeDirectory(pathName);
							if(!isOk){
								logger.error("创建目录失败！");
								throw new MyBusinessRuntimeException("创建目录失败！");
							}
							
							isOk = ftp.changeWorkingDirectory(pathName);
							if(!isOk){
								logger.error("更换目录失败！");
								throw new MyBusinessRuntimeException("更换目录失败！");
							}
						}
						
					}
				}
			}
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			boolean flag =ftp.storeFile(fileName, input);
			return flag;
		} catch(Exception e){
			throw new MyBusinessRuntimeException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(input);
			closeFtp(ftp);
		}
	}

	/**
	 * @param ip FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param remotePath FTP服务器上的相对路径
	 * @param fileName 要下载的文件名
	 * @return
	 * @description 从FTP服务器获取文件，此方法不保存文件
	 */
	public static byte[] getFile(String ip, int port, String username, String password, String remotePath, String fileName) {
		FTPClient ftp = new FTPClient();
		InputStream input = null;
		
		try {
			InetAddress addr = InetAddress.getByName(ip);
			ftp.connect(addr, port);
			ftp.login(username, password);
			
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				
				logger.error("ftp登录失败！");
				throw new MyBusinessRuntimeException("ftp登录失败！");
			}
			
			if (StringUtils.isNotEmpty(remotePath)) {
				ftp.changeWorkingDirectory(remotePath);
			}
			//返回文件流
			return IOUtils.toByteArray(ftp.retrieveFileStream(fileName));
		}
		catch (Exception e) {
			throw new MyBusinessRuntimeException(e.getMessage());
		}
		finally {
			IOUtils.closeQuietly(input);
			closeFtp(ftp);
		}
	}
	
	
	/**
	 * @param ip FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param remotePath FTP服务器上的相对路径
	 * @param fileName 要下载的文件名
	 * @param localPath 本地保存文件的路径
	 * @return true 成功、false 失败
	 * @description 从FTP服务器获取文件，并保存文件
	 */
	public static boolean downFile(String ip, int port,String username, String password, String remotePath,String fileName,String localPath) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		OutputStream is=null;
		try {
			int reply;
			ftp.connect(ip, port);
			//如果采用默认端口，可以使用ftp.connect(ip)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
			FTPFile[] fs = ftp.listFiles();
			for(FTPFile ff:fs){
				System.out.println(ff.getName());
				if(ff.getName().equals(fileName)){
					File localFile = new File(localPath+"/"+ff.getName());
					is = new FileOutputStream(localFile); 
					ftp.retrieveFile(ff.getName(), is);
					break;
				}
			}
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
			closeFtp(ftp);
		}
		return success;
	}
	
	/**
	 * @param ip FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param remotePath FTP服务器上的相对路径
	 * @param fileName 要下载的文件名
	 * @return true 存在  false 不存在
	 * @description 判断ftp上是否存在该文件
	 */
	public static boolean isFile(String ip, int port,String username, String password, String remotePath,String fileName) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(ip, port);
			//如果采用默认端口，可以使用ftp.connect(ip)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
			
			FTPFile[] fs = ftp.listFiles();
			for(FTPFile ff:fs){
				System.out.println(ff.getName());
				if(ff.getName().equals(fileName)){
					success = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeFtp(ftp);
		}
		return success;
	}
	
	/**
	 * 关闭.
	 * @param input
	 * @param ftp
	 */
	private static void closeFtp(FTPClient ftp) {
		try {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	
	public static void main(String[] args) throws FileNotFoundException {
		String path = "/usr/ftp/client/test.txt";
		String localPath = "/usr/ftp/client";
		String ftppath="/usr/ftp/service";
		String fileName = "test.txt";
		FileInputStream jksInputStream;

		jksInputStream = new FileInputStream(new File(path));
		FtpUtils.upload("127.0.0.1", 21, "ftp", "ftp", ftppath, fileName, jksInputStream);
		FtpUtils.downFile("127.0.0.1", 21, "ftp", "ftp", ftppath, fileName, localPath);

	}
}
