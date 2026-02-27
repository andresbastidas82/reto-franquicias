# =============================================================================
# outputs.tf - Salidas de Terraform
# Valores mostrados después de 'terraform apply' y accesibles mediante
# 'terraform output <nombre>'. Útiles para pipelines CI/CD y pruebas manuales.
# =============================================================================

# URL pública para acceder a la API (punto de entrada principal)
output "api_gateway_url" {
  description = "URL del API Gateway (punto de entrada público)"
  value       = aws_apigatewayv2_stage.default.invoke_url
}

# DNS interno del ALB (no accesible directamente desde internet)
output "alb_dns_name" {
  description = "DNS del ALB (interno, accesible vía API Gateway)"
  value       = aws_lb.main.dns_name
}

# URL del ECR para subir imágenes Docker
output "ecr_repository_url" {
  description = "URL del repositorio ECR para subir imágenes Docker"
  value       = aws_ecr_repository.app.repository_url
}

# Endpoint de RDS para conexiones a la base de datos
output "rds_endpoint" {
  description = "Endpoint de RDS PostgreSQL (host:puerto)"
  value       = aws_db_instance.postgres.endpoint
}

# ARN de Secrets Manager (útil para depuración o CI/CD)
output "secrets_manager_arn" {
  description = "ARN de Secrets Manager con las credenciales de BD"
  value       = aws_secretsmanager_secret.db_credentials.arn
}
