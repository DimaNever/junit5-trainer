package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.dmdev.entity.Provider.APPLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void SuccessValidate() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .name("example")
                .userId(1)
                .provider(String.valueOf(APPLE))
                .expirationDate(Instant.parse("2023-12-12T10:15:30.00Z"))
                .build();

        var actualResult = validator.validate(dto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserId() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .name("example")
                .userId(null)
                .provider(String.valueOf(APPLE))
                .expirationDate(Instant.parse("2023-12-12T10:15:30.00Z"))
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @Test
    void invalidUserName() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("")
                .provider(String.valueOf(APPLE))
                .expirationDate(Instant.parse("2023-12-12T10:15:30.00Z"))
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    void invalidateProvider() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("example")
                .provider("YouTube")
                .expirationDate(Instant.parse("2023-12-12T10:15:30.00Z"))
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);

    }

    @Test
    void invalidateExpirationDate() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("example")
                .provider(String.valueOf(APPLE))
                .expirationDate(Instant.parse("2022-12-12T10:15:30.00Z"))
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void invalidateUserIdUserNameProviderExpirationDate() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .provider("YouTube")
                .expirationDate(Instant.parse("2022-12-12T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(dto);
        List<Integer> errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();

        assertThat(errorCodes).contains(100, 101, 102, 103);
    }
}
