package co.com.crediya.solicitudes.model.excepciones;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
