import axios from "axios";

const Api = axios.create({
  // Tenta pegar a variável, se não achar, usa o localhost como segurança
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
});

export default Api;