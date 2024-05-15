-- topic=artificial-intelligence -> category=2111
UPDATE T_STORY set category_fk=2111 WHERE topic_fk IN (select id from T_TOPIC where name='artificial-intelligence');

-- topic=programming -> category=2112
UPDATE T_STORY set category_fk=2112 WHERE topic_fk IN (select id from T_TOPIC where name in ('programing'));

-- topic=software-engineeering -> category=2119
UPDATE T_STORY set category_fk=2119 WHERE topic_fk IN (select id from T_TOPIC where name in ('software-engineering'));

-- topic=program-management -> category=2120
UPDATE T_STORY set category_fk=2120 WHERE topic_fk IN (select id from T_TOPIC where name in ('program-management'));

-- topic=can2023|chan2021 -> category=2020
UPDATE T_STORY set category_fk=2020 WHERE topic_fk IN (select id from T_TOPIC where name in ('can2023', 'chan2021'));

-- topic=sports -> category=2000I
UPDATE T_STORY set category_fk=2000 WHERE topic_fk IN (select id from T_TOPIC where name in ('sports'));

-- topic=politics -> category=1813
UPDATE T_STORY set category_fk=1813 WHERE topic_fk IN (select id from T_TOPIC where name in ('politics'));

-- topic=justice -> category=1815
UPDATE T_STORY set category_fk=1815 WHERE topic_fk IN (select id from T_TOPIC where name in ('justice'));

-- topic=culture -> category=1715
UPDATE T_STORY set category_fk=1715 WHERE topic_fk IN (select id from T_TOPIC where name in ('culture'));

-- topic=lifestyle -> category=1900
UPDATE T_STORY set category_fk=1900 WHERE topic_fk IN (select id from T_TOPIC where name in ('lifestyle'));

-- topic=health -> category=2224
UPDATE T_STORY set category_fk=2224 WHERE topic_fk IN (select id from T_TOPIC where name in ('health'));

-- topic=coronavirus -> category=2211
UPDATE T_STORY set category_fk=2211 WHERE topic_fk IN (select id from T_TOPIC where name in ('coronavirus'));

-- topic=media -> category=2221
UPDATE T_STORY set category_fk=2221 WHERE topic_fk IN (select id from T_TOPIC where name in ('coronavirus'));

-- topic=sexuality -> category=2225
UPDATE T_STORY set category_fk=2225 WHERE topic_fk IN (select id from T_TOPIC where name in ('sexuality'));

-- topic=music -> category=1716
UPDATE T_STORY set category_fk=1716 WHERE topic_fk IN (select id from T_TOPIC where name in ('music'));

-- topic=society -> category=2200
UPDATE T_STORY set category_fk=2200 WHERE topic_fk IN (select id from T_TOPIC where name in ('society'));

-- topic=world -> category=1816
UPDATE T_STORY set category_fk=1816 WHERE topic_fk IN (select id from T_TOPIC where name in ('world'));

-- topic=tv -> category=1717
UPDATE T_STORY set category_fk=1717 WHERE topic_fk IN (select id from T_TOPIC where name in ('tv'));

-- topic=education -> category=2212
UPDATE T_STORY set category_fk=2212 WHERE topic_fk IN (select id from T_TOPIC where name in ('education'));

-- topic=art -> category=1700
UPDATE T_STORY set category_fk=1700 WHERE topic_fk IN (select id from T_TOPIC where name in ('art'));

-- topic=social-media -> category=2226
UPDATE T_STORY set category_fk=2226 WHERE topic_fk IN (select id from T_TOPIC where name in ('social-media'));

-- topic=cities -> category=2210
UPDATE T_STORY set category_fk=2210 WHERE topic_fk IN (select id from T_TOPIC where name in ('cities'));

-- topic=economy|business -> category=1814
UPDATE T_STORY set category_fk=1814 WHERE topic_fk IN (select id from T_TOPIC where name in ('economy', 'business'));

-- topic=religion -> category=2222
UPDATE T_STORY set category_fk=2222 WHERE topic_fk IN (select id from T_TOPIC where name in ('religion'));

