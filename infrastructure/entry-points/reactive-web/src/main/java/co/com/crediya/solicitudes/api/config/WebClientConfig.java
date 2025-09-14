package co.com.crediya.solicitudes.api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuración de WebClient para comunicación inter-servicio.
 * Proporciona configuración optimizada para llamadas HTTP a otros microservicios.
 *
 * @author CrediYa Development Team
 * @version 1.0
 * @since 2024
 */
@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${webclient.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${webclient.read-timeout:10000}")
    private int readTimeout;

    @Value("${webclient.write-timeout:10000}")
    private int writeTimeout;

    /**
     * Configura un WebClient con timeouts y manejo de errores optimizado.
     *
     * @return WebClient configurado para comunicación inter-servicio
     */
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .filter((request, next) -> {
                    log.debug("WebClient Request: {} {}", request.method(), request.url());
                    return next.exchange(request)
                            .doOnNext(response -> {
                                log.debug("WebClient Response: {} for {}", response.statusCode(), request.url());
                            })
                            .doOnError(error -> {
                                log.error("WebClient Error for {}: {}", request.url(), error.getMessage());
                            });
                })
                .build();
    }
}