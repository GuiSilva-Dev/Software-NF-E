package com.Guilherme.Api_Recibos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Indica que estas regras de CORS se aplicam a todas as rotas da API (/**)
        registry.addMapping("/**")
                // Permite requisições de qualquer origem (seja localhost, um domínio de produção, etc.)
                .allowedOriginPatterns("*") 
                // Autoriza o front-end a fazer requisições usando qualquer um desses métodos HTTP
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                // Permite o envio de quaisquer cabeçalhos na requisição
                .allowedHeaders("*")
                // Permite o uso de credenciais (cookies, headers de autenticação, etc)
                .allowCredentials(true)
                // EXPÕE o cabeçalho 'Content-Disposition' para o navegador ler. Isso é crucial
                // para a funcionalidade de DOWNLOAD DE PDF, pois o React precisa extrair o nome do arquivo.
                .exposedHeaders("Content-Disposition"); 
    }
}
