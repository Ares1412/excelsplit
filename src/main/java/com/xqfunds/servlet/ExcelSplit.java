package com.xqfunds.servlet;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 拆分excel表格
 * 前端传入拆分字段索引集 ：index
 * 和是否拆分成多个excel ：group
 */
public class ExcelSplit extends HttpServlet {

    //临时文件夹存放目录
    private static final String TEMP_DIRECTORY = "D:"+File.separator+"TempExcelFile"+File.separator+"temp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //取得前端传来的数据
        String index = request.getParameter("index");
        boolean IsGroup = Boolean.valueOf(request.getParameter("group"));
        System.out.println("index = "+index);
        System.out.println("IsGroup = "+IsGroup);
        System.out.println("ContentPath: "+request.getScheme()+"://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath()+request.getServletPath());

        //通过session获取上传的文件名
        HttpSession session = request.getSession();
        String dir = (String) session.getAttribute("filename");
        System.out.println("session保存的文件名信息：" + dir);
        if(dir == null||index.equals("")){
            try {
                //在PrintWriter前设置字符集才有效,避免中文乱码
                request.setCharacterEncoding("utf-8");
                response.setContentType("text/html;charset=utf-8");
                response.setCharacterEncoding("utf-8");
                request.getRequestDispatcher("/index.jsp").forward(request,response);
                System.out.println("=========================");
                return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //读excel数据
        List<List<String>> datas = getExcel(dir);

        //取出表头并转换为sheet表头
        List<String> head = datas.get(0);
        List<List<String>> sheet_head = getSheetHead(head);
        datas.remove(0);

        //解析传入的index集合
        List<String> indexList = new ArrayList<>(Arrays.asList(index.split(",")));
        List<Integer> fieldsIndex = getFieldsIndex(indexList);
        System.out.println("字段索引：" + fieldsIndex.toString());

        //根据index排序
        datas.sort(new ExtralComparetor(fieldsIndex.toArray(new Integer[1])));

        //写文件到输出流中
        //输出到web的文件名称
        String fileName = new String(("FineReport " +
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .getBytes());
        try (ServletOutputStream out = response.getOutputStream()){
            if(IsGroup){
                //拆分为多个excel文件并打包成zip文件
                response.setContentType("application/zip");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment; filename="+fileName+".zip");
                //调用函数写
                writeExcelGroup(out,datas,sheet_head,fieldsIndex);
            }else {
                //拆分为多个sheet在一个excel文件中
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
                //调用函数写
                writeExcel(out,datas,sheet_head,fieldsIndex);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("=========================");
    }

    private List<Integer> getFieldsIndex(List<String> indexes){
        List<Integer> result = new ArrayList<>();
        for (String index : indexes){
            result.add(Integer.valueOf(index));
        }
        return result;
    }

    private List<List<String>> getExcel(String dir){
        try(InputStream inputStream = new FileInputStream(dir)){
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener();
            EasyExcelFactory.readBySax(inputStream,new Sheet(1,0),listener);
            return listener.getDatas();
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<List<String>> getSheetHead(List<String> head){
        List<List<String>> sheethead = new ArrayList<>();
        for(int i=0;i<head.size();i++){
            List<String> field = new ArrayList<>();
            field.add(head.get(i));
            sheethead.add(field);
        }
        return sheethead;
    }

    /**
     * 将excel拆分为多个sheet输出
     * @param out 输出流
     * @param datas sheet数据
     * @param head sheet表头
     * @param field_index 要拆分的字段索引集
     * @throws IOException
     */
    private void writeExcel(OutputStream out,
                           List<List<String>> datas,
                           List<List<String>> head,
                           List<Integer> field_index) throws IOException{
        ExcelWriter writer = EasyExcelFactory.getWriter(out);

        //记录需要的sheet名
        Set<String> fields = new LinkedHashSet<>();
        //sheet计数器
        int sheet_count = 1;
        //记录sheet内容
        List<List<String>> sheet_date = new ArrayList<>();
        //记录上一个sheet的名字
        String last_sheet_name = null;


        for (List<String> data : datas){
            //构造sheetname
            String temp_sheet_name = "";
            for(int i=0;i<field_index.size();i++){
                temp_sheet_name += data.get(field_index.get(i));
                if (i != field_index.size()-1){
                    temp_sheet_name += "-";
                }
            }
            if(last_sheet_name == null){
                last_sheet_name = temp_sheet_name;
            }

            if(fields.add(temp_sheet_name)){
                if(!sheet_date.isEmpty()){
                    Sheet sheet = new Sheet(sheet_count,0);
                    sheet.setSheetName(last_sheet_name);
                    //设置表头
                    sheet.setHead(head);
                    sheet.setAutoWidth(Boolean.TRUE);
                    writer.write0(sheet_date,sheet);
                    //写入一个sheet后清空数据
                    sheet_date.clear();
                    sheet_count++;
                    //数据为空时，记录当次数据
                    sheet_date.add(data);
                }else {
                    //第一个sheet，记录当次数据
                    sheet_date.add(data);
                }
            }else{
                sheet_date.add(data);
            }
            last_sheet_name = temp_sheet_name;
        }
        //最后一个sheet单独写
        Sheet sheet = new Sheet(sheet_count,0);
        sheet.setSheetName(last_sheet_name);
        //设置表头
        sheet.setHead(head);
        sheet.setAutoWidth(Boolean.TRUE);
        writer.write0(sheet_date,sheet);
        //写完一个excel关闭writer
        writer.finish();
        System.out.println("excel文件输出完成");
    }

    /**
     * 将excel拆分为多个excel并压缩输出
     * @param out 输出流
     * @param datas 总的sheet数据
     * @param head sheet表头
     * @param field_index field_index 要拆分的字段索引集
     * @throws IOException
     */
    private void writeExcelGroup(OutputStream out,
                                 List<List<String>> datas,
                                 List<List<String>> head,
                                 List<Integer> field_index) throws IOException{
        //临时文件夹，存放拆分的excel
        String temp_dir = TEMP_DIRECTORY + File.separator+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File tempPath = new File(temp_dir);
        if(!tempPath.exists()){
            tempPath.mkdirs();
        }

        //记录需要的sheet名
        Set<String> fields = new LinkedHashSet<>();
        //sheet计数器
        int sheet_count = 1;
        //记录sheet内容
        List<List<String>> sheet_date = new ArrayList<>();
        //记录上一个sheet的名字
        String last_sheet_name = null;


        for (List<String> data : datas){
            //构造sheetname
            String temp_sheet_name = "";
            for(int i=0;i<field_index.size();i++){
                temp_sheet_name += data.get(field_index.get(i));
                if (i != field_index.size()-1){
                    temp_sheet_name += "-";
                }
            }
            if(last_sheet_name == null){
                last_sheet_name = temp_sheet_name;
            }

            if(fields.add(temp_sheet_name)){
                if(!sheet_date.isEmpty()){
                    Sheet sheet = new Sheet(1,0);
                    sheet.setSheetName(last_sheet_name);
                    //设置表头
                    sheet.setHead(head);
                    sheet.setAutoWidth(Boolean.TRUE);

                    //以sheet名作为excel文件名
                    String outfile_name = temp_dir + File.separator + last_sheet_name + ".xlsx";
                    OutputStream outstream = new FileOutputStream(getOutFile(outfile_name));
                    ExcelWriter writer = EasyExcelFactory.getWriter(outstream);
                    writer.write0(sheet_date,sheet);
                    writer.finish();

                    //写入一个sheet后清空数据
                    sheet_date.clear();
                    sheet_count++;
                    //数据为空时，记录当次数据
                    sheet_date.add(data);
                }else {
                    //第一个sheet，记录当次数据
                    sheet_date.add(data);
                }
            }else{
                sheet_date.add(data);
            }
            last_sheet_name = temp_sheet_name;
        }
        //最后一个sheet单独写
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName(last_sheet_name);
        //设置表头
        sheet.setHead(head);
        sheet.setAutoWidth(Boolean.TRUE);
        //以sheet名作为excel文件名
        String outfile_name = temp_dir + File.separator + last_sheet_name + ".xlsx";
        OutputStream outstream = new FileOutputStream(getOutFile(outfile_name));
        ExcelWriter writer = EasyExcelFactory.getWriter(outstream);
        writer.write0(sheet_date,sheet);
        writer.finish();

        //完成拆分进行压缩
        ZipUtils.toZip(temp_dir,out,true);
        //删除临时文件夹
        File[] listFiles = tempPath.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            listFiles[i].delete();
        }
        tempPath.delete();
        System.out.println("zip文件输出完成");
    }

    private File getOutFile(String filename){
        //将sheetname作为excel文件名
        File outfile = new File(filename);
        if(!outfile.exists()){
            File parentfile = outfile.getParentFile();
            if(!parentfile.exists()){
                parentfile.mkdirs();
            }
            try {
                outfile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return outfile;
    }
}

