$(function () {
    //0.初始化fileinput
    var oFileInput = new FileInput();
    oFileInput.Init("txt_file", contentpath+"/upload");
});

//初始化fileinput
var FileInput = function () {
    var oFile = new Object();

    //初始化fileinput控件（第一次初始化）
    oFile.Init = function(ctrlName, uploadUrl) {
        var control = $('#' + ctrlName);

        //初始化上传控件的样式
        control.fileinput({
            language: 'zh', //设置语言
            uploadUrl: uploadUrl, //上传的地址
            allowedFileExtensions: ['xlsx'],//接收的文件后缀
            showUpload: true, //是否显示上传按钮
            showCaption: false,//是否显示标题
            showClose: false,
            browseClass: "btn btn-primary", //按钮样式
            maxFileCount: 1, //表示允许同时上传的最大文件个数
            enctype: 'multipart/form-data',
            validateInitialCount:true,
            dropZoneTitle: '拖拽文件到这里',
            layoutTemplates:{
                actionUpload:'',//去除上传预览缩略图中的上传图片
                actionZoom:'',   //去除上传预览缩略图中的查看详情预览的缩略图标
                actionDownload:'', //去除上传预览缩略图中的下载图标
                actionDelete:'' //去除上传预览的缩略图中的删除图标
            },//对象用于渲染布局的每个部分的模板配置。您可以设置以下模板来控制窗口小部件布局.eg:去除上传图标
            previewFileIcon: "<i class='fa fa-file'></i>",
            msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！"
        });

        //导入文件上传完成之后的事件
       control.on("fileuploaded", function (event, data, previewId, index) {

           $("#myModal").modal("hide");
           $('#txt_file').fileinput('clear');//清空所有文件
           console.log(uploadUrl);
           console.log(data);

           $.ajax({
               url: contentpath+'/getexcel',
               type: 'get',
               dataType: 'json',
               contentType: 'application/json;charset=utf-8',
               success: function (data) {
                   console.log(data);
                   if(data.success){
                       RefreshExcel(data);
                   }
               }
           });
        });
    };
    return oFile;
};

function RefreshExcel(data) {
    var excel_data = data.data;
    console.log('line_count = '+excel_data.length);
    //更新表的头部和选择框
    var head = excel_data[0];
    console.log(head);
    var checkbox = "";
    var head_text = "<thead><tr>";
    var index = 0;
    for(var key in head){
        head_text = head_text + "<th>"+head[key]+"</th>";
        checkbox = checkbox + "<option value='"+index+"'>"+head[key]+"</option>";
        index++;
    }
    head_text = head_text + "</tr></thead>";
    // console.log(head_text);
    //更新表格数据
    var data_text = "<tbody>";
    for(var i=1;i<excel_data.length;i++){
        var data = excel_data[i];
        data_text = data_text + "<tr>";
        for(var k in data){
            data_text = data_text + "<td>"+data[k]+"</td>";
        }
        data_text = data_text + "</tr>";
    }
    data_text = data_text + "</tbody>";
    $('#excel_table').empty()
        .append(head_text).append(data_text);

    //更新选择框
    // checkbox = "<select multiple class='selectpicker w-100' id='sel1'>"+checkbox+"</select>";
    console.log(checkbox);
    $('#sel1').html("")
        .append(checkbox)
        .selectpicker('refresh');
}

function excelsplit(flag) {
    var values = $('#sel1').val();
    var text = $('#sel1').text();
    console.log(text);
    console.log(values);
    console.log(values.length);
    if(text.trim()===''){
        alert("请先上传文件");
    }else{
        if(values.length===0){
            alert("请选择需要拆分的字段");
        }else{
            var url = contentpath+'/download?index='+values.toString();
            if(flag){
                url = url + '&group=true';
            }else{
                url = url + '&group=false';
            }
            download(url)
        }
    }
}

function download(blobUrl) {
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = blobUrl;
    a.click();
    // document.body.removeChild(a);
}
