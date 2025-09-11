/* Banco Caio  */ 

create table usuario(
    idusuario serial primary key,
    loginusuario varchar(50) not null,
    nomeusuario varchar(50) not null,
    emailusuario varchar(50)not null,
    foneusuario varchar(20)not null,
    cpf varchar(18)not null,
    senhausuario varchar(100)not null,
    ativo boolean,
    administrador boolean,

	CONSTRAINT uq_loginusuario UNIQUE (loginusuario),
    CONSTRAINT uq_emailusuario UNIQUE (emailusuario),
    CONSTRAINT uq_cpf UNIQUE (cpf)

);

INSERT INTO usuario
(loginusuario, nomeusuario, emailusuario, foneusuario, cpf, senhausuario, ativo, administrador)
VALUES ('admin','Administrador','admin@local','(17) 00000-0000','00000000000',
'$2b$12$IEAt90DnyvEECOjnhOaRtu671hToB2AjdnwHNNPW4XHwYNabM/Dwm', true, true);

create table endereco(
	idendereco serial primary key,
	rua varchar(50) not null,
	cep varchar(10) not null,
	numero integer not null,
	bairro varchar(50),	
	nomeCidade varchar(50),
	siglaestado varchar(2),
	idusuario int not null,
	constraint fk_usuario foreign key (idusuario) 
	references usuario (idusuario)
);

--insert into endereco (rua, cep,numero,bairro,nomeCidade,siglaestado,idusuario)
--values
--('Teste 1','15780-115',12,'San Francisco','Jales','SP',1),
--('Teste 2','15715-132',3021,'Aclimação','Jales','SP',1);

/* Banco Valmor  */ 

create table editora
(
	ideditora serial primary key,
	nomeeditora varchar(100) not null,
	ruaeditora varchar(100),
	numeroeditora varchar(10),
	bairroeditora varchar(50),
	cidadeeditora varchar(50),
	estadoeditora varchar(20),
	cepeditora varchar(10),
	foneeditora varchar(15),
	siteeditora varchar(100),
	ativo boolean
);

insert into editora (nomeeditora,ruaeditora,numeroeditora,
	bairroeditora,cidadeeditora,estadoeditora,cepeditora,foneeditora,
	siteeditora,ativo) 
	values ('VR Editora','Rua Mário Quintana','22','Centro',
	'São Borja','RS','06713-270','51-4563-675','https://www.vreditora.com.br/',
	true);

select * from editora;


create table genero(
	idgenero serial primary key,
	descricaogenero varchar(50),
	ficcao boolean
);

insert into genero(descricaogenero, ficcao)
	values('Ação e aventura', true),
('Ficção afro-americana', true),
('Antologias', true),
('Infantil', true),
('Infantil Grau médio', true),
('Fantasia de grau médio', true),
('Livros de imagens', true),
('Ficção Cristã', true),
('Clássicos', true),
('Quadrinhos e romances gráficos', true),
('Chegando à maioridade', true),
('Ficção Contemporânea', true),
('Cultural e étnico', true),
('Fantasia', true),
('Fantasia épica', true),
('Jogos e LitRPG', true),
('Fantasia Urbana', true),
('Ficção histórica', true),
('Humor e comédia', true),
('Ficção LGBTQ', true),
('Ficção Literária', true),
('Realismo mágico', true),
('Mashups', true),
('Mistério e crime', true),
('Mistérios aconchegantes', true),
('Mistérios Históricos', true),
('Peças e roteiros', true),
('Poesia', true),
('Romance', true),
('Romance contemporâneo', true),
('Romance histórico', true),
('Romance Paranormal', true),
('Comédia romântica', true),
('Suspense Romântico', true),
('Romance de viagem no tempo', true),
('Ficção científica', true),
('Distópico', true),
('Ficção científica militar', true),
('Pós-apocalíptico', true),
('Space Opera', true),
('Steampunk (e outros gêneros punk na ficção)', true),
('Viagem no tempo', true),
('Contos', true),
('Temáticas e motivações', true),
('Thriller e Suspense', true),
('Espionagem', true),
('Horror', true),
('Terror', true),
('Thriller legal', true),
('Thriller médico', true),
('Thriller psicológico', true),
('Technothriller', true),
('Ficção Feminina', true),
('Chick Lit', true),
('Jovem adulto', true),
('Novo Adulto', true),
('Questões Sociais e Familiares', true),
('Fantasia para jovens adultos', true),
('Agricultura', false),
('Biografias e memórias', false),
('Gestão de negócios', false),
('Guias de carreira', false),
('Não-ficção infantil', false),
('Quadrinhos não ficção', false),
('Computadores e Internet', false),
('Culinária, comida, vinho e bebidas espirituosas', false),
('Faça você mesmo e artesanato', false),
('Projeto', false),
('Educação e Referência', false),
('Entretenimento', false),
('Saúde e Bem-Estar', false),
('Casa e jardim', false),
('Humanidades e Ciências Sociais', false),
('Arte', false),
('História', false),
('Lei', false),
('Música', false),
('Filosofia', false),
('Ciência Política e Atualidades', false),
('Psicologia', false),
('Sociologia', false),
('Inspirador', false),
('LGBTQ Não Ficção', false),
('Matemática e Ciências', false),
('Ciências da Terra, do Espaço e do Meio Ambiente', false),
('Engenharia', false),
('Contabilidade Finanças', false),
('Medicina, enfermagem e odontologia', false),
('Natureza', false),
('Nova era', false),
('Paternidade e famílias', false),
('Fotografia', false),
('Religião e Espiritualidade', false),
('Ateísmo', false),
('Não-ficção cristã', false),
('Autoajuda e autoaprimoramento', false),
('Sexo e Relacionamentos', false),
('Esportes e atividades ao ar livre', false),
('Tecnologia', false),
('Viajar por', false),
('Crime Verdadeiro', false),
('Casamentos', false),
('Escrita e Publicação', false); 

