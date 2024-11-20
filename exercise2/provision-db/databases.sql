# create databases
CREATE DATABASE IF NOT EXISTS `cs` ;
CREATE DATABASE IF NOT EXISTS `sb`;

# create root user and grant rights
use mysql;
CREATE USER 'cs'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'eda-password';
CREATE USER 'cs'@'%' IDENTIFIED WITH caching_sha2_password BY 'eda-password';
CREATE USER 'sb'@'localhost' IDENTIFIED BY 'eda-password';
CREATE USER 'sb'@'%' IDENTIFIED BY 'eda-password';

GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH privileges;

GRANT ALL PRIVILEGES ON cs.* TO 'cs'@'localhost';
GRANT ALL PRIVILEGES ON cs.* TO 'cs'@'%';
GRANT ALL PRIVILEGES on sb.* TO 'sb'@'localhost';
GRANT ALL PRIVILEGES on sb.* TO 'sb'@'%';

flush privileges;
