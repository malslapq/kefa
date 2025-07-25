package com.kefa.infrastructure.mail;

public class EmailTemplate {

    /**
     * Generates an HTML email template in Korean for password reset instructions, embedding the provided token in the reset URL.
     *
     * @param token the unique token to be included in the password reset link
     * @return a complete HTML string for the password reset email, including a styled button and fallback link
     */
    public static String createPasswordResetEmailContent(String token){
        // 프론트엔드의 비밀번호 재설정 페이지 URL (실제 배포 시 도메인 변경 필요)
        String resetUrl = String.format("http://localhost:3000/auth/password-reset?token=%s", token);

        // HTML 형식의 이메일 내용
        return """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>비밀번호 재설정 안내</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 20px;
                }
                .container {
                    max-width: 600px;
                    margin: 0 auto;
                    background-color: #ffffff;
                    padding: 30px;
                    border-radius: 8px;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                }
                .header {
                    text-align: center;
                    padding-bottom: 20px;
                    border-bottom: 1px solid #eee;
                }
                .header h1 {
                    color: #0056b3;
                    margin: 0;
                }
                .content {
                    padding: 20px 0;
                }
                .button-container {
                    text-align: center;
                    margin-top: 30px;
                }
                .button {
                    display: inline-block;
                    padding: 12px 25px;
                    background-color: #007bff;
                    color: #ffffff;
                    text-decoration: none;
                    border-radius: 5px;
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>비밀번호 재설정 안내</h1>
                </div>
                <div class="content">
                    <p>안녕하세요.</p>
                    <p>회원님의 비밀번호 재설정 링크를 전달드립니다.</p>
                    <p>5분 안에 아래 버튼을 클릭하여 비밀번호를 재설정해 주세요.</p>
                    
                    <div class="button-container">
                        <a href="%s" class="button">비밀번호 재설정하기</a>
                    </div>
                    
                    <p style="margin-top: 30px;">
                        만약 위 버튼이 작동하지 않는다면, 다음 링크를 복사하여 브라우저에 붙여넣어 주세요:<br>
                        <a href="%s" target="_blank" style="word-break: break-all;">%s</a>
                    </p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(resetUrl, resetUrl, resetUrl);
    }

    /**
     * Generates an HTML email template for email verification, embedding the provided token in the verification link.
     *
     * @param token the unique token to be included in the verification URL
     * @return a styled HTML string for email verification, including a button and fallback link
     */
    public static String createVerificationEmailContent(String token) {
        String verificationUrl = String.format("http://localhost:8080/auth/email-verify?token=%s", token);

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
