$(function () {
    InitExcelFile
});
//初始化Excel导入的文件
function InitExcelFile() {

    $("#txt_file").fileinput({
        language: 'zh', //设置语言
        uploadUrl: '/upload', //上传的地址
        allowedFileExtensions: ['xls','xlsx'],//接收的文件后缀
        showUpload: true, //是否显示上传按钮
        showCaption: false,//是否显示标题
        showClose: false,
        browseClass: "btn btn-primary", //按钮样式
        maxFileCount: 10, //表示允许同时上传的最大文件个数
        enctype: 'multipart/form-data',
        validateInitialCount:true,
        // previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
        msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！"
    })  //文件上传完成后的事件
        .on('fileuploaded', function (event, data, previewId, index) {

        });
}