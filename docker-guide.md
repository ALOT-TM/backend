# Guía de Docker para IAM

Comandos esenciales para ejecutar el proyecto con Docker.

## Prerrequisitos

- Docker y Docker Compose instalados
- Archivo `.env` configurado (ya existe en el proyecto)

## Comandos Principales

### 🏗️ Construir la imagen

```bash
docker compose build
```

### ▶️ Iniciar la aplicación

```bash
docker compose up -d
```

### 📋 Ver logs

```bash
docker compose logs -f
```

### ⏹️ Detener la aplicación

```bash
docker compose stop
```

### 🔄 Reiniciar la aplicación

```bash
docker compose restart
```

### 🗑️ Eliminar contenedores

```bash
docker compose down
```

### 🔨 Reconstruir y ejecutar

```bash
docker compose up -d --build
```

## Acceso a la Aplicación

- **Aplicación**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## Comandos de Diagnóstico

### Ver estado de contenedores

```bash
docker compose ps
```

### Entrar al contenedor

```bash
docker exec -it moni-container sh
```

### Ver todos los logs desde el inicio

```bash
docker compose logs
```

## Limpieza Completa

### Eliminar contenedor, imagen y volúmenes

```bash
docker compose down --rmi all --volumes
```

### Limpiar sistema Docker completo

```bash
docker system prune -a --volumes
```

## Notas Importantes

⚠️ **El archivo `.env` debe existir** antes de ejecutar los comandos. Ya está configurado en el proyecto.

⚠️ **El `.env` está en `.gitignore`** y no se sube a Git por seguridad.

⚠️ **Usa `.env.example`** como plantilla si necesitas compartir la configuración con otros desarrolladores.