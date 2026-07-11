package com.Guilherme.Api_Recibos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import com.Guilherme.Api_Recibos.dto.*;
import com.Guilherme.Api_Recibos.repository.*;
import com.Guilherme.Api_Recibos.service.*;
import com.Guilherme.Api_Recibos.domain.*;

@RestController
@RequestMapping("/api/recibos") // Define o caminho
public class ReciboController {

    private static final Logger logger = LoggerFactory.getLogger(ReciboController.class);

    @Autowired
    private SistemaRecibo sistemaRecibo; // chama minha regra de negocio

    @Autowired
    private ReciboRepository reciboRepository;

    @Value("${api.access.token}")
    private String accessToken;

    @PostMapping // quando o react fizer um post para /api/recibos aciona este metodo
    public ResponseEntity<String> gerarrecibo(@RequestBody DadosRecibo dados) {
        try {
            logger.info("Recebido pedido do react para a ficha N° {}", dados.servico.record);

            boolean emailEnviado = sistemaRecibo.processarRecibo(dados);

            String mensagem = emailEnviado
                    ? "Recibo salvo e email enviado com sucesso"
                    : "Recibo salvo com sucesso (email não enviado)";

            return ResponseEntity.ok(mensagem);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao processar recibo", e);
            return ResponseEntity.internalServerError().body("Erro ao processar o recibo. Tente novamente.");
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("API de recibos funcionando!");
    }

    /**
     * Rota responsável por buscar um recibo no banco de dados e enviá-lo para download no Front-end.
     * Exige o header X-Api-Key para evitar que qualquer pessoa baixe recibos de terceiros
     * apenas sabendo o número de ficha.
     * Exemplo de URL de acesso: GET /api/recibos/download/123
     */
    @GetMapping("/download/{record}")
    public ResponseEntity<byte[]> downloadRecibo(@PathVariable String record,
                                                  @RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<ReciboEntity> reciboOpt = reciboRepository.findByRecord(record);

        if (reciboOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ReciboEntity recibo = reciboOpt.get();
        byte[] conteudo = recibo.getPdfBytes(); // direto, sem decodificar Base64

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Recibo_" + record + ".pdf");
        headers.setContentLength(conteudo.length);

        return new ResponseEntity<>(conteudo, headers, HttpStatus.OK);
    }
}