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
                // Restringe às origens conhecidas do front-end (produção + dev local do Vite)
                .allowedOriginPatterns(
                        "https://aquamarine-sprite-f048cf.netlify.app",
                        "http://localhost:5173"
                )
                // Autoriza o front-end a fazer requisições usando qualquer um desses métodos HTTP
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Cabeçalhos usados pelo front-end (JSON + chave de acesso)
                .allowedHeaders("Content-Type", "X-Api-Key")
                // EXPÕE o cabeçalho 'Content-Disposition' para o navegador ler. Isso é crucial
                // para a funcionalidade de DOWNLOAD DE PDF, pois o React precisa extrair o nome do arquivo.
                .exposedHeaders("Content-Disposition");
    }
}
