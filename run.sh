#!/bin/bash

# Construir y ejecutar los contenedores
docker-compose up --build -d

# Esperar a que todos los servicios estén disponibles
echo "Esperando a que todos los servicios estén disponibles..."
sleep 30

echo "Servicios disponibles:"
echo "- Eureka Server: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- User Service: http://localhost:8081"
echo "- Space Service: http://localhost:8082"
echo "- Reservation Service: http://localhost:8083"

echo "Endpoints disponibles a través del API Gateway:"
echo "- Usuarios: http://localhost:8080/api/users"
echo "- Espacios: http://localhost:8080/api/spaces"
echo "- Reservas: http://localhost:8080/api/reservations"

echo "Para detener los servicios, ejecuta: docker-compose down"

