package com.Guilherme.Api_Recibos.service;

import com.resend.Resend;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

import com.Guilherme.Api_Recibos.dto.*;
import com.Guilherme.Api_Recibos.repository.*;
import com.Guilherme.Api_Recibos.domain.*;

@Service
public class SistemaRecibo {

    private static final Logger logger = LoggerFactory.getLogger(SistemaRecibo.class);

    @Autowired
    private ReciboRepository reciboRepository;

    /**
     * @return true se o e-mail foi enviado com sucesso; false se não havia e-mail
     *         de destino ou se o envio falhou (o recibo já foi salvo em ambos os casos).
     */
    public boolean processarRecibo(DadosRecibo dados) {
        logger.info("Iniciando processamento do recibo N° {}", dados.servico.record);

        if (reciboRepository.findByRecord(dados.servico.record).isPresent()) {
            throw new DuplicateKeyException("Já existe um recibo com o número de ficha " + dados.servico.record);
        }

        try {
            GeradorPDF gerador = new GeradorPDF();
            byte[] pdfBytes = gerador.gerar(dados);

            logger.info("PDF gerado com sucesso para: {}", dados.cliente.nameCustomer);

            ReciboEntity novoRecibo = new ReciboEntity();
            novoRecibo.setNomeCliente(dados.cliente.nameCustomer);
            novoRecibo.setValorTotal(dados.servico.value);
            novoRecibo.setDataGeracao(LocalDateTime.now());
            novoRecibo.setRecord(dados.servico.record);
            novoRecibo.setPdfBytes(pdfBytes);

            reciboRepository.save(novoRecibo);
            logger.info("{} salvo com sucesso", dados.cliente.nameCustomer);

            String emailDestino = dados.cliente.emailCustomer;

            if (emailDestino == null || emailDestino.trim().isEmpty()) {
                logger.info("Nenhum email foi preenchido. O PDF foi gerado e salvo.");
                return false;
            }

            try {
                String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
                String nomeArquivo = "Recibo_" + dados.servico.record + ".pdf";

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
                logger.info("Email enviado com sucesso via Resend! ID: {}", resposta.getId());
                return true;

            } catch (Exception erroResend) {
                logger.error("Erro ao tentar enviar o e-mail pelo Resend: {}", erroResend.getMessage());
                return false;
            }

        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Falha ao processar o recibo N° {}", dados.servico.record, e);
            throw new RuntimeException("Falha ao processar o recibo: " + e.getMessage());
        }
    }
}