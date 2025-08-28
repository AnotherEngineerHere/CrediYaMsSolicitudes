package co.com.crediya.solicitudes.model.excepciones;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