create table categoria(
	idcategoria serial primary key,
	descricao varchar(50)
);

insert into categoria(descricao)
	values ('Livros'), 
	('Folhetos'), 
	('Braille'),
	('TCC (Graduação)'), 
	('TCCP (Pós-Graduação)'),
	('Normas'), 
	('Catálogos'), 
	('Artigos'), 
	('Periódicos'), 
	('Diários oficiais'), 
	('Acervo digital'), 
	('CDs'), 
	('DVDs'), 
	('Mapas'), 
	('Teses'),
	('Relatórios'),	
	('Dissertações');

create table acervo (
	idacervo serial primary key,
	isbn varchar(13),
	tituloacervo varchar(50),
	numEdicaoacervo int,
	anoPublicacaoacervo int,
	resumoacervo varchar(200),
	proibidoMenor boolean,
	subTituloacervo varchar(50),
	qtdPaginas int,
	numVolumeacervo int,
	corCapa varchar(20), 
	ativo boolean,
	idgenero integer references genero(idgenero),
	ideditora integer references editora(ideditora),
	idcategoria int references categoria(idcategoria)
); 


-- aceita ISBN-10 ou 13 (sem hífens), com 'X' no ISBN-10
ALTER TABLE acervo
  ADD CONSTRAINT chk_isbn_format
  CHECK (isbn ~ '^(97(8|9))?\d{9}(\d|X)$');

ALTER TABLE acervo
  ADD CONSTRAINT uq_acervo_isbn UNIQUE (isbn);

-- carga de acervo
-- 4.1) Dom Casmurro
INSERT INTO acervo (
  isbn, tituloacervo, numedicaoacervo, anopublicacaoacervo,
  resumoacervo, proibidomenor, subtituloacervo, qtdpaginas,
  numvolumeacervo, corcapa, ativo, idgenero, ideditora, idcategoria
) VALUES (
  '9788535921234', 'Dom Casmurro', 1, 1899,
  'Romance clássico brasileiro.', false, NULL, 288,
  1, 'Verde', true,
  (SELECT idgenero  FROM genero   WHERE descricaogenero = 'Clássicos' LIMIT 1),
  (SELECT ideditora FROM editora  WHERE nomeeditora = 'Companhia das Letras' LIMIT 1),
  (SELECT idcategoria FROM categoria WHERE descricao = 'Livros' LIMIT 1)
);

-- 4.2) O Hobbit
INSERT INTO acervo (
  isbn, tituloacervo, numedicaoacervo, anopublicacaoacervo,
  resumoacervo, proibidomenor, subtituloacervo, qtdpaginas,
  numvolumeacervo, corcapa, ativo, idgenero, ideditora, idcategoria
) VALUES (
  '9780547928227', 'O Hobbit', 1, 1937,
  'A aventura de Bilbo Bolseiro.', false, NULL, 320,
  1, 'Verde', true,
  (SELECT idgenero  FROM genero   WHERE descricaogenero = 'Fantasia' LIMIT 1),
  (SELECT ideditora FROM editora  WHERE nomeeditora = 'HarperCollins Brasil' LIMIT 1),
  (SELECT idcategoria FROM categoria WHERE descricao = 'Livros' LIMIT 1)
);

-- 4.3) Algorithms (4th Edition)
INSERT INTO acervo (
  isbn, tituloacervo, numedicaoacervo, anopublicacaoacervo,
  resumoacervo, proibidomenor, subtituloacervo, qtdpaginas,
  numvolumeacervo, corcapa, ativo, idgenero, ideditora, idcategoria
) VALUES (
  '9780321573513', 'Algorithms', 4, 2011,
  'Estruturas de dados e algoritmos, 4ª edição.', false, 'Fourth Edition', 976,
  1, 'Azul', true,
  (SELECT idgenero  FROM genero   WHERE descricaogenero = 'Computadores e Internet' LIMIT 1),
  (SELECT ideditora FROM editora  WHERE nomeeditora = 'Pearson' LIMIT 1),
  (SELECT idcategoria FROM categoria WHERE descricao = 'Livros' LIMIT 1)
);

