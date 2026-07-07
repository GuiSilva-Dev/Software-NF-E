package com.Guilherme.Api_Recibos.service;

import com.resend.Resend;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

import com.Guilherme.Api_Recibos.dto.*;
import com.Guilherme.Api_Recibos.repository.*;
import com.Guilherme.Api_Recibos.domain.*;

@Service // Avisa o Spring para gerenciar a classe
public class SistemaRecibo {

    @Autowired
    private ReciboRepository Repository;

    public void processarRecibo(DadosRecibo dados) {
        try { // INÍCIO DO TRY PRINCIPAL
            System.out.println("Iniciando processo");

            String nomeDoBanco = "Pdfs_gerados/recibosdb";
            Path caminhoDaPasta = Paths.get(nomeDoBanco);

            if (!Files.exists(caminhoDaPasta)) {
                Files.createDirectories(caminhoDaPasta);
            }

            String nomeArquivo = "Recibo_" + dados.servico.record + ".pdf";
            String caminhoCompleto = caminhoDaPasta + "/" + nomeArquivo;

            System.out.println("Iniciando a geração do PDF...");

            GeradorPDF gerador = new GeradorPDF();
            gerador.gerar(dados, caminhoCompleto);

            System.out.println("PDF gerado com sucesso para: " + dados.cliente.nameCustomer);

            // SALVA NO BANCO
            ReciboEntity novoRecibo = new ReciboEntity();
            novoRecibo.setNomeCliente(dados.cliente.nameCustomer);
            novoRecibo.setValorTotal(dados.servico.value);
            novoRecibo.setDataGeracao(LocalDateTime.now());
            novoRecibo.setCaminhoArquivo(caminhoCompleto);
            novoRecibo.setRecord(dados.servico.record);

            Repository.save(novoRecibo);
            System.out.println(dados.cliente.nameCustomer + " salvo com sucesso");

            // --- ENVIAR POR EMAIL O PDF (VIA RESEND) ---
            String emailDestino = dados.cliente.emailCustomer;

            if (emailDestino != null && !emailDestino.trim().isEmpty()) {
                try { // INÍCIO DO TRY DO RESEND
                    byte[] pdfBytes = Files.readAllBytes(Paths.get(caminhoCompleto));
                    String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

                    Attachment anexoResend = Attachment.builder()
                            .fileName(nomeArquivo)
                            .content(pdfBase64)
                            .build();

                    String chave = System.getenv("CHAVE_EMAIL");
                    Resend resend = new Resend(chave);

                    CreateEmailOptions parametros = CreateEmailOptions.builder()
                            .from("Recibos Hdmr Ar <onboarding@resend.dev>")
                            .to(emailDestino)
                            .subject("Recibo de Serviço Hdmr Ar - " + dados.cliente.nameCustomer)
                            .html("<p>Olá <strong>" + dados.cliente.nameCustomer + "</strong>,</p><p>Segue em anexo o seu recibo digital referente ao serviço prestado.</p><p>Obrigado pela preferência!</p>")
                            .attachments(anexoResend)
                            .build();

                    CreateEmailResponse resposta = resend.emails().send(parametros);
                    System.out.println("Email enviado com sucesso via Resend! ID: " + resposta.getId());

                } catch (Exception erroResend) {
                    System.err.println("Erro ao tentar enviar o e-mail pelo Resend: " + erroResend.getMessage());
                } // FIM DO TRY/CATCH DO RESEND

                //email.attach(anexo);
                //email.send();

                //System.out.println("Email enviado com sucesso para: " + emailDestino);
            } else {
                System.out.println("Nenhum email foi preenchido. O PDF foi gerado e salvo.");
            }

        } catch (Exception e) { // ESSE ERA O CATCH QUE FALTAVA
            e.printStackTrace();
            throw new RuntimeException("Falha ao processar o recibo: " + e.getMessage());
        }
    }
}

