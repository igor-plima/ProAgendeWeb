# ProAgendeWeb — Projeto Integrador SENAC (Etapa 9)

Sistema web de agendamento de serviços (Profissional, Cliente, Serviço e Agendamento).
Back-end Spring Boot (REST) com JDBC/MySQL, reaproveitando as camadas da Etapa 6;
front-end da Etapa 8 servido pela própria aplicação; testes JUnit 5 da Etapa 7 integrados.

## Como executar (NetBeans)
1. Rode `banco/ProAgende_estrutura_e_dados.sql` no MySQL (se o banco já existir, apague-o antes).
2. Copie `src/main/resources/banco.properties.example` para `banco.properties` e informe usuário e senha
   (ou defina `PROAGENDE_DB_URL`, `PROAGENDE_DB_USER`, `PROAGENDE_DB_PASSWORD`).
3. Abra a pasta no NetBeans (File > Open Project), faça **Clean and Build** e depois **Run**.
4. Acesse http://localhost:8080

Sem MySQL: em `application.properties`, use `proagende.memoria=true` (dados ficam só na memória).

## Testes
NetBeans: botão direito no projeto > **Test** (ou `mvn test`). Não precisam de MySQL.

## API REST
| Método | Caminho | Descrição |
|---|---|---|
| GET/POST | `/api/clientes` | Listar / cadastrar |
| GET/PUT/DELETE | `/api/clientes/{id}` | Buscar / atualizar / excluir |
| GET/POST | `/api/profissionais` | idem |
| GET/PUT/DELETE | `/api/profissionais/{id}` | idem |
| GET/POST | `/api/servicos` | idem |
| GET/PUT/DELETE | `/api/servicos/{id}` | idem |
| GET/POST | `/api/agendamentos` | Listar / agendar (`data` dd/MM/aaaa, `hora` HH:mm) |
| PATCH | `/api/agendamentos/{id}/status` | Confirmar, cancelar ou reativar |
| DELETE | `/api/agendamentos/{id}` | Excluir |
| GET | `/api/agendamentos/clientes-por-servico/{idServico}` | Clientes sem repetição |

Erros de regra de negócio voltam com HTTP 400 e corpo `{"mensagem": "..."}`.
