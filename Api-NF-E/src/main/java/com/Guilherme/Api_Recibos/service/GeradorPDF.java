package com.Guilherme.Api_Recibos.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.Guilherme.Api_Recibos.dto.*;
import com.Guilherme.Api_Recibos.util.Design;

public class GeradorPDF {

    /// O método recebe o pacote completo de dados
    public void gerar(DadosRecibo dados, String caminhoArquivo) throws IOException {

        Document documento = new Document(PageSize.LEGAL);
        PdfWriter.getInstance(documento, new FileOutputStream(caminhoArquivo));
        documento.open();

        montarHeader(documento, dados);
        createTable(documento, dados);
        montarGarantia(documento, dados);
        addAssinaturas(documento);

        documento.close();
    }

    private void montarHeader(Document documento, DadosRecibo dados) throws IOException {


        //Usa 'dados.empresa' em vez de 'DadosEmpresa'
        Paragraph titulo = new Paragraph("NOTA DE SERVIÇO", Design.F_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        // Subtítulo com CNPJ e Telefone
        Paragraph subtitulo = new Paragraph("CNPJ: " + dados.empresa.cnpjEnterprise + " | Contato: " + dados.empresa.phoneEnterprise, Design.F_RODAPE);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        documento.add(subtitulo);
    }

    private void createTable(Document documento, DadosRecibo dados) throws IOException {
        // Criando a Tabela Principal
        PdfPTable table = new PdfPTable(new float[]{1f, 2f});
        table.setWidthPercentage(100);

        //Variavel de formatação de data e hora
        String dataEntradaFormatada = formatarDataReactParaBR(dados.servico.inputDate);
        //Empresa
        addCabecalhoSecao(table, "Dados da Empresa");
        addLinha(table, "Razão Social:", dados.empresa.nameEnterprise);
        addLinha(table, "CNPJ:", dados.empresa.cnpjEnterprise);
        addLinha(table, "Telefone:", dados.empresa.phoneEnterprise);
        //Cliente
        addCabecalhoSecao(table, "Dados do Cliente");
        addLinha(table, "Nome:", dados.cliente.nameCustomer);
        addLinha(table, "CPF:", dados.cliente.cpfCustomer);
        addLinha(table, "Email:", dados.cliente.emailCustomer);
        addLinha(table, "Telefone:", dados.cliente.phoneCustomer);
        addLinha(table, "Endereço:", dados.cliente.addressCustomer);
        addLinha(table, "Numero: ", dados.cliente.numberCustomer);
        addLinha(table, "Cidade:", dados.cliente.cityCustomer);
        addLinha(table, "Estado:", dados.cliente.stateCustomer);
        //Serviço
        addCabecalhoSecao(table, "Dados do Conserto");
        addLinha(table, "N° Ficha:", dados.servico.record);
        addLinha(table, "Data de Entrada", dataEntradaFormatada);
        addLinha(table, "Equipamento:", dados.servico.device);
        addLinha(table, "N° Serie:", dados.servico.series);
        addLinha(table, "Reparo:", dados.servico.descriptionRepair);
        addLinha(table, "Peça:", dados.servico.replacedPart);

        addCabecalhoSecao(table, "Despesas do Serviço");
        addLinha(table, "Valor: ", "R$ " + dados.servico.value);
        addLinha(table, "Pagamento: ", dados.servico.paymentMethod);
        documento.add(table);
    }

    private void montarGarantia(Document documento, DadosRecibo dados) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1f, 2f});
        table.setWidthPercentage(100);

        String dataAtualFormatada = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        addCabecalhoSecao(table, "Garantia");
        addLinha(table, "Data Saida:", dataAtualFormatada);
        addLinha(table, "Garantia: ", dados.servico.warrantyPeriod + " dias");

        documento.add(table);

        Paragraph mensagem = new Paragraph("A garantia de " + dados.servico.warrantyPeriod + " dias cobre apenas os serviços e peças descritos neste documento. Não nos responsabilizamos por mau uso ou danos causados por picos de energia", Design.F_RODAPE);
        mensagem.setAlignment(Element.ALIGN_CENTER);
        mensagem.setSpacingAfter(3);

        documento.add(mensagem);
    }


    private void addAssinaturas(Document documento) throws DocumentException {
        /// Cria a coluna de assinaturas
        PdfPTable tabelaAssinaturas = new PdfPTable(2);
        tabelaAssinaturas.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        tabelaAssinaturas.setWidthPercentage(100);
        tabelaAssinaturas.setSpacingBefore(60f);

        PdfPCell celulaCliente = new PdfPCell();
        celulaCliente.setBorder(Rectangle.NO_BORDER);
        celulaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph linhaCliente = new Paragraph("\n\n____________________________________\nAssinatura do Cliente", Design.F_NEGRITO);
        linhaCliente.setAlignment(Element.ALIGN_CENTER);

        celulaCliente.addElement(linhaCliente);

        PdfPCell celulaEmpresa = new PdfPCell();
        celulaEmpresa.setBorder(Rectangle.NO_BORDER);
        celulaEmpresa.setHorizontalAlignment(Element.ALIGN_CENTER);

        try {
            /// IMG DA ASSINATURA DA EMPRESA
            Image imgAssinatura = Image.getInstance("src/main/resources/img/Assinatura-Digital.png");
            imgAssinatura.scaleToFit(100, 60);
            imgAssinatura.setAlignment(Element.ALIGN_CENTER);
            celulaEmpresa.addElement(imgAssinatura);

            /// IMG DO WRCODE DO WEBSITE DA LOJA
            Image imgQrcode = Image.getInstance("src/main/resources/img/Loja-Hdmr-Ar.png");
            imgQrcode.scaleToFit(60, 60);
            imgQrcode.setAlignment(Element.ALIGN_RIGHT);
            imgQrcode.setSpacingBefore(30);
            documento.add(imgQrcode);

        } catch (Exception e) {
            System.out.println("Erro ao tentar gerar imagem ao arquivo");
            Paragraph espaco = new Paragraph("\n\n\n");
            documento.add(espaco);
        }

        Paragraph linhaEmpresa = new Paragraph("____________________________________\nAssinatura do Responsável", Design.F_NEGRITO);
        linhaEmpresa.setAlignment(Element.ALIGN_CENTER);
        celulaEmpresa.addElement(linhaEmpresa);

        tabelaAssinaturas.addCell(celulaCliente);
        tabelaAssinaturas.addCell(celulaEmpresa);

        documento.add(tabelaAssinaturas);
    }

    private String formatarDataReactParaBR(String dataReact) {
        if (dataReact != null && !dataReact.trim().isEmpty()) {
            DateTimeFormatter moldeLeitura = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter moldeEscrita = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataReal = LocalDate.parse(dataReact, moldeLeitura);
            return dataReal.format(moldeEscrita);
        }
        return "";
    }

    /// Cria a faixa divisória azul clara com borda
    private void addCabecalhoSecao(PdfPTable table, String titulo) {
        PdfPCell cell = new PdfPCell(new Phrase(titulo, Design.F_SECAO));
        cell.setColspan(2);
        cell.setBackgroundColor(Design.AZUL_CLARO);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        /// MUDANÇA: Definimos uma cor de borda visível (pode ser cinza ou a cor do título)
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setBorderWidth(0.5f); // Define a grossura da linha

        table.addCell(cell);
    }

    // Cria as linhas normais de dados com bordas conectadas
    private void addLinha(PdfPTable table, String rotulo, String valor) {
        // Coluna 1 (Rótulo)
        PdfPCell c1 = new PdfPCell(new Phrase(rotulo, Design.F_NEGRITO));
        c1.setBackgroundColor(Design.CINZA_CLARO);
        c1.setPadding(6);
        c1.setBorderColor(Color.LIGHT_GRAY);
        c1.setBorderWidth(0.5f); // Garante que a linha externa apareça
        table.addCell(c1);
        // Coluna 2 (Valor)
        PdfPCell c2 = new PdfPCell(new Phrase(valor != null ? valor : "", Design.F_NORMAL));
        c2.setBackgroundColor(Design.BRANCO);
        c2.setPadding(6);
        c2.setBorderColor(Color.LIGHT_GRAY);
        c2.setBorderWidth(0.5f); // Garante que a linha externa apareça
        table.addCell(c2);
    }
}