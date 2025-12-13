#!/bin/bash

# Script de verificación pre-despliegue
# Este script verifica que todos los archivos necesarios estén presentes

echo "🔍 Verificando configuración para despliegue..."
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Contador de errores
ERRORS=0
WARNINGS=0

# Verificar archivos del backend
echo "📦 BACKEND:"
echo "----------"

if [ -f "inventory_app/Dockerfile" ]; then
    echo -e "${GREEN}✓${NC} Dockerfile encontrado"
else
    echo -e "${RED}✗${NC} Dockerfile NO encontrado"
    ((ERRORS++))
fi

if [ -f "inventory_app/src/main/resources/application-prod.properties" ]; then
    echo -e "${GREEN}✓${NC} application-prod.properties encontrado"
else
    echo -e "${RED}✗${NC} application-prod.properties NO encontrado"
    ((ERRORS++))
fi

if [ -f "inventory_app/.env.example" ]; then
    echo -e "${GREEN}✓${NC} .env.example encontrado"
else
    echo -e "${YELLOW}⚠${NC} .env.example NO encontrado (opcional)"
    ((WARNINGS++))
fi

if [ -f "inventory_app/pom.xml" ]; then
    echo -e "${GREEN}✓${NC} pom.xml encontrado"
else
    echo -e "${RED}✗${NC} pom.xml NO encontrado"
    ((ERRORS++))
fi

echo ""
echo "⚛️  FRONTEND:"
echo "----------"

if [ -f "frontend/package.json" ]; then
    echo -e "${GREEN}✓${NC} package.json encontrado"
else
    echo -e "${RED}✗${NC} package.json NO encontrado"
    ((ERRORS++))
fi

if [ -f "frontend/.env.example" ]; then
    echo -e "${GREEN}✓${NC} .env.example encontrado"
else
    echo -e "${YELLOW}⚠${NC} .env.example NO encontrado (recomendado)"
    ((WARNINGS++))
fi

if [ -f "frontend/.env.production" ]; then
    echo -e "${GREEN}✓${NC} .env.production encontrado"
else
    echo -e "${YELLOW}⚠${NC} .env.production NO encontrado (recomendado)"
    ((WARNINGS++))
fi

if [ -f "frontend/vite.config.js" ]; then
    echo -e "${GREEN}✓${NC} vite.config.js encontrado"
else
    echo -e "${RED}✗${NC} vite.config.js NO encontrado"
    ((ERRORS++))
fi

echo ""
echo "📚 DOCUMENTACIÓN:"
echo "---------------"

if [ -f "GUIA_DESPLIEGUE.md" ]; then
    echo -e "${GREEN}✓${NC} GUIA_DESPLIEGUE.md encontrado"
else
    echo -e "${YELLOW}⚠${NC} GUIA_DESPLIEGUE.md NO encontrado"
    ((WARNINGS++))
fi

echo ""
echo "🔐 VERIFICACIONES DE SEGURIDAD:"
echo "----------------------------"

# Verificar que no haya archivos .env con datos reales commiteados
if [ -f "inventory_app/.env" ]; then
    echo -e "${RED}✗${NC} PELIGRO: .env encontrado en backend (NO commitear archivos .env)"
    ((ERRORS++))
else
    echo -e "${GREEN}✓${NC} No hay .env en backend (correcto)"
fi

if [ -f "frontend/.env" ]; then
    echo -e "${YELLOW}⚠${NC} .env encontrado en frontend (asegúrate de que esté en .gitignore)"
    ((WARNINGS++))
else
    echo -e "${GREEN}✓${NC} No hay .env en frontend (correcto)"
fi

# Verificar .gitignore
if [ -f ".gitignore" ]; then
    if grep -q ".env" .gitignore; then
        echo -e "${GREEN}✓${NC} .env está en .gitignore"
    else
        echo -e "${RED}✗${NC} .env NO está en .gitignore"
        ((ERRORS++))
    fi
else
    echo -e "${YELLOW}⚠${NC} .gitignore no encontrado"
    ((WARNINGS++))
fi

echo ""
echo "📊 RESUMEN:"
echo "----------"

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✓ Todo está listo para el despliegue!${NC}"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠ $WARNINGS advertencias encontradas${NC}"
    echo "Puedes continuar con el despliegue, pero revisa las advertencias"
    exit 0
else
    echo -e "${RED}✗ $ERRORS errores y $WARNINGS advertencias encontrados${NC}"
    echo "Por favor, corrige los errores antes de desplegar"
    exit 1
fi
