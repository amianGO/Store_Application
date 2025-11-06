import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import {Grid, Card, CardContent, Typography, CircularProgress, Box} from '@mui/material'
import axiosInstance from '../config/axios';

export default function Dashboard() {
  const [loading, setLoading] = useState(true)
  const [productos, setProductos] = useState('')
  const [error, setError] = useState('')

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) navigate('/login'); // redirige si no hay token

    const fetchProductos = async () => {
    try {
      const response = await axiosInstance.get('/productos')
      setProductos(response.data)
    } catch (error) {
      console.log("Error al cargar los productos", error)
    } finally {
      setLoading(false)
    }
  };

  fetchProductos();
  }, [navigate]);

  if (loading) {
    return(
      <Box sx={{ display: 'flex', justifyContent:'center', alignItems:'center', height: '100hv'}}>
        <CircularProgress/>
      </Box>
    )
  }

  return(
    <>
      <Navbar />
      <Box sx={{p: 4}}>
        <Typography variant='h4' sx={{mb: 3, fontWeight: 'bold'}} >
          Lista de Productos
        </Typography>
        {productos.length === 0 ? (
          <Typography>No hay productos Disponibles</Typography>
        ): (
          <Grid container spacing={3}>
            {productos.map((prod) => (
              <Grid items xs={12} sm={6} md={4} lg={3} key={prod.id}>
                <Card sx={{
                  backgroundColor: '#b47edaff',
                  borderRadius: 3,
                  boxShadow: 3,
                  '&:hover': {transform: 'scale(1.02)', transition: '0.3s'}
                }}>
                  <CardContent>
                    <Typography>
                      {prod.nombre}
                    </Typography>

                    <Typography>
                      {prod.codigo}
                    </Typography>

                    <Typography>
                      {prod.descripcion}
                    </Typography>

                    <Typography>
                      {prod.precioCompra}
                    </Typography>

                    <Typography>
                      {prod.precioVenta}
                    </Typography>

                    <Typography>
                      {prod.stock}
                    </Typography>

                    <Typography>
                      {prod.stockMinimo}
                    </Typography>

                    <Typography>
                      {prod.fechaRegistro}
                    </Typography>

                    <Typography>
                      {prod.estadoActivo}
                    </Typography>

                    <Typography>
                      {prod.categoria }
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    </>
  );
}