package com.Guilherme.Api_Recibos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import com.Guilherme.Api_Recibos.dto.*;
import com.Guilherme.Api_Recibos.repository.*;
import com.Guilherme.Api_Recibos.service.*;
import  com.Guilherme.Api_Recibos.domain.*;


@RestController
@RequestMapping("/api/recibos")//Define o caminho
public class ReciboController {

    @Autowired
    private SistemaRecibo sistemaRecibo; //chama minha regra de negocio

    @Autowired
    private ReciboRepository reciboRepository;

    @PostMapping //quando o react fizer um post para /api/recibos aciona este metodo
    public ResponseEntity<String> gerarrecibo(@RequestBody DadosRecibo dados) {
        try {
            System.out.println("Recebido pedido do react para: " + dados);

            //vai gerar o pdf e enviar
            sistemaRecibo.processarRecibo(dados);

            return ResponseEntity.ok("email enviado com sucesso");
        } catch (
                Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao processar: " + e.getMessage());
        }
    }

    /**
     * Rota responsável por buscar um recibo no banco de dados,
     * localizar o arquivo físico no servidor e enviá-lo para download no Front-end.
     * Exemplo de URL de acesso: GET /api/recibos/download/123
     */
    @GetMapping("/download/{record}")
    public ResponseEntity<byte[]> downloadRecibo(@PathVariable String record) {
        try {
            // Busca no banco de dados a ficha solicitada.
            // Usamos Optional para evitar erros (NullPointerException) caso a ficha não exista.
            Optional<ReciboEntity> reciboOpt = reciboRepository.findByRecord(record);

            // Se o banco de dados não encontrar a ficha, devolvemos o erro HTTP 404 (Not Found)
            if (reciboOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Extrai os dados do recibo do banco e prepara a busca no disco rígido do servidor
            ReciboEntity recibo = reciboOpt.get();
            File arquivo = new File(recibo.getCaminhoArquivo());

            // Dupla checagem de segurança: verifica se o arquivo físico realmente está na pasta
            // (evita falhas caso o arquivo tenha sido deletado manualmente do computador)
            if (!arquivo.exists()) {
                System.out.println("Arquivo PDF não encontrado no disco: " + recibo.getCaminhoArquivo());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Lê o arquivo físico (PDF) e o converte em um array de bytes
            // Isso é necessário porque arquivos trafegam pela internet em formato binário
            byte[] conteudo = Files.readAllBytes(arquivo.toPath());

            // Configura as instruções para o navegador do cliente (Headers)
            HttpHeaders headers = new HttpHeaders();
            // Avisa que o conteúdo é um PDF
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Força o navegador a abrir a janela de "Salvar como..." (download em anexo) com o nome original
            headers.setContentDispositionFormData("attachment", arquivo.getName());
            // Informa o tamanho do arquivo para a barra de progresso do download funcionar
            headers.setContentLength(conteudo.length);

            // Log de sucesso no terminal do servidor
            System.out.println("Download do recibo ficha N° " + record + " realizado com sucesso.");

            // Empacota os bytes, as configurações (headers) e o status 200 (OK) e despacha a resposta
            return new ResponseEntity<>(conteudo, headers, HttpStatus.OK);

        } catch (IOException e) {
            // Captura falhas de leitura no disco (ex: falta de permissão do Windows ou arquivo corrompido)
            // Imprime o erro no console e devolve o status HTTP 500 (Internal Server Error)
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}