<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>ユーザー編集画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


</head>
<body>
	<h1>ユーザー編集</h1>

	<!-- キャンセルボタンのフォーム -->
	<s:form action="usercancel" method="post">
		<s:hidden name="action" value="usercancel" />
		<s:submit value="キャンセル" />
	</s:form>

	<!-- エラーメッセージの表示 -->

	<s:actionerror />

	<s:form action="UpdateUser">

		<!-- ユーザーIDを非表示で送信 -->
		<s:hidden name="user.user_id" value="%{user.user_id}" />

		<div>
			ユーザー名:
			<s:textfield name="user.user_name" value="%{user.user_name}" />
			<s:fielderror fieldName="user.user_name" />
		</div>
		<div>
			パスワード:
			<s:textfield name="user.password" value="%{user.password}" />
			<s:fielderror fieldName="user.password" />
		</div>
		<div>
			権限:
			<s:select name="user.auth_type" list="#{'0':'一般ユーザー権限', '1':'管理者権限'}"
				label="権限フラグ" />
		</div>
		<div>
			ユーザー削除フラグ:
			<s:select name="user.delete_flag" list="#{'0':'削除しない', '1':'削除する'}"
				label="削除フラグ" />
		</div>
		<div>
			削除日:
			<s:textfield name="user.delete_day" value="%{user.delete_day}" />
			<s:fielderror fieldName="user.delete_day" />
		</div>
		<s:submit value="更新" />
	</s:form>
</body>
</html>
