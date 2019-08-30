<%--
  Created by IntelliJ IDEA.
  User: sx-wangxiao
  Date: 2019/8/16
  Time: 16:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<script>
    var contentpath = '<%=basePath%>'
</script>
<%--<base href="<%=basePath%>" >--%>
<html>
  <head>
    <title>EXCEL_SPLIT</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="static/lib/css/bootstrap.min.css">
    <link rel="stylesheet" href="static/lib/css/fileinput.min.css">
      <link href="static/lib/css/bootstrap-select.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="static/lib/css/font-awesome.min.css"/>

    <script src="static/lib/js/popper.min.js"></script>
    <script src="static/lib/js/jquery-3.4.1.min.js"></script>
    <script src="static/lib/js/bootstrap.min.js"></script>
    <script src="static/lib/js/fileinput.min.js"></script>
    <script src="static/lib/js/locales/zh.js"></script>
      <script src="static/lib/js/bootstrap-select.min.js"></script>
      <script src="static/lib/js/locales/defaults-zh_CN.js"></script>
    <script src="static/lib/js/fileini.js"></script>

  </head>
  <body>
  <div class="container">
    <br>
    <h2 class="text-center">EXCEL文档拆分</h2>
    <hr>
    <div class="row">
      <div class="col-2">
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">上传EXCEL文件</button>
      </div>
      <div class="col-10 form-group">
          <div class="row">
              <div class="col-3">
                  <label for="sel1">请选择需要拆分的字段</label>
              </div>
              <div class="col-7">
                  <select multiple class="selectpicker w-100" id="sel1">
                  </select>
              </div>
              <div class="col-2">
                  <div class="btn-group">
                      <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                          拆分并下载为
                      </button>
                      <div class="dropdown-menu">
                          <a class="dropdown-item" href="javascript:excelsplit(false);">单个文件</a>
                          <a class="dropdown-item" href="javascript:excelsplit(true)">多个文件</a>
                      </div>
                  </div>
              </div>
          </div>
      </div>
    </div>
    <form>
      <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h4 class="modal-title" id="myModalLabel">请选择Excel文件</h4>
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>
            <div class="modal-body">
              <input name="txt_file" id="txt_file" type="file" class="file-loading" />
            </div>
          </div>
        </div>
      </div>
    </form>
      <hr>
  </div>
  <div class="container">
    <%--条纹表格--%>
    <p>表格数据（最多显示10行）</p>
    <table class="table table-striped" id="excel_table">
      <thead>
      <tr>
        <th>字段1</th>
        <th>字段2</th>
        <th>字段3</th>
      </tr>
      </thead>
    </table>
  </div>

  </body>
</html>

