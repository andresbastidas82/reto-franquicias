# =============================================================================
# ecr.tf - Elastic Container Registry
# Registro privado de imágenes Docker donde se almacena la imagen de la app.
# ECS descarga la imagen desde aquí al desplegar las tareas.
# El escaneo al subir está habilitado para detectar vulnerabilidades.
# =============================================================================

resource "aws_ecr_repository" "app" {
  name                 = "${var.project_name}-${var.environment}"
  image_tag_mutability = "MUTABLE"   # Permite sobrescribir tags (ej: "latest")
  force_delete         = true        # Permite eliminar aunque tenga imágenes (solo dev)

  image_scanning_configuration {
    scan_on_push = true # Escanear imágenes automáticamente al subirlas
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-ecr"
  }
}
