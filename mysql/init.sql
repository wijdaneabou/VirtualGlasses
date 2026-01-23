CREATE DATABASE IF NOT EXISTS glasses_db;
USE glasses_db;

CREATE TABLE FaceShapes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shape_name VARCHAR(50) NOT NULL,
    description TEXT,
    forme VARCHAR(50) NOT NULL
);

CREATE TABLE TypeGlasses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    Type VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE Glasses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type_id INT,
    face_shape_id INT NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    FOREIGN KEY (type_id) REFERENCES TypeGlasses(id),
    FOREIGN KEY (face_shape_id) REFERENCES FaceShapes(id)
);

CREATE TABLE Recommendations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    face_shape_id INT NOT NULL,
    glasses_id INT NOT NULL,
    FOREIGN KEY (face_shape_id) REFERENCES FaceShapes(id),
    FOREIGN KEY (glasses_id) REFERENCES Glasses(id)
);

-- Données initiales pour FaceShapes
INSERT INTO FaceShapes (shape_name, description, forme) VALUES 
('Rond', 'Visage avec une largeur et une longueur similaires, joues arrondies', 'rond'),
('Ovale', 'Visage équilibré avec un front légèrement plus large que le menton', 'oval'),
('Carré', 'Visage avec des angles prononcés et une mâchoire forte', 'carré'),
('Rectangle', 'Visage allongé avec front, joues et mâchoire de largeur similaire', 'rectangle'),
('Cœur', 'Visage avec un front large et un menton pointu', 'coeur');

-- Données initiales pour TypeGlasses
INSERT INTO TypeGlasses (Type, description) VALUES 
('Vue', 'Lunettes correctrices pour ameliorer la vision'),
('Soleil', 'Lunettes protégeant des rayons UV');

-- Lunettes pour visage carré
INSERT INTO Glasses (name, type_id, face_shape_id, image_path) VALUES
('Model 1', 1, 3, '/Glasses/Carre/Vue/image (1).jpg'),
('Model 2', 1, 3, '/Glasses/Carre/Vue/image (2).jpg'),
('Model 3', 1, 3, '/Glasses/Carre/Vue/image (3).jpg'),
('Model 4', 1, 3, '/Glasses/Carre/Vue/image (4).jpg'),
('Model 5', 1, 3, '/Glasses/Carre/Vue/image (5).jpg'),
('Model 6', 1, 3, '/Glasses/Carre/Vue/image (6).jpg'),
('Model 7', 1, 3, '/Glasses/Carre/Vue/image (7).jpg'),

('Model 1', 2, 3, '/Glasses/Carre/Soleil/image (1).jpg'),
('Model 2', 2, 3, '/Glasses/Carre/Soleil/image (2).jpg'),
('Model 3', 2, 3, 'Glasses/Carre/Soleil/image (3).jpg'),
('Model 4', 2, 3, '/Glasses/Carre/Soleil/image (4).jpg'),
('Model 5', 2, 3, '/Glasses/Carre/Soleil/image (5).jpg'),
('Model 6', 2, 3, 'Glasses/Carre/Soleil/image (6).jpg'),
('Model 7', 2, 3, 'Glasses/Carre/Soleil/image (7).jpg'),
('Model 8', 2, 3, 'Glasses/Carre/Soleil/image (8).jpg');

-- Lunettes pour visage ovale
INSERT INTO Glasses (name, type_id, face_shape_id, image_path) VALUES
('Model 1', 1, 2, '/Glasses/Oval/Vue/image (1).jpg'),
('Model 2', 1, 2, '/Glasses/Oval/Vue/image (2).jpg'),
('Model 3', 1, 2, '/Glasses/Oval/Vue/image (3).jpg'),
('Model 1', 2, 2, '/Glasses/Oval/Soleil/image (1).jpg'),
('Model 2', 2, 2, '/Glasses/Oval/Soleil/image (2).jpg'),
('Model 3', 2, 2, '/Glasses/Oval/Soleil/image (3).jpg');
--Coeur
INSERT INTO Glasses (name, type_id, face_shape_id, image_path) VALUES
('Model 1', 1, 2, '/Glasses/Coeur/Vue/image (1).jpg'),
('Model 2', 1, 2, '/Glasses/Coeur/Vue/image (2).jpg'),
('Model 3', 1, 2, '/Glasses/Coeur/Vue/image (3).jpg'),
('Model 1', 2, 2, '/Glasses/Coeur/Soleil/image (1).jpg'),
('Model 2', 2, 2, '/Glasses/Coeur/Soleil/image (2).jpg'),
('Model 3', 2, 2, '/Glasses/Coeur/Soleil/image (3).jpg');

--Rond
INSERT INTO Glasses (name, type_id, face_shape_id, image_path) VALUES
('Model 1', 1, 2, '/Glasses/Rond/Vue/image (1).jpg'),
('Model 2', 1, 2, '/Glasses/Rond/Vue/image (2).jpg'),
('Model 3', 1, 2, '/Glasses/Rond/Vue/image (3).jpg'),
('Model 1', 2, 2, '/Glasses/Rond/Soleil/image (1).jpg'),
('Model 2', 2, 2, '/Glasses/Rond/Soleil/image (2).jpg'),
('Model 3', 2, 2, '/Glasses/Rond/Soleil/image (3).jpg');


--Rectangulaire
INSERT INTO Glasses (name, type_id, face_shape_id, image_path) VALUES
('Model 1', 1, 2, '/Glasses/Rectangulaire/Vue/image (1).jpg'),
('Model 2', 1, 2, '/Glasses/Rectangulaire/Vue/image (2).jpg'),
('Model 3', 1, 2, '/Glasses/Rectangulaire/Vue/image (3).jpg'),
('Model 1', 2, 2, '/Glasses/Rectangulaire/Soleil/image (1).jpg'),
('Model 2', 2, 2, '/Glasses/Rectangulaire/Soleil/image (2).jpg'),
('Model 3', 2, 2, '/Glasses/Rectangulaire/Soleil/image (3).jpg');

-

-- Pour visage Carré (face_shape_id = 3)
INSERT INTO Recommendations (face_shape_id, glasses_id) VALUES
  (3,  1),
  (3,  2),
  (3,  3),
  (3,  4),
  (3,  5),
  (3,  6),
  (3,  7),
  (3,  8),
  (3,  9),
  (3, 10),
  (3, 11),
  (3, 12),
  (3, 13),
  (3, 14),
  (3, 15);
oval:

INSERT INTO Recommendations (face_shape_id, glasses_id) VALUES
(2, 16),
(2, 17),
(2, 18),
(2, 19),
(2, 20),
(2, 21);

Rond

INSERT INTO Recommendations (face_shape_id, glasses_id) VALUES
  (1, 22),
  (1, 23),
  (1, 24),
  (1, 25),
  (1, 26),
  (1, 27);
coeur
INSERT INTO Recommendations (face_shape_id, glasses_id) VALUES
  (5, 28),
  (5, 29),
  (5, 30),
  (5, 31),
  (5, 32),
  (5, 33);
rectangle
INSERT INTO Recommendations (face_shape_id, glasses_id) VALUES
  (4, 34),
  (4, 35),
  (4, 36),
  (4, 37),
  (4, 38),
  (4, 39);
  





