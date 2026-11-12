package co.com.crediya.model.transaction.gateways;

import reactor.core.publisher.Mono;

/**
 * Puerto que permite al dominio ejecutar un flujo reactivo de forma atómica
 * sin depender de Spring ni de la tecnología de persistencia.
 */
public interface TransactionalGateway {
    <T> Mono<T> execute(Mono<T> operation);
}
