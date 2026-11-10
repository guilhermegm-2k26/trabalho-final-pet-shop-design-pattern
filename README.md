# Pet Shop

Projeto final da disciplina de Design Patterns (2026.2). A aplicação de terminal registra um agendamento por execução. O tutor informa o nome do pet, escolhe a data, o horário, o serviço e um adicional opcional. Ao final, o sistema mostra a confirmação com o preço total. Os dados ficam em memória durante a execução. [Enunciado da atividade](https://designpatterns.venson.dev/projetos/projeto-final/)

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

### Pré-requisitos e execução

JDK 17 ou superior, com `java` e `javac` disponíveis no terminal. O projeto usa Java puro e a biblioteca padrão do JDK.

Na raiz do repositório, execute no terminal do macOS ou Linux:

```bash
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name '*.java')
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

Com os mesmos dados, escolha `2` e `n` para agendar apenas tosa. O resumo mostra `Tosa` e o total de `R$ 70,00`. Se informar uma data como `31/02/2030 14:30`, o programa pede uma nova data e hora.

### Padrões de projeto

#### Factory Method

O fluxo de agendamento é o mesmo para banho e tosa. `AgendamentoFactory.agendar()` concentra esse fluxo e chama `criarServico()` para obter o serviço base. `BanhoFactory` e `TosaFactory` implementam esse método e criam, respectivamente, `Banho` e `Tosa`. Os dois serviços implementam `Servico`. A operação de agendamento funciona com qualquer serviço criado por uma subclasse de `AgendamentoFactory`.

#### Decorator

O corte de unhas é opcional e pode ser combinado com qualquer serviço base. `ServicoComAdicional` implementa `Servico` e delega a descrição e o preço ao serviço que recebe. `CorteDeUnhas` acrescenta seu nome à descrição e soma R$ 15,00 ao preço. `Agendamento` usa a mesma interface `Servico` para um banho simples ou um banho com adicional. A composição permite envolver um serviço já decorado com outro adicional.

### Contribuições da equipe dona

Cada integrante deve fazer pelo menos um commit autoral com a própria conta do GitHub e preencher a tabela com o que entregou. Acrescentem uma linha se o grupo tiver quatro pessoas.

| Integrante | Contribuição realizada | Commit |
| --- | --- | --- |
| Guilherme | A preencher após o commit autoral. | — |
| A preencher | A preencher após o commit autoral. | — |
| A preencher | A preencher após o commit autoral. | — |

#### Melhorias para distribuir entre os integrantes

- Escrever testes para as duas fábricas e conferir o preço de cada serviço com e sem corte de unhas.
- Implementar a validação de horários no passado e documentar um exemplo de entrada rejeitada.
- Mostrar o preço dos serviços e do adicional durante a seleção e conferir se o valor exibido bate com o resumo final.

### Declaração de uso de IA

Na Parte 1, foi usado OpenAI Codex (GPT-6) para gerar a implementação inicial em Java, estruturar este README e verificar a compilação e os exemplos do fluxo de agendamento.

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
