# API Payment Authentication – Segurança, Transações & Carteiras Digitais

**Segurança, Monitoramento e Gestão de Transações financeiras**

Este sistema foi desenvolvido para garantir transações seguras, observabilidade com métricas em tempo real e um controle eficiente de carteiras digitais (Wallet), utilizando Spring Security + JWT, Spring Boot Actuator, Prometheus e Grafana.

**Principais funcionalidades:**

**Autenticação JWT** → Segurança com OAuth2 e JWT.  

**Perfis de usuário (`CLIENT`, `ADMIN`)** → Controle preciso de acessos.  

**Gestão de carteiras digitais (`Wallet`)** → Cada usuário pode ter **uma carteira `PERSONAL` e uma `COMPANY`**, sem duplicações.  

**Módulo de Transações Financeiras** → Transferências entre carteiras com validações e controle de saldo.

**Monitoramento com Prometheus e Grafana** → Métricas expostas via Spring Actuator e visualizadas com dashboards no Grafana.

**Documentação Swagger** → API totalmente documentada e pronta para uso!

---

## Documentação da API com Swagger

Esta aplicação utiliza o Swagger para fornecer uma interface de documentação e testes da API.

### Acessando a Documentação da API
- **Swagger UI: http://localhost:8080/api-payment-authentication/swagger-ui.html**

A documentação do Swagger oferece uma visão detalhada dos endpoints disponíveis, dos parâmetros que eles aceitam e das respostas esperadas, tudo em uma interface gráfica acessível diretamente pelo navegador.

#### Exemplo de Requisição no Postman para Login ####

- **Endpoint: POST** /oauth2/token
- **Content-Typex**: application/x-www-form-urlencoded
- **Authorization**: Basic base64(myclientid:mysecretkey)
- **Body (x-www-form-urlencoded)**:


    Key        |      Value
    -----------|------------------------
    username   |     email@example.com
    password   |     Senha12345
    grant_type |     password

Resposta no postman:

    {
        "access_token": "eyJraWQiOiI2NDc5YWQxOS0wY2MyLTRhNGYtYWNkNy1lZWVlNjk0NjEwMGIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJteWNsaWVudGlkIiwiYXVkIjoibXljbGllbnRpZCIsIm5iZiI6MTc1MDY4NjI3MSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiZXhwIjoxNzUwNzcyNjcxLCJpYXQiOjE3NTA2ODYyNzEsImp0aSI6IjEwOGUyMzZlLTc5NzYtNDE1MS1hM2JmLTUxNjJkYzE4YzZhOCIsImF1dGhvcml0aWVzIjpbIkNMSUVOVCJdLCJ1c2VybmFtZSI6ImpvaG5kb2VAZXhhbXBsZS5jb20ifQ.sjZxs4kShoCUnPSS5R38QtX-LMDpJj9JNp7vSQp0xtzg8L_ziNr29p4Be7FjdF3Ji0C2pJoyY92xVR5cTtSyZavS6sjp7lWI6VzY7ENxrFCz20ym_SgSPP1jaW8RuntZ0ZCtJFlG-HMK7IcgWtkxJxFWZmVat9pB9fBOr-CXBua0cOXodHX8Gn3F12sH2XpOmEZnCL3UdaFvNNH3vbw2W8I76YnBeLVTsfRPY-nj5-tr9KKqE6EBRtkwWHZch0sLxoSun9GrElz9-umoAnErAmbJ3Ufu0j6C-b93JDU1W4fC9xqlhmUt642QqAaFaVby2A_UZ2vBErBslEwY6DU12w",
        "token_type": "Bearer",
        "expires_in": 86399
    }

Copie o "access_token" e cole no em Authorize no Swagger para liberar os endpoints de Transação

>A autenticação do client é feita via header HTTP Authorization: Basic ..., usando client_id e client_secret codificados em Base64.

- **Endpoint: POST** /transaction
- **Body**:



    {
        "receiverId": 2,
        "amount": 1000
    }

A resposta esperada para realizar uma transação seria:

    {
        "id": 3,
        "senderId": 1,
        "receiverId": 2,
        "amount": 1000,
        "moment": "2025-06-23T10:36:07.292349"
    }

Em caso de erro, por exemplo, se o saldo não for suficiente, a API retornará:

    {
        "timestamp": "2025-06-23T13:37:44.323555Z",
        "status": 400,
        "error": "Saldo insuficiente",
        "path": "/transaction"
    }

---

## Tecnologias Utilizadas

**Spring Boot** → Framework poderoso para back-end.  
**Spring Security + JWT** → Autenticação e segurança com boas práticas.  
**Spring Data JPA / Hibernate** → Gerenciamento eficiente de dados.  
**Spring Boot Actuator** → Exposição de métricas para monitoramento.  
**H2 Database** → Banco de dados leve e ágil para testes.  
**Mockito/JUnit** → Testes unitários completos e mockados.  
**Prometheus + Grafana** → Coleta e visualização de métricas em tempo real.  
**Swagger** → API documentada e intuitiva para desenvolvedores.

---

## Monitoramento com Spring Actuator, Prometheus e Grafana

A aplicação conta com integração completa para **monitoramento e observabilidade**, utilizando:

- **Spring Boot Actuator** para exposição de métricas.
- **Prometheus** para coleta periódica dessas métricas.
- **Grafana** para visualização em tempo real, com dashboards personalizáveis.

Métricas disponíveis:
- Status de saúde da aplicação (`/actuator/health`)
- Uso da JVM, memória, threads e endpoints (`/actuator/metrics`)
- Métricas financeiras personalizadas das transações

Após subir os containers, acesse:
- Prometheus: [http://localhost:9090](http://localhost:9090)
- Grafana: [http://localhost:3000](http://localhost:3000)*(deve baixar a imagem e criar o container, não foi colocado no projeto)*

> Dica: O dashboard do Grafana já vem pronto para visualizar os dados coletados da sua aplicação em tempo real!

---

## Sistema de Transações

**Regras de negócio para transferências financeiras**:  
O sistema permite **transferências entre carteiras (`Wallet`)**.  
Antes de processar uma transação, **o saldo do usuário é validado**.  
Se a transação for aprovada, ela é registrada e **atualiza os saldos das carteiras envolvidas**.

**Fluxo da transação**:  
**Usuário inicia uma transferência**, informando carteira de origem, destino e valor.  
**O sistema verifica saldo disponível** na carteira de origem.  
**Caso aprovado**, o saldo das carteiras é atualizado e a transação é registrada.

---

## Como Rodar o Projeto

**Clone o repositório**
```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
```

**Entre no diretório do projeto**
```bash
cd api-payment-authentication
```

**Execute a aplicação**
```bash
./mvnw spring-boot:run
```

**Agora a API está rodando localmente em http://localhost:8080!**

**Documentação da API**

A API conta com Swagger para facilitar a visualização dos endpoints.

http://localhost:8080/swagger-ui/index.html

## Sobre a aplicação
Pensado para aplicações reais → Segurança, escalabilidade e processamento eficiente de eventos. Módulo de Transações → Transferências financeiras seguras entre carteiras, com regras bem definidas. Código limpo e testado → Garantia de qualidade através de testes unitários sólidos. Tecnologias modernas → Segue boas práticas e tendências de desenvolvimento backend.