package com.xqfunds.servlet;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExcelListener extends AnalysisEventListener {

    //自定义用于暂时存储data
    //可以通过实例获取该值
    private List<List<String>> datas = new ArrayList<>();
    int field_length = 0;

    @Override
    public void invoke(Object object, AnalysisContext context) {
        List<String> temp = (List<String>) object;
        //记录总字段个数
        if (context.getCurrentRowNum() == 0) {
            field_length = temp.size();
            System.out.println("field_length = " + field_length);
        }
        doSomething(temp);//根据自己业务做处理
        datas.add(temp);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
    }

    private void doSomething(List<String> object) {
        //1、入库调用接口
        //处理空字段
        for (int i = 0; i < object.size(); i++) {
            if (object.get(i) == null)
                object.set(i, "");
        }
        while (object.size() < field_length) {
            object.add("");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // datas.clear();//解析结束销毁不用的资源
    }

    public List<List<String>> getDatas() {
        return datas;
    }

    public void setDatas(List<List<String>> datas) {
        this.datas = datas;
    }
}
