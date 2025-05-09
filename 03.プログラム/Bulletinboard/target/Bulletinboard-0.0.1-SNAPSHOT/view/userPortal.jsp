<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>利用者ポータル</title>
<link rel="stylesheet" type="text/css" href="view/styles.css">
</head>
<body>
	<h1>利用者ポータル</h1>
	<br></br>
	<p class="user-info">
		ログインユーザー：
		<!-- <s:property value="#session.user.user_name" /> -->
		<s:property value="#session.loggedInUser.user_name" />
		さん
	</p>

	<!-- アクション名が"getBulletinboards"で、.MoveBulletinboardManagementActionの処理が結果が"bulletinboardsAction"に保存される -->
	<s:action name="getBulletinboards" var="bulletinboardsAction" />

	<!-- エラーメッセージ表示部分 -->
	<!-- bulletinboardsActionにエラーがある場合にエラーメッセージを表示 -->
	<s:if test="%{#bulletinboardsAction.hasActionErrors()}">
		<div class="error">
			<s:actionerror />
		</div>
	</s:if>

	<!-- アクション名が"getThreads"で、.GetThreadActionの処理が結果が"threadsAction"に保存される -->
	<s:action name="getThreads" var="threadsAction" />

	<!-- エラーメッセージ表示部分 -->
	<!-- threadsActionにエラーがある場合にエラーメッセージを表示 -->
	<s:if test="%{#threadsAction.hasActionErrors()}">
		<div class="error">
			<s:actionerror />
		</div>
	</s:if>


	<!-- アクション名が"getThreadsById"で、.GetThreadByIdActionの処理結果が"threadsByIdAction"に保存される -->
	<s:action name="getThreadsById" var="threadsByIdAction" />

	<!-- 以下はセッション内のスレッド存在確認用デバッグ処理 -->
	<!-- <s:if test="#session.threads != null"> -->
	<!-- スレッドリストはセッションに存在します。<br /> -->
	<!-- スレッドの数: <s:property value="#session.threads.size()" /> -->
	<!-- </s:if> -->
	<!-- <s:else> -->
	<!-- スレッドリストはセッションに存在しません。 -->
	<!-- </s:else> -->

	<!-- 以下はスレッドのサイズ認用デバッグ処理 -->
	<!-- <p>threadsByIdAction Debug Info:</p> -->
	<!-- <p> -->
	<!-- 	Threads List Size: -->
	<!-- 	<s:property value="#threadsByIdAction.threads.size()" /> -->
	<!-- </p> -->
	<!-- デバッグ用の出力 -->

	<!-- エラーメッセージ表示部分 -->
	<!-- threadsByIdActionにエラーがある場合にエラーメッセージを表示 -->
	<!-- <s:if test="%{#threadsByIdAction.hasActionErrors()}"> -->
	<!-- 	<div class="error"> -->
	<!-- 		<s:actionerror /> -->
	<!-- 	</div> -->
	<!-- </s:if> -->


	<!-- アクション名が"getPostsById"で、.GetPostByIdActionの処理結果が"postsByIdAction"に保存される -->
	<s:action name="getPostsById" var="postsByIdAction" />

	<!-- 以下はセッション内のスレッド存在確認用デバッグ処理 -->
	<!-- <s:if test="#session.posts != null"> -->
	<!-- 投稿リストはセッションに存在します。<br /> -->
	<!-- 投稿の数: <s:property value="#session.posts.size()" /> -->
	<!-- </s:if> -->
	<!-- <s:else> -->
	<!-- 投稿リストはセッションに存在しません。 -->
	<!-- </s:else> -->

	<!-- 以下は投稿のサイズ認用デバッグ処理 -->
	<!-- <p>postsByIdAction Debug Info:</p> -->
	<!-- <p> -->
	<!-- 	Posts List Size: -->
	<!-- 	<s:property value="#postsByIdAction.posts.size()" /> -->
	<!-- </p> -->
	<!-- デバッグ用の出力 -->
	<!-- スレッドIDはpostsリストに存在します。 -->
	<!-- <br /> -->
	<!-- <s:iterator value="#postsByIdAction.posts" var="post"> -->
	<!-- スレッドID: <s:property value="#post.thread_id" /> -->
	<!-- 	<br /> -->
	<!-- </s:iterator> -->

	<!-- エラーメッセージ表示部分 -->
	<!-- postsByIdActionにエラーがある場合にエラーメッセージを表示 -->
	<!-- <s:if test="%{#postsByIdAction.hasActionErrors()}"> -->
	<!-- 	<div class="error"> -->
	<!-- 		<s:actionerror /> -->
	<!-- 	</div> -->
	<!-- </s:if> -->


	<div class="portal">
		<div class="column">
			<h3>掲示板一覧</h3>
			<!-- bulletinboardsリストが存在し、かつ空でない場合にそのリストを表示 -->
			<s:if
				test="%{#bulletinboardsAction.bulletinboards != null && !#bulletinboardsAction.bulletinboards.isEmpty()}">
				<ul>
					<!-- bulletinboardsリストを反復処理し、各掲示板のリンクを生成 -->
					<s:iterator value="#bulletinboardsAction.bulletinboards">
						<!-- 各掲示板のIDを含むリンクを生成し、そのリンクテキストとして掲示板のタイトルを表示 -->
						<!-- boardDetailアクションにリクエストを送信し、指定されたIDの掲示板詳細画面に遷移する  -->
						<li><s:a href="boardDetail?id=%{bulletinboard_id}">
								<s:property value="bulletinboard_title" />
							</s:a></li>
					</s:iterator>

				</ul>
			</s:if>
			<s:else>
				<p>No bulletinboards found in the database.</p>
			</s:else>


			<h3>スレッド一覧</h3>
			<!-- threadsリストが存在し、かつ空でない場合にそのリストを表示 -->
			<s:if
				test="%{#threadsAction.threads != null && !#threadsAction.threads.isEmpty()}">
				<ul>
					<!-- threadsリストを反復処理し、各スレッドのリンクを生成 -->
					<s:iterator value="#threadsAction.threads">
						<!-- 各スレッドのIDを含むリンクを生成し、そのリンクテキストとしてスレッドのタイトルを表示 -->
						<!-- threadDetailアクションにリクエストを送信し、指定されたIDのスレッド詳細画面に遷移する  -->
						<li><s:a href="threadDetail?id=%{thread_id}">
								<s:property value="thread_title" />
							</s:a> <!-- カウンターをリンクの右側に表示 --> <span style="margin-left: 10px;">
								(投稿件数:<s:property value="post_count" />件)
						</span></li>
					</s:iterator>
				</ul>
			</s:if>
			<s:else>
				<p>No threads found in the database.</p>
			</s:else>
		</div>

		<div class="column">
			<h3>
				<s:property value="#session.loggedInUser.user_name" />
				さんのスレッド一覧
			</h3>
			<!-- threadsリストが存在し、かつ空でない場合にそのリストを表示 -->
			<s:if
				test="%{#threadsByIdAction.threads != null && !#threadsByIdAction.threads.isEmpty()}">
				<ul>
					<!-- threadsリストを反復処理し、各スレッドのリンクを生成 -->
					<s:iterator value="#threadsByIdAction.threads">
						<li>
							<!-- スレッドのタイトルをリンクとして表示 --> <s:a
								href="threadDetail?id=%{thread_id}">
								<s:property value="thread_title" />
							</s:a> <!-- カウンターをリンクの右側に表示 --> <span style="margin-left: 10px;">
								(投稿件数:<s:property value="post_count" />件)
						</span>
						</li>
					</s:iterator>
				</ul>
			</s:if>
			<s:else>
				<p>No threads found in the database.</p>
			</s:else>

		</div>

		<div class="column">
			<h3>
				<s:property value="#session.loggedInUser.user_name" />
				さんの投稿一覧
			</h3>
			<!-- postsリストが存在し、かつ空でない場合にそのリストを表示 -->
			<s:if
				test="%{#postsByIdAction.posts != null && !#postsByIdAction.posts.isEmpty()}">
				<ul>
					<s:iterator value="#postsByIdAction.posts" var="post">
						<!-- 各投稿のHTML要素に一意のIDを付与するために、各投稿の li 要素に以下を追加  -->
						<li id="post_<s:property value="#post.post_id" />">投稿ID: <s:property
								value="#post.post_id" /> <br> <s:a
								href="%{'UserPostThreadDetail.action?postId=' + #post.post_id}">投稿を見る</s:a>
							<s:property value="#post.post_content" />
						</li>
					</s:iterator>
				</ul>

			</s:if>
			<s:else>
				<p>No posts found in the database.</p>
			</s:else>
		</div>

	</div>

	<s:form action="logout">
		<s:submit value="ログアウト" />
	</s:form>
</body>
</html>
