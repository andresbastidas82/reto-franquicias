# =============================================================================
# ecs.tf - ECS Fargate (Elastic Container Service)
# Ejecuta la aplicación Spring Boot como un servicio contenerizado.
#
# Componentes:
#   - Rol IAM: permite a ECS descargar imágenes de ECR, escribir logs en
#     CloudWatch y leer secretos de Secrets Manager
#   - Cluster ECS: agrupación lógica de servicios
#   - Task Definition: describe el contenedor (imagen, CPU, memoria, env vars)
#   - Servicio ECS: mantiene el número deseado de tareas en ejecución
#
# Configuración Fargate (mínima para capa gratuita):
#   - 0.25 vCPU (256 unidades de CPU)
#   - 512 MB de memoria
#   - 1 tarea deseada
#
# Las credenciales se inyectan desde Secrets Manager (no en texto plano).
# =============================================================================

# Rol IAM - permite a ECS interactuar con servicios AWS
resource "aws_iam_role" "ecs_execution" {
  name = "${var.project_name}-${var.environment}-ecs-execution"

  # Política de confianza: solo las tareas ECS pueden asumir este rol
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
    }]
  })
}

# Adjuntar política administrada de AWS para ejecución de tareas ECS
# Otorga permisos para descargar imágenes de ECR y escribir logs en CloudWatch
resource "aws_iam_role_policy_attachment" "ecs_execution" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Política personalizada para leer credenciales de Secrets Manager
resource "aws_iam_role_policy" "ecs_secrets" {
  name = "${var.project_name}-${var.environment}-ecs-secrets"
  role = aws_iam_role.ecs_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["secretsmanager:GetSecretValue"]
      Resource = [aws_secretsmanager_secret.db_credentials.arn]
    }]
  })
}

# Cluster ECS - agrupación lógica para el servicio
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-${var.environment}-cluster"

  tags = {
    Name = "${var.project_name}-${var.environment}-cluster"
  }
}

# Grupo de logs en CloudWatch - almacena los logs de las tareas ECS
resource "aws_cloudwatch_log_group" "app" {
  name              = "/ecs/${var.project_name}-${var.environment}"
  retention_in_days = 7 # Mantener logs 7 días para minimizar costos
}

# Task Definition - describe cómo ejecutar el contenedor de la aplicación
# Las credenciales se inyectan desde Secrets Manager como variables de entorno
resource "aws_ecs_task_definition" "app" {
  family                   = "${var.project_name}-${var.environment}"
  network_mode             = "awsvpc"    # Requerido para Fargate
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"       # 0.25 vCPU (mínimo)
  memory                   = "512"       # 512 MB (mínimo para Fargate)
  execution_role_arn       = aws_iam_role.ecs_execution.arn

  container_definitions = jsonencode([{
    name  = "${var.project_name}-app"
    image = "${aws_ecr_repository.app.repository_url}:${var.app_image_tag}"

    # Mapeo de puertos para la aplicación Spring Boot
    portMappings = [{
      containerPort = var.app_port
      protocol      = "tcp"
    }]

    # Credenciales de BD inyectadas desde Secrets Manager
    # ECS resuelve el ARN del secreto e inyecta el valor como variable de entorno
    secrets = [
      {
        name      = "DB_URL"
        valueFrom = "${aws_secretsmanager_secret.db_credentials.arn}:DB_URL::"
      },
      {
        name      = "DB_USERNAME"
        valueFrom = "${aws_secretsmanager_secret.db_credentials.arn}:DB_USERNAME::"
      },
      {
        name      = "DB_PASSWORD"
        valueFrom = "${aws_secretsmanager_secret.db_credentials.arn}:DB_PASSWORD::"
      }
    ]

    # Enviar logs del contenedor a CloudWatch
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.app.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
}

# Servicio ECS - mantiene el número deseado de tareas en ejecución
# Se conecta al target group del ALB para balanceo de carga
resource "aws_ecs_service" "app" {
  name            = "${var.project_name}-${var.environment}-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = 1          # Una sola instancia para dev
  launch_type     = "FARGATE"

  # Desplegar tareas en subnets privadas (sin IP pública)
  network_configuration {
    subnets          = aws_subnet.private[*].id
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = false # Las tareas acceden a internet vía NAT Gateway
  }

  # Registrar tareas en el target group del ALB
  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = "${var.project_name}-app"
    container_port   = var.app_port
  }

  depends_on = [aws_lb_listener.http]
}
