# =============================================================================
# alb.tf - Application Load Balancer
# ALB interno que recibe tráfico del API Gateway (a través del VPC Link)
# y lo distribuye a las tareas ECS.
#
# El ALB es interno (no expuesto a internet) porque API Gateway es el
# punto de entrada público. Esto agrega una capa de seguridad: el ALB
# solo es alcanzable a través del VPC Link.
#
# El health check usa el endpoint de Spring Boot Actuator /actuator/health
# para verificar que la aplicación está corriendo correctamente.
# =============================================================================

# Application Load Balancer interno
resource "aws_lb" "main" {
  name               = "${var.project_name}-${var.environment}-alb"
  internal           = true            # No expuesto a internet
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = aws_subnet.public[*].id

  tags = {
    Name = "${var.project_name}-${var.environment}-alb"
  }
}

# Target group - registra las tareas ECS como destinos
# Usa tipo IP porque las tareas Fargate usan red awsvpc
resource "aws_lb_target_group" "app" {
  name        = "${var.project_name}-${var.environment}-tg"
  port        = var.app_port
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip" # Requerido para Fargate (modo de red awsvpc)

  # Configuración del health check usando Spring Boot Actuator
  health_check {
    path                = "/actuator/health"
    port                = "traffic-port"
    healthy_threshold   = 2   # 2 éxitos consecutivos para marcar como saludable
    unhealthy_threshold = 3   # 3 fallos consecutivos para marcar como no saludable
    timeout             = 5   # 5 segundos de timeout por verificación
    interval            = 30  # Verificar cada 30 segundos
    matcher             = "200"
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-tg"
  }
}

# Listener HTTP - reenvía todo el tráfico al target group
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}
