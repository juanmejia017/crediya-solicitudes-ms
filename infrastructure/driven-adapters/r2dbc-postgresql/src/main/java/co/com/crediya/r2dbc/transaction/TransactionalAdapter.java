package co.com.crediya.r2dbc.transaction;

import co.com.crediya.model.transaction.gateways.TransactionalGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

/**
 * Implementa el puerto TransactionalGateway con el TransactionalOperator de R2DBC.
 * Si alguna señal del flujo termina en error, todos los cambios se revierten (rollback).
 */
@Component
@RequiredArgsConstructor
public class TransactionalAdapter implements TransactionalGateway {

    private final TransactionalOperator transactionalOperator;

    @Override
    public <T> Mono<T> execute(Mono<T> operation) {
        return transactionalOperator.transactional(operation);
    }
}
