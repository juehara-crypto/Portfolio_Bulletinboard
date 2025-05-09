<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ログイン画面</title>
</head>
<body>
	<h1>ログイン画面</h1>

	<!-- エラーメッセージの表示 -->
	<s:actionerror /> <!-- アクションエラーを表示 -->
	
	<!-- フィールドエラーを表示 -->
	<s:fielderror fieldName="user.user_name" /> <!-- ユーザー名のフィールドエラー -->
	<s:fielderror fieldName="user.password" /> <!-- パスワードのフィールドエラー -->

	<s:form action="login" method="post">
		<label>ユーザー名:</label>
		<s:textfield name="user.user_name" label="ログインID" />
		<br>
		<br>
		<label>パスワード:</label>
		<s:password name="user.password" label="パスワード" />
		<br>
		<br>
		<s:submit value="ログイン" />
	</s:form>
</body>
</html>
