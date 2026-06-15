# Spec

Aplicativo desktop multiplataforma que monta o dossiê completo de peças de moto a partir de dados do Mercado Livre.

O usuário digita uma descrição de peça (ex.: "pastilha freio titan 125"). O Spec consulta a API pública do Mercado Livre, obtém os dados de catálogo do produto (nome, especificações, fotos, descrição do vendedor), e usa um LLM para extrair informações de compatibilidade veicular da descrição. O resultado é um dossiê estruturado com ficha técnica, modelos de moto compatíveis e fotos.

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Framework | Ktor |
| Linguagem | Kotlin |
| HTTP Client | Ktor Client |
| Serialização | kotlinx.serialization |
| Banco | SQLite (Exposed) |
| Provedor LLM | DeepSeek (API compatível com OpenAI) |
| Fonte de dados | API pública do Mercado Livre (products/search, products/{id}) |

## Arquitetura

Clean Architecture com DDD. Quatro camadas:

- **api/** — rotas HTTP. Traduz requisições em chamadas de casos de uso.
- **application/** — casos de uso. Orquestram o fluxo sem lógica de negócio.
- **domain/** — entidades e value objects. Regras de negócio, zero dependência externa.
- **infrastructure/** — implementações concretas (HTTP, banco, LLM).

```
src/main/kotlin/com/spec/
  api/            → rotas, serialização, AppModule
  application/    → SearchUseCase, ExtractUseCase
  domain/         → Product, SearchResult, LlmClient
  infrastructure/ → MercadoLivreClient, MercadoLivreParser, LlmHttpClient, ProductRepository
  Application.kt  → entry point
```

## Endpoints

| Método | Rota | Descrição | Cache |
|--------|------|-----------|-------|
| GET | `/health` | Health check | Não |
| GET | `/search?q={descricao}` | Busca produtos no Mercado Livre | Não |
| GET | `/product?id={productId}` | Obtém dossiê completo com compatibilidade | Sim |

### Exemplos de resposta

```
GET /health → 200
{ "status": "ok" }

GET /search?q=pastilha+freio+titan → 200
{
  "results": [
    { "title": "Pastilha Freio Titan 125", "url": "MLB64412608", "thumbnail": "https://...", "price": null }
  ]
}

GET /product?id=MLB39308378 → 200
{
  "name": "Câmara Pneu Moto 14",
  "description": "A Câmara Pneu Moto 14 da marca Riffel...",
  "specifications": [
    { "key": "Marca", "value": "Riffel" },
    { "key": "Aro", "value": "14" }
  ],
  "compatibility": [
    { "model": "Honda Biz125", "yearRange": "2006+" },
    { "model": "Kasinski Win110", "yearRange": "2006-2013" }
  ],
  "images": [
    { "url": "https://http2.mlstatic.com/...", "order": 0 }
  ],
  "sourceUrl": "MLB39308378"
}
```

## Fluxo de Dados

```
[Cliente] → api/ (rota) → application/ (use case) → infrastructure/ (API ML, LLM, DB)
                                                          ↓
                                                    domain/ (entidades)

1. Usuário busca → SearchUseCase → MercadoLivreClient.search(query)
2. Usuário seleciona produto → ExtractUseCase:
   a. Verifica cache → retorna se existir
   b. MercadoLivreClient.getProduct(productId) → Product (nome, specs, imagens, descrição)
   c. LlmClient.synthesize(descrição) → List<Compatibility>
   d. Mescla compatibilidade no Product → salva no cache → retorna
```

## Setup

### Pré-requisitos

- JDK 21+
- Credenciais de aplicação do Mercado Livre (client_id e client_secret)
- Chave de API do DeepSeek (ou provedor compatível com OpenAI)

### Variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```
ML_CLIENT_ID=seu_client_id
ML_CLIENT_SECRET=seu_client_secret
DEEPSEEK_API_KEY=sua_chave_deepseek
```

### Executando

```bash
./gradlew run
```

O servidor inicia em `http://0.0.0.0:8080`.

## Domínio

### Product

Entidade raiz. Agrega todas as informações do dossiê.

- `name` — nome do produto no catálogo do Mercado Livre
- `description` — descrição completa do vendedor (fonte dos dados de compatibilidade)
- `specifications` — especificações técnicas do catálogo (marca, modelo, etc.)
- `compatibility` — modelos de moto compatíveis (extraídos pelo LLM)
- `images` — fotos do produto
- `sourceUrl` — ID do produto no Mercado Livre (ex.: MLB39308378)

### SearchResult

Resultado da busca no Mercado Livre, antes de selecionar um produto específico.

- `title` — nome do produto
- `url` — ID do produto (usado no endpoint /product)
- `thumbnail` — URL da foto do produto
- `price` — sempre null (não relevante para o dossiê)

## Prompt do LLM

O prompt usado para extrair informações de compatibilidade está em `src/main/resources/prompts/extract-compatibility.txt`. Ele instrui o LLM a retornar um array JSON de objetos `{model, yearRange}` extraídos da descrição do produto. O prompt é carregado em tempo de execução e pode ser editado sem recompilar o projeto.
