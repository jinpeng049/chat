package com.sunny.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;


public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 获取get请求
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		//将接受内容以utf-8接收
		request.setCharacterEncoding("UTF-8");
		Date date = new Date();// 日期
		SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMddHHmmss");
		//SimpleDateFormat sdfFolder = new SimpleDateFormat("yyMM");
		String newfileName = sdfFileName.format(date);//格式化日期
		String fileRealPath = "";//真实文件路径

		String fileRealResistPath = "";//保存文件路径
		if(null==session.getAttribute("LOGINUSER")){
			return;
		}
		// 获取用户
		//String name = session.getAttribute("LOGINUSER").toString();
		String firstFileName = "";
		//保存路径
		String savePath = this.getServletConfig().getServletContext()
				.getRealPath("/")
				+ "uploads\\" + newfileName + "\\";
		File file = new File(savePath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}

		try {
			DiskFileItemFactory fac = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(fac);
			upload.setHeaderEncoding("UTF-8");

			//获取文件列表
			List<FileItem> fileList = upload.parseRequest(request);
			//遍历文件arrayList
			Iterator it = fileList.iterator();
			while (it.hasNext()) {
				Object obit = it.next();
				if (obit instanceof DiskFileItem) {
					DiskFileItem item = (DiskFileItem) obit;

					//获取文件名
					String fileName = item.getName();
					if (fileName != null) {
						firstFileName = item.getName().substring(
								item.getName().lastIndexOf("\\") + 1);
						String formatName = firstFileName
								.substring(firstFileName.lastIndexOf("."));//获取文件名
						fileRealPath = savePath + newfileName + formatName;//保存的真实文件路径

						BufferedInputStream in = new BufferedInputStream(
								item.getInputStream());//读取文件流
						BufferedOutputStream outStream = new BufferedOutputStream(
								new FileOutputStream(new File(fileRealPath)));//
						Streams.copy(in, outStream, true);// 文件流复制
						//写入文件
						if (new File(fileRealPath).exists()) {
							//
							fileRealResistPath = fileRealPath
									.substring(fileRealPath.lastIndexOf("\\") + 1);
							//获取当前用户
							String user = session.getAttribute("LOGINUSER")
									.toString();
							Connection cnn = null;
							PreparedStatement pst = null;
							try {
								//记录用户上传文件的信息到数据库
								Class.forName("com.mysql.jdbc.Driver");
								cnn = DriverManager
										.getConnection("jdbc:mysql://localhost:3306/file?user=root&password=123");
								String sql="insert into file(user,path,realname) values('"+user+"','"+newfileName+"','"+fileName+"')";
								
								pst = cnn.prepareStatement(sql);
								pst.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} finally {
								try {
									if (pst != null) {
										pst.close();
										pst = null;
									}
									if (cnn != null) {
										cnn.close();
										cnn = null;
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							//截取文件的后缀
							String FileExt = fileName
									.substring(fileName.lastIndexOf("."));
							//用户发送文件的信息
							String str=user+"发送了文件："+"<a href='uploads/"+newfileName+"/"+newfileName+FileExt+"'>"+fileName+"</a>";
							HelloServlet.strSendConentList.add(str);
						}

					}
				}
			}
		} catch (FileUploadException ex) {
			ex.printStackTrace();
			response.getWriter().write("Upload error！");
			return;
		}
		response.getWriter().write("Upload success！");

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
