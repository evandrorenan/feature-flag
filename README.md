# feature-flag

Este projeto foi gerado automaticamente pelo Artemis Cloud Native Framework.

## Descrição

`feature-flag` é um microserviço responsável por gerenciar e processar flags de recursos. Ele utiliza o Spring Boot e o Camel para orquestrar a lógica de negócios e acessar o repositório de flags de recursos.

## Requisitos

- Java 11 ou superior
- Maven 3.6.0 ou superior

## Configuração

### Variáveis de Ambiente

Configure as seguintes variáveis de ambiente para que o microserviço funcione corretamente:

- `DB_URL`: URL de conexão com o banco de dados.
- `DB_USERNAME`: Nome de usuário do banco de dados.
- `DB_PASSWORD`: Senha do banco de dados.
- `CACHE_EXPIRATION_MINUTES`: Tempo de expiração do cache em minutos (padrão: 5).

Exemplo:

```bash
export DB_URL=jdbc:mysql://localhost:3306/feature_flags
export DB_USERNAME=root
export DB_PASSWORD=secret
export CACHE_EXPIRATION_MINUTES=5
```

## Execução

Para executar o microserviço, utilize os seguintes comandos:

Compilação:

```bash
mvn clean install
```

Execução:

```bash
mvn spring-boot:run
```
## Documentação da API

Este aplicativo utiliza o Swagger para documentação da API. Uma vez que o aplicativo esteja em execução, você pode acessar o Swagger UI na seguinte URL:

```

http://localhost:8080/swagger-ui/index.html
O Swagger UI fornece uma interface visual para interagir com os endpoints da API, visualizar modelos de solicitação/resposta e executar chamadas de API diretamente do navegador.

Para obter mais informações sobre o Swagger, visite a [documentação oficial do Swagger](https://swagger.io/docs/).