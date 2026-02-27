# =============================================================================
# main.tf - Configuración del proveedor y versión de Terraform
# Define la versión mínima de Terraform y la configuración del proveedor AWS.
# =============================================================================

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# Proveedor AWS configurado con la región definida en variables
provider "aws" {
  region = var.aws_region
}
