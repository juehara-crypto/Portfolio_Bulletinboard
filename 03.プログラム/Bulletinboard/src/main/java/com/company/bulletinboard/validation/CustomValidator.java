package com.company.bulletinboard.validation;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class CustomValidator extends FieldValidatorSupport {

    // validateメソッドをオーバーライド
    @Override
    public void validate(Object object) throws ValidationException {
        // フィールド名を取得
        String fieldName = getFieldName();
        // フィールド値（ユーザー名やパスワードなど）を取得
        String value = (String) getFieldValue(fieldName, object);

        if ("username".equals(fieldName)) {
            // ユーザー名のバリデーションを実行
            if (!validateUsername(value)) {
                addFieldError(fieldName, object);
            }
        } else if ("password".equals(fieldName)) {
            // パスワードのバリデーションを実行
            if (!validatePassword(value)) {
                addFieldError(fieldName, object);
            }
        }
    }

    // ユーザー名の特殊文字チェック
    public static boolean validateUsername(String username) {
        // 特殊文字を含まないことを確認
        return username != null && username.matches("^[^@#$%&*!?/\\\\=+^|<>{}\\[\\]~]+$");
    }

    // パスワードの英字、数字、記号の組み合わせチェック
    public static boolean validatePassword(String password) {
        // 英字、数字、記号を含むことを確認
        return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_]).*$");
    }
}
