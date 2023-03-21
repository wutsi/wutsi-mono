# Script run by CI/CD to create the databases

DROP IF EXISTS membership;
DROP IF EXISTS marketplace;
DROP IF EXISTS security;
DROP IF EXISTS checkout;

CREATE DATABASE membership;
CREATE DATABASE marketplace;
CREATE DATABASE security;
DROP IF EXISTS checkout;
