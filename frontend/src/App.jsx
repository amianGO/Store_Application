import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/auth/Login.jsx'
import Register from './pages/auth/Register.jsx';
import Dashboard from './pages/Dashboard.jsx'
export default function App() {
  console.log("React renderizando correctamente");
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  );
}
