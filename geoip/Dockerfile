FROM alpine:3.20

RUN apk add --no-cache curl gzip dcron coreutils tzdata

COPY download-geoip.sh /usr/local/bin/download-geoip.sh
RUN chmod +x /usr/local/bin/download-geoip.sh

RUN echo "0 4 1 * * /usr/local/bin/download-geoip.sh >> /var/log/geoip.log 2>&1" > /etc/crontabs/root

ENTRYPOINT ["/bin/sh", "-c", "/usr/local/bin/download-geoip.sh && crond -f -l 2"]