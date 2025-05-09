<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<title>投稿管理画面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<body>
	<h1>投稿管理画面</h1>
	<br>

	<%-- URLをgoToManagementMenu.actionにマッピングさせる --%>
	<%-- URLをgoToManagementMenu.actionにマッピングさせ、ユーザーが「管理メニューに戻る」リンクをクリックすると対応するアクションが呼び出される --%>
	<p>
		<a href="<%=request.getContextPath()%>/goToManagementMenu.action">管理メニューに戻る</a>
	</p>
	<br>

	<table border="1">
		<tr>
			<th>投稿タイトル</th>
			<th>投稿本文</th>
		</tr>
		<tbody>
			<td>第一章</td>
			<td>はじめに.......</td>
			<td><p>
					<button id="update">
				</p>編集
				</button></td>
			<td><p>
					<button id="delete">
				</p>削除
				</button></td>
		</tbody>
		<tbody>
			<td>第二章</td>
			<td>次に.......</td>
			<td><p>
					<button id="update">
				</p>編集
				</button></td>
			<td><p>
					<button id="delete">
				</p>削除
				</button></td>
		</tbody>
		<tbody>
			<td>第三章</td>
			<td>その他.......</td>
			<td><p>
					<button id="update">
				</p>編集
				</button></td>
			<td><p>
					<button id="delete">
				</p>削除
				</button></td>
		</tbody>
	</table>

</body>
</html>