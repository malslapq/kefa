package com.kefa.application.usecase;

import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemUpdateCommand;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemUpdateRequest;
import com.kefa.api.dto.developmentItem.response.DevelopmentItemResponse;
import com.kefa.common.exception.DevelopmentItemException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Company;
import com.kefa.domain.entity.DevelopmentItem;
import com.kefa.domain.type.Stage;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.repository.DevelopmentItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DevelopmentItemUseCaseTest {

    @InjectMocks
    private DevelopmentItemUseCase developmentItemUseCase;

    @Mock
    private DevelopmentItemRepository developmentItemRepository;

    @Mock
    private CompanyRepository companyRepository;

    private DevelopmentItem developmentItem;
    private Company company;
    private Account account;
    private final Long itemId = 1L;
    private final Long companyId = 1L;
    private final Long loginAccountId = 1L;
    private final String itemName = "Test Item";
    private final Stage itemStage = Stage.COMMERCIALIZED;
    private final String techField = "AI";
    private final String description = "Test Description";

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(loginAccountId)
            .email("test@test.com")
            .name("testAccount")
            .build();

        company = Company.builder()
            .id(companyId)
            .name("testCompany")
            .account(account)
            .developmentItems(new ArrayList<>())
            .build();

        developmentItem = DevelopmentItem.builder()
            .id(itemId)
            .name(itemName)
            .stage(itemStage)
            .technologyField(techField)
            .description(description)
            .company(company)
            .build();
    }

    @DisplayName("개발 아이템 삭제 성공")
    @Test
    void deleteDevelopmentItemSuccess() {
        // given
        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(developmentItem));

        // when
        developmentItemUseCase.delete(itemId, loginAccountId);

        // then
        verify(developmentItemRepository, times(1)).delete(developmentItem);
    }

    @DisplayName("개발 아이템 삭제 실패 - 아이템 없음")
    @Test
    void deleteDevelopmentItemFailNotFound() {
        // given
        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.delete(itemId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.DEVELOPMENT_ITEM_NOT_FOUND.getMessage());
    }

    @DisplayName("개발 아이템 삭제 실패 - 권한 없음")
    @Test
    void deleteDevelopmentItemFailUnauthorized() {
        // given
        Account otherAccount = Account.builder().id(2L).build();
        Company otherCompany = Company.builder().id(companyId).account(otherAccount).build();
        DevelopmentItem item = DevelopmentItem.builder().id(itemId).company(otherCompany).build();

        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.delete(itemId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @DisplayName("개발 아이템 업데이트 성공")
    @Test
    void updateDevelopmentItemSuccess() {
        // given
        DevelopmentItemUpdateRequest updateRequest = new DevelopmentItemUpdateRequest(
            "updatedItem", Stage.PROGRAMMING, "updatedTech", "updatedDescription"
        );

        DevelopmentItemUpdateCommand command = DevelopmentItemUpdateCommand.builder()
            .itemId(itemId)
            .accountId(loginAccountId)
            .request(updateRequest)
            .build();

        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(developmentItem));
        given(developmentItemRepository.save(any(DevelopmentItem.class))).willReturn(developmentItem);

        // when
        DevelopmentItemResponse response = developmentItemUseCase.update(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
    }

    @DisplayName("개발 아이템 업데이트 실패 - 아이템 없음")
    @Test
    void updateDevelopmentItemFailNotFound() {
        // given
        DevelopmentItemUpdateRequest updateRequest = new DevelopmentItemUpdateRequest(
            "updatedItem", Stage.PROGRAMMING, "updatedTech", "updatedDescription"
        );

        DevelopmentItemUpdateCommand command = DevelopmentItemUpdateCommand.builder()
            .itemId(itemId)
            .accountId(loginAccountId)
            .request(updateRequest)
            .build();

        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.update(command))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.DEVELOPMENT_ITEM_NOT_FOUND.getMessage());
    }

    @DisplayName("개발 아이템 업데이트 실패 - 권한 없음")
    @Test
    void updateDevelopmentItemFailUnauthorized() {
        // given
        DevelopmentItemUpdateRequest updateRequest = new DevelopmentItemUpdateRequest(
            "updatedItem", Stage.PROGRAMMING, "updatedTech", "updatedDescription"
        );

        DevelopmentItemUpdateCommand command = DevelopmentItemUpdateCommand.builder()
            .itemId(itemId)
            .accountId(loginAccountId)
            .request(updateRequest)
            .build();

        Account otherAccount = Account.builder().id(2L).build();
        Company otherCompany = Company.builder().id(companyId).account(otherAccount).build();
        DevelopmentItem item = DevelopmentItem.builder().id(itemId).company(otherCompany).build();

        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.update(command))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());
    }


    @DisplayName("개발 아이템 추가 성공")
    @Test
    void addDevelopmentItemSuccess() {
        // given
        DevelopmentItemAddRequest addRequest =
            DevelopmentItemAddRequest.builder()
                .name(itemName)
                .stage(itemStage)
                .technologyField(techField)
                .description(description)
                .build();

        given(companyRepository.findById(companyId)).willReturn(Optional.of(company));
        given(developmentItemRepository.save(any(DevelopmentItem.class))).willReturn(developmentItem);

        // when
        DevelopmentItemResponse response = developmentItemUseCase.add(companyId, addRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
        assertThat(response.getName()).isEqualTo(itemName);
    }

    @DisplayName("개발 아이템 추가 실패 - 회사 없음")
    @Test
    void addDevelopmentItemFailCompanyNotFound() {
        // given
        DevelopmentItemAddRequest addRequest = new DevelopmentItemAddRequest(
            itemName, itemStage, techField, description
        );

        given(companyRepository.findById(companyId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.add(companyId, addRequest))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @DisplayName("개발 아이템 목록 조회 성공")
    @Test
    void getAllDevelopmentItemsSuccess() {
        // given
        List<DevelopmentItem> items = Arrays.asList(
            developmentItem,
            DevelopmentItem.builder().id(2L).name("Item 2").company(company).build()
        );

        given(developmentItemRepository.findAllByCompanyIdWithAccount(companyId)).willReturn(items);

        // when
        List<DevelopmentItemResponse> responses = developmentItemUseCase.getAll(companyId, loginAccountId);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(itemId);
        assertThat(responses.get(0).getName()).isEqualTo(itemName);
    }

    @DisplayName("개발 아이템 목록 조회 - 빈 목록")
    @Test
    void getAllDevelopmentItemsEmptyList() {
        // given
        given(developmentItemRepository.findAllByCompanyIdWithAccount(companyId)).willReturn(Collections.emptyList());

        // when
        List<DevelopmentItemResponse> responses = developmentItemUseCase.getAll(companyId, loginAccountId);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }

    @DisplayName("개발 아이템 목록 조회 실패 - 권한 없음")
    @Test
    void getAllDevelopmentItemsFailUnauthorized() {
        // given
        Account otherAccount = Account.builder().id(2L).build();
        Company otherCompany = Company.builder().id(companyId).account(otherAccount).build();
        DevelopmentItem item = DevelopmentItem.builder().id(itemId).company(otherCompany).build();

        List<DevelopmentItem> items = Collections.singletonList(item);

        given(developmentItemRepository.findAllByCompanyIdWithAccount(companyId)).willReturn(items);

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.getAll(companyId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @DisplayName("개발 아이템 단일 조회 성공")
    @Test
    void getDevelopmentItemSuccess() {
        // given
        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(developmentItem));

        // when
        DevelopmentItemResponse response = developmentItemUseCase.get(companyId, itemId, loginAccountId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
        assertThat(response.getName()).isEqualTo(itemName);
    }

    @DisplayName("개발 아이템 단일 조회 실패 - 아이템 없음")
    @Test
    void getDevelopmentItemFailNotFound() {
        // given
        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.get(companyId, itemId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.DEVELOPMENT_ITEM_NOT_FOUND.getMessage());
    }

    @DisplayName("개발 아이템 단일 조회 실패 - 권한 없음")
    @Test
    void getDevelopmentItemFailUnauthorized() {
        // given
        Account otherAccount = Account.builder().id(2L).build();
        Company otherCompany = Company.builder().id(companyId).account(otherAccount).build();
        DevelopmentItem item = DevelopmentItem.builder().id(itemId).company(otherCompany).build();

        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.get(companyId, itemId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @DisplayName("개발 아이템 단일 조회 실패 - 회사 ID 불일치")
    @Test
    void getDevelopmentItemFailCompanyIdMismatch() {
        // given
        Long differentCompanyId = 2L;

        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(developmentItem));

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.get(differentCompanyId, itemId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @DisplayName("개발 아이템 조회 메서드 성공")
    @Test
    void getDevelopmentItemWithCompanyAndAccountSuccess() {
        // given
        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.of(developmentItem));

        // when
        DevelopmentItemResponse response = developmentItemUseCase.get(companyId, itemId, loginAccountId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
        assertThat(response.getName()).isEqualTo(itemName);
    }

    @DisplayName("개발 아이템 조회 실패 - 아이템 없음")
    @Test
    void getDevelopmentItemWithCompanyAndAccountFailNotFound() {
        // given
        given(developmentItemRepository.findByIdWithCompanyAndAccount(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> developmentItemUseCase.get(companyId, itemId, loginAccountId))
            .isInstanceOf(DevelopmentItemException.class)
            .hasMessage(ErrorCode.DEVELOPMENT_ITEM_NOT_FOUND.getMessage());
    }


}