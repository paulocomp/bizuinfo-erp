CREATE DATABASE IF NOT EXISTS bizuinfo_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bizuinfo_db;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS usuario
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(100)                             NOT NULL,
    email             VARCHAR(100)                             NOT NULL UNIQUE,
    senha             VARCHAR(255)                             NOT NULL,
    role              ENUM ('FUNCIONARIO', 'GERENTE', 'ADMIN') NOT NULL DEFAULT 'FUNCIONARIO',
    email_verificado  BOOLEAN                                  NOT NULL DEFAULT FALSE,
    token_verificacao VARCHAR(255),
    token_reset       VARCHAR(255),
    token_expiracao   DATETIME
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tipousuario
(
    idtipoUsuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao     VARCHAR(45),
    usuario_id    BIGINT NOT NULL,

    CONSTRAINT fk_tipousuario_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categoria
(
    categoria_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(255) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fornecedor
(
    idfornecedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(45),
    cnpj         VARCHAR(20),
    telefone     VARCHAR(20),
    email        VARCHAR(100)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS produto
(
    idproduto               BIGINT AUTO_INCREMENT PRIMARY KEY,

    nome                    VARCHAR(45),
    preco                   DOUBLE,
    estoqueAtual            INT,
    estoqueMinimo           INT DEFAULT 5,
    descricao               VARCHAR(45),

    fornecedor_idfornecedor BIGINT,
    categoria_categoria_id  BIGINT,

    CONSTRAINT fk_produto_fornecedor
        FOREIGN KEY (fornecedor_idfornecedor)
            REFERENCES fornecedor (idfornecedor),

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (categoria_categoria_id)
            REFERENCES categoria (categoria_id)
            ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS venda
(
    idvenda    BIGINT AUTO_INCREMENT PRIMARY KEY,

    dataVenda  DATETIME NOT NULL,
    valorTotal DOUBLE   NOT NULL,

    usuario_id BIGINT   NOT NULL,

    CONSTRAINT fk_venda_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS itemvenda
(
    iditemVenda       BIGINT AUTO_INCREMENT PRIMARY KEY,

    quantidade        INT    NOT NULL,
    valorUnitario     DOUBLE NOT NULL,
    subtotal          DOUBLE NOT NULL,

    venda_idvenda     BIGINT NOT NULL,
    produto_idproduto BIGINT NOT NULL,

    CONSTRAINT fk_itemvenda_venda
        FOREIGN KEY (venda_idvenda)
            REFERENCES venda (idvenda),

    CONSTRAINT fk_itemvenda_produto
        FOREIGN KEY (produto_idproduto)
            REFERENCES produto (idproduto)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pagamento
(
    idpagamento     BIGINT AUTO_INCREMENT PRIMARY KEY,

    valor           DOUBLE      NOT NULL,

    formaPagamento  VARCHAR(30) NOT NULL,
    statusPagamento VARCHAR(30) NOT NULL,

    venda_idvenda   BIGINT      NOT NULL,

    CONSTRAINT fk_pagamento_venda
        FOREIGN KEY (venda_idvenda)
            REFERENCES venda (idvenda)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS log_auditoria
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    acao                VARCHAR(100) NOT NULL,
    detalhe             TEXT,
    data_hora           DATETIME     NOT NULL,
    usuario_responsavel VARCHAR(100),
    ip_origem           VARCHAR(100)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO usuario (nome, email, senha, role, email_verificado)
VALUES ('Joao Paulo', 'bizuinfo.contato@gmail.com',
        '$2a$12$7RBVdohjJCyADskdJTDmmOa6MOUNcOBrNH3..AS/SZAwoUpNFU086',
        'ADMIN', TRUE),

       ('Miguel Fernandes', 'miguel.rspp@gmail.com',
        '$2a$12$7RBVdohjJCyADskdJTDmmOa6MOUNcOBrNH3..AS/SZAwoUpNFU086',
        'FUNCIONARIO', TRUE),

       ('Nicolas Caseiro', 'nickcda@gmail.com',
        '$2a$12$7RBVdohjJCyADskdJTDmmOa6MOUNcOBrNH3..AS/SZAwoUpNFU086',
        'GERENTE', TRUE),

       ('Apagar', 'davi.colosso50@gmail.com',
        '',
        'FUNCIONARIO', TRUE),

       ('Apagar2', 'miguel.control19@gmail.com',
        '',
        'FUNCIONARIO', FALSE),

       ('Teste', 'teste@gmail.com',
        '$2a$10$z/eXQYdYSLo7R4NVNPwZ9uFvDNn/bM2P9ulTgYP3MaGyPrCwDvRQ.',
        'ADMIN', TRUE);

INSERT INTO tipousuario (descricao, usuario_id)
VALUES ('Acesso Total', 1),
       ('Acesso de Vendas', 2),
       ('Acesso Gerencial', 3);

INSERT INTO categoria (nome)
VALUES ('Periféricos'),
       ('Hardware'),
       ('Monitores'),
       ('Webcams e Áudio'),
       ('Armazenamento'),
       ('Placas-Mãe');

INSERT INTO fornecedor (nome, cnpj, telefone, email)
VALUES ('Logitech', '11.111.111/0001-11', '(11) 99999-1111', 'vendas@logitech.com'),
       ('Razer', '22.222.222/0001-22', '(11) 99999-2222', 'contato@razer.com'),
       ('NVIDIA', '33.333.333/0001-33', '(11) 99999-3333', 'distribuicao@nvidia.com'),
       ('Corsair', '44.444.444/0001-44', '(11) 99999-4444', 'sales@corsair.com'),
       ('Kingston', '55.555.555/0001-55', '(11) 99999-5555', 'b2b@kingston.com'),
       ('ASUS', '66.666.666/0001-66', '(11) 99999-6666', 'parceiros@asus.com'),
       ('LG', '77.777.777/0001-77', '(11) 99999-7777', 'comercial@lg.com');

INSERT INTO produto
(nome,
 preco,
 estoqueAtual,
 estoqueMinimo,
 descricao,
 fornecedor_idfornecedor,
 categoria_categoria_id)
VALUES ('Teclado Mecânico RGB', 450.50, 25, 2, 'Switch Blue TKL', 2, 1),
       ('Mouse Gamer Sem Fio', 320.00, 40, 5, '10000 DPI Bateria 50h', 1, 1),
       ('Placa de Vídeo RTX 4060', 2150.00, 10, 1, '8GB GDDR6 Dual Fan', 3, 2),
       ('Headset Gamer', 280.90, 30, 5, 'Som Surround 7.1', 4, 4),
       ('Webcam Full HD', 199.99, 15, 5, '1080p 60fps c/ Microfone', 1, 4),
       ('Monitor LG UltraGear 24"', 1150.00, 20, 2, '144Hz 1ms IPS', 7, 3),
       ('SSD 1TB NVMe M.2', 450.90, 50, 10, 'Leitura 3500MB/s', 5, 5),
       ('Memória RAM 16GB DDR4', 280.00, 40, 5, '3200MHz CL16', 5, 2),
       ('Placa-Mãe ROG Strix B550', 1320.00, 12, 3, 'AM4 ATX Wi-Fi', 6, 6),
       ('Placa de Vídeo RTX 4070 Ti', 5400.00, 5, 1, '12GB GDDR6X', 6, 2),
       ('Mousepad Gamer Estendido', 120.00, 60, 5, 'Superfície Speed 90x40cm', 2, 1),
       ('Microfone Seiren Mini', 350.00, 15, 5, 'Condensador USB', 2, 4),
       ('Volante G29 Driving Force', 1800.00, 8, 2, 'Com Pedais', 1, 1),
       ('Caixa de Som Z906', 2200.00, 4, 1, 'Surround 5.1 1000W', 1, 4);


INSERT INTO venda (dataVenda, valorTotal, usuario_id)
VALUES (NOW(), 13515.00, 1),
       (DATE_SUB(NOW(), INTERVAL 10 DAY), 32250.00, 2);

INSERT INTO itemvenda (quantidade, valorUnitario, subtotal, venda_idvenda, produto_idproduto)
VALUES (30, 450.50, 13515.00, 1, 1),
       (15, 2150.00, 32250.00, 2, 3);

INSERT INTO pagamento
(valor,
 formaPagamento,
 statusPagamento,
 venda_idvenda)
VALUES (13515.00, 'PIX', 'APROVADO', 1),
       (32250.00, 'CARTAO_CREDITO', 'APROVADO', 2);

SET GLOBAL event_scheduler = ON;

DELIMITER $$

CREATE EVENT IF NOT EXISTS limpar_usuarios_pendentes
    ON SCHEDULE EVERY 24 HOUR
    DO
    BEGIN
        DELETE
        FROM usuario
        WHERE email_verificado = FALSE
          AND token_expiracao IS NOT NULL
          AND token_expiracao < NOW();
    END$$

DELIMITER ;