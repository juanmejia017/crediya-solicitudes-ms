package co.com.crediya.jpa.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionalAdapterTest {

    @Mock
    private PlatformTransactionManager transactionManager;

    private TransactionalAdapter adapter;
    private TransactionStatus status;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        status = new SimpleTransactionStatus();
        when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(status);
        adapter = new TransactionalAdapter(transactionManager);
    }

    @Test
    void confirmaLaTransaccionCuandoLaOperacionTerminaBien() {
        String resultado = adapter.execute(() -> "ok");

        assertEquals("ok", resultado);
        verify(transactionManager).commit(status);
    }

    @Test
    void revierteLaTransaccionCuandoLaOperacionFalla() {
        assertThrows(IllegalStateException.class, () -> adapter.execute(() -> {
            throw new IllegalStateException("fallo");
        }));

        verify(transactionManager).rollback(status);
        verify(transactionManager, never()).commit(status);
    }
}
