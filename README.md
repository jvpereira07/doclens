# 🔍 DocLens

O **DocLens** é uma plataforma moderna e de alto desempenho desenvolvida em **Spring Boot** para a extração inteligente e estruturada de dados de documentos físicos ou digitais (como RGs, CNHs, comprovantes, etc.) utilizando inteligência artificial multimodal através da **API do Gemini** e armazenamento de arquivos em **MinIO**.

Com o DocLens, você pode criar templates de documentos dinamicamente, definir quais campos precisam ser extraídos (com seus respectivos tipos e regras) e enviar arquivos para que a IA faça a leitura e retorne os dados estruturados de forma precisa no formato JSON.

---

## ✨ Funcionalidades Principais

- **Gerenciamento de Templates Dinâmicos**: Crie, edite e ative templates de documentos especificando as chaves que a inteligência artificial deve procurar e extrair.
- **Extração Multimodal**: Envio direto de imagens e arquivos de documentos para a API do Gemini (`gemini-2.5-flash`), extraindo as informações solicitadas em JSON nativo.
- **Armazenamento de Arquivos com MinIO**: Os arquivos enviados são automaticamente salvos de forma organizada em buckets de armazenamento compatível com S3 (MinIO).
- **Interface Monocromática Moderna**: Painel web com tema minimalista escuro (monocromático) contendo feedbacks semânticos pontuais para status.
- **Histórico de Extrações**: Histórico interativo contendo a data, o arquivo enviado, uma miniatura da imagem (com link para download direto do MinIO) e um botão para inspecionar o JSON resultante.
- **APIs REST de Integração**: Endpoints dedicados para criação de templates e execução de extrações programáticas.

---

## 🛠️ Tecnologias Utilizadas

- **Core**: Java 21, Spring Boot 3
- **Persistência**: Spring Data JPA, Hibernate, PostgreSQL
- **Armazenamento de Objetos**: MinIO (S3-compatible Object Storage)
- **Motor de Template Web**: Thymeleaf + Vanilla CSS
- **Inteligência Artificial**: API Google Gemini (`gemini-2.5-flash` via HTTP Rest)
- **Segurança**: Spring Security

---

## ⚙️ Variáveis de Ambiente e Configuração

O projeto lê as seguintes configurações a partir do [application.yaml](file:///c:/Users/joaov/Documents/Projects/doclens/src/main/resources/application.yaml). Você pode configurá-las no seu ambiente de desenvolvimento:

| Variável | Descrição | Valor Padrão |
|---|---|---|
| `GEMINI_API_KEY` | Chave de API da IA do Google Gemini (obrigatória) | - |
| `MINIO_URL` | Endpoint do servidor MinIO | `http://localhost:9000` |
| `MINIO_ACCESS_KEY` | Chave Acesso do MinIO | `minioadmin` |
| `MINIO_SECRET_KEY` | Chave Secreta do MinIO | `minioadmin` |
| `MINIO_BUCKET_NAME`| Nome do bucket para armazenar os documentos | `doclens-documents` |
| `SPRING_DATASOURCE_URL` | URL de conexão do PostgreSQL | `jdbc:postgresql://localhost:5432/doclens` |
| `SPRING_DATASOURCE_USERNAME`| Usuário do banco de dados PostgreSQL | `postgres` |
| `SPRING_DATASOURCE_PASSWORD`| Senha do banco de dados PostgreSQL | `root` |

---

## 🚀 Como Executar o Projeto

1. **Pré-requisitos**:
   * Instalar o **Java 21**.
   * Ter um banco de dados **PostgreSQL** rodando localmente (com o database `doclens`).
   * Ter o **MinIO** rodando localmente (ex: via Docker).

2. **Configurar a API Key**:
   Configure a variável de ambiente com a sua chave do Gemini obtida no Google AI Studio:
   ```bash
   # Linux/macOS
   export GEMINI_API_KEY="sua_chave_aqui"

   # Windows (PowerShell)
   $env:GEMINI_API_KEY="sua_chave_aqui"
   ```

3. **Compilar e Rodar**:
   Execute a aplicação usando o wrapper do Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Acessar o Painel**:
   Acesse no navegador: **[http://localhost:8080](http://localhost:8080)**

---

## 🔌 Principais Endpoints da API REST

### 1. Criar um Novo Template
* **POST** `/api/templates`
* **Payload de Exemplo (RG)**:
  ```json
  {
    "tag": "rg",
    "name": "Carteira de Identidade (RG)",
    "description": "Template para extração de dados do RG.",
    "status": "ACTIVE",
    "fields": [
      {
        "code": "nome_completo",
        "name": "Nome Completo",
        "required": true,
        "type": "STRING"
      },
      {
        "code": "numero_rg",
        "name": "Número do RG",
        "required": true,
        "type": "STRING"
      }
    ]
  }
  ```

### 2. Executar Extração via API
* **POST** `/api/extraction`
* **Tipo do Conteúdo**: `multipart/form-data`
* **Parâmetros**:
  * `templateCode` (String): Tag do template (ex: `rg`).
  * `file` (Arquivo): O documento que deseja extrair.
