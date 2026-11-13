const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

const PATA =
  '<svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><ellipse cx="5.5" cy="10" rx="2.2" ry="2.7"/><ellipse cx="9.5" cy="5.2" rx="2.2" ry="2.7"/><ellipse cx="14.5" cy="5.2" rx="2.2" ry="2.7"/><ellipse cx="18.5" cy="10" rx="2.2" ry="2.7"/><path d="M12 10.8c-3 0-6.2 4.3-6.2 6.8 0 1.8 1.4 2.6 3 2.6 1.4 0 2.1-.7 3.2-.7s1.8.7 3.2.7c1.6 0 3-.8 3-2.6 0-2.5-3.2-6.8-6.2-6.8z"/></svg>';

const ICONES = {
  banho:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 12h18v1.5A5.5 5.5 0 0 1 15.5 19h-7A5.5 5.5 0 0 1 3 13.5z"/><path d="M6 12V6.5a2.5 2.5 0 0 1 4.6-1.4"/><path d="m7 19-1 2m11-2 1 2"/><circle cx="14" cy="7.5" r="1.3"/><circle cx="17.5" cy="4.8" r="1"/><circle cx="17.8" cy="9.4" r=".8"/></svg>',
  tosa:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="6" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><path d="M20 4 8.1 15.9M14.5 14.5 20 20M8.1 8.1 12 12"/></svg>',
  "corte-unhas": PATA,
};

const ICONE_PADRAO =
  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9z"/><path d="M19 16v4M17 18h4"/></svg>';

const MARCA =
  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M5 12.5l4.5 4.5L19 7.5"/></svg>';

const form = document.querySelector("#form-agendamento");
const listaServicos = document.querySelector("#lista-servicos");
const listaAdicionais = document.querySelector("#lista-adicionais");
const etapaAdicionais = document.querySelector("#etapa-adicionais");
const ticket = document.querySelector("#ticket");
const carimbo = document.querySelector("#carimbo");
const erro = document.querySelector("#erro");
const anuncio = document.querySelector("#anuncio");
const botaoConfirmar = document.querySelector("#botao-confirmar");
const botaoNovo = document.querySelector("#botao-novo");
const avisoServidor = document.querySelector("#aviso-servidor");
const listaAgendamentos = document.querySelector("#lista-agendamentos");
const estadoHistorico = document.querySelector("#estado-historico");
const resumo = Object.fromEntries(
  [...document.querySelectorAll("[data-resumo]")].map((el) => [el.dataset.resumo, el]),
);

let ultimoOrcamento = 0;
let ultimoHistorico = 0;
let enviando = false;

async function api(caminho, opcoes) {
  const resposta = await fetch(caminho, opcoes);
  const corpo = await resposta.json().catch(() => ({}));
  if (!resposta.ok) {
    throw new Error(corpo.erro || "Não foi possível concluir a operação.");
  }
  return corpo;
}

function criarOpcao({ id, nome, preco }, tipo) {
  const label = document.createElement("label");
  label.className = "opcao";

  const input = document.createElement("input");
  input.type = tipo === "servico" ? "radio" : "checkbox";
  input.name = tipo;
  input.value = id;
  input.required = tipo === "servico";

  const corpo = document.createElement("span");
  corpo.className = "opcao-corpo";
  corpo.innerHTML = `
    <span class="opcao-icone">${ICONES[id] ?? ICONE_PADRAO}</span>
    <span class="opcao-marca">${MARCA}</span>
    <span class="opcao-nome"></span>
    <span class="opcao-preco"></span>`;
  corpo.querySelector(".opcao-nome").textContent = nome;
  corpo.querySelector(".opcao-preco").textContent =
    (tipo === "adicional" ? "+ " : "") + moeda.format(preco);

  label.append(input, corpo);
  return label;
}

async function carregarCatalogo() {
  try {
    const { servicos, adicionais } = await api("/api/catalogo");
    listaServicos.replaceChildren(...servicos.map((s) => criarOpcao(s, "servico")));
    listaAdicionais.replaceChildren(...adicionais.map((a) => criarOpcao(a, "adicional")));
    etapaAdicionais.hidden = adicionais.length === 0;
  } catch {
    avisoServidor.hidden = false;
    botaoConfirmar.disabled = true;
    listaServicos.replaceChildren();
    listaAdicionais.replaceChildren();
  }
}

function preencher(chave, texto) {
  resumo[chave].textContent = texto || "—";
  resumo[chave].classList.toggle("pendente", !texto);
}

function definirTotal(valor, animar = true) {
  const texto = moeda.format(valor);
  if (resumo.total.textContent === texto) return;
  resumo.total.textContent = texto;
  if (animar) {
    resumo.total.classList.remove("pulsar");
    void resumo.total.offsetWidth;
    resumo.total.classList.add("pulsar");
  }
}

function formatarHorario(data, hora) {
  if (!data) return "";
  const [ano, mes, dia] = data.split("-");
  return `${dia}/${mes}/${ano}${hora ? ` ${hora.slice(0, 5)}` : ""}`;
}

function atualizarDataMinima() {
  const hoje = new Date();
  const mes = String(hoje.getMonth() + 1).padStart(2, "0");
  const dia = String(hoje.getDate()).padStart(2, "0");
  form.elements.data.min = `${hoje.getFullYear()}-${mes}-${dia}`;
}

