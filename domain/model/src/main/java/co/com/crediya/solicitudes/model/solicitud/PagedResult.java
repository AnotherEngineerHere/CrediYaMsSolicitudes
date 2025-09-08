package co.com.crediya.solicitudes.model.solicitud;

import lombok.Value;
import java.util.List;

@Value
public class PagedResult<T> {
    List<T> items;
    long total;
    int page;
    int size;
}