package com.kefa.infrastructure.mail;

public class EmailTemplate {

    public static String createVerificationEmailContent(String token) {
        String verificationUrl = String.format("http://localhost:8080/auth/verify?token=%s", token);

        return """
            <html>
            <head>
                <style>
                    .container {
                        width: 100%%;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        font-family: Arial, sans-serif;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #4CAF50;
                        color: white;
                        text-decoration: none;
                        border-radius: 4px;
                        margin: 20px 0;
                    }
                    .message {
                        color: #666;
                        line-height: 1.5;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>이메일 인증</h1>
                    <p class="message">
                        Kefa 서비스에 가입해 주셔서 감사합니다.<br>
                        아래 버튼을 클릭하여 이메일 인증을 완료해 주세요.
                    </p>
                    <a href="%s" class="button">이메일 인증하기</a>
                    <p class="message">
                        버튼이 작동하지 않는 경우 아래 링크를 복사하여 브라우저에 붙여넣어 주세요.<br>
                        %s
                    </p>
                </div>
            </body>
            </html>
            """.formatted(verificationUrl, verificationUrl);
    }

}
