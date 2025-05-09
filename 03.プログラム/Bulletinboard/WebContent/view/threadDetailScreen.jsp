<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>スレッド詳細画面</title>
<link rel="stylesheet" type="text/css"
	href="/Bulletinboard/view/styles.css">

<script type="text/javascript">
        // JavaScriptによるバリデーション処理
        function validatePost() {
            const postContent = document.getElementById("post_content").value.trim();  // 投稿内容を取得

            // バリデーションエラーメッセージ用の要素
            const errorMessage = document.getElementById("error_message");
            errorMessage.innerHTML = "";  // エラーメッセージを初期化

            // ① 投稿内容必須チェック
            if (postContent === "") {
                errorMessage.innerHTML = "投稿内容を入力してください。";  // エラーメッセージを表示
                return false;  // フォームの送信を中止
            }

            // ② 投稿内容最大文字数チェック (250文字以内)
            if (postContent.length > 250) {
                errorMessage.innerHTML = "投稿内容は250文字以内で入力してください。";  // エラーメッセージを表示
                return false;  // フォームの送信を中止
            }

            // すべてのバリデーションをクリアした場合、trueを返してフォーム送信を許可
            return true;
        }
    </script>

</head>
<body>
	<div class="container">
		<div class="header">



			<div>
				ログインユーザー：
				<s:property value="#session.loggedInUser.user_name" />
				さん
			</div>

			<!-- スレッド編集ボタン表示判定用のデバッグ処理 -->
			<!-- ログインユーザーの表示と、スレッド作成ユーザーを表示 -->
			<div>
				<!-- Logged in User ID: -->
				<!-- <s:property value="%{#session.loggedInUser.user_id}" /> -->
				<!-- <br /> Thread User ID: -->
				<!-- <s:property value="%{#session.thread_user_id}" /> -->
				<!-- <br /> -->
				<!-- threadオブジェクト全体をJSON形式で出力 -->
				<!-- <s:property value="%{thread}" />  -->
			</div>

			<!-- 「スレッド編集」ボタン表示＆非表示の判定処理 -->
			<!-- ログインユーザーと、スレッド作成者が一致した場合にボタンを表示する -->
			<!-- スレッド作成者の情報はThreadDetailActionクラスにて、スレッドの作成者IDをセッションに保存している -->
			<div>
				<s:if
					test="%{#session.loggedInUser.user_id == #session.thread_user_id}">
					<s:form action="EditThread" method="post">
						<s:hidden name="thread_id" value="%{thread_id}" />
						<s:submit value="スレッド編集" />
					</s:form>
				</s:if>
			</div>

		</div>
		<div class="thread-title">
			<label>スレッドタイトル：</label>
			<div>
				<s:property value="thread.thread_title" />
			</div>
		</div>
		<div class="post-list">
			<s:iterator value="postList" var="post">

				<!-- 各投稿に対して一意のIDを設定 -->
				<div class="post-item"
					id="post_<s:property value="#post.post_id" />">
					<div class="post-item-content">
						<a name="post_<s:property value="#post.post_id" />"></a>
						<!-- アンカーリンクの追加 -->
						<s:property value="#post.post_id" />
						名前 ：
						<s:property value="#post.user_name" />
						：
						<s:property value="#post.formattedPostTimestamp" />
						<br>

						<!-- アンカーのHTMLリンクへの変換処理はUserモデルの getPostContent()メソッドで実行 -->
						<!-- escapeHtmlを無効にしてHTMLとして出力する -->
						<s:property value="postContent" escapeHtml="false" />
					</div>
					<div>
						<s:if test="#session.loggedInUser.user_id == #post.user_id">
							<s:form action="EditPost" method="post">
								<s:hidden name="post_id" value="%{post_id}" />
								<s:submit value="投稿編集/削除" />
							</s:form>
						</s:if>
					</div>

				</div>
			</s:iterator>
		</div>

		<br> <br>

		<!-- バリデーションエラーメッセージを表示する場所 -->
		<div id="error_message" style="color: red;"></div>

		<!-- 投稿フォーム -->
		<div>

			<s:form action="post" method="post" onsubmit="return validatePost()">
				<s:hidden name="thread_id" value="%{thread_id}" />
				<label>投稿内容：</label>
				<br>
				<s:fielderror fieldName="post_content" cssClass="error_message" />
				<s:textarea id="post_content" name="post_content" rows="5"
					cols="100"></s:textarea>
				<br>
				<s:submit value="投稿書き込み" />
				<s:reset value="キャンセル" />
			</s:form>
		</div>

		<br>
		<div>
			<s:form action="moveToUserPortal" method="post">
				<s:submit value="TOPに戻る" />
			</s:form>
		</div>

	</div>

	<script>
    // URLからパラメータを取得する関数
    function getParameterByName(name) {
        const urlParams = new URLSearchParams(window.location.search);
        console.log('URL Parameters:', urlParams);
        return urlParams.get(name);
    }

    document.addEventListener('DOMContentLoaded', function() {
        // URLから'postId'パラメータを取得
        const postId = getParameterByName('postId');
        console.log('postId:', postId);

        // 'postId'パラメータが存在する場合のみスクロール処理を行う
        if (postId) {
            const targetElement = document.getElementById('post_' + postId);
            console.log('targetElement:', targetElement);

            // targetElementが存在する場合にスクロール
            if (targetElement) {
                // 少し待機してからスクロール処理を実行
                setTimeout(() => {
                    targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });

                    // ハイライトクラスを追加
                    targetElement.classList.add('highlight');

                    // 2秒後にハイライトクラスを削除
                    setTimeout(() => {
                        targetElement.classList.remove('highlight');
                    }, 2000);
                }, 500); // 500ミリ秒の待機時間
            } else {
                console.warn('該当する投稿が見つかりませんでした: post_' + postId);
            }
        } else {
            console.warn('postIdパラメータがURLに存在しません。');
        }
    });
</script>
</body>
</html>
