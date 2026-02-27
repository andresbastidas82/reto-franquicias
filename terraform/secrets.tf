# =============================================================================
# secrets.tf - AWS Secrets Manager
# Almacena las credenciales de la base de datos de forma segura en lugar
# de pasarlas como variables de entorno en texto plano en la task definition.
#
# ECS inyecta los valores del secreto como variables de entorno en tiempo
# de ejecución. El secreto contiene un JSON con DB_URL, DB_USERNAME, DB_PASSWORD.
#
# recovery_window_in_days = 0 permite eliminación inmediata (solo dev).
# En producción, usar 7-30 días para protección contra eliminación accidental.
# =============================================================================

# Contenedor del secreto (metadatos)
resource "aws_secretsmanager_secret" "db_credentials" {
  name                    = "${var.project_name}-${var.environment}-db-credentials"
  recovery_window_in_days = 0 # Eliminación inmediata permitida (solo dev)

  tags = {
    Name = "${var.project_name}-${var.environment}-db-credentials"
  }
}

# Valor del secreto - JSON con los datos de conexión a la base de datos
# El endpoint de RDS y las credenciales se almacenan aquí
resource "aws_secretsmanager_secret_version" "db_credentials" {
  secret_id = aws_secretsmanager_secret.db_credentials.id
  secret_string = jsonencode({
    DB_URL      = "${aws_db_instance.postgres.address}:${aws_db_instance.postgres.port}/${var.db_name}"
    DB_USERNAME = var.db_username
    DB_PASSWORD = var.db_password
  })
}
