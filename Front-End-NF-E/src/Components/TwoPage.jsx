import React, { useState } from "react";
import Api from "../services/Api";
import './TwoPage.css';

//voltar para pagina anterior
function TwoPage({ onVoltar }) {
    const [record, setRecord] = useState("");
    const [loading, setLoading] = useState(false);
    const [mensagem, setMensagem] = useState({ texto: "", tipo: "" });

    //impede que a pagina recarregue e pisque ao clicar
    async function handleDownload(e) {
        e.preventDefault();

        if (!record.trim()) {
            setMensagem({ texto: "Por favor, digite o número da ficha.", tipo: "erro" });
            return;
        }
        //limpa qualquer mensagem de erro anterior depois de avisar sobre o carregamento
        setLoading(true);
        setMensagem({ texto: "", tipo: "" });

        try {
            const response = await Api.get(`/api/recibos/download/${record}`, {
                responseType: 'blob'
            });

            //* Cria link temporário para download do PDF
            const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `recibo_${record}.pdf`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);

            //Tratamento de erro 
            setMensagem({ texto: `Recibo N° ${record} baixado com sucesso!`, tipo: "sucesso" });
        } catch (error) {
            console.error("Erro ao baixar recibo:", error);
            if (error.response && error.response.status === 404) {
                setMensagem({ texto: `Recibo com ficha N° ${record} não encontrado.`, tipo: "erro" });
            } else {
                setMensagem({ texto: "Erro ao conectar com o servidor. Tente novamente.", tipo: "erro" });
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="Container pageTwo">
            <h1>Buscador de Recibo</h1>

            <form className="download-form" onSubmit={handleDownload}>
                <h2>Buscar Recibo</h2>

                <p>
                    <label htmlFor="recordSearch">N° Ficha:</label>
                    <input
                        type="text"
                        id="recordSearch"
                        name="recordSearch"
                        placeholder="Digite o número da ficha"
                        value={record}
                        onChange={(e) => setRecord(e.target.value)}
                        autoComplete="off"
                        disabled={loading}
                    />
                </p>

                {mensagem.texto && (
                    <div className={`mensagem ${mensagem.tipo}`}>
                        {mensagem.texto}
                    </div>
                )}

                <div className="botoes-download">
                    <button
                        type="submit"
                        className="btn-download"
                        disabled={loading}
                    >
                        {loading ? "Baixando..." : "Baixar Recibo"}
                    </button>

                    <button
                        type="button"
                        className="btn-voltar"
                        onClick={onVoltar}
                        disabled={loading}
                    >
                        Voltar
                    </button>
                </div>
            </form>
        </div>
    );
}

export default TwoPage;