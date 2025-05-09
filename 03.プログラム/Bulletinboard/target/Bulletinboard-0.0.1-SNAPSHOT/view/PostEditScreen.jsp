<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>投稿編集画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


</head>
<body>

	<s:actionerror />

	<h1>投稿編集/投稿削除</h1>

	<!-- キャンセルボタンのフォーム -->
	<s:form action="postcancel" method="post">
		<s:hidden name="action" value="postcancel" />
		<s:submit value="キャンセル" />
	</s:form>

	<s:actionerror />

	<s:form action="UpdatePost">

		<!-- 投稿IDを非表示で送信 -->
		<s:hidden name="post.post_id" value="%{post.post_id}" />
		
		<!-- スレッドIDを非表示で送信 -->
		<s:hidden name="post.thread_id" value="%{post.thread_id}" />

		<!-- <div>
			投稿ID:
			<s:textfield name="post.post_id" value="%{post.post_id}"
				readonly="true" />
		</div> -->

		<!-- <div>
			スレッドID:
			<s:textfield name="post.thread_id" value="%{post.thread_id}"
				readonly="true" />
		</div> -->

		<div>
			投稿内容: <br></br>
			<s:textarea name="post.post_content" value="%{post.post_content}" />
			<s:fielderror fieldName="post.post_content" />
		</div>


		<div></div>
		<br></br>
		<s:submit value="投稿更新" />
	</s:form>

	<s:form action="DeletePost" method="post">
		<s:hidden name="post_id" value="%{post_id}" />
		<s:submit value="投稿削除" />
	</s:form>


</body>
</html>