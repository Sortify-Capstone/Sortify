# Gunakan Nginx sebagai base image
FROM nginx

# Copy konfigurasi Nginx ke direktori kontainer yang sesuai
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy file statis ke direktori kontainer yang sesuai
COPY static /usr/share/nginx/html

# Instal Node.js dan dependensi
RUN apt-get update && apt-get install -y curl && \
    curl -sL https://deb.nodesource.com/setup_14.x | bash - && \
    apt-get install -y nodejs

# Set direktori kerja ke aplikasi Hapi.js
WORKDIR /app

# Salin package.json dan package-lock.json
COPY package*.json ./

# Instal dependensi Hapi.js
RUN npm install

# Menyalin seluruh isi direktori saat ini ke dalam container
COPY . .

# Menentukan port yang akan digunakan
EXPOSE 8080
EXPOSE 3000

# Menjalankan Nginx dan node saat container berjalan
CMD service nginx start && npm run start

