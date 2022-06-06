CREATE DATABASE cameras;
CREATE USER cameras_user WITH PASSWORD 'my_super_uber_password';
GRANT ALL PRIVILEGES ON DATABASE cameras TO cameras_user;
\c cameras
CREATE SCHEMA cameras AUTHORIZATION cameras_user;
SET search_path TO cameras;
