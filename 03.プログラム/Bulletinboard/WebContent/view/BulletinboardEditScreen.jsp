<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>掲示板編集画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


</head>
<body>

	<s:actionerror />

	<h1>掲示板編集</h1>

	<!-- キャンセルボタンのフォーム -->
	<s:form action="cancel" method="post">
		<s:hidden name="action" value="cancel" />
		<s:submit value="キャンセル" />
	</s:form>

	<s:form action="UpdateBulletinboard">

		<%-- bulletinboardビーンオブジェクトの各プロパティへのアクセスについては、EditBulletinboardActionクラスに
		    「public User getBulletinboard()」メソッドが存在し、そのメソッドの処理によって、
		     このViewから直接アクセスできるようになっている--%>
		<%-- 以下、Viewの22行目から44行目にて「bulletinboard.bulletinboard__xxx」と記述しているのは、その為である --%>

		<%-- <div>
			掲示板ID:
			<s:textfield name="bulletinboard.bulletinboard_id"
				value="%{bulletinboard.bulletinboard_id}" readonly="true" />
		</div> --%>


		<!-- 掲示板IDを非表示で送信 -->
		<s:hidden name="bulletinboard.bulletinboard_id"
			value="%{bulletinboard.bulletinboard_id}" />

		<div>
			掲示板タイトル:
			<s:textfield name="bulletinboard.bulletinboard_title"
				value="%{bulletinboard.bulletinboard_title}" />
			<s:fielderror fieldName="bulletinboard.bulletinboard_title" />
		</div>
		<div>
			掲示板本文:
			<s:textarea name="bulletinboard.bulletinboard_content"
				value="%{bulletinboard.bulletinboard_content}" />
			<s:fielderror fieldName="bulletinboard.bulletinboard_content" />
		</div>

		<div>
			掲示板削除フラグ:
			<s:select name="bulletinboard.bulletinboard_delete_flag"
				list="#{'0':'削除しない', '1':'削除する'}" label="削除フラグ" />
		</div>

		<div>
			掲示板削除日:
			<s:textfield name="bulletinboard.bulletinboard_delete_day"
				value="%{bulletinboard.bulletinboard_delete_day}" />
			<s:fielderror fieldName="bulletinboard.bulletinboard_delete_day" />
		</div>
		<div></div>
		<s:submit value="更新" />
	</s:form>
</body>
</html>
