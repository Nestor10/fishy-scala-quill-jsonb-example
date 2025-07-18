#!/bin/bash

# Quick Setup Script for Scala 3 + ZIO + Quill + PostgreSQL JSONB Example
# This script helps you get started without installing go-task

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
POSTGRES_DB="testdb"
POSTGRES_USER="testuser"
POSTGRES_PASSWORD="testpass"
POSTGRES_CONTAINER="postgres-jsonb"
POSTGRES_PORT="5432"

# Functions
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "ℹ $1"
}

# Check if required tools are installed
check_requirements() {
    print_info "Checking requirements..."
    
    if ! command -v podman &> /dev/null; then
        print_error "podman is not installed"
        exit 1
    fi
    
    if ! command -v sbt &> /dev/null; then
        print_error "sbt is not installed"
        exit 1
    fi
    
    print_success "Requirements check passed"
}

# Start PostgreSQL container
start_postgres() {
    print_info "Starting PostgreSQL container..."
    
    if podman ps | grep -q $POSTGRES_CONTAINER; then
        print_warning "PostgreSQL container is already running"
    else
        podman run -d \
            --name $POSTGRES_CONTAINER \
            -p $POSTGRES_PORT:5432 \
            -e POSTGRES_DB=$POSTGRES_DB \
            -e POSTGRES_USER=$POSTGRES_USER \
            -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
            docker.io/library/postgres:15
        
        print_success "PostgreSQL container started"
        print_info "Waiting for PostgreSQL to be ready..."
        sleep 5
    fi
}

# Start PostgreSQL as Kubernetes Pod
start_postgres_k8s() {
    print_info "Starting PostgreSQL as Podman Pod with K8s YAML..."
    
    if podman pod exists postgres-jsonb-pod; then
        print_warning "PostgreSQL pod is already running"
    else
        podman play kube k8s/postgres-pod.yaml
        print_success "PostgreSQL pod started"
        print_info "Waiting for PostgreSQL to be ready..."
        sleep 10
    fi
}

# Stop PostgreSQL pod
stop_postgres_k8s() {
    print_info "Stopping PostgreSQL pod..."
    podman play kube --down k8s/postgres-pod.yaml || true
    podman pod rm -f postgres-jsonb-pod || true
    print_success "PostgreSQL pod stopped and removed"
}

# Stop PostgreSQL container
stop_postgres() {
    print_info "Stopping PostgreSQL container..."
    podman stop $POSTGRES_CONTAINER || true
    podman rm $POSTGRES_CONTAINER || true
    print_success "PostgreSQL container stopped and removed"
}

# Setup database schema
setup_database() {
    print_info "Setting up database schema..."
    
    if [ ! -f "resources/postgres-schema.sql" ]; then
        print_error "Schema file not found: resources/postgres-schema.sql"
        exit 1
    fi
    
    podman exec -i $POSTGRES_CONTAINER psql -U $POSTGRES_USER -d $POSTGRES_DB < resources/postgres-schema.sql
    print_success "Database schema loaded"
}

# Compile the project
compile_project() {
    print_info "Compiling Scala project..."
    sbt compile
    print_success "Project compiled successfully"
}

# Run the project
run_project() {
    print_info "Running Scala application..."
    sbt run
}

# Full setup
full_setup() {
    check_requirements
    start_postgres
    setup_database
    compile_project
    print_success "Development environment ready!"
    print_info "Run './setup.sh run' to start the application"
}

# Full setup with K8s pod
full_setup_k8s() {
    check_requirements
    start_postgres_k8s
    sleep 5  # Extra time for pod to be ready
    setup_database
    compile_project
    print_success "Development environment ready (K8s pod)!"
    print_info "Run './setup.sh run' to start the application"
}

# Show help
show_help() {
    cat << EOF
Scala 3 + ZIO + Quill + PostgreSQL JSONB Example - Setup Script

Usage: ./setup.sh [command]

Commands:
  setup        - Full development environment setup (container)
  setup-k8s    - Full development environment setup (K8s pod)
  start        - Start PostgreSQL container
  start-k8s    - Start PostgreSQL as K8s pod
  stop         - Stop PostgreSQL container  
  stop-k8s     - Stop PostgreSQL pod
  db           - Setup database schema
  compile      - Compile the Scala project
  run          - Run the Scala application
  clean        - Clean build artifacts
  status       - Show system status
  help         - Show this help message

Examples:
  ./setup.sh setup       # Full setup with container
  ./setup.sh setup-k8s   # Full setup with K8s pod
  ./setup.sh start-k8s   # Just start PostgreSQL as pod
  ./setup.sh run         # Run the application
  ./setup.sh stop        # Stop everything
EOF
}

# Show status
show_status() {
    print_info "=== System Status ==="
    echo
    echo "PostgreSQL Container:"
    podman ps | grep $POSTGRES_CONTAINER || echo "  Container not running"
    echo
    echo "Port Status:"
    ss -tulpn | grep $POSTGRES_PORT || echo "  Port $POSTGRES_PORT not in use"
    echo
    echo "Build Status:"
    ls -la target/ 2>/dev/null || echo "  No build artifacts found"
}

# Main script logic
case "${1:-help}" in
    setup)
        full_setup
        ;;
    setup-k8s)
        full_setup_k8s
        ;;
    start)
        check_requirements
        start_postgres
        ;;
    start-k8s)
        check_requirements
        start_postgres_k8s
        ;;
    stop)
        stop_postgres
        ;;
    stop-k8s)
        stop_postgres_k8s
        ;;
    db)
        setup_database
        ;;
    compile)
        compile_project
        ;;
    run)
        run_project
        ;;
    clean)
        print_info "Cleaning build artifacts..."
        sbt clean
        rm -rf target/
        print_success "Build artifacts cleaned"
        ;;
    status)
        show_status
        ;;
    help|*)
        show_help
        ;;
esac
