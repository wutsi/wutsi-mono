# Script run by CI/CD to create the databases

DROP IF EXISTS membership;
DROP IF EXISTS marketplace;
DROP IF EXISTS checkout;

CREATE DATABASE membership;
CREATE DATABASE marketplace;
DROP IF EXISTS checkout;
