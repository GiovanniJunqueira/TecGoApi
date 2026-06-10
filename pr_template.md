## 🚀 Descrição

Implementa a integração com MinIO para armazenamento de arquivos na aplicação, permitindo o upload e download de arquivos de forma escalável e segura.

## ✅ Objetivo deste PR

- [x] Nova funcionalidade
- [ ] Bug fix
- [ ] Refatoração
- [ ] Documentação
- [ ] Outro: _______

## 🔍 Detalhes das alterações

- Adicionado `MinioService` para gerenciar operações de upload/download
- Criado `SchoolCacheService` para melhorar desempenho
- Adicionado `SchoolResponseDTO` para melhor estrutura de resposta
- Atualizado `application.properties` com configurações do MinIO
- Adicionado serviço MinIO no `docker-compose.yml`
- Atualizado `SecurityConfig` para permitir acesso aos novos endpoints

## 🧪 Como testar

1. Suba os containers com `docker-compose up -d`
2. Acesse o MinIO em `http://localhost:9000` (credenciais no .env)
3. Teste os endpoints de upload/download de arquivos
4. Verifique se os arquivos estão sendo armazenados corretamente no MinIO

## 📌 Observações

- Foram adicionadas novas variáveis de ambiente no `.env.example`
- O MinIO está configurado para desenvolvimento local
- A autenticação para os endpoints de arquivos segue as mesmas regras de autenticação existentes

---

> ⚠️ Este PR requer **aprovação de outro membro antes de ser mesclado**.
