# =============================================================================
# rds.tf - Base de datos RDS PostgreSQL
# Crea la instancia de PostgreSQL en las subnets privadas.
#
# Elegible para capa gratuita:
#   - db.t3.micro: 750 horas/mes durante 12 meses
#   - 20 GB de almacenamiento gp2
#   - Sin Multi-AZ (instancia única)
#
# Un parameter group personalizado desactiva SSL obligatorio para que
# el driver R2DBC pueda conectarse sin cifrado (aceptable para dev).
# =============================================================================

# Grupo de subnets - indica a RDS qué subnets usar (subnets privadas)
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-db-subnet"
  subnet_ids = aws_subnet.private[*].id

  tags = {
    Name = "${var.project_name}-${var.environment}-db-subnet"
  }
}

# Parameter group personalizado para desactivar el requisito de SSL
# Por defecto, RDS PostgreSQL 16 obliga conexiones SSL.
# R2DBC se conecta sin SSL, así que desactivamos este requisito.
resource "aws_db_parameter_group" "postgres" {
  name   = "${var.project_name}-${var.environment}-pg16"
  family = "postgres16"

  parameter {
    name  = "rds.force_ssl"
    value = "0" # 0 = SSL no requerido, 1 = SSL obligatorio
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-pg16"
  }
}

# Instancia RDS PostgreSQL
resource "aws_db_instance" "postgres" {
  identifier     = "${var.project_name}-${var.environment}-db"
  engine         = "postgres"
  engine_version = "16.4"
  instance_class = "db.t3.micro" # Elegible para capa gratuita

  allocated_storage     = 20    # 20 GB incluidos en capa gratuita
  max_allocated_storage = 20    # Desactivar autoescalado para no salir de capa gratuita
  storage_type          = "gp2" # gp2 es elegible para capa gratuita (gp3 no lo es)

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  parameter_group_name   = aws_db_parameter_group.postgres.name
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az            = false # Una sola AZ para capa gratuita
  publicly_accessible = false # Solo accesible desde dentro de la VPC
  skip_final_snapshot = true  # Sin snapshot al eliminar (solo dev)

  tags = {
    Name = "${var.project_name}-${var.environment}-db"
  }
}
