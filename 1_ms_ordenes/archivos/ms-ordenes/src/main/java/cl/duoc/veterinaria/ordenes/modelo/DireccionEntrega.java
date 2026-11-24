package cl.duoc.veterinaria.ordenes.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DireccionEntrega {

    @Column(nullable = false, length = 150)
    private String calle;

    @Column(length = 50)
    private String departamento;

    @Column(nullable = false, length = 60)
    private String region;

    @Column(nullable = false, length = 60)
    private String comuna;

    @Column(length = 300)
    private String indicaciones;

    protected DireccionEntrega() {
    }

    public DireccionEntrega(String calle, String departamento, String region, String comuna, String indicaciones) {
        this.calle = calle;
        this.departamento = departamento;
        this.region = region;
        this.comuna = comuna;
        this.indicaciones = indicaciones;
    }

    public String getCalle() { return calle; }
    public String getDepartamento() { return departamento; }
    public String getRegion() { return region; }
    public String getComuna() { return comuna; }
    public String getIndicaciones() { return indicaciones; }
}
