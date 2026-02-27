# =============================================================================
# apigateway.tf - API Gateway HTTP (v2)
# Punto de entrada público de la API. Recibe peticiones desde internet
# y las reenvía al ALB interno a través de un VPC Link.
#
# Capa gratuita: 1 millón de peticiones/mes durante 12 meses.
#
# Arquitectura:
#   Internet → API Gateway → VPC Link → ALB (interno) → ECS
#
# Usa una ruta $default (catch-all) que reenvía TODAS las peticiones
# (cualquier método, cualquier path) al ALB. Los routers de Spring Boot
# se encargan del enrutamiento real.
#
# Throttling configurado para prevenir abuso:
#   - Límite de tasa: 50 peticiones/segundo
#   - Límite de ráfaga: 100 peticiones
# =============================================================================

# API HTTP (v2) - más ligera y económica que REST API (v1)
resource "aws_apigatewayv2_api" "main" {
  name          = "${var.project_name}-${var.environment}-api"
  protocol_type = "HTTP"

  tags = {
    Name = "${var.project_name}-${var.environment}-api"
  }
}

# VPC Link - puente entre API Gateway y el ALB interno
# API Gateway vive fuera de la VPC, así que necesita un VPC Link
# para alcanzar el ALB interno
resource "aws_apigatewayv2_vpc_link" "main" {
  name               = "${var.project_name}-${var.environment}-vpc-link"
  security_group_ids = [aws_security_group.alb.id]
  subnet_ids         = aws_subnet.public[*].id

  tags = {
    Name = "${var.project_name}-${var.environment}-vpc-link"
  }
}

# Integración - conecta API Gateway al listener del ALB vía VPC Link
resource "aws_apigatewayv2_integration" "alb" {
  api_id             = aws_apigatewayv2_api.main.id
  integration_type   = "HTTP_PROXY"                    # Proxy transparente
  integration_uri    = aws_lb_listener.http.arn         # ARN del listener del ALB
  integration_method = "ANY"                            # Reenviar cualquier método HTTP
  connection_type    = "VPC_LINK"                       # Usar VPC Link
  connection_id      = aws_apigatewayv2_vpc_link.main.id
}

# Ruta por defecto - catch-all que reenvía todo al ALB
# No es necesario definir rutas individuales para cada endpoint
resource "aws_apigatewayv2_route" "default" {
  api_id    = aws_apigatewayv2_api.main.id
  route_key = "$default"
  target    = "integrations/${aws_apigatewayv2_integration.alb.id}"
}

# Stage con auto-deploy - despliega cambios automáticamente
resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.main.id
  name        = "$default"
  auto_deploy = true # Desplegar automáticamente cambios en rutas/integraciones

  # Throttling para prevenir abuso y mantenerse en capa gratuita
  default_route_settings {
    throttling_burst_limit = 100 # Máximo de peticiones concurrentes
    throttling_rate_limit  = 50  # Peticiones por segundo
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-stage"
  }
}
