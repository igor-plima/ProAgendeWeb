-- Criando Banco de Dados
CREATE DATABASE ProAgende;
USE ProAgende;


-- Criando as Tabelas
CREATE TABLE Profissional(
id_Profissional INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL,
telefone VARCHAR(20)
);

CREATE TABLE Cliente(
id_Cliente INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL,
telefone VARCHAR(20)
);

CREATE TABLE Servico(
id_Servico INT AUTO_INCREMENT PRIMARY KEY,
id_Profissional INT NOT NULL,
nome VARCHAR(100) NOT NULL,
descricao TEXT,
preco DECIMAL(10,2) NOT NULL,
duracao_minutos INT,
FOREIGN KEY (id_Profissional) REFERENCES Profissional(id_Profissional)
);

CREATE TABLE Agendamento(
id_Agendamento INT AUTO_INCREMENT PRIMARY KEY,
id_Servico INT NOT NULL,
Id_Cliente INT NOT NULL,
data_hora DATETIME(0) NOT NULL,
status ENUM('pendente', 'confirmado', 'cancelado') DEFAULT 'pendente',
FOREIGN KEY (id_Servico) REFERENCES Servico(id_servico),
FOREIGN KEY (id_Cliente) REFERENCES Cliente(id_Cliente)
);


-- Inserindo os Dados
INSERT INTO Profissional (nome, email, telefone) VALUES
('João Silva', 'joao@proagende.com', '11999990000'),
('Maria Oliveira', 'maria@proagende.com', '11988880000'),
('Carlos Souza', 'carlos@proagende.com', '11977770000'),
('Ana Lima', 'ana@proagende.com', '11966660000'),
('Fernanda Alves', 'fernanda@proagende.com', '11955550000');

INSERT INTO Cliente (nome, email, telefone) VALUES
('Lucas Costa', 'lucas@email.com', '11911112222'),
('Beatriz Ramos', 'beatriz@email.com', '11922223333'),
('Marcos Pereira', 'marcos@email.com', '11933334444'),
('Juliana Dias', 'juliana@email.com', '11944445555'),
('Felipe Martins', 'felipe@email.com', '11955556666');

INSERT INTO Servico (id_Profissional, nome, descricao, preco, duracao_minutos) VALUES
(1, 'Corte de Cabelo', 'Corte masculino padrão', 40.00, 30),
(2, 'Manicure', 'Serviço de manicure com esmaltação', 50.00, 45),
(3, 'Massagem Relaxante', 'Massagem corporal completa', 120.00, 60),
(4, 'Design de Sobrancelha', 'Modelagem com pinça', 35.00, 25),
(5, 'Consulta Estética', 'Consulta inicial com avaliação', 80.00, 40);

INSERT INTO Agendamento (id_Servico, id_Cliente, data_hora, status) VALUES
(1, 1, '2025-07-01 10:00:00', 'confirmado'),
(2, 2, '2025-07-01 11:00:00', 'pendente'),
(3, 3, '2025-07-01 12:00:00', 'confirmado'),
(4, 4, '2025-07-01 13:00:00', 'cancelado'),
(5, 5, '2025-07-01 14:00:00', 'pendente');


