# DocLens

Projeto em Spring Boot para extração de dados estruturados de documentos usando a API do Gemini e armazenamento de arquivos no MinIO.

## Configuração

Adicione as credenciais e variáveis no seu ambiente ou no `application.yaml`:

- `GEMINI_API_KEY`: Chave da API do Gemini.xxx'x'
- `MINIO_URL`: Endpoint do MinIO (Padrão: `http://localhost:9000`).
- `MINIO_ACCESS_KEY`: Chave de acesso do MinIO (Padrão: `minioadmin`).
- `MINIO_SECRET_KEY`: Chave secreta do MinIO (Padrão: `minioadmin`).
- `MINIO_BUCKET_NAME`: Bucket para upload (Padrão: `doclens-documents`).
- Configurações normais do PostgreSQL.

## Endpoints

- **POST** `/api/templates`: Cadastro de templates de documentos.
- **POST** `/api/extraction`: Rota de API para enviar arquivo (`file`) e tag do template (`templateCode`) para extração.
- **GET** `/extraction`: Página web para envio de arquivos e visualização do histórico de extrações.
