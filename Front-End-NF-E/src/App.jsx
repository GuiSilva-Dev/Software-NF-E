import { useState } from 'react'
import Api from './services/Api'
import './App.css'
import ButtonPdf from './Components/Button.jsx'
import TwoPage from './Components/TwoPage.jsx'
import swal from 'sweetalert2'


function App() {
  //* Atualiza o estado ao clicar no button twopage
  const [exibirPdf, setExibirPdf] = useState(false);

  const [formulario, setFormulario] = useState({
    //*OBJETOS JAVASCRIPT
    nameEnterprise: "",
    cnpjEnterprise: "",
    phoneEnterprise: "",

    nameCustomer: "",
    cpfCustomer: "",
    emailCustomer: "",
    phoneCustomer: "",
    addressCustomer: "",
    numberCustomer: "",
    cityCustomer: "",
    stateCustomer: "",

    record: "",
    inputDate: "",
    device: "",
    series: "",
    descriptionRepair: "",
    replacedPart: "",
    value: "",
    paymentMethod: "",
    warrantyPeriod: ""
  });

  //*ATUALIZA QUALQUER CAMPO
  function handleChange(e) {
    const { name, value } = e.target;
    //* O console.log ajuda a ver acontecendo em tempo real
    console.log(`Digitando em ${name}: ${value}`);

    setFormulario({
      ...formulario,
      [name]: value
    });
  }
  const validarCampos = () => {

    const NomesCampos = {
      nameEnterprise: "Nome",
      cnpjEnterprise: "CNPJ",//* 'cnpjEnterprise' é a KEY
      phoneEnterprise: "Telefone",
      nameCustomer: "Nome",
      cpfCustomer: "CPF",
      emailCustomer: "Email",
      phoneCustomer: "Telefone",
      addressCustomer: "Endereço",
      numberCustomer: "Número",
      cityCustomer: "Cidade",
      stateCustomer: "Estado",
      record: "Ficha",
      inputDate: "DataEntrada",
      device: "Equipamento",
      series: "Série",
      descriptionRepair: "Reparo",
      replacedPart: "PeçaSubstituída",
      value: "Valor",
      paymentMethod: "Método",
      warrantyPeriod: "Garantia"
    };
    //*bloco para !validar campos vazios ou com espaços
    for (const key in formulario) {
      //*O Js está "lendo as etiquetas" uma por uma(key).
      if (String(formulario[key]).trim() === "") {
        swal.fire({
          icon: 'error',
          title: 'Campo vazio',
          text: `O campo ${NomesCampos[key]} não pode estar vazio!`,
        });
        return false;
      }
    }
    return true;
  }

  //*ENVIAR DADOS PARA O JAVA
  async function HandleSubmit(e) {
    e.preventDefault();//* Evita que a página recarregue

    if (!validarCampos()) {//* Se a validação falhar, para aqui
      return;
    }

    //*FUNÇÃO PARA CONFIRMAÇÃO DE N° FICHA
    const confirmacao = await swal.fire({
      icon: 'question',
      title: 'Confirmar envio',
      text: `O número da ficha é ${formulario.record}. Deseja continuar?`,
      showCancelButton: true,

      cancelButtonText: 'Cancelar',
    });

    if (!confirmacao.isConfirmed) {
      return;
    }

    const dadosParaOJava = {
      empresa: {
        nameEnterprise: formulario.nameEnterprise,
        cnpjEnterprise: formulario.cnpjEnterprise,
        phoneEnterprise: formulario.phoneEnterprise,
      },
      cliente: {
        nameCustomer: formulario.nameCustomer,
        cpfCustomer: formulario.cpfCustomer,
        emailCustomer: formulario.emailCustomer,
        phoneCustomer: formulario.phoneCustomer,
        addressCustomer: formulario.addressCustomer,
        numberCustomer: formulario.numberCustomer,
        cityCustomer: formulario.cityCustomer,
        stateCustomer: formulario.stateCustomer,
      },
      servico: {
        record: formulario.record,
        inputDate: formulario.inputDate,
        device: formulario.device,
        series: formulario.series,
        descriptionRepair: formulario.descriptionRepair,
        replacedPart: formulario.replacedPart,
        value: formulario.value,
        paymentMethod: formulario.paymentMethod,
        warrantyPeriod: formulario.warrantyPeriod
      },

    }

    console.log("Dados para o Java:", dadosParaOJava);

    //*ENVIAR DADOS PELO AXIOS
    try {
      const response = await Api.post('/api/recibos', dadosParaOJava); //*COMO A API É CONFIGURADA, O ENDPOINT É O CAMINHO DA API
      console.log("Resposta do Java:", response.data); //*RESPOSTA DA API

      //*OPCIONAL: LIMPAR O FORMULÁRIO
      setFormulario({
        //*Empresa
        nameEnterprise: "",
        cnpjEnterprise: "",
        phoneEnterprise: "",
        //*Cliente
        nameCustomer: "",
        cpfCustomer: "",
        emailCustomer: "",
        phoneCustomer: "",
        addressCustomer: "",
        numberCustomer: "",
        cityCustomer: "",
        stateCustomer: "",
        //*Serviço
        record: "",
        inputDate: "",
        device: "",
        series: "",
        descriptionRepair: "",
        replacedPart: "",
        value: "",
        paymentMethod: "",
        warrantyPeriod: "",
      });


    } catch (error) {
      console.error("Erro ao enviar dados para o Java:", error); //*ERRO DA API
    }
  }
  //*FIM DA FUNÇÃO DE ENVIAR DADOS PARA O JAVA


  //* Função temporária para o botão funcionar
  function handleGeneratePdf() {
    setExibirPdf(true);
  }

  if (exibirPdf) {
    return <TwoPage onVoltar={() => setExibirPdf(false)} />;
  }

  //*INÍCIO DO RETORNO DA FUNÇÃO
  return (
    <div className='Container'>
      <h1>Recibo Digital</h1>

      <form onSubmit={HandleSubmit} noValidate>
        <h2>Dados da Empresa</h2>
        <p>
          <label htmlFor="nameEnterprise" autoComplete='off'>Razão Social:</label>
          <input
            type="text"
            id="nameEnterprise"
            name="nameEnterprise"
            required               // CRUCIAL: Tem que ser igual à chave do useState
            value={formulario.nameEnterprise}   // CRUCIAL: Liga o visual ao dado
            onChange={handleChange}   // CRUCIAL: Avisa quando muda
          />
        </p>

        <p>
          <label htmlFor="cnpjEnterprise" autoComplete='off'>CNPJ:</label>
          <input
            type="number"
            id="cnpjEnterprise"
            name="cnpjEnterprise"
            required
            value={formulario.cnpjEnterprise}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="phoneEnterprise" autoComplete='off'>Telefone:</label>
          <input
            type="number"
            id="phoneEnterprise"
            name="phoneEnterprise"
            required
            value={formulario.phoneEnterprise}
            onChange={handleChange}
            maxLength="14" // Uma dica simples para limitar tamanho
          />
        </p>
        <h2>Dados do Cliente</h2>
        <p>
          <label htmlFor="nameCustomer" autoComplete='off'>Nome:</label>
          <input
            type="text"
            id="nameCustomer"
            name="nameCustomer"
            required
            value={formulario.nameCustomer}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="cpfCustomer" autoComplete='off'>CPF:</label>
          <input
            type="number"
            id="cpfCustomer"
            name="cpfCustomer"
            required
            value={formulario.cpfCustomer}
            onChange={handleChange}
            maxLength="14"
          />
        </p>

        <p>
          <label htmlFor="emailCustomer" autoComplete='off'>Email:</label>
          <input
            type="text"
            id="emailCustomer"
            name="emailCustomer"
            required
            value={formulario.emailCustomer}
            onChange={handleChange}
          />
        </p>
        <p>
          <label htmlFor="phoneCustomer" autoComplete='off'>Telefone:</label>
          <input
            type="number"
            id="phoneCustomer"
            name="phoneCustomer"
            required
            value={formulario.phoneCustomer}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="addressCustomer" autoComplete='off'>Endereço:</label>
          <input
            type="text"
            id="addressCustomer"
            name="addressCustomer"
            required
            value={formulario.addressCustomer}
            onChange={handleChange}
          />
        </p>
        <p>
          <label htmlFor="numberCustomer" autoComplete='off'>N°:</label>
          <input
            type="number"
            id="numberCustomer"
            name="numberCustomer"
            required
            value={formulario.numberCustomer}
            onChange={handleChange}
          />
        </p>
        <p>
          <label htmlFor="cityCustomer" autoComplete='off'>Cidade:</label>
          <input
            type="text"
            id="cityCustomer"
            name="cityCustomer"
            required
            value={formulario.cityCustomer}
            onChange={handleChange}
          />
        </p>
        <p>
          <label htmlFor="stateCustomer" autoComplete='off'>Estado:</label>
          <input
            type="text"
            id="stateCustomer"
            name="stateCustomer"
            required
            value={formulario.stateCustomer}
            onChange={handleChange}
          />
        </p>


        <h2 translate='no'>Dados do Serviço</h2>
        <p>
          <label htmlFor="record" autoComplete='off'>N° Ficha:</label>
          <input
            type="number"
            id="record"
            name="record"
            required
            value={formulario.record}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="inputDate" translate='no' autoComplete='off'>Data Entrada:</label>
          <input
            type="date"
            id="inputDate"
            name="inputDate"
            required
            value={formulario.inputDate}
            onChange={handleChange}
          />
        </p>
        <p>
          <label htmlFor="device" autoComplete='off'>Equipamento:</label>
          <input
            type="text"
            id="device"
            name="device"
            required
            value={formulario.device}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="series" autoComplete='off'>Série:</label>
          <input
            type="text"
            id="series"
            name="series"
            required
            value={formulario.series}
            onChange={handleChange}
          />
        </p>
        <p>
          <label htmlFor="descriptionRepair" autoComplete='off'>Reparo:</label>
          <input
            type="text"
            id="descriptionRepair"
            name="descriptionRepair"
            required
            value={formulario.descriptionRepair}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="replacedPart" translate='no' autoComplete='off'>Peça Substituída:</label>
          <input
            type="text"
            id="replacedPart"
            name="replacedPart"
            required
            value={formulario.replacedPart}
            onChange={handleChange}
          />
        </p>

        <h2 translate='no'>Valores e Pagamento</h2>
        <p>
          <label htmlFor="value" translate="no" autoComplete='off'>Valor:</label>
          <input
            type="number"
            id="value"
            name="value"
            required
            value={formulario.value}
            onChange={handleChange}
          />
        </p>

        <p>
          <label htmlFor="paymentMethod" autoComplete='off'>Método:</label>
          <select
            type="text"
            id="paymentMethod"
            name="paymentMethod"
            required
            value={formulario.paymentMethod}
            onChange={handleChange}>

            <option value="" disabled selected>Selecione uma forma de pagamento</option>
            <option value="dinheiro">Dinheiro</option>
            <option value="cartao">Cartão</option>
            <option value="pix">PIX</option>

          </select>
        </p>

        <h2>Garantia</h2>

        <p>
          <label htmlFor="warrantyPeriod" autoComplete='off'>Garantia:</label>
          <input
            type="number"
            id="warrantyPeriod"
            name="warrantyPeriod"
            required
            value={formulario.warrantyPeriod}
            onChange={handleChange}
          />
        </p>
        <div>
          <button type="submit" translate='no'>Enviar</button>

          <ButtonPdf label="Gerar PDF" onClick={handleGeneratePdf} />
        </div>
      </form>



    </div>

  )
}

export default App