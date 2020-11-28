FROM postgres:11.2
ENV POSTGRES_PASSWORD=secret
COPY target/classes/delimited_create.sql /docker-entrypoint-initdb.d/
COPY docker-entrypoint-initdb.d/import.sql /docker-entrypoint-initdb.d/