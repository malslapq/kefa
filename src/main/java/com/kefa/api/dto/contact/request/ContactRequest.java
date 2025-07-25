package com.kefa.api.dto.contact.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ContactRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    @NotBlank(message = "사업자등록번호는 필수입니다")
    private String businessNumber;
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소 형식이 아닙니다.")
    private String email;
    @NotBlank(message = "전화번호는 필수입니다..")
    private String phoneNumber;
    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;

}
