package cl.duoc.veterinaria.ordenes.config;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP para llamar a otros microservicios.
 * Con @LoadBalanced, una URL como http://ms-productos se resuelve preguntando a Eureka
 * dónde está ese servicio (dirección y puerto reales).
 */
@Configuration
public class ClienteHttpConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(RestClientBuilderConfigurer configurer) {
        return configurer.configure(RestClient.builder());
    }
}
