import React from "react";
import {AppBar, Toolbar, Typography, Button} from '@mui/material'
import {useNavigate} from 'react-router-dom'

export default function Navbar(){

    const navigate = useNavigate()

    const handleLogout = () => {
        localStorage.removeItem('token')
        navigate('/login')
    }

    return(
        <AppBar sx={{backgroundColor: '1e1e2f'}} >
            <Toolbar sx={{ display: "flex", justifyContent: "space-between"}} >
                <Typography variant="h6" sx={{ fontWeight: 'bold', cursor: 'pointer'}} onClick={() => navigate('/dashboard')}>
                    Tienda Adrian
                </Typography>
                <div>
                    <Button color="inherit" onClick={() => navigate('/dashboard')}>Productos</Button>
                    <Button color="inherit" onClick={handleLogout}>Salir</Button>
                </div>
            </Toolbar>
        </AppBar>
    )
}