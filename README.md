# Vendas API

Uma API REST completa para gerenciamento de vendas com observabilidade avançada e monitoramento em tempo real.

## Tecnologias

- **Backend:** Java 21, Spring Boot 3.5.4
- **Banco:** PostgreSQL 15
- **Observabilidade:** Micrometer, Prometheus, Grafana
- **Documentação:** SpringDoc OpenAPI 3
- **Containerização:** Docker & Docker Compose

## Pré-requisitos

- Docker

*Obs: Java e Maven **não são necessários** pois a aplicação roda completamente em containers.*

## Como executar

### 1. **Clone o repositório**
```bash
git clone https://github.com/juliojec/projeto-vendas
cd projeto-vendas
```

### 2. **Execute todo o ambiente**
```bash
docker-compose up -d
```

**Isso irá subir:**
- PostgreSQL (banco de dados)
- Vendas API (aplicação Spring Boot)
- Prometheus (coleta de métricas)
- Grafana (dashboards e visualização)

### 3. **Acesse os serviços**
- **API:** http://localhost:8080
- **Swagger:** http://localhost:8080/swagger-ui.html
- **Métricas:** http://localhost:8080/actuator/prometheus
- **Grafana:** http://localhost:3000 (admin/admin123)
- **Prometheus:** http://localhost:9090

## API Endpoints

### **Vendas**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/vendas` | Criar nova venda |
| `GET` | `/api/v1/vendas` | Listar vendas (paginado) |
| `GET` | `/api/v1/vendas/{numeroVenda}` | Buscar venda específica |
| `PUT` | `/api/v1/vendas/{numeroVenda}/cancelar` | Cancelar venda |
| `PUT` | `/api/v1/vendas/{numeroVenda}/itens/{itemId}/cancelar` | Cancelar item |

### **Monitoramento**

| Endpoint | Descrição |
|----------|-----------|
| `/actuator/health` | Status da aplicação |
| `/actuator/metrics` | Métricas disponíveis |
| `/actuator/prometheus` | Métricas formato Prometheus |
| `/actuator/info` | Informações da aplicação |

## Dashboards & Métricas

### **Grafana Dashboards Inclusos:**
1. **Vendas API - Monitoramento:** Métricas de negócio e aplicação
2. **Performance & Gargalos:** Detecção de problemas

### **Métricas Principais:**
- `vendas_eventos_total{evento="criada"}` - Total vendas criadas
- `vendas_eventos_total{evento="cancelada"}` - Total vendas canceladas
- `repository_method_time` - Performance do banco
- `service_method_time` - Performance dos serviços
- `http_server_requests` - Métricas HTTP

### **Alertas Automáticos:**
- Queries lentas (>100ms)
- Alta taxa de erros
- Aplicação offline

### **Como acessar os dashboards:**
1. **Acesse Grafana:** http://localhost:3000
2. **Login:** admin / admin123
3. **Dashboards disponíveis:**
    - "Vendas API - Monitoramento" (métricas de negócio)
    - "Performance & Gargalos" (performance técnica)


### **Logs da Aplicação:**
```bash
# Logs em tempo real
docker-compose logs -f vendas-api

# Logs específicos
tail -f logs/vendas-api.log
```

### **Verificar Saúde:**
```bash
curl http://localhost:8080/actuator/health
```