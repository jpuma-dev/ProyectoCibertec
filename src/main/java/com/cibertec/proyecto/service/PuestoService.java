package com.cibertec.proyecto.service;

import com.cibertec.proyecto.dto.PuestoDTO;
import com.cibertec.proyecto.entity.Puesto;
import com.cibertec.proyecto.entity.Socio;
import com.cibertec.proyecto.repository.PuestoRepository;
import com.cibertec.proyecto.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PuestoService {

    private final PuestoRepository puestoRepository;
    private final SocioRepository socioRepository;

    public Puesto crearPuesto(PuestoDTO dto) {

        boolean existe = puestoRepository.existsByNumero(dto.getNumero());
        if (existe) {
            throw new RuntimeException("El número de puesto ya existe");
        }

        Socio socio = null;

        if (dto.getSocioId() != null) {
            Optional<Socio> optionalSocio = socioRepository.findById(dto.getSocioId());

            if (optionalSocio.isEmpty()) {
                throw new RuntimeException("Socio no encontrado");
            }

            socio = optionalSocio.get();
        }

        Puesto puesto = new Puesto();
        puesto.setNumero(dto.getNumero());
        puesto.setSocio(socio);

        return puestoRepository.save(puesto);
    }
}