function atualizarResumo() {
  const dados = new FormData(form);
  preencher("tutor", (dados.get("tutor") ?? "").trim());
  preencher("pet", (dados.get("pet") ?? "").trim());
  preencher("horario", formatarHorario(dados.get("data"), dados.get("hora")));
}

async function atualizarOrcamento() {
  const dados = new FormData(form);
  const servico = dados.get("servico");
  const pedido = ++ultimoOrcamento;
  botaoConfirmar.disabled = true;

  if (!servico) {
    preencher("servico", "");
    definirTotal(0, false);
    return;
  }

  const parametros = new URLSearchParams({ servico });
  dados.getAll("adicional").forEach((a) => parametros.append("adicional", a));

  preencher("servico", "Calculando…");
  resumo.total.textContent = "—";
  try {
    const orcamento = await api(`/api/orcamento?${parametros}`);
    if (pedido !== ultimoOrcamento) return;
    preencher("servico", orcamento.servico);
    definirTotal(orcamento.total);
    botaoConfirmar.disabled = false;
  } catch (falha) {
    if (pedido !== ultimoOrcamento) return;
    preencher("servico", "");
    mostrarErro(falha.message);
  }
}

function mostrarErro(mensagem) {
  erro.textContent = mensagem;
  erro.hidden = false;
}

function esconderErro() {
  erro.hidden = true;
}

function bloquearCampos(bloqueado) {
  form.querySelectorAll("fieldset").forEach((etapa) => {
    etapa.disabled = bloqueado;
  });
}

function definirConfirmado(confirmado) {
  ticket.classList.toggle("confirmado", confirmado);
  carimbo.hidden = !confirmado;
  botaoConfirmar.hidden = confirmado;
  botaoNovo.hidden = !confirmado;
  bloquearCampos(confirmado);
}

function mostrarConfirmacao(agendamento) {
  preencher("tutor", agendamento.tutor);
  preencher("pet", agendamento.pet);
  preencher("horario", agendamento.horario);
  preencher("servico", agendamento.servico);
  definirTotal(agendamento.total, false);
  definirConfirmado(true);
  anuncio.textContent = `Agendamento confirmado para ${agendamento.pet} em ${agendamento.horario}.`;
  botaoNovo.focus();
}

function itemAgendamento(agendamento, destacar) {
  const item = document.createElement("li");
  item.className = destacar ? "agendamento novo" : "agendamento";
  item.innerHTML = `
    <span class="avatar" aria-hidden="true"></span>
    <div class="agendamento-info"><strong></strong><small></small></div>
    <div class="agendamento-lado"><time></time><strong></strong></div>`;

  item.querySelector(".avatar").textContent = [...agendamento.pet][0]?.toUpperCase() ?? "";
  item.querySelector(".agendamento-info strong").textContent = agendamento.pet;
  item.querySelector(".agendamento-info small").textContent =
    `${agendamento.servico} · ${agendamento.tutor}`;
  item.querySelector(".agendamento-lado time").textContent = agendamento.horario;
  item.querySelector(".agendamento-lado strong").textContent = moeda.format(agendamento.total);
  return item;
}

async function carregarAgendamentos(destacarUltimo = false) {
  const pedido = ++ultimoHistorico;
  try {
    const agendamentos = await api("/api/agendamentos");
    if (pedido !== ultimoHistorico) return;
    const itens = agendamentos
      .map((a, i) => itemAgendamento(a, destacarUltimo && i === agendamentos.length - 1))
      .reverse();
    listaAgendamentos.replaceChildren(...itens);
    estadoHistorico.textContent = "Nenhum agendamento ainda. O primeiro aparece aqui.";
    estadoHistorico.hidden = agendamentos.length > 0;
  } catch {
    if (pedido !== ultimoHistorico) return;
    estadoHistorico.textContent = "Não foi possível carregar os agendamentos.";
    estadoHistorico.hidden = false;
  }
}

form.addEventListener("input", () => {
  esconderErro();
  atualizarResumo();
});

form.addEventListener("change", (evento) => {
  if (evento.target.name === "servico" || evento.target.name === "adicional") {
    atualizarOrcamento();
  }
});

form.addEventListener("submit", async (evento) => {
  evento.preventDefault();
  if (enviando || botaoConfirmar.disabled) return;
  esconderErro();

  const dados = new FormData(form);
  const corpo = new URLSearchParams({
    tutor: dados.get("tutor"),
    pet: dados.get("pet"),
    horario: `${dados.get("data")}T${dados.get("hora")}`,
    servico: dados.get("servico"),
  });
  dados.getAll("adicional").forEach((a) => corpo.append("adicional", a));

  enviando = true;
  bloquearCampos(true);
  botaoConfirmar.disabled = true;
  botaoConfirmar.textContent = "Confirmando…";
  try {
    const agendamento = await api("/api/agendamentos", { method: "POST", body: corpo });
    mostrarConfirmacao(agendamento);
    carregarAgendamentos(true);
  } catch (falha) {
    bloquearCampos(false);
    mostrarErro(falha.message);
  } finally {
    enviando = false;
    botaoConfirmar.disabled = false;
    botaoConfirmar.textContent = "Confirmar agendamento";
  }
});

botaoNovo.addEventListener("click", () => {
  form.reset();
  definirConfirmado(false);
  anuncio.textContent = "";
  esconderErro();
  atualizarDataMinima();
  atualizarResumo();
  atualizarOrcamento();
  form.elements.tutor.focus();
});

atualizarDataMinima();
carregarCatalogo().then(() => {
  atualizarResumo();
  atualizarOrcamento();
});
carregarAgendamentos();
