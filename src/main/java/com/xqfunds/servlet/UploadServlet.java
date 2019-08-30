package com.xqfunds.servlet;


import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.util.UUID;

/**
 * 接收前端上传的文件，通过session记录文件存储的名称
 */
@MultipartConfig
public class UploadServlet extends HttpServlet {

    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "D:"+File.separator+"TempExcelFile"+File.separator+"upload";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject result = new JSONObject();
        PrintWriter out = response.getWriter();
        try {
            HttpSession session = request.getSession();
            request.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            //获取上传的文件
            Part part = request.getPart("txt_file");
            //获取请求的信息
            String name = part.getHeader("content-disposition");
            //测试使用
//            System.out.println(request.getContentType());
            System.out.println("上传请求的文件信息："+name);
            System.out.println("session保存的文件名信息："+session.getAttribute("filename"));

            //获取上传文件的目录,若没有则创建目录
            String root=UPLOAD_DIRECTORY;
            System.out.println("上传文件的路径："+root);
            File temp = new File(root);
            if(!temp.exists()){
                temp.mkdirs();
            }
            //获取文件的后缀
            String str=name.substring(name.lastIndexOf("."), name.length()-1);
            System.out.println("获取文件的后缀："+str);
            //生成一个新的文件名，不重复，数据库存储的就是这个文件名，不重复的
            String filename=root+File.separator+ UUID.randomUUID().toString().replaceAll("-","")+str;
            //将文件名用session存储
            session.setAttribute("filename",filename);
            System.out.println("产生新的文件名："+filename);
            //上传文件到指定目录
            part.write(filename);

            //返回数据
            request.setAttribute("info", "上传文件成功");
            result.put("success",true);
            //前端ajax，需要返回json数据
            out.println(result.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("info", "上传文件失败");
                result.put("success",false);
                out.println(result.toJSONString());
            }
        System.out.println("=========================");
    }

}
