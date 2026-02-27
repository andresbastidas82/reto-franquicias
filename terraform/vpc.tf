# =============================================================================
# vpc.tf - Infraestructura de red
# Crea la VPC, subnets, gateways y tablas de rutas.
#
# Arquitectura:
#   - 2 Subnets públicas  (10.0.0.0/24, 10.0.1.0/24) → ALB, NAT Gateway
#   - 2 Subnets privadas  (10.0.10.0/24, 10.0.11.0/24) → ECS, RDS
#   - Internet Gateway → permite a las subnets públicas acceder a internet
#   - NAT Gateway → permite a las subnets privadas salir a internet (solo salida)
#   - 2 zonas de disponibilidad para alta disponibilidad
# =============================================================================

# Obtener las zonas de disponibilidad de la región seleccionada
data "aws_availability_zones" "available" {
  state = "available"
}

# VPC principal con soporte DNS habilitado (requerido para RDS)
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.project_name}-${var.environment}-vpc"
  }
}

# Subnets públicas - alojan el ALB y el NAT Gateway
# Tienen acceso directo a internet a través del Internet Gateway
resource "aws_subnet" "public" {
  count                   = 2
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(aws_vpc.main.cidr_block, 8, count.index)
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.project_name}-${var.environment}-public-${count.index + 1}"
  }
}

# Subnets privadas - alojan las tareas ECS y la base de datos RDS
# Solo acceden a internet a través del NAT Gateway (tráfico de salida)
resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(aws_vpc.main.cidr_block, 8, count.index + 10)
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${var.project_name}-${var.environment}-private-${count.index + 1}"
  }
}

# Internet Gateway - proporciona acceso a internet a las subnets públicas
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-${var.environment}-igw"
  }
}

# IP elástica para el NAT Gateway (IP pública estática)
resource "aws_eip" "nat" {
  domain = "vpc"

  tags = {
    Name = "${var.project_name}-${var.environment}-nat-eip"
  }
}

# NAT Gateway - permite a las subnets privadas hacer peticiones salientes a internet
# (ej: descargar imágenes Docker, conectarse a servicios AWS)
resource "aws_nat_gateway" "main" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public[0].id

  tags = {
    Name = "${var.project_name}-${var.environment}-nat"
  }

  depends_on = [aws_internet_gateway.main]
}

# Tabla de rutas pública - enruta tráfico a internet por el Internet Gateway
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-public-rt"
  }
}

# Asociar subnets públicas con la tabla de rutas pública
resource "aws_route_table_association" "public" {
  count          = 2
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# Tabla de rutas privada - enruta tráfico a internet por el NAT Gateway
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-private-rt"
  }
}

# Asociar subnets privadas con la tabla de rutas privada
resource "aws_route_table_association" "private" {
  count          = 2
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}
