# BizuInfo ERP

Sistema ERP web desenvolvido em grupo com foco em gestão empresarial, controle de usuários, estoque, vendas e segurança.

---

## 🎯 Sobre o projeto

Projeto acadêmico voltado para simular um sistema ERP completo, com módulos administrativos, controle de acesso e funcionalidades de gestão empresarial.

---

## ⚙️ Tecnologias

- Java (JSF + PrimeFaces)
- EJB 3.2
- JPA / Hibernate 6
- Banco de dados relacional (MySQL)
- Docker / Docker Compose
- Spring Security

---

## 🔐 Funcionalidades

- Controle de acesso baseado em papéis (RBAC)
- Autenticação de usuários
- Gestão de usuários (CRUD)
- Gestão de produtos e estoque
- Gestão de fornecedores
- Fluxo de vendas e simulação de pagamento
- Relatórios administrativos
- Auditoria de ações dos usuários (log de operações)

---

## 🚀 Como executar o projeto

### 📦 Pré-requisitos
- Docker Desktop instalado e rodando

---

### ▶️ Executando a aplicação

Na raiz do projeto (onde está o `docker-compose.deploy.yml`):

```bash
docker build -t bizuinfoerp-app .
docker compose -f docker-compose.deploy.yml up
```

A aplicação ficará disponível em:


http://localhost:8080


---

### ⛔ Parando a aplicação

```bash
docker compose -f docker-compose.deploy.yml down
```
