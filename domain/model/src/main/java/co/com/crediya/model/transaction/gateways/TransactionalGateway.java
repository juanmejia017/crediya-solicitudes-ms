package co.com.crediya.model.transaction.gateways;

import java.util.function.Supplier;

/**
 * Puerto que permite al dominio ejecutar una operación de forma atómica
 * sin depender de Spring ni de la tecnología de persistencia.
 */
public interface TransactionalGateway {
    <T> T execute(Supplier<T> operation);
}
