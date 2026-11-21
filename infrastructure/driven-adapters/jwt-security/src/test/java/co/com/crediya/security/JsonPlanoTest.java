package co.com.crediya.security;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonPlanoTest {

    @Test
    void escribeYLeeTextosNumerosYEscapes() {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("sub", "ana \"la\" \\ grande");
        valores.put("exp", 1790000000L);
        valores.put("documento", null);

        String json = JsonPlano.escribir(valores);
        Map<String, Object> leido = JsonPlano.leer(json);

        assertEquals("{\"sub\":\"ana \\\"la\\\" \\\\ grande\",\"exp\":1790000000}", json);
        assertEquals("ana \"la\" \\ grande", leido.get("sub"));
        assertEquals(1790000000L, leido.get("exp"));
    }

    @Test
    void rechazaJsonMalFormado() {
        assertThrows(IllegalArgumentException.class, () -> JsonPlano.leer("{\"a\":"));
        assertThrows(IllegalArgumentException.class, () -> JsonPlano.leer("{\"a\":1} extra"));
        assertThrows(IllegalArgumentException.class, () -> JsonPlano.leer("[1,2]"));
    }
}
