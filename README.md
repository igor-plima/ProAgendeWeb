# ProAgendeWeb — Projeto Integrador SENAC

O **ProAgendeWeb** é meu Projeto Integrador final, desenvolvido como parte da conclusão do curso Técnico em Análise e Desenvolvimento de Sistemas do Senac.

A aplicação web auxilia no gerenciamento de agendamentos de serviços. Ela permite organizar os cadastros de profissionais, clientes e serviços, além de criar e acompanhar agendamentos. O projeto utiliza Spring Boot para o back-end e a API REST, JDBC para a integração com o MySQL e o front-end desenvolvido na Etapa 8. Os testes automatizados da Etapa 7 também foram integrados ao projeto.

## Tecnologias

- Java 17
- Spring Boot 3.3.4
- Maven
- MySQL 8 e JDBC
- JUnit 5 e MockMvc
- HTML, CSS e JavaScript

## Como executar no NetBeans

1. Configure o banco de dados MySQL executando o script `banco/ProAgende_estrutura_e_dados.sql`. Se o banco já existir e você precisar recriá-lo, faça um backup antes, pois essa operação pode apagar os dados existentes.
2. Configure a conexão com o MySQL escolhendo **uma** destas opções:
   - **Arquivo de configuração:** copie `src/main/resources/banco.properties.example` para `banco.properties` e preencha nesse arquivo os dados de acesso ao banco, como usuário e senha.
   - **Variáveis de ambiente:** em vez de criar o arquivo `banco.properties`, configure `PROAGENDE_DB_URL`, `PROAGENDE_DB_USER` e `PROAGENDE_DB_PASSWORD` com o endereço do banco, o usuário e a senha.
3. Abra a pasta do projeto no NetBeans em **File > Open Project**.
4. Execute **Clean and Build** e, em seguida, **Run**.
5. Acesse [http://localhost:8080](http://localhost:8080).

### Executar sem MySQL

Para usar o modo em memória, configure `proagende.memoria=true` em `application.properties`. Nesse modo, os dados ficam apenas na memória e não são persistidos no MySQL.

## Testes automatizados

Os testes podem ser executados pelo NetBeans, clicando com o botão direito no projeto e selecionando **Test**, ou pelo terminal, na pasta do projeto:

```bash
mvn test
