package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.dmdev.entity.Provider.APPLE;
import static com.dmdev.entity.Provider.GOOGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    private Clock clock;
    @Mock
    private SubscriptionDao subscriptionDao;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void upsertSuccess() {
        CreateSubscriptionDto subscriptionDto = getCreateSubscriptionDto();
        Subscription expectedResult = getSubscription();
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
        doReturn(List.of(expectedResult)).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(expectedResult).when(subscriptionDao).upsert(expectedResult);

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(createSubscriptionValidator).validate(subscriptionDto);
        verify(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        verify(subscriptionDao).upsert(expectedResult);
    }

    @Test
    void upsertSuccessWithProvider() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("example")
                .provider(String.valueOf(GOOGLE))
                .expirationDate(Instant.parse("2023-12-12T00:00:00.00Z"))
                .build();
        Subscription subscription = spy(getSubscription());
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
        doReturn(List.of(subscription)).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(subscription).when(createSubscriptionMapper).map(subscriptionDto);
        doReturn(subscription).when(subscriptionDao).upsert(subscription);

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);

        verify(subscription, times(1)).getProvider();
    }

    @Test
    void cancel() {
        Subscription expectedResult = getSubscription();
        doReturn(Optional.of(expectedResult)).when(subscriptionDao).findById(expectedResult.getId());

        subscriptionService.cancel(expectedResult.getId());

        verify(subscriptionDao).findById(expectedResult.getId());
        verify(subscriptionDao).update(expectedResult);
    }

    private static Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .userId(1)
                .name("example")
                .provider(APPLE)
                .expirationDate(Instant.parse("2023-12-12T10:15:30.00Z"))
                .status(Status.ACTIVE)
                .build();
    }

    private static CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("example")
                .provider(String.valueOf(APPLE))
                .expirationDate(Instant.parse("2023-12-12T00:00:00.00Z"))
                .build();
    }
}
