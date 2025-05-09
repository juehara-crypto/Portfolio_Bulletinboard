<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>

<body>
	<h1>管理者メニュー</h1>
	<form>
		<s:a action="/view/BulletinboardManagementScreen">
            掲示板管理画面へ
        </s:a>
		<br>
		<br>

		<s:a action="/view/PostManagementScreen">
            投稿管理画面へ
        </s:a>
		<br>
		<br>

		<s:a action="/view/UserManagementScreen">
            ユーザー管理画面へ
        </s:a>
		<br>
		<br>

		<s:a action="logout">
            ログアウト
        </s:a>
		<br>
		<br>
	</form>
</body>
</html>
