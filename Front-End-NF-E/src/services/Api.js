import axios from "axios";

const Api = axios.create({
  // Já incluímos o caminho base da API aqui
  baseURL: (import.meta.env.VITE_API_URL || 'http://localhost:8080') + '/api/recibos'
});

export default Api;
