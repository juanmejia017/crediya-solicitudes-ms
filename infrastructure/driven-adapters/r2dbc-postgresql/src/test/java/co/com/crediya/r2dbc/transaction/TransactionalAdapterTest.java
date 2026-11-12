package co.com.crediya.r2dbc.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionalAdapterTest {

    @Test
    void ejecutaLaOperacionDentroDelTransactionalOperator() {
        TransactionalOperator operator = mock(TransactionalOperator.class);
        when(operator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TransactionalAdapter adapter = new TransactionalAdapter(operator);

        StepVerifier.create(adapter.execute(Mono.just("ok")))
                .expectNext("ok")
                .verifyComplete();

        verify(operator).transactional(any(Mono.class));
    }
}
