CREATE TABLE T_TOPIC(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  name                    VARCHAR(100) NOT NULL,
  parent_fk               BIGINT,

  UNIQUE(parent_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

ALTER TABLE T_TOPIC ADD CONSTRAINT T_TOPIC__parent_fk FOREIGN KEY (parent_fk) REFERENCES T_TOPIC(id);

ALTER TABLE T_STORY ADD COLUMN topic_fk BIGINT;
ALTER TABLE T_STORY ADD CONSTRAINT T_STORY__topic_fk FOREIGN KEY (topic_fk) REFERENCES T_TOPIC(id);



INSERT INTO T_TOPIC(id, parent_fk, name) VALUES
    (100, null, 'art-entertainment')
  , (101, 100, 'art')
  , (102, 100, 'beauty')
  , (103, 100, 'books')
  , (104, 100, 'comics')
  , (105, 100, 'culture')
  , (106, 100, 'film')
  , (107, 100, 'food')
  , (108, 100, 'gaming')
  , (109, 100, 'humor')
  , (110, 100, 'maker')
  , (111, 100, 'music')
  , (112, 100, 'photograpgy')
  , (113, 100, 'podcast')
  , (115, 100, 'poetry')
  , (116, 100, 'social-media')
  , (117, 100, 'sports')
  , (118, 100, 'style')
  , (119, 100, 'true-crime')
  , (120, 100, 'tv')
  , (121, 100, 'writing')


  , (200, null, 'industry')
  , (201, 200, 'biotech')
  , (202, 200, 'business')
  , (203, 200, 'design')
  , (204, 200, 'economy')
  , (205, 200, 'freelancing')
  , (206, 200, 'leadership')
  , (207, 200, 'marketing')
  , (208, 200, 'product-management')
  , (209, 200, 'productivity')
  , (210, 200, 'remote-work')
  , (211, 200, 'startups')
  , (212, 200, 'venture-capital')
  , (213, 200, 'work')

  , (300, null, 'innovation-tech')
  , (301, 300, 'accessibility')
  , (302, 300, 'android-dev')
  , (303, 300, 'artificial-intelligence')
  , (304, 300, 'blockchain')
  , (305, 300, 'cryptocurrency')
  , (306, 300, 'data-science')
  , (307, 300, 'digital-life')
  , (308, 300, 'gadgets')
  , (309, 300, 'ios-dev')
  , (310, 300, 'java')
  , (311, 300, 'javascript')
  , (312, 300, 'machine-learning')
  , (313, 300, 'math')
  , (314, 300, 'neuro-science')
  , (315, 300, 'programing')
  , (316, 300, 'science')
  , (317, 300, 'self-driving-car')
  , (318, 300, 'software-engineering')
  , (319, 300, 'space')
  , (320, 300, 'technology')
  , (321, 300, 'ux')
  , (322, 300, 'visual-design')

  , (400, null, 'life')
  , (401, 400, 'addition')
  , (402, 400, 'cannabis')
  , (403, 400, 'creativity')
  , (404, 400, 'disability')
  , (405, 400, 'family')
  , (406, 400, 'fitness')
  , (407, 400, 'health')
  , (408, 400, 'lifestyle')
  , (409, 400, 'mental-health')
  , (410, 400, 'mindfullness')
  , (411, 400, 'money')
  , (412, 400, 'outdoor')
  , (413, 400, 'parenting')
  , (414, 400, 'pets')
  , (415, 400, 'psychology')
  , (416, 400, 'relationship')
  , (417, 400, 'self')
  , (418, 400, 'sexuality')
  , (419, 400, 'spirituality')
  , (420, 400, 'travel')


  , (500, null, 'society')
  , (501, 500, 'cities')
  , (502, 500, 'coronavirus')
  , (503, 500, 'education')
  , (504, 500, 'environment')
  , (505, 500, 'equality')
  , (506, 500, 'future')
  , (507, 500, 'history')
  , (508, 500, 'immigration')
  , (509, 500, 'justice')
  , (510, 500, 'language')
  , (511, 500, 'lgbtq')
  , (512, 500, 'media')
  , (513, 500, 'philosophy')
  , (514, 500, 'politics')
  , (515, 500, 'privacy')
  , (516, 500, 'race')
  , (517, 500, 'religion')
  , (518, 500, 'society')
  , (519, 500, 'transportation')
  , (520, 500, 'world')
;

