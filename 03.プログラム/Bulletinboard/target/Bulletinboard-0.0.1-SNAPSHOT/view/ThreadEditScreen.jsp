<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>スレッド編集画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


</head>
<body>

	<h1>スレッド編集</h1>

	<!-- スレッド編集画面用のキャンセルボタン -->
	<s:form action="threadcancel" method="post">
		<s:hidden name="action" value="threadcancel" />
		<s:submit value="キャンセル" />
	</s:form>

	<!-- エラーメッセージの表示 -->
	<s:actionerror />


	<s:form action="UpdateThread">

		<!-- <div>
			スレッドID:
			<s:textfield name="thread.thread_id" value="%{thread.thread_id}"
				readonly="true" />
		</div>  -->

		<!-- <div>
			掲示板ID:
			<s:textfield name="thread.bulletinboard_id"
				value="%{thread.bulletinboard_id}" readonly="true" />
		</div>  -->


		<!-- スレッドIDを非表示で送信 -->
		<s:hidden name="thread.thread_id" value="%{thread.thread_id}" />


		<!-- 掲示板IDを非表示で送信 -->
		<s:hidden name="thread.bulletinboard_id"
			value="%{thread.bulletinboard_id}" />

		<div>
			スレッドタイトル: <br></br>
			<s:textfield name="thread.thread_title"
				value="%{thread.thread_title}" />
			<s:fielderror fieldName="thread.thread_title" />
		</div>


		<div></div>
		<br></br>
		<s:submit value="スレッド編集" />
	</s:form>
</body>
</html>