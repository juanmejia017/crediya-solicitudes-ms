package co.com.crediya.api;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

/**
 * BlockHound (activado por el plugin en proyectos reactivos) marca como bloqueante la escritura
 * de logs en consola. En las pruebas se permite solo dentro del framework de logging.
 */
public class BlockHoundLoggingIntegration implements BlockHoundIntegration {
    @Override
    public void applyTo(BlockHound.Builder builder) {
        builder.allowBlockingCallsInside("ch.qos.logback.classic.Logger", "callAppenders");
        builder.allowBlockingCallsInside("org.apache.logging.log4j.core.Logger", "logMessage");
    }
}
