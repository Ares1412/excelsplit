package com.xqfunds.servlet;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传文件后前端通过此接口获取部分excel表格的数据来展示
 * 通过session获取文件存储的名称
 */
public class GetExcel extends HttpServlet {
//    上传文件存储目录
//    private static final String UPLOAD_DIRECTORY = "D:"+ File.separator+"testfile"+File.separator+"upload";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject result = new JSONObject();
        //在PrintWriter前设置字符集才有效,避免中文乱码
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        //通过session获取上传的文件名
        HttpSession session = request.getSession();
        String dir = (String) session.getAttribute("filename");
        System.out.println("session保存的文件名信息："+dir);

        if(dir != null){
            try(InputStream inputStream = new FileInputStream(dir)) {
                // 解析每行结果在listener中处理
                ExcelListener listener = new ExcelListener();
                EasyExcelFactory.readBySax(inputStream,new Sheet(1,0),listener);
                List<List<String>> results = listener.getDatas();

                //取出表头
                List<String> head = results.get(0);

                //将表格数据转化成JSON
                List<JSONObject> datas = new ArrayList<>();
                for(int i=0;i<results.size();i++){
                    //返回给前端最多11行数据
                    if(i==11){
                        break;
                    }
                    //初始化JSON为有序对象，保证给前端的字段顺序一致
                    JSONObject row = new JSONObject(true);
                    for(int j=0;j<head.size();j++){
                        List<String> temp = results.get(i);
                        row.put(head.get(j),temp.get(j));
                    }
                    datas.add(row);
                }
                //将数据转化为JSON数组并且保留字段顺序
                JSONArray array = (JSONArray) JSONArray.parse(JSON.toJSONString(datas), Feature.OrderedField);
                result.put("data",array);
                result.put("success",true);
            } catch (IOException e) {
                e.printStackTrace();
                result.put("success",false);
            }
        }else{
            result.put("success",false);
        }
        out.println(result);
        System.out.println("=========================");
    }
}
