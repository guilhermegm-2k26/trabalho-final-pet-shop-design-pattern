# Pet Shop

Projeto final da disciplina de Design Patterns (2026.2), tema 4: Pet Shop. O tutor informa seu nome e o do pet, escolhe um horário futuro e seleciona banho ou tosa, com corte de unhas opcional. O sistema exibe o valor total e confirma o agendamento. No terminal, é feito um agendamento por execução; na web, é possível criar vários e ver a lista enquanto o servidor estiver rodando. [Enunciado da atividade](https://designpatterns.venson.dev/projetos/projeto-final/)

| Etapa | Responsável | Entrega |
| --- | --- | --- |
| Parte 1 | Equipe dona | Aplicação Java executável com Factory Method e Decorator, publicada em repositório público no GitHub. |
| Parte 2 | Equipe extensora | Pull request com um novo serviço e um novo adicional, após o sorteio. |
| Parte 3 | Equipe dona | Revisão do pull request e merge. As duas equipes participam da arguição. |

## Parte 1 — Aplicação base

### Entidades

| Entidade | Responsabilidade |
| --- | --- |
| `Tutor` | Identifica a pessoa responsável pelo pet. |
| `Pet` | Guarda o nome do animal e o vínculo com seu tutor. |
| `Agendamento` | Reúne o pet, o horário e o serviço escolhido com seus adicionais. |

### Tecnologias e execução

JDK 17 ou superior, com `java` e `javac` disponíveis no terminal. O projeto usa Java e a biblioteca padrão do JDK; a interface web usa HTML, CSS e JavaScript. Os agendamentos ficam em memória. Não é necessário banco de dados, gerenciador de dependências ou conexão externa para executar a aplicação.

Na raiz do repositório, execute no terminal do macOS ou Linux:

```bash
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name '*.java')
```

Para usar no terminal:

```bash
java -cp out br.edu.petshop.Main
```

### Exemplos de uso

Informe `Ana` como tutora, `Luna` como pet, `20/11/2030 14:30` como horário, `1` para banho e `s` para incluir corte de unhas. O resumo exibido será:

```text
Agendamento confirmado
Tutor: Ana
Pet: Luna
Horário: 20/11/2030 14:30
Serviço: Banho + corte de unhas
Total: R$ 65,00
```

| Serviço | Sem adicional | Com corte de unhas |
| --- | ---: | ---: |
| Banho | R$ 50,00 | R$ 65,00 |
| Tosa | R$ 70,00 | R$ 85,00 |

Para agendar apenas tosa no terminal, escolha `2` e responda `n` ao adicional. Uma data inexistente, como `31/02/2030 14:30`, ou um horário passado, como `01/01/2020 10:00`, é rejeitado. No terminal, o programa pede um novo horário; no navegador, a pessoa pode corrigir os campos e tentar novamente.

### Interface web

Além do terminal, o projeto tem uma página de agendamento na pasta `web/`. A classe `ServidorWeb` usa o `HttpServer` do próprio JDK para servir a página e expor uma API pequena. Essa API usa as mesmas fábricas e os mesmos adicionais da aplicação de terminal. Depois de compilar com o comando acima, execute na raiz do repositório:

```bash
java -cp out br.edu.petshop.web.ServidorWeb
```

Abra [http://localhost:8080](http://localhost:8080) no navegador. Para repetir o exemplo, preencha Ana, Luna, 20/11/2030 às 14:30, selecione **Banho** e **Corte de unhas** e confira o total de **R$ 65,00** antes de confirmar. O agendamento aparece em **Nesta sessão**. Para usar outra porta, passe o número como argumento, por exemplo `java -cp out br.edu.petshop.web.ServidorWeb 3000`. Encerre o servidor com Ctrl+C.

| Rota | Uso |
| --- | --- |
| `GET /api/catalogo` | Lista os serviços base e os adicionais com seus preços. |
| `GET /api/orcamento?servico=banho&adicional=corte-unhas` | Monta o serviço com os adicionais e devolve a descrição e o total. |
| `POST /api/agendamentos` | Cria um agendamento. Envie `tutor`, `pet`, `horario` (`2030-11-20T14:30`), `servico` e `adicional` (opcional e repetível) como formulário `application/x-www-form-urlencoded`. |
| `GET /api/agendamentos` | Lista os agendamentos feitos desde que o servidor foi iniciado. |

O terminal e a web usam `CatalogoServicos` para listar as mesmas opções. Na Parte 2, o cadastro de uma nova fábrica ou de um novo adicional fica nessa classe. Na web, um identificador sem ícone específico usa o ícone padrão.

### Padrões de projeto

#### Factory Method

O fluxo de agendamento é o mesmo para banho e tosa. `AgendamentoFactory` oferece `montarServico()` para orçar e `agendar()` para confirmar; ambos chegam a `criarServico()`, o método de fábrica. `BanhoFactory` e `TosaFactory` implementam esse método e criam, respectivamente, `Banho` e `Tosa`. Os produtos implementam `Servico`. Assim, os fluxos de orçamento e confirmação trabalham com o mesmo contrato, sem precisar instanciar os produtos concretos.

#### Decorator

O corte de unhas é opcional e pode ser combinado com qualquer serviço base. `ServicoComAdicional` implementa `Servico` e delega a descrição e o preço ao serviço que recebe. `CorteDeUnhas` envolve o serviço, acrescenta seu nome à descrição e soma R$ 15,00 ao preço. `Agendamento` usa a mesma interface `Servico` para um banho simples ou um banho com adicional. Isso permite acrescentar opções sem criar uma classe para cada combinação de serviço e adicional.

### Contribuições da equipe dona

Contribuições já registradas no histórico do Git:

| Integrante | Contribuição realizada | Commit |
| --- | --- | --- |
| Guilherme | Estrutura inicial, entidades, fluxo no terminal, Factory Method e Decorator. | `ffe5d16` |
| `omrl` | Interface web, servidor HTTP e integração com o agendamento em Java. | `5863007` |

Antes da entrega, a equipe deve confirmar os nomes, registrar as contribuições restantes e garantir pelo menos um commit autoral de cada integrante. A tarefa de fechamento técnico é automatizar as verificações das duas fábricas, dos quatro totais da tabela e da rejeição de horários passados.

### Declaração de uso de IA

Na Parte 1, foi usado OpenAI Codex (GPT-6) para auxiliar na implementação inicial em Java, na organização do catálogo comum, na validação de horários, nos ajustes do formulário e na revisão deste README e dos fluxos de execução. A interface web inicial (pasta `web/` e classe `ServidorWeb`) foi gerada com Claude Code (Claude Opus 5.5), conforme o registro original do projeto.

## Parte 2 — Extensão via pull request

Após o sorteio, a equipe extensora faz um fork, cria uma branch e acrescenta um serviço base via Factory Method e um adicional via Decorator. A aplicação deve continuar executável. O pull request deve ter como destino a branch `main` deste repositório. A implementação da extensão deve ser feita sem IA.

Na descrição do PR, registrem o contexto da proposta, como os dois padrões foram ampliados, as decisões e refatorações realizadas e como testar a extensão. Atualizem este README com a descrição da extensão, instruções de uso e a contribuição de cada integrante da equipe extensora. Cada pessoa deve ter pelo menos um commit autoral no PR. O registro da equipe dona fica na Parte 1.

**Descrição da extensão e contribuições da equipe extensora** — a preencher após o sorteio.

## Parte 3 — Revisão e arguição

Todos os integrantes da equipe dona participam da revisão autoral. No GitHub, deixem um parecer geral e comentários em trechos específicos do código. Solicitem ajustes quando necessário e, após a aprovação, façam o merge. Na arguição em aula, cada integrante deve saber explicar sua contribuição.

- [ ] Compilar e executar a aplicação seguindo este README.
- [ ] Conferir a aderência da extensão ao tema e a aplicação dos dois padrões.
- [ ] Avaliar a justificativa técnica e as refatorações descritas no PR.
- [ ] Conferir as instruções e as contribuições no README.
- [ ] Verificar se há indícios de uso de IA na extensão.

**PR revisado e participação da equipe dona na revisão** — a preencher na Parte 3.
