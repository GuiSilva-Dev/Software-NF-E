package com.Guilherme.Api_Recibos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// O scanBasePackages garante explicitamente que todos os diretórios do projeto (como controller e config) sejam lidos.
// Além disso, esta classe estar na raiz do pacote 'com.Guilherme.Api_Recibos' (e não dentro da pasta oculta Api) é 
// fundamental para que as anotações do Spring Boot funcionem em todas as outras pastas.
@SpringBootApplication(scanBasePackages = "com.Guilherme.Api_Recibos")
@EnableAsync
public class ApiRecibosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRecibosApplication.class, args);
    }

}
