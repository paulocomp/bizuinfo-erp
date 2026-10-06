FROM tomee:10-jre21-plus

RUN rm -rf /usr/local/tomee/webapps/*

COPY bizuinfo-web/target/bizuinfo.erp.war /usr/local/tomee/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]