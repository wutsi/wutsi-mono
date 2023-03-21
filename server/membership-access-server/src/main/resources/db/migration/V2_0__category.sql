CREATE TABLE T_CATEGORY(
    id              SERIAL NOT NULL,

    title           VARCHAR(100),
    title_french    VARCHAR(100),

    PRIMARY KEY (id)
);

INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1000,'Advertising/Marketing','Marketing publicitaire');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1001,'Agriculture','Agriculture');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1002,'Arts and Entertainment','Les arts et le divertissement');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1003,'Automotive, Aircraft and Boat','Automobile, aéronautique et bateau');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1004,'Beauty, Cosmetic and Personal Care','Beauté, Cosmétique et Soins Personnels');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1005,'Commercial and Industrial','Commercial et Industriel');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1006,'Education','Éducation');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1007,'Finance','Finance');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1008,'Food and Beverage','Nourriture et boisson');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1009,'Hotel and Lodging','Hôtel et Logement');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1010,'Legal','Légal');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1011,'Local Service','Service local');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1012,'Media/News Company','Société de médias/d''actualités');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1013,'Medical and Health','Médical et Santé');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1014,'Non-Government Organization','Organisation non Gouvernementale');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1015,'Nonprofit Organization','Organisation à but non lucratif');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1016,'Public and Government Services','Services publics et gouvernementaux');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1017,'Real Estate','Immobilier');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1018,'Science, Technology and Engineering','Science, Technologie et Ingénierie');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1019,'Shopping and Retail','Achats et Vente au Détail');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1020,'Sports and Recreation','Sports et loisirs');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1021,'Travel and Transportation','Voyage et Transport');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1022,'Armed Forces','Forces Armées');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1023,'Charity Organization','Organisation Aaritative');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1024,'Community Service','Service Publique');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1025,'Country Club / Clubhouse','Country Club / Club-house');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1026,'Environmental Conservation Organization','Organisation de conservation de l''environnement');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1027,'Labor Union','Syndicat de Travail');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1028,'Private Members Club','Club des Membres Privés');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1029,'Religious Organization','Organisation Religieuse');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1030,'Social Club','Club social');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1031,'Sorority and Fraternity','Sororité et Fraternité');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1032,'Sports Club','Club de sport');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1033,'Youth Organization','Organisation de jeunesse');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1034,'Art','Art');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1035,'Book and Magazine','Livre et revue');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1036,'Music','Musique');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1037,'Show','Montrer');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1038,'TV and Movies','Télévision et films');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1039,'ATM','AU M');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1040,'Campus Building','Bâtiment du campus');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1041,'City Infrastructure','Infrastructures de la ville');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1042,'Landmark and Historical Places','Monuments et lieux historiques');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1043,'Locality','Localité');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1044,'Meeting Room','Salle de réunion');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1045,'Outdoor Recreation','Loisirs de plein air');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1046,'Public Toilet','Toilette Publique');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1047,'Religious Place of Worship','Lieu de Culte Religieux');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1048,'Residence','Résidence');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1049,'Brand','Marque');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1050,'Cause','Cause');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1051,'Just for Fun','Juste pour le Fun');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1052,'Public Figure','Personnalité Publique');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1053,'Model','Modèle');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1054,'Writer','Écrivain');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1055,'Financial Services','Services Financiers');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1056,'Restaurant','Restaurant');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1057,'Bakery','Pâtisserie');

UPDATE T_CATEGORY SET title_french= 'Marketing/Publicité' WHERE id=1000;
UPDATE T_CATEGORY SET title_french= 'Arts et Divertissement' WHERE id=1002;
UPDATE T_CATEGORY SET title_french= 'Automobile, Aéronautique et Bateau' WHERE id=1003;
UPDATE T_CATEGORY SET title_french= 'Nourriture et Boisson' WHERE id=1008;
UPDATE T_CATEGORY SET title_french= 'Service Locaux' WHERE id=10011;
UPDATE T_CATEGORY SET title_french= 'Société de Presse/Médias' WHERE id=10012;
UPDATE T_CATEGORY SET title_french= 'Services Publics et Gouvernementaux' WHERE id=10016;
UPDATE T_CATEGORY SET title_french= 'Sports et Loisir' WHERE id=10020;
UPDATE T_CATEGORY SET title_french= 'Organisation Caritative' WHERE id=10023;
UPDATE T_CATEGORY SET title_french= 'Country Club / Clubhouse' WHERE id=10025;
UPDATE T_CATEGORY SET title_french= 'Organisation de Conservation de l''Environnement' WHERE id=10026;
UPDATE T_CATEGORY SET title_french= 'Club Social' WHERE id=10030;
UPDATE T_CATEGORY SET title_french= 'Club de Sport' WHERE id=10032;
UPDATE T_CATEGORY SET title_french= 'Organisation de Jeunes' WHERE id=10033;
UPDATE T_CATEGORY SET title_french= 'Livres et Revues' WHERE id=10034;
UPDATE T_CATEGORY SET title_french= 'Spectacles' WHERE id=10037;
UPDATE T_CATEGORY SET title_french= 'Télévision et Films' WHERE id=10030;
DELETE FROM T_CATEGORY WHERE id=1039; -- ATM
DELETE FROM T_CATEGORY WHERE id=1040; -- Campus Building
DELETE FROM T_CATEGORY WHERE id=1041; -- City Infrastructure
DELETE FROM T_CATEGORY WHERE id=1042; -- Landmark and Historical Places
DELETE FROM T_CATEGORY WHERE id=1043; -- Locality
DELETE FROM T_CATEGORY WHERE id=1044; -- Meeting Room
DELETE FROM T_CATEGORY WHERE id=1045; -- Outdoor Recreation
DELETE FROM T_CATEGORY WHERE id=1046; -- Public Toilet
DELETE FROM T_CATEGORY WHERE id=1047; -- Religious Place of Worship
DELETE FROM T_CATEGORY WHERE id=1048; -- Residence
UPDATE T_CATEGORY SET title_french= 'Juste pour Rire' WHERE id=10051;

