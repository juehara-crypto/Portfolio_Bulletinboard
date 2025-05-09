<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>掲示板管理画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<!-- エラーメッセージ表示部分 -->
	<s:if test="hasActionErrors()">
		<div class="error">
			<s:actionerror />
		</div>
	</s:if>
	
	<s:actionerror />

	<s:if test="bulletinboards != null && !bulletinboards.isEmpty()">
		<h1>掲示板管理</h1>

		<p>
			<a href="<%=request.getContextPath()%>/goToManagementMenu.action">管理メニューに戻る</a>
		</p>
		<p>
			<s:form action="CreateBulletinboardScreen">
				<s:submit value="掲示板作成" />
			</s:form>
		</p>

		<table border="1">
			<s:iterator value="bulletinboards">
				<tr>
					<td><s:property value="bulletinboard_title" /></td>
					<td><s:url var="editbulletinboardUrl"
							action="EditBulletinboard">
							<s:param name="bulletinboard_id" value="%{bulletinboard_id}" />
						</s:url> <s:a href="%{editbulletinboardUrl}">編集</s:a></td>
					<td><s:url var="deletebulletinboardUrl"
							action="DeleteBulletinboard">
							<s:param name="bulletinboard_id" value="%{bulletinboard_id}" />
						</s:url> <s:a href="%{deletebulletinboardUrl}">削除</s:a></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<s:else>
		<p>No bulletinboards found in the database.</p>
	</s:else>

</body>
</html>
