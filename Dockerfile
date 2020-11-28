FROM payara/server-full:5.192
RUN wget --no-verbose -O postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.2.5.jar && mv postgresql.jar ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/lib
COPY init_0_create_datasource.sh ${SCRIPT_DIR}
COPY ui/target/car.war ${DEPLOY_DIR}
