<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>スレッド作成画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

	<s:actionerror />

	<h1>スレッド作成</h1>

	<!-- CreateThreadScreenActionから送られて来た、bulletinboard_idを隠しフィールドの値に設定します。 -->
	<!-- スレッドタイトルの入力フィールドを表示し、フォームをbulletinboard_idと一緒に、InsertThreadアクションに送信します。 -->
	<s:form action="InsertThread" method="post">

		<s:hidden name="bulletinboard_id" value="%{bulletinboard_id}" />
		<div>
			スレッドタイトル:
			<s:textfield name="thread.thread_title"
				value="%{thread.thread_title}" />
			<s:fielderror fieldName="thread.thread_title" />
		</div>
		<s:submit value="作成" />
	</s:form>

</body>
</html>
