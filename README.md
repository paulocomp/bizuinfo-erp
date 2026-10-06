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

## 🧱 Estrutura do projeto

Maven multi-módulo, empacotado em um único WAR:

| Módulo | O que tem |
|---|---|
| `bizuinfo-core` | DAO genérico, controle de transação (`@Transacional`), email, utilitários. Não conhece o domínio. |
| `bizuinfo-auditoria` | Log de auditoria |
| `bizuinfo-usuario` | Usuários, papéis e o contrato `UsuarioLogado` |
| `bizuinfo-acesso` | Login, cadastro, confirmação de email, recuperação de acesso |
| `bizuinfo-produto` | Produtos, categorias, fornecedores, estoque |
| `bizuinfo-venda` | Vendas, pagamentos, recibo em PDF, sugestão de compra |
| `bizuinfo-web` (WAR) | Telas `.xhtml`, beans `@Named`, filtros, servlets, `persistence.xml` |

Dependências permitidas (um módulo só usa os que estão à esquerda):

```
core <- auditoria <- usuario <- acesso
                     usuario <- produto <- venda
web junta todos
```

Regras de camada:

- **bean → service → DAO**. Bean de tela nunca acessa DAO.
- **Regra de negócio fica no service.** Violação de regra é `RegraNegocioException`; o bean só mostra a mensagem.
- **Service que acessa o banco é `@ApplicationScoped @Transacional`.** A transação vale para o método inteiro, inclusive chamadas a outros services; qualquer exceção desfaz tudo.
- **Injeção sempre com `@Inject`** (não usamos EJB).
- **Para saber quem está logado, injete `UsuarioLogado`**, não `LoginBean`/`SessaoBean`.
- **Entidade nova precisa ser listada** em `bizuinfo-web/src/main/resources/META-INF/persistence.xml`.

Para compilar tudo, na raiz:

```bash
mvn package
```

O WAR sai em `bizuinfo-web/target/bizuinfo.erp.war`.

---

## 🚀 Como executar o projeto

### 📦 Pré-requisitos
- Docker Desktop instalado e rodando

---

### ▶️ Executando a aplicação

Na raiz do projeto (onde está o `docker-compose.deploy.yml`):

```bash
mvn package
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
