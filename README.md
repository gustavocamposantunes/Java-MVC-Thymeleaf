# Java MVC Thymeleaf

Projeto Spring Boot MVC com CRUD de propostas e cursos usando Thymeleaf.

## Requisitos
- Java 11+
- Maven 3.9+
- PostgreSQL no Neon para ambiente de produção

## Banco de dados
O projeto foi preparado para funcionar com:
- H2 em desenvolvimento local, sem configuração adicional
- PostgreSQL do Neon em produção, via variáveis de ambiente

### Variáveis de ambiente para Neon / Render
- `POSTGRES_URL`
	- exemplo: `postgresql://neondb_owner:SUASENHA@ep-billowing-smoke-aclsb5gc-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require`

Se nenhuma variável do banco for informada, o app usa H2 local automaticamente.

### Seed inicial de dados
- O projeto possui seeder automático para `cursos` e `propostas`.
- O seed roda apenas quando as tabelas estão vazias.
- Para desabilitar, defina: `APP_SEED_ENABLED=false`.

### Exemplo de configuração no Render
```bash
POSTGRES_URL=postgresql://neondb_owner:SUA_SENHA@ep-billowing-smoke-aclsb5gc-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
```

### Arquivo .env local
Se quiser organizar as variáveis localmente, crie um arquivo .env na raiz do projeto copiando o conteúdo de .env.example.

Importante:
- o arquivo .env não deve ser commitado
- no Spring Boot, as variáveis precisam estar disponíveis no ambiente da execução
- no Render, elas devem ser configuradas no painel do serviço

## Executar localmente
```bash
mvn spring-boot:run
```

## URLs locais
- Aplicação: http://localhost:8080
- CRUD Propostas: http://localhost:8080/propostas
- CRUD Cursos: http://localhost:8080/cursos
- H2 Console: http://localhost:8080/h2-console
	- JDBC URL: `jdbc:h2:mem:propostasdb`
	- User: `sa`
	- Password: vazio

## Deploy no Render
1. Crie um novo Web Service no Render apontando para este repositório.
2. Escolha a opção Docker.
3. O Render vai usar o Dockerfile na raiz do projeto automaticamente.
4. Configure a variável de ambiente `POSTGRES_URL`.
5. O Render fornece automaticamente a variável `PORT`, que já está suportada pela aplicação.

## Entidade Proposta
- `id_proposta` (int)
- `titulo` (string)
- `descricao` (string)
- `data_submissao` (date)
- `status` (`aprovado`, `aguardando aprovação`, `reijeitado`)
- `anexos` (blob)

## Entidade Curso
- `id_curso` (int)
- `nome` (string)
- `descricao` (string)
