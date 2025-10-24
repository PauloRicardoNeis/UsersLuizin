# UsersLuizin

Microserviço de usuários para o sistema de gerenciamento de eventos do Luizin. O serviço expõe endpoints REST para cadastro de usuários, autenticação via JWT e verificação de permissões com RBAC.

## Tecnologias

- Java 17
- Spring Boot 3 (Web, Data JPA, Security, Validation)
- H2 (banco em memória para desenvolvimento)
- JWT (io.jsonwebtoken)

## Executando o serviço

```bash
mvn spring-boot:run
```

O serviço inicia em `http://localhost:8080`.

### Usuário padrão

Um administrador padrão é inserido automaticamente via `data.sql`:

- **Email:** `admin@luizin.com`
- **Senha:** `admin123`

Esse usuário possui os papéis `ADMIN` e `INSTITUTION` para fins de autorização.

## Endpoints principais

| Método | Caminho | Descrição | Permissão |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Autentica e retorna um JWT. | Público |
| `GET` | `/users` | Lista usuários (query `includeInactive` para incluir inativos). | ADMIN / INSTITUTION |
| `GET` | `/users/{id}` | Detalha um usuário. | ADMIN / INSTITUTION |
| `POST` | `/users` | Cria um usuário (ADMIN é tratado como instituição). | ADMIN / INSTITUTION |
| `PUT` | `/users/{id}` | Atualiza dados. Usuários podem atualizar os próprios dados (sem mudar papel ou ativação). | Usuário autenticado |
| `DELETE` | `/users/{id}` | Desativa o usuário (soft delete). | ADMIN / INSTITUTION |

### Campos do usuário

- `firstName` (obrigatório)
- `lastName` (obrigatório)
- `cpf` (11 dígitos, obrigatório)
- `email` (obrigatório)
- `password` (mínimo 6 caracteres)
- `role` (`ADMIN`, `INSTITUTION`, `USER`, `SPEAKER`)

O endpoint de remoção apenas desativa o usuário (`active = false`). Para reativar, utilize `PUT /users/{id}` com `{"active": true}`.

## Variáveis de ambiente

Configurações de JWT podem ser sobrescritas:

```yaml
security:
  jwt:
    secret: <segredo base64>
    expiration-minutes: <minutos>
```

## Próximos passos

- Substituir H2 por um banco relacional dedicado (PostgreSQL, MySQL etc.)
- Integração com Keycloak ou outro provedor de identidade centralizado
- Publicar imagens Docker para orquestração em arquitetura de microserviços
