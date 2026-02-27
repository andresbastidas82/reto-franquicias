# =============================================================================
# security.tf - Grupos de Seguridad
# Define las reglas de acceso a nivel de red para cada capa de la arquitectura.
#
# Flujo de tráfico:
#   Internet → ALB (puerto 80) → ECS (puerto 8081) → RDS (puerto 5432)
#
# Cada grupo de seguridad solo permite tráfico de la capa anterior,
# siguiendo el principio de mínimo privilegio.
# =============================================================================

# Grupo de seguridad del ALB - acepta tráfico HTTP desde internet
resource "aws_security_group" "alb" {
  name        = "${var.project_name}-${var.environment}-alb-sg"
  description = "Grupo de seguridad para ALB - permite HTTP desde cualquier origen"
  vpc_id      = aws_vpc.main.id

  # Permitir HTTP desde cualquier origen (API Gateway conecta por VPC Link)
  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permitir todo el tráfico de salida (para alcanzar las tareas ECS)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-alb-sg"
  }
}

# Grupo de seguridad de ECS - solo acepta tráfico del ALB
resource "aws_security_group" "ecs" {
  name        = "${var.project_name}-${var.environment}-ecs-sg"
  description = "Grupo de seguridad para tareas ECS - permite puerto de app solo desde ALB"
  vpc_id      = aws_vpc.main.id

  # Permitir puerto de la app solo desde el grupo de seguridad del ALB
  ingress {
    description     = "Puerto de la app desde ALB"
    from_port       = var.app_port
    to_port         = var.app_port
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  # Permitir todo el tráfico de salida (para alcanzar RDS, ECR, CloudWatch, Secrets Manager)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-ecs-sg"
  }
}

# Grupo de seguridad de RDS - solo acepta tráfico de las tareas ECS
resource "aws_security_group" "rds" {
  name        = "${var.project_name}-${var.environment}-rds-sg"
  description = "Grupo de seguridad para RDS - permite PostgreSQL solo desde ECS"
  vpc_id      = aws_vpc.main.id

  # Permitir puerto PostgreSQL solo desde el grupo de seguridad de ECS
  ingress {
    description     = "PostgreSQL desde ECS"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  # Permitir todo el tráfico de salida
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-rds-sg"
  }
}
