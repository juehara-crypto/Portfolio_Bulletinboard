<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><s:property value="bulletinboard.bulletinboard_title" />詳細画面</title>
<link rel="stylesheet" type="text/css"
	href="/Bulletinboard/view/styles.css">
</head>
<body>
	<div class="container">
		<div class="title">
			<s:property value="bulletinboard.bulletinboard_title" />
			掲示板
		</div>

		<div>
			<s:form action="moveToUserPortal" method="post">
				<s:submit value="TOPに戻る" />
			</s:form>
		</div>

		<div class="user-info">
			ログインユーザー：
			<!-- <s:property value="#session.user.user_name" /> -->
			<s:property value="#session.loggedInUser.user_name" />
			さん
		</div>

		<div class="label">掲示板本文：</div>
		<div class="textbox">
			<s:property value="bulletinboard.bulletinboard_content" />
		</div>

		<!-- 確認のためのデバッグ出力 -->
		<!-- BulletinboardDetailScreen.jsp -->
		<p>
			<!-- 掲示板ID: -->
			<!-- <s:property value="bulletinboard.bulletinboard_id" /> -->
		</p>


		<!-- s:urlタグを使用して、MoveCreateThread アクションにリダイレクトするURLを生成 -->
		<!-- s:paramタグで、bulletinboard_idパラメータをURLに追加。これにより、現在表示している掲示板のIDを次のアクションに渡す -->
		<!-- aタグで生成したURLを使ってリンクを作成し、「スレッド作成」ボタンを表示 -->
		<p>
			<s:url var="createThreadUrl" action="MoveCreateThread">
				<s:param name="bulletinboard_id" value="%{bulletinboard_id}" />
			</s:url>
			<a href="<s:property value="#createThreadUrl" />">スレッド作成</a>
		</p>


		<div class="thread-list-title">
			<s:property value="bulletinboard.bulletinboard_title" />
			スレッド一覧
		</div>
		<s:if test="%{threads != null && !threads.isEmpty()}">
			<table class="thread-table">
				<tr>
					<s:iterator value="threads" var="thread" status="status">
						<td><s:a href="threadDetail?id=%{#thread.thread_id}">
								<s:property value="#thread.thread_title" />

								<!-- BoardDetailActionクラスで取得したスレッドの投稿件数を表示する --> 
                        (投稿件数:<s:property value="#thread.post_count" />件)
                        
                    </s:a></td>
						<s:if test="#status.count % 3 == 0">
				</tr>
				<tr>
					</s:if>
					</s:iterator>
				</tr>
			</table>
		</s:if>
		<s:else>
			<p>No threads found for this bulletinboard.</p>
		</s:else>
</body>
</html>
