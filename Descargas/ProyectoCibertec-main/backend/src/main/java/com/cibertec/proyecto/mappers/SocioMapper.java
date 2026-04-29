package com.cibertec.proyecto.mappers;

import com.cibertec.proyecto.dtos.SocioDTO;
import com.cibertec.proyecto.dtos.SocioResponseDTO;
import com.cibertec.proyecto.entities.Socio;
import org.springframework.stereotype.Component;

@Component
public class SocioMapper {

    public Socio toEntity(SocioDTO dto) {
        if (dto == null) return null;
        Socio socio = new Socio();
        socio.setNombre(dto.nombre());
        socio.setApellido(dto.apellido());
        socio.setDni(dto.dni());
        socio.setTelefono(dto.telefono());
        socio.setEmail(dto.email());
        return socio;
    }

    public SocioResponseDTO toResponseDTO(Socio socio) {
        if (socio == null) return null;
        return new SocioResponseDTO(
                socio.getId(),
                socio.getNombre(),
                socio.getApellido(),
                socio.getDni(),
                socio.getTelefono(),
                socio.getEmail()
        );
    }
}
