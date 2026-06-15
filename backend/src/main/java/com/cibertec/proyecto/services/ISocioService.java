package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.SocioDTO;
import com.cibertec.proyecto.dtos.SocioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISocioService {
    SocioResponseDTO crearSocio(SocioDTO dto);
    void eliminarSocioPorDni(String dni);
    SocioResponseDTO actualizarSocio(String dni, SocioDTO dto);
    List<SocioResponseDTO> listarSocios();
    Page<SocioResponseDTO> listarSociosPaginado(Pageable pageable);
    Page<SocioResponseDTO> buscarSocios(String term, Pageable pageable);
    SocioResponseDTO obtenerPorDni(String dni);
}