-- topic=relationship -> category=1912
UPDATE T_STORY set category_fk=1912 WHERE topic_fk IN (select id from T_TOPIC where name in ('relationship'));

-- topic=equality -> category=1912
UPDATE T_STORY set category_fk=2223 WHERE topic_fk IN (select id from T_TOPIC where name in ('equality'));

-- topic=blogging -> category=1720
UPDATE T_STORY set category_fk=1720 WHERE topic_fk IN (select id from T_TOPIC where name in ('blogging'));

-- topic=beauty -> category=1921
UPDATE T_STORY set category_fk=1921 WHERE topic_fk IN (select id from T_TOPIC where name in ('beauty'));

-- topic=film -> category=1713
UPDATE T_STORY set category_fk=1713 WHERE topic_fk IN (select id from T_TOPIC where name in ('film'));

-- topic=books -> category=1719
UPDATE T_STORY set category_fk=1719 WHERE topic_fk IN (select id from T_TOPIC where name in ('books'));

-- topic=remote-work -> category=1924
UPDATE T_STORY set category_fk=1924 WHERE topic_fk IN (select id from T_TOPIC where name in ('remote-work'));




-- leschroniquesdemwasita: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='leschroniquesdemwasita');

-- salem: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='salem');

-- marietellaayaba: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='marietellaayaba');

-- leschroniquesdelarrime: Littérature > Romans et nouvelles (1119)
UPDATE T_STORY set category_fk=1119 WHERE user_fk IN (select id from T_USER where name='leschroniquesdelarrime');

-- mariamadorego21: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='mariamadorego21');

-- andychronik: Littérature > Science fiction et fantastique (1123)
UPDATE T_STORY set category_fk=1123 WHERE user_fk IN (select id from T_USER where name='andychronik');

-- daniellemaguia: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='daniellemaguia');

-- lapetiteplume: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='lapetiteplume');

-- nellycarelle: Littérature > Romans et nouvelles (1119)
UPDATE T_STORY set category_fk=1119 WHERE user_fk IN (select id from T_USER where name='nellycarelle');

-- nellycarelle: MURIEL LA PROSTITUE -> Littérature > Littérature érotique(1122)
UPDATE T_STORY set category_fk=1122 WHERE user_fk IN (select id from T_USER where name='nellycarelle') AND title LIKE 'Muriel la prostituée%';

-- miss-diva: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='miss-diva');

-- leshistoiresdebrendaaude: J'étais mariée avec un fantôme -> Littérature > Science fiction et fantastique (1123)
UPDATE T_STORY set category_fk=1123 WHERE user_fk IN (select id from T_USER where name='leshistoiresdebrendaaude') and title like 'J''étais mariée avec un fantôme%';

-- leshistoiresdebrendaaude: Je couchais avec ma cousine -> Littérature > Littérature érotique(1122)
UPDATE T_STORY set category_fk=1122 WHERE user_fk IN (select id from T_USER where name='leshistoiresdebrendaaude') and title like 'Je couchais avec ma cousine%';

-- leshistoiresdebrendaaude: La mystérieuse prostituée -> Littérature > Littérature érotique(1122)
UPDATE T_STORY set category_fk=1122 WHERE user_fk IN (select id from T_USER where name='leshistoiresdebrendaaude') and title like 'La mystérieuse prostituée%';

-- laplumedor: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='laplumedor');

-- nathalieflore: Littérature > Romance (1118)
UPDATE T_STORY set category_fk=1118 WHERE user_fk IN (select id from T_USER where name='nathalieflore');

-- themysteryofthestories: Littérature > Science fiction et fantastique (1123)
UPDATE T_STORY set category_fk=1123 WHERE user_fk IN (select id from T_USER where name='themysteryofthestories');

-- richyplumeram: Littérature > Littérature érotique(1122)
UPDATE T_STORY set category_fk=1122 WHERE user_fk IN (select id from T_USER where name='richyplumeram');

-- leschroniquesdenice: Littérature > Romans et nouvelles (1119)
UPDATE T_STORY set category_fk=1119 WHERE user_fk IN (select id from T_USER where name='leschroniquesdenice');
