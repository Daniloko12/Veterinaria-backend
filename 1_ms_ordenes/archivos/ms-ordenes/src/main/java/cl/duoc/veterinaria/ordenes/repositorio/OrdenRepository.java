package cl.duoc.veterinaria.ordenes.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.veterinaria.ordenes.modelo.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    /** Compras de un cliente: las hechas con su cuenta o con su correo como invitado */
    @Query("select o from Orden o where o.usuarioId = :usuarioId or lower(o.cliente.correo) = lower(:correo) "
            + "order by o.fecha desc")
    List<Orden> buscarDelCliente(@Param("usuarioId") Long usuarioId, @Param("correo") String correo);
}
