<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>ユーザー作成画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


</head>
<body>

	<h1>ユーザー作成</h1>

	<!-- キャンセルボタンのフォーム -->
	<s:form action="usercancel" method="post">
		<s:hidden name="action" value="usercancel" />
		<s:submit value="キャンセル" />
	</s:form>

	<!-- エラーメッセージの表示 -->

	<s:actionerror />
	<!-- <s:fielderror /> -->

	<s:form action="InsertUser">

		<%-- userビーンオブジェクトの各プロパティへのアクセスについては、InsertUserAction クラスに
		    public User getUser()メソッドが存在し、そのメソッドの処理によって、
		     このViewから直接アクセスできるようになっている--%>
		<%-- 以下、Viewの22行目から44行目にて「user.user__xxx」と記述しているのは、その為である --%>

		<%-- <div>
			ユーザーID:
			<s:textfield name="user.user_id" value="%{user.user_id}"
				readonly="true" />
		</div> --%>

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
		<s:submit value="作成" />
	</s:form>

</body>
</html>