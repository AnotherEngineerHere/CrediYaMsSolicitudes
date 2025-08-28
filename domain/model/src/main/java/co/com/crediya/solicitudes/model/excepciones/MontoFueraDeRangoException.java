package co.com.crediya.solicitudes.model.excepciones;

public class MontoFueraDeRangoException extends RuntimeException {
  public MontoFueraDeRangoException(String message) {
    super(message);
  }
}
