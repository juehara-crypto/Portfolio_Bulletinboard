<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<html>
<head>
    <title>エラー</title>
</head>
<body>
    <h1>エラーが発生しました</h1>
    <s:actionerror />
    <a href="<s:url action='index'/>">ホームに戻る</a>
</body>
</html>