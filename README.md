# Project Recibo Digital

Este projeto é uma aplicação full-stack desenvolvida para facilitar a geração, armazenamento e envio de recibos de serviços digitais. O sistema consiste em uma API robusta em Java (Spring Boot) e um Frontend (React).

## 🚀 Funcionalidades

*   **Geração de PDF:** Criação automática de recibos em formato PDF baseados nos dados fornecidos.
*   **Envio de E-mail:** Envia automaticamente o recibo gerado para o e-mail do cliente via SMTP (Gmail).
*   **Histórico:** Salva os registros dos recibos gerados no banco de dados.
*   **API REST:** Comunicação clara entre frontend e backend via JSON.

## 🛠️ Tecnologias Utilizadas

### Backend (`Api-Recibos`)
*   **Java**
*   **Spring Boot** (Web, Data JPA)
*   **Apache Commons Email** (Para envio de e-mails)
*   **GeradorPDF** (Classe interna para manipulação de arquivos)
*   **Maven** (Gerenciamento de dependências)

### Frontend (`Front do recibo/Recibo-Digital`)
*   **React.js**
*   **Node.js**

## ⚙️ Configuração e Instalação

### Pré-requisitos
*   Java JDK 11 ou superior.
*   Maven instalado.
*   Node.js e npm instalados.

### 1. Configurando o Backend

O backend é responsável pela lógica de negócios e envio de e-mails.

1.  Navegue até a pasta do backend:
    ```bash
    cd "Api-Recibos"
    ```
2.  **Configuração de E-mail:**
    O sistema utiliza o Gmail para envio. Verifique o arquivo `src/main/java/com/Guilherme/Api_Recibos/SistemaRecibo.java`.
    *   Certifique-se de que as credenciais de e-mail e senha de aplicativo (App Password) estão corretas no método `processarRecibo`.

3.  Execute a aplicação Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
    O servidor iniciará (padrão geralmente na porta `8080`).

### 2. Configurando o Frontend

1.  Navegue até a pasta do frontend:
    ```bash
    cd "Front do recibo/Recibo-Digital"
    ```
2.  Instale as dependências:
    ```bash
    npm install
    ```
3.  Inicie o servidor de desenvolvimento:
    ```bash
    npm start
    ```

## 📡 Endpoints da API

### Gerar Recibo

*   **URL:** `/api/recibos`
*   **Método:** `POST`
*   **Corpo da Requisição (JSON Exemplo):**
    ```json
    {
      "cliente": { "nameCustomer": "Nome do Cliente", "emailCustomer": "cliente@email.com" },
      "servico": { "value": 150.00, "descricao": "Manutenção de Ar Condicionado" }
    }
    ```
*   **Resposta:** "email enviado com sucesso" ou mensagem de erro.