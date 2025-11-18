import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/auth/Login.jsx'
import Register from './pages/auth/Register.jsx';
import Dashboard from './pages/Dashboard.jsx'
import CreateProduct from './pages/productos/CreateProduct.jsx'
import ProductDetail from './pages/productos/ProductDetail.jsx'
import EditProduct from './pages/productos/EditProduct.jsx'
import ClienteManagement from './pages/ClienteManagement.jsx'
import EmpleadoManagement from './pages/EmpleadoManagement.jsx'
import VentasRealizadas from './pages/ventas/VentasRealizadas.jsx'

export default function App() {
  console.log("React renderizando correctamente");
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/productos/create" element={<CreateProduct />} />
      <Route path="/productos/detail/:id" element={<ProductDetail />} />
      <Route path="/productos/edit/:id" element={<EditProduct />} />
      <Route path="/clientes" element={<ClienteManagement />} />
      <Route path="/empleados" element={<EmpleadoManagement />} />
      <Route path="/ventas" element={<VentasRealizadas />} />
    </Routes>
  );
}
