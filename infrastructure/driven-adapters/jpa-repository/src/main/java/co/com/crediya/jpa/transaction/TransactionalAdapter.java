package co.com.crediya.jpa.transaction;

import co.com.crediya.model.transaction.gateways.TransactionalGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Implementa el puerto TransactionalGateway con el manejador de transacciones de JPA.
 * Si la operación lanza una excepción, todos los cambios se revierten (rollback).
 */
@Component
public class TransactionalAdapter implements TransactionalGateway {

    private final TransactionTemplate transactionTemplate;

    public TransactionalAdapter(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T execute(Supplier<T> operation) {
        return transactionTemplate.execute(status -> operation.get());
    }
}
