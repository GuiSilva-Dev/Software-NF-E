package com.Guilherme.Api_Recibos.service;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import com.Guilherme.Api_Recibos.dto.*;
import com.Guilherme.Api_Recibos.repository.*;
import com.Guilherme.Api_Recibos.domain.*;

@Service // Avisa o Spring para gerenciar a classe
public class SistemaRecibo {

    @Autowired
    private ReciboRepository Repository;

    //Recebe o (DadosRecibo) inteiro!
    public void processarRecibo(DadosRecibo dados) {
        try {
            System.out.println("Iniciando processo");

            /// CRIA A VARIAVEIS DO BANCO E DO CAMINHO COMPLETO PRO BANCO
            /// nome da pasta para 'pdfs_salvos' para não misturar o arquivo do banco com os PDFs
            String nomeDoBanco = "Pdfs_gerados/recibosdb";
            Path caminhoDaPasta = Paths.get(nomeDoBanco);

            /// SE A PASTA NAO EXISTIR, CRIA UMA
            if (!Files.exists(caminhoDaPasta)) {
                Files.createDirectory(caminhoDaPasta);
            }

            // 2. Pega o nome do cliente diretamente da "gaveta" de dados
            String nomeArquivo = "Recibo_" + dados.servico.record + ".pdf";
            String caminhoCompleto = caminhoDaPasta + "/" + nomeArquivo;

            System.out.println("Iniciando a geração do PDF...");

            //Passa as gavetas que vieram do React direto para o PDF.
            GeradorPDF gerador = new GeradorPDF();
            // Lembre-se que o seu GeradorPDF agora espera (DadosRecibo, String)
            gerador.gerar(dados, caminhoCompleto);

            System.out.println("PDF gerado com sucesso para: " + dados.cliente.nameCustomer);
            /// SALVA NO BANCO
            ReciboEntity novoRecibo = new ReciboEntity();
            novoRecibo.setNomeCliente(dados.cliente.nameCustomer);
            novoRecibo.setValorTotal(dados.servico.value);
            novoRecibo.setDataGeracao(LocalDateTime.now());
            novoRecibo.setCaminhoArquivo(caminhoCompleto);
            novoRecibo.setRecord(dados.servico.record);

            Repository.save(novoRecibo);
            System.out.println("E-mail enviado");

            System.out.println(dados.cliente.nameCustomer + " salvo com sucesso");

            // --- ENVIAR POR EMAIL O PDF ---
            String emailDestino = dados.cliente.emailCustomer;

            // Só tenta enviar se o cliente realmente digitou um email no React
            if (emailDestino != null && !emailDestino.trim().isEmpty()) {

                EmailAttachment anexo = new EmailAttachment();
                anexo.setPath(caminhoCompleto);
                anexo.setDisposition(EmailAttachment.ATTACHMENT);
                anexo.setName(nomeArquivo);

                MultiPartEmail email = new MultiPartEmail();
                email.setHostName("smtp.gmail.com");
                email.setSmtpPort(587); // Porta do TLS

                email.setAuthentication("Guih.bittencourt23@gmail.com", "ldzfgjylkmoubslo");
                email.setStartTLSEnabled(true);

                email.addTo(emailDestino);
                email.setFrom("Guih.bittencourt23@gmail.com", "Hdmr Ar"); // Remetente
                email.setSubject("Recibo de Serviço Hdmr Ar - " + dados.cliente.nameCustomer);
                email.setMsg("Olá " + dados.cliente.nameCustomer + ",\n\nSegue em anexo o seu recibo digital referente ao serviço prestado.\n\nObrigado pela preferência!");

                email.attach(anexo);
                email.send();

                System.out.println("Email enviado com sucesso para: " + emailDestino);
            } else {
                System.out.println("Nenhum email foi preenchido. O PDF foi gerado e salvo no computador, mas não foi enviado por email.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Avisa o Controller que deu erro
            throw new RuntimeException("Falha ao processar o recibo: " + e.getMessage());
        }
    }

}