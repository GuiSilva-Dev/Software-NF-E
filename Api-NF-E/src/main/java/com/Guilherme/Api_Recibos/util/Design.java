package com.Guilherme.Api_Recibos.util;

import com.lowagie.text.Font;

import java.awt.Color;

public final class Design {

    private Design() {
    }

    // --- CORES ---
    public static final Color AZUL_ESCURO = new Color(0, 51, 102);
    public static final Color AZUL_CLARO = new Color(224, 240, 255); // Fundo para separar as seções
    public static final Color CINZA_CLARO = new Color(245, 245, 245); // Fundo suave para as linhas
    public static final Color BRANCO = new Color(255, 255, 255);

    // --- FONTES ---
    // Título principal do documento
    public static final Font F_TITULO = new Font(Font.HELVETICA, 22, Font.BOLD, AZUL_ESCURO);

    // Títulos das divisões (Cliente, Serviço, etc)
    public static final Font F_SECAO = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);

    // Textos comuns
    public static final Font F_NORMAL = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
    public static final Font F_NEGRITO = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);

    // Textos pequenos (Rodapé e Garantia)
    public static final Font F_RODAPE = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY);
}