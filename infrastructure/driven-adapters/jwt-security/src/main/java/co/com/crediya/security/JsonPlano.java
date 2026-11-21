package co.com.crediya.security;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lee y escribe objetos JSON planos (sin objetos ni listas anidadas), que es todo lo que
 * necesita el header y el payload del token. Solo se lee JSON que la firma ya validó.
 */
final class JsonPlano {

    private JsonPlano() {
    }

    static String escribir(Map<String, Object> valores) {
        StringBuilder json = new StringBuilder("{");
        valores.forEach((clave, valor) -> {
            if (valor == null) {
                return;
            }
            if (json.length() > 1) {
                json.append(',');
            }
            json.append(texto(clave)).append(':');
            json.append(valor instanceof Number ? valor.toString() : texto(valor.toString()));
        });
        return json.append('}').toString();
    }

    static Map<String, Object> leer(String json) {
        Lector lector = new Lector(json.trim());
        return lector.objeto();
    }

    private static String texto(String valor) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }

    private static final class Lector {
        private final String json;
        private int pos;

        private Lector(String json) {
            this.json = json;
        }

        private Map<String, Object> objeto() {
            Map<String, Object> valores = new LinkedHashMap<>();
            esperar('{');
            espacios();
            if (ver() == '}') {
                pos++;
                return terminar(valores);
            }
            while (true) {
                espacios();
                String clave = cadena();
                espacios();
                esperar(':');
                espacios();
                valores.put(clave, valor());
                espacios();
                char c = siguiente();
                if (c == '}') {
                    return terminar(valores);
                }
                if (c != ',') {
                    throw new IllegalArgumentException("JSON inválido en la posición " + pos);
                }
            }
        }

        private Map<String, Object> terminar(Map<String, Object> valores) {
            espacios();
            if (pos != json.length()) {
                throw new IllegalArgumentException("JSON con contenido sobrante");
            }
            return valores;
        }

        private Object valor() {
            char c = ver();
            if (c == '"') {
                return cadena();
            }
            if (json.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            if (json.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            }
            if (json.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            int inicio = pos;
            while (pos < json.length() && "-0123456789".indexOf(json.charAt(pos)) >= 0) {
                pos++;
            }
            if (inicio == pos) {
                throw new IllegalArgumentException("Valor JSON no soportado en la posición " + pos);
            }
            return Long.parseLong(json.substring(inicio, pos));
        }

        private String cadena() {
            esperar('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = siguiente();
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char e = siguiente();
                switch (e) {
                    case '"', '\\', '/' -> sb.append(e);
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        sb.append((char) Integer.parseInt(json.substring(pos, pos + 4), 16));
                        pos += 4;
                    }
                    default -> throw new IllegalArgumentException("Escape JSON inválido");
                }
            }
        }

        private void espacios() {
            while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
                pos++;
            }
        }

        private void esperar(char esperado) {
            if (siguiente() != esperado) {
                throw new IllegalArgumentException("Se esperaba '" + esperado + "' en la posición " + (pos - 1));
            }
        }

        private char ver() {
            if (pos >= json.length()) {
                throw new IllegalArgumentException("JSON incompleto");
            }
            return json.charAt(pos);
        }

        private char siguiente() {
            char c = ver();
            pos++;
            return c;
        }
    }
}
