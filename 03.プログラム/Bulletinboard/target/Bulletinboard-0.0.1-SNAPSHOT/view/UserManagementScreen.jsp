<%-- JSPページのコンテンツタイプと文字エンコーディングを設定する --%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>ユーザー管理画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


</head>
<body>

	<s:actionerror />

	<s:if test="users != null && !users.isEmpty()">
		<h1>ユーザー管理</h1>


		<%-- URLをgoToManagementMenu.actionにマッピングさせる --%>
		<%-- URLをgoToManagementMenu.actionにマッピングさせ、ユーザーが「管理メニューに戻る」リンクをクリックすると対応するアクションが呼び出される --%>
		<p>
			<a href="<%=request.getContextPath()%>/goToManagementMenu.action">管理メニューに戻る</a>
		</p>

		<p>
			<s:form action="CreateUserScreen">
				<s:submit value="ユーザー作成" />
			</s:form>
		</p>

		<table border="1">
			<tr>
				<th>ユーザーID</th>
				<th>ユーザー名</th>
				<th>権限</th>
				<th>操作</th>
			</tr>

			<%-- usersリストの各要素を反復処理 --%>
			<s:iterator value="users">
				<tr>
					<%-- 現在のUserオブジェクトの各「user_id」、user_name、auth_typeプロパティを表示 --%>
					<td><s:property value="user_id" /></td>
					<td><s:property value="user_name" /></td>
					<td><s:property value="auth_type" /></td>

					<%-- 「s:url」でStruts2のアクションURLを生成する、「var=変数」に生成したURLを格納する
                         「s:param」タグでURLにパラメータを追加、「value="%{user_id}"」で現在のユーザーIDを指定する
                         「s:a」タグで生成されたURLを使用してリンクを作成する --%>
					<td><s:url var="editUrl" action="EditUser">
							<s:param name="id" value="%{user_id}" />
						</s:url> <s:a href="%{editUrl}">編集</s:a></td>
					<td><s:url var="deleteUrl" action="DeleteUser">
							<s:param name="id" value="%{user_id}" />
						</s:url> <s:a href="%{deleteUrl}">削除</s:a></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<s:else>
		<p>No users found in the database.</p>
	</s:else>

</body>
</html>
