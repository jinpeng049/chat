package com.sunny.main;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    List<String> OnLineUserList = new ArrayList<String>();
    static List<String> strSendConentList = new ArrayList<String>();

    static Date date = new Date();

    static int listLength = 0;

    static String lastSendUser = null;

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取行为和用户名密码聊天内容
        String strAction = request.getParameter("action");
        String strName = request.getParameter("name");
        String strPass = request.getParameter("pass");
        String strContent = request.getParameter("content");
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        // 根据行为处理
        if ("Login".equals(strAction)) {
            boolean res = UserLogin(strName, strPass, session);
            out.println(res);
            out.close();
        } else if ("ChatList".equals(strAction)) {
            String result1 = AllChatList();
            Date d_ = new Date();
            boolean haveMessage = false;
            String user = (String) session.getAttribute("LOGINUSER");
            if (user != null && lastSendUser != null && !user.equals(lastSendUser)) {
                if ((d_.after(DateUtil.addMinute(date, 1)) && listLength < strSendConentList.size()) || (d_.after(DateUtil.addMinute(date, 1)) && listLength - strSendConentList.size() > 3)) {
                    haveMessage = true;
                    date = d_;
                    listLength = strSendConentList.size();
                } else {
                    haveMessage = false;
                }
            }
            boolean cc = haveMessage;
            JSONObject obj = new JSONObject();
            obj.put("msg", cc);
            obj.put("result1", result1);
            out.println(obj);
            out.close();
            haveMessage = false;
        } else if ("OnLineList".equals(strAction)) {
            String result2 = GetOnlineUserList(session);
            out.println(result2);
            out.close();
        } else if ("SendContent".equals(strAction)) {
            Boolean res2 = AddSendContent(strContent, session);
            String result = "";
            if (res2) {
                result = "1";
            } else {
                result = "2";
            }
            out.println(result);
            out.close();
        } else if ("Logout".equals(strAction)) {
            boolean res3 = Logout(session);
            out.println(res3);
            out.close();
        } else if ("clear".equals(strAction)) {
            strSendConentList = new ArrayList<String>();
        }
    }

    // 登录处理
    public boolean UserLogin(String strName, String strPass, HttpSession session) {
        boolean blnR = false;
        if (strPass.equals("lzx"))// 验证密码
        {
            if (OnLineUserList.size() == 0)// 判断用户是否为空
            {
                OnLineUserList = new ArrayList<String>();// 为空则为用户arrayList重新初始化
            }
            OnLineUserList.add(strName);// 保存用户到用户arrayList方便在聊天用户内容列显示
            session.setAttribute("LOGINUSER", strName);// 保存登录信息到session
            blnR = true;
        }
        return blnR;
    }

    // 获取聊天内容
    public String AllChatList() {
        String result = "";
        if (strSendConentList.size() == 0) {
            result = "日前还没有找到聊天记录";
        } else {
            Iterator<String> it = strSendConentList.iterator();
            while (it.hasNext()) {
                result += it.next() + "</br>";
            }
        }
        result = result.replace("<:", "<img src='Face/");
        result = result.replace(":>", ".gif '/>");
        return result;
    }

    // 获取用户列表
    public String GetOnlineUserList(HttpSession session) {
        String result = "";
        if (OnLineUserList.size() == 0) {
            result = "暂时没有人在线";
        } else {
            Iterator<String> it = OnLineUserList.iterator();
            while (it.hasNext()) {
                result += it.next() + "</br>";
            }
        }
        return result;
    }

    // 发送信息
    public Boolean AddSendContent(String strContent, HttpSession session) {
        String user = (String) session.getAttribute("LOGINUSER");

        lastSendUser = user;

        // String name = session.getAttribute("LOGINUSER").toString();
        if (null == user) {
            return false;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String strSendConent = user + " 于 " + date + " 说: " + strContent;
        if (strSendConentList.size() == 0) {
            strSendConentList = new ArrayList<String>();
        }
        strSendConentList.add(strSendConent);
        return true;
    }

    // 登出
    public boolean Logout(HttpSession session) {
        if (null == session.getAttribute("LOGINUSER")) {
            return false;
        }
        String name = session.getAttribute("LOGINUSER").toString();
        session.removeAttribute("LOGINUSER");
        if (OnLineUserList.size() != 0) {
            for (int i = 0; i < OnLineUserList.size(); i++) {
                if (name.equals(OnLineUserList.get(i))) {
                    OnLineUserList.remove(i);
                }
                return true;
            }
        }
        return false;
    }
}