-- 4.4) A Arte da Guerra
INSERT INTO acervo (
  isbn, tituloacervo, numedicaoacervo, anopublicacaoacervo,
  resumoacervo, proibidomenor, subtituloacervo, qtdpaginas,
  numvolumeacervo, corcapa, ativo, idgenero, ideditora, idcategoria
) VALUES (
  '0307465357', 'A Arte da Guerra', 1, 2009,
  'Tratado clássico de estratégia.', false, NULL, 288,
  1, 'Preta', true,
  (SELECT idgenero  FROM genero   WHERE descricaogenero = 'Filosofia' LIMIT 1),
  (SELECT ideditora FROM editora  WHERE nomeeditora = 'Martins Fontes' LIMIT 1),
  (SELECT idcategoria FROM categoria WHERE descricao = 'Livros' LIMIT 1)
);

create table autor
(
	idautor serial primary key,
	nomeautor varchar(100) not null,
	descricaoautor varchar(200)
);

insert into autor (nomeautor, descricaoautor) VALUES
  ('Machado de Assis', 'Clássico da literatura brasileira'),
  ('J. R. R. Tolkien', 'Autor de O Hobbit e O Senhor dos Anéis'),
  ('Robert Sedgewick', 'Coautor de Algorithms, Princeton'),
  ('Kevin Wayne', 'Coautor de Algorithms, Princeton'),
  ('Sun Tzu', 'Autor de A Arte da Guerra');

--Tabela de junção Autor x Acervo
create table escreve (
  idacervo  int NOT NULL REFERENCES acervo(idacervo) ON DELETE CASCADE,
  idautor   int NOT NULL REFERENCES autor(idautor)  ON DELETE CASCADE,
  primary key (idacervo, idautor)
);
create INDEX IF NOT EXISTS idx_escreve_idautor  ON escreve(idautor);
create INDEX IF NOT EXISTS idx_escreve_idacervo ON escreve(idacervo);

-- carga tabela escreve
-- Dom Casmurro -> Machado de Assis
INSERT INTO escreve (idacervo, idautor)
SELECT a.idacervo, au.idautor
  FROM acervo a, autor au
 WHERE a.isbn = '9788535921234'
   AND au.nomeautor IN ('Machado de Assis');

-- O Hobbit -> Tolkien
INSERT INTO escreve (idacervo, idautor)
SELECT a.idacervo, au.idautor
  FROM acervo a, autor au
 WHERE a.isbn = '9780547928227'
   AND au.nomeautor IN ('J. R. R. Tolkien');

-- Algorithms -> Sedgewick & Wayne
INSERT INTO escreve (idacervo, idautor)
SELECT a.idacervo, au.idautor
  FROM acervo a, autor au
 WHERE a.isbn = '9780321573513'
   AND au.nomeautor IN ('Robert Sedgewick', 'Kevin Wayne');

-- A Arte da Guerra -> Sun Tzu
INSERT INTO escreve (idacervo, idautor)
SELECT a.idacervo, au.idautor
  FROM acervo a, autor au
 WHERE a.isbn = '0307465357'
   AND au.nomeautor IN ('Sun Tzu');


-- tabela exemplar

CREATE TABLE exemplar (
    idTombo        VARCHAR(20) PRIMARY KEY,
    idAcervo       INTEGER NOT NULL REFERENCES acervo(idAcervo) ON DELETE CASCADE,
    situacao       VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
    observacao     VARCHAR(255),
    criado_em      TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_situacao_exemplar CHECK (situacao IN (
        'DISPONIVEL','EMPRESTADO','RESERVADO',
        'DANIFICADO','DESCONTINUADO','MANUTENCAO',
        'EXTRAVIADO','HIGIENIZACAO','DOADO'
    ))
);

-- TRIGGER para manter atualizado_em em updates
CREATE OR REPLACE FUNCTION trg_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.atualizado_em := NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_exemplar_updated_at ON exemplar;
CREATE TRIGGER set_exemplar_updated_at
BEFORE UPDATE ON exemplar
FOR EACH ROW
EXECUTE FUNCTION trg_set_updated_at();

-- Tabela de Empréstimos
CREATE TABLE emprestimo (
  idemprestimo           SERIAL PRIMARY KEY,
  idtombo                VARCHAR(20) NOT NULL REFERENCES exemplar(idTombo) ON DELETE RESTRICT,
  idusuario              INT NOT NULL REFERENCES usuario(idusuario) ON DELETE RESTRICT,
  dataemprestimo         DATE NOT NULL DEFAULT CURRENT_DATE,
  dataprevistadevolucao  DATE NOT NULL,
  datadevolucao          DATE NULL,
  observacao             VARCHAR(255),
  reserva                BOOLEAN NOT NULL DEFAULT FALSE
);

-- Um exemplar não pode ter dois empréstimos ativos ao mesmo tempo
-- (ativo = sem data de devolução)
CREATE UNIQUE INDEX uq_emprestimo_tombo_ativo
  ON emprestimo (idtombo)
  WHERE datadevolucao IS NULL;

-- Índices de busca
CREATE INDEX idx_emprestimo_usuario ON emprestimo(idusuario);
CREATE INDEX idx_emprestimo_ativo   ON emprestimo(idtombo) WHERE datadevolucao IS NULL;
