package cl.duoc.veterinaria.ordenes.dto;

import java.util.List;

import cl.duoc.veterinaria.ordenes.modelo.DetalleOrden;
import cl.duoc.veterinaria.ordenes.modelo.Orden;
import cl.duoc.veterinaria.ordenes.modelo.PagoTarjeta;

/** Orden con la misma forma que usa el frontend (tipo Orden de src/tipos/index.ts) */
public record OrdenResponse(
        Long id,
        String numero,
        String fecha,
        ClienteDto cliente,
        DireccionDto direccion,
        List<ItemResponse> items,
        Integer total,
        String estado,
        String motivoRechazo,
        Long usuarioId,
        PagoResponse pago) {

    public record ItemResponse(Long productoId, String nombre, Integer precio, Integer cantidad, String imagen) {

        static ItemResponse desde(DetalleOrden detalle) {
            return new ItemResponse(detalle.getProductoId(), detalle.getNombre(), detalle.getPrecio(),
                    detalle.getCantidad(), detalle.getImagen());
        }
    }

    public record PagoResponse(String marca, String ultimos4, String titular, String codigoAutorizacion) {

        static PagoResponse desde(PagoTarjeta pago) {
            if (pago == null) {
                return null;
            }
            return new PagoResponse(pago.getMarca(), pago.getUltimos4(), pago.getTitular(),
                    pago.getCodigoAutorizacion() == null ? "" : pago.getCodigoAutorizacion());
        }
    }

    public static OrdenResponse desde(Orden orden) {
        return new OrdenResponse(
                orden.getId(),
                orden.getNumero(),
                orden.getFecha().toString(),
                new ClienteDto(orden.getCliente().getNombre(), orden.getCliente().getApellidos(),
                        orden.getCliente().getCorreo()),
                new DireccionDto(orden.getDireccion().getCalle(), vacioSiNulo(orden.getDireccion().getDepartamento()),
                        orden.getDireccion().getRegion(), orden.getDireccion().getComuna(),
                        vacioSiNulo(orden.getDireccion().getIndicaciones())),
                orden.getItems().stream().map(ItemResponse::desde).toList(),
                orden.getTotal(),
                orden.getEstado().aTexto(),
                vacioSiNulo(orden.getMotivoRechazo()),
                orden.getUsuarioId(),
                PagoResponse.desde(orden.getPago()));
    }

    private static String vacioSiNulo(String texto) {
        return texto == null ? "" : texto;
    }
}
