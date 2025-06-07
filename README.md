# API Payment Authentication – Segurança, Transações & Carteiras Digitais

**Autenticação, segurança reforçada, gestão de transações financeiras e mensageria com RabbitMQ em um só projeto!**

Este sistema foi desenvolvido para garantir **transações seguras**, **processamento assíncrono de eventos** e um **controle eficiente de carteiras digitais (`Wallet`)**, utilizando **Spring Security + JWT**, **RabbitMQ para notificações** e um **módulo completo de transações financeiras**.

**Principais funcionalidades:**  
**Autenticação JWT** → Segurança com OAuth2 e JWT.  
**Perfis de usuário (`CLIENT`, `ADMIN`)** → Controle preciso de acessos.  
**Gestão de carteiras digitais (`Wallet`)** → Cada usuário pode ter **uma carteira `PERSONAL` e uma `COMPANY`**, sem duplicações.  
**Módulo de Transações Financeiras** → Transferências entre carteiras com validações e controle de saldo.  
**Mensageria Assíncrona com RabbitMQ** → Processamento eficiente de notificações e eventos no sistema.  
**Sistema de Notificações** → Cada ação relevante do usuário gera **notificações em tempo real**.  
**Documentação Swagger** → API totalmente documentada e pronta para uso!

---

## Tecnologias Utilizadas

**Spring Boot** → Framework poderoso para back-end.  
**Spring Security + JWT** → Autenticação e segurança com boas práticas.  
**Spring AMQP (RabbitMQ)** → Mensageria assíncrona para notificações.  
**Spring Data JPA / Hibernate** → Gerenciamento eficiente de dados.  
**H2 Database** → Banco de dados leve e ágil para testes.  
**Mockito/JUnit** → Testes unitários completos e mockados.  
**Swagger** → API documentada e intuitiva para desenvolvedores.

---

## Sistema de Transações

**Regras de negócio para transferências financeiras**:  
O sistema permite **transferências entre carteiras (`Wallet`)**.  
Antes de processar uma transação, **o saldo do usuário é validado**.  
Se a transação for aprovada, ela é registrada e **atualiza os saldos das carteiras envolvidas**.  
O usuário recebe **uma notificação** sobre o status da transação via RabbitMQ.

**Fluxo da transação**:  
**Usuário inicia uma transferência**, informando carteira de origem, destino e valor.  
**O sistema verifica saldo disponível** na carteira de origem.  
**Caso aprovado**, o saldo das carteiras é atualizado e a transação é registrada.  
**Uma notificação é enviada** ao usuário informando sucesso ou falha na transação.

---

## Sistema de Notificações

A API conta com **mensageria assíncrona via RabbitMQ**, garantindo que notificações sejam **processadas de forma eficiente e não bloqueiem o fluxo do sistema**.

**Usuários recebem notificações para eventos importantes**, como:
- Criação de uma nova Wallet.
- Atualização de saldo em uma Wallet.
- Mudança de status em transações financeiras.

**Como funciona?**  
O serviço detecta uma ação relevante (ex: transação financeira).  
O evento é enviado para uma **fila RabbitMQ**.  
Um consumidor assíncrono processa o evento e dispara a notificação.

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

**Suba o ambiente RabbitMQ (Docker recomendado)**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
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
Pensado para aplicações reais → Segurança, escalabilidade e processamento eficiente de eventos. Mensageria com RabbitMQ → Fluxo assíncrono, garantindo performance e estabilidade na entrega de notificações. Módulo de Transações → Transferências financeiras seguras entre carteiras, com regras bem definidas. Código limpo e testado → Garantia de qualidade através de testes unitários sólidos. Tecnologias modernas → Segue boas práticas e tendências de desenvolvimento backend.