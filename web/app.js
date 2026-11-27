const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

const PATA =
  '<svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><ellipse cx="5.5" cy="10" rx="2.2" ry="2.7"/><ellipse cx="9.5" cy="5.2" rx="2.2" ry="2.7"/><ellipse cx="14.5" cy="5.2" rx="2.2" ry="2.7"/><ellipse cx="18.5" cy="10" rx="2.2" ry="2.7"/><path d="M12 10.8c-3 0-6.2 4.3-6.2 6.8 0 1.8 1.4 2.6 3 2.6 1.4 0 2.1-.7 3.2-.7s1.8.7 3.2.7c1.6 0 3-.8 3-2.6 0-2.5-3.2-6.8-6.2-6.8z"/></svg>';

const ESCOVA_DENTES = `<svg fill="currentColor" height="800px" width="800px" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
	 viewBox="0 0 512.001 512.001" xml:space="preserve">
<g>
	<g>
		<path d="M456.497,125.949c-7.073-7.075-17.604-8.549-26.149-4.474l-49.716-49.714c-8.977-8.977-23.534-8.977-32.513,0
			l-97.543,97.538c-4.311,4.311-6.733,10.16-6.733,16.257c0,6.099,2.422,11.946,6.733,16.256l48.771,48.77l-36.585,36.585
			l-74.317,53.081c-1.03,0.736-1.999,1.557-2.894,2.452L55.501,472.754c-8.975,8.979-8.975,23.536,0.003,32.514
			c4.489,4.489,10.373,6.733,16.256,6.733c5.882,0,11.768-2.245,16.256-6.733l128.705-128.705l74.317-53.083
			c1.03-0.736,1.999-1.556,2.894-2.452l162.565-162.565C465.475,149.485,465.475,134.927,456.497,125.949z"/>
	</g>
</g>
<g>
	<g>
		<path d="M315.608,6.734c-8.975-8.978-23.533-8.978-32.513-0.002l-97.541,97.54c-8.978,8.977-8.978,23.534,0,32.513
			c4.489,4.491,10.373,6.735,16.257,6.735c5.882,0,11.768-2.245,16.256-6.735l97.541-97.538
			C324.587,30.27,324.587,15.711,315.608,6.734z"/>
	</g>
</g>
</svg>`;

const VETERINARIO = `<svg fill="CurrentColor" version="1.1" id="Uploaded to svgrepo.com" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                          	 width="800px" height="800px" viewBox="0 0 32 32" xml:space="preserve">

                                          <path d="M8,16c0,1.105-0.895,2-2,2s-2-0.895-2-2c0-1.105,0.895-2,2-2S8,14.895,8,16z M30.973,3.329
                                          	l-1.025,6.153c-0.546,3.273-2.512,6.096-5.393,7.742L21,19.272V24c0,4.411-3.589,8-8,8s-8-3.589-8-8v-4.141
                                          	C3.278,19.412,2,17.862,2,16c0-2.209,1.791-4,4-4c2.209,0,4,1.791,4,4c0,1.862-1.278,3.412-3,3.859V24
                                          	c0,1.796,0.797,3.406,2.051,4.506C9.03,28.339,9,28.173,9,28c0-2.206,1.794-4,4-4s4,1.794,4,4c0,0.173-0.03,0.339-0.051,0.506
                                          	C18.203,27.406,19,25.796,19,24v-4.76l-3.555-2.016c-2.881-1.646-4.847-4.469-5.393-7.742L9.027,3.329
                                          	C8.931,2.748,9.094,2.156,9.474,1.707C9.854,1.258,10.411,1,11,1h1.277C12.624,0.405,13.262,0,14,0c1.105,0,2,0.895,2,2
                                          	c0,1.105-0.895,2-2,2c-0.738,0-1.376-0.405-1.723-1H11.59c-0.309,0-0.544,0.277-0.493,0.582l0.929,5.571
                                          	c0.444,2.667,2.064,4.992,4.412,6.334l1.853,1.059C18.811,16.844,19.4,17,20,17s1.189-0.156,1.71-0.454l1.852-1.058
                                          	c2.348-1.342,3.969-3.668,4.413-6.336l0.928-5.57C28.954,3.277,28.719,3,28.41,3h-0.687C27.376,3.595,26.738,4,26,4
                                          	c-1.105,0-2-0.895-2-2c0-1.105,0.895-2,2-2c0.738,0,1.376,0.405,1.723,1H29c0.589,0,1.146,0.258,1.526,0.707
                                          	C30.906,2.156,31.069,2.748,30.973,3.329z M6,19c1.654,0,3-1.346,3-3c0-1.654-1.346-3-3-3c-1.654,0-3,1.346-3,3
                                          	C3,17.654,4.346,19,6,19z M15,28c0-1.103-0.897-2-2-2s-2,0.897-2,2s0.897,2,2,2S15,29.103,15,28z"/>
                                          </svg>`;

const ICONES = {
  banho:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 12h18v1.5A5.5 5.5 0 0 1 15.5 19h-7A5.5 5.5 0 0 1 3 13.5z"/><path d="M6 12V6.5a2.5 2.5 0 0 1 4.6-1.4"/><path d="m7 19-1 2m11-2 1 2"/><circle cx="14" cy="7.5" r="1.3"/><circle cx="17.5" cy="4.8" r="1"/><circle cx="17.8" cy="9.4" r=".8"/></svg>',
  tosa:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="6" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><path d="M20 4 8.1 15.9M14.5 14.5 20 20M8.1 8.1 12 12"/></svg>',
  "corte-unhas": PATA,
  'escovacao-dentes': ESCOVA_DENTES,
  veterinario: VETERINARIO,
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
