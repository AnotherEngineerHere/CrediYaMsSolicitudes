package co.com.crediya.solicitudes.model.excepciones;

public class TipoPrestamoNoEncontradoException extends RuntimeException {
    public TipoPrestamoNoEncontradoException(String message) {
        super(message);
    }
}
