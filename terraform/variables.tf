# =============================================================================
# variables.tf - Variables de entrada para la infraestructura
# Centraliza todos los parámetros configurables. Los valores sensibles
# (db_username, db_password) están marcados para que Terraform no los
# muestre en logs ni en la salida. Valores por defecto para entorno dev.
# =============================================================================

variable "aws_region" {
  description = "Región de AWS donde se crearán todos los recursos"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Nombre del proyecto, usado como prefijo en todos los recursos"
  type        = string
  default     = "franchise"
}

variable "environment" {
  description = "Nombre del entorno (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "db_username" {
  description = "Usuario maestro de la base de datos"
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "db_password" {
  description = "Contraseña maestra de la base de datos (mínimo 8 caracteres)"
  type        = string
  sensitive   = true
}

variable "db_name" {
  description = "Nombre de la base de datos PostgreSQL a crear"
  type        = string
  default     = "franchise_db"
}

variable "app_port" {
  description = "Puerto en el que escucha la aplicación Spring Boot"
  type        = number
  default     = 8081
}

variable "app_image_tag" {
  description = "Tag de la imagen Docker a desplegar desde ECR"
  type        = string
  default     = "latest"
}
