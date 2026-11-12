package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.transaction.gateways.TransactionalGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitudUseCaseTest {

    private FakeSolicitudRepository solicitudRepository;
    private FakeTipoPrestamoRepository tipoPrestamoRepository;
    private FakeEstadoRepository estadoRepository;
    private FakeTransactionalGateway transactionalGateway;
    private SolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudRepository = new FakeSolicitudRepository();
        tipoPrestamoRepository = new FakeTipoPrestamoRepository();
        estadoRepository = new FakeEstadoRepository();
        transactionalGateway = new FakeTransactionalGateway();

        tipoPrestamoRepository.tipos.put(1L, TipoPrestamo.builder()
                .idTipoPrestamo(1L).nombre("Libre inversión")
                .montoMinimo(new BigDecimal("1000000")).montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("1.80")).validacionAutomatica(true)
                .build());
        estadoRepository.estados.put(Estado.PENDIENTE_REVISION,
                Estado.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).descripcion("Inicial").build());

        useCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadoRepository, transactionalGateway);
    }

    private Solicitud.SolicitudBuilder solicitudValida() {
        return Solicitud.builder()
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .email(" Juan@Correo.com ")
                .documentoIdentidad("1020304050")
                .idTipoPrestamo(1L);
    }

    @Test
    void registraSolicitudConEstadoPendienteDeRevision() {
        Solicitud creada = useCase.registrar(solicitudValida().build());

        assertEquals(1L, creada.getIdSolicitud());
        assertEquals(1L, creada.getIdEstado());
        assertEquals(Estado.PENDIENTE_REVISION, creada.getEstado());
        assertEquals("Libre inversión", creada.getTipoPrestamo());
        assertEquals("juan@correo.com", creada.getEmail());
        assertEquals(1, solicitudRepository.guardadas.size());
    }

    @Test
    void ignoraEstadoEnviadoPorElClienteYUsaElInicial() {
        Solicitud creada = useCase.registrar(solicitudValida().idEstado(99L).idSolicitud(50L).build());

        assertEquals(1L, creada.getIdEstado());
        assertEquals(1L, creada.getIdSolicitud());
    }

    @Test
    void ejecutaElRegistroDentroDeUnaTransaccion() {
        useCase.registrar(solicitudValida().build());

        assertEquals(1, transactionalGateway.ejecuciones);
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        Solicitud vacia = Solicitud.builder().email(" ").build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> useCase.registrar(vacia));

        assertEquals(5, ex.getErrores().size());
        assertEquals(0, transactionalGateway.ejecuciones);
        assertTrue(solicitudRepository.guardadas.isEmpty());
    }

    @Test
    void rechazaFormatosInvalidos() {
        Solicitud invalida = solicitudValida()
                .monto(new BigDecimal("-1"))
                .plazo(0)
                .email("correo-invalido")
                .documentoIdentidad("12")
                .idTipoPrestamo(-3L)
                .build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> useCase.registrar(invalida));

        assertEquals(5, ex.getErrores().size());
    }

    @Test
    void rechazaCuerpoNulo() {
        assertThrows(DatosInvalidosException.class, () -> useCase.registrar(null));
    }

    @Test
    void rechazaTipoDePrestamoInexistente() {
        Solicitud solicitud = solicitudValida().idTipoPrestamo(77L).build();

        assertThrows(TipoPrestamoNoExisteException.class, () -> useCase.registrar(solicitud));
        assertTrue(solicitudRepository.guardadas.isEmpty());
    }

    @Test
    void rechazaMontoFueraDelRangoDelTipo() {
        Solicitud solicitud = solicitudValida().monto(new BigDecimal("900000000")).build();

        assertThrows(MontoFueraDeRangoException.class, () -> useCase.registrar(solicitud));
        assertTrue(solicitudRepository.guardadas.isEmpty());
    }

    @Test
    void fallaConErrorTecnicoSiNoExisteElEstadoInicial() {
        estadoRepository.estados.clear();
        Solicitud solicitud = solicitudValida().build();

        assertThrows(TechnicalException.class, () -> useCase.registrar(solicitud));
        assertTrue(solicitudRepository.guardadas.isEmpty());
    }

    @Test
    void noGuardaDatosDeLecturaComoEstadoOTipo() {
        useCase.registrar(solicitudValida().estado("Aprobada").build());

        assertNull(solicitudRepository.guardadas.get(0).getEstado());
    }

    private static class FakeSolicitudRepository implements SolicitudRepository {
        private final List<Solicitud> guardadas = new ArrayList<>();

        @Override
        public Solicitud save(Solicitud solicitud) {
            guardadas.add(solicitud);
            return solicitud.toBuilder().idSolicitud((long) guardadas.size()).build();
        }
    }

    private static class FakeTipoPrestamoRepository implements TipoPrestamoRepository {
        private final Map<Long, TipoPrestamo> tipos = new HashMap<>();

        @Override
        public Optional<TipoPrestamo> findByIdTipoPrestamo(Long idTipoPrestamo) {
            return Optional.ofNullable(tipos.get(idTipoPrestamo));
        }
    }

    private static class FakeEstadoRepository implements EstadoRepository {
        private final Map<String, Estado> estados = new HashMap<>();

        @Override
        public Optional<Estado> findByNombre(String nombre) {
            return Optional.ofNullable(estados.get(nombre));
        }
    }

    private static class FakeTransactionalGateway implements TransactionalGateway {
        private int ejecuciones;

        @Override
        public <T> T execute(Supplier<T> operation) {
            ejecuciones++;
            return operation.get();
        }
    }
}